package com.ipl.dashboard.data;

import com.ipl.dashboard.model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    private final EntityManager entityManager;

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate, EntityManager entityManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            /*jdbcTemplate.query("SELECT team1, count(1) FROM match group by team1",
                    (rs, row) -> "Team 1-> " + rs.getString(1) + " : Team 2->" + rs.getString(2)
            ).forEach(str -> log.info(str));*/

            Map<String, Team> teamData = new HashMap<>();

            entityManager.createQuery("SELECT team1, count(1) FROM Match group by team1", Object[].class)
                    .getResultList()
                    .stream()
                    .map(obj -> new Team((String) obj[0], (long) obj[1]))
                    .forEach(team -> teamData.put(team.getTeamName(), team));

            entityManager.createQuery("SELECT team2, count(1) FROM Match group by team2", Object[].class)
                    .getResultList()
                    .stream()
                    .forEach(obj -> {
                        Team team = teamData.get((String) obj[0]);
                        if (team != null)
                            team.setTotalMatches(team.getTotalMatches() + (long) obj[1]);
                        else {
                            team = new Team((String) obj[0], (long) obj[1]);
                            teamData.put(team.getTeamName(), team);
                            team.setTotalMatches(team.getTotalMatches());
                        }
                    });

            entityManager.createQuery("select matchWinner, count(1) from Match group by matchWinner",Object[].class)
                    .getResultList()
                    .stream()
                    .forEach(obj -> {
                        Team team = teamData.get((String) obj[0]);
                        if(team!=null) {
                           team.setTotalWins((long) obj[1]);
                        }
                    });

            teamData.values().forEach(team -> entityManager.persist(team));

            teamData.values().forEach(team -> System.out.println(team.toString()));

        }
    }
}
