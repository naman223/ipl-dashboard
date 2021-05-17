package com.ipl.dashboard.controller;

import com.ipl.dashboard.model.Match;
import com.ipl.dashboard.model.Team;
import com.ipl.dashboard.repository.MatchRepository;
import com.ipl.dashboard.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
public class TeamController {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchRepository matchRepository;

    private static final Logger log = LoggerFactory.getLogger(TeamController.class);

    @GetMapping("/team/{teamName}")
    public ResponseEntity<Team> getTeam(@PathVariable String teamName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        Team team = teamRepository.findByTeamName(teamName);
        if(team==null) {
            log.error("Team not found having name {}",teamName);
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        } else {
            log.info("Team found having name {}", teamName);
            team.setMatches(matchRepository.findLatestMatchesByTeam(teamName,4));
            return new ResponseEntity<>(team, headers, HttpStatus.OK);
        }
    }

    @GetMapping("/team/{teamName}/matches")
    public List<Match> getMatchesForTeam(@PathVariable String teamName, @RequestParam int year) {
        LocalDate startDate = LocalDate.of(year,1,1);
        LocalDate endDate = LocalDate.of(year + 1,1,1);
        Team team = teamRepository.findByTeamName(teamName);
        if(team==null) {
            log.error("Team not found having name {}",teamName);
            return null;
        } else {
            log.info("Team found having name {}", teamName);
             return matchRepository.getMatchByTeamBetweenDates(
                     teamName, startDate,endDate);
        }
    }

    @GetMapping("/team")
    public Iterable<Team> getAllTeam() {
        return teamRepository.findAll();
    }
}
