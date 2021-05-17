import './MatchPage.scss';
import { React, useEffect, useState, } from 'react';
import { useParams } from 'react-router-dom';
import {MatchDetailCard} from "../components/MatchDetailCard";
import {YearSelector} from "../components/YearSelector";

export const MatchPage = () => {

    const [matches, setMatches] = useState([]);
    const {teamName, year} = useParams();
    useEffect( // method when component load
        () => {
            const fetchMatches = async () => {
                //body of function, useEffect doesn't contain async function so define async
                const response = await fetch(`http://localhost:8081/team/${teamName}/matches?year=${year}`);
                const data = await response.json();
                console.log(data);
                setMatches(data); //store data in state which used to display
            };
            fetchMatches();
        }, [teamName, year] //call only once to avoid infinite call
    );

    return (
        <div className="MatchPage">
            <div className="year-selector">
                <h3> Select Year </h3>
                <YearSelector teamName={teamName} />
            </div>
            <div>
                <h1 className="page-heading">{teamName} matches in {year}</h1>
                {
                    matches.map(match => <MatchDetailCard key={match.id} teamName={teamName} match={match} />)
                }
            </div>

        </div>
    );
}

