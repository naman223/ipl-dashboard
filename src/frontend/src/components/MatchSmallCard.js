import "./MatchSmallCard.scss";
import { React } from 'react';
import {Link} from 'react-router-dom';

export const MatchSmallCard = ({teamName, match}) => {

    if(!match) return null; //if no matches defined return null
    const otherTeam = match.team1 === teamName ? match.team2 : match.team1;
    const otherTeamRoute = `/teams/${otherTeam}`;
    const isMatchWon = teamName === match.matchWinner;
    return (
        <div className={isMatchWon ? 'MatchSmallCard won-card' : 'MatchSmallCard lost-card'}>
            <span className="vs">vs</span>
            <h1><Link to={otherTeamRoute}>{otherTeam}</Link></h1>
            <p className="match-result-section">{match.date}</p>
            <p className="match-result-section">{match.matchWinner} won by {match.resultMargin} {match.result}</p>
        </div>
    );
}

