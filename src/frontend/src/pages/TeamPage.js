import './TeamPage.scss';
import { React, useEffect, useState, } from 'react';
import {Link, useParams} from 'react-router-dom';
import { PieChart } from 'react-minimal-pie-chart';
import {MatchDetailCard} from "../components/MatchDetailCard";
import {MatchSmallCard} from "../components/MatchSmallCard";

export const TeamPage = () => {

    //initialize state [ name, function ]
    const [team, setTeam ] = useState({ matches: []});
    const {teamName} = useParams();
    useEffect( // method when component load
        () => {
            const fetchMatches = async () => {
                //body of function, useEffect doesn't contain async function so define async
                const response = await fetch(`http://localhost:8081/team/${teamName}`);
                const data = await response.json();
                console.log(data);
                setTeam(data); //store data in state which used to display
            };
            fetchMatches();
        }, [teamName] //call only once to avoid infinite call
    );

    if( !team || !team.teamName) {
        return <h1>Team Not Found</h1>
    }
    return (
        <div className="TeamPage">
            <div className="team-name-section">
                <h1 className="team-name">{team.teamName}</h1>
            </div>
            <div className="win-loss-section">
                <h3>Win/Losses</h3>
                <PieChart
                    data={[
                        { title: 'Losses', value: team.totalMatches - team.totalWins, color: '#C70039' },
                        { title: 'Wins', value: team.totalWins, color: '#29961A' },
                    ]}
                />
            </div>
            <div className="match-detail-section">
                <h3>Latest Matches</h3>
                <MatchDetailCard teamName={team.teamName} match={team.matches[0]}/>
            </div>
            {team.matches.slice(1).map(match => <MatchSmallCard teamName={team.teamName} match={match} />)}
            <div className="more-section">
                <Link to={`/teams/${teamName}/matches/${process.env.REACT_APP_DATA_END_YEAR}`}>More ></Link>
            </div>
        </div>
    );
}

