import React, {useState, useEffect, useRef} from 'react';
import axios from 'axios';
import './Queue.css';

const Queue = props => {
    const changeColor = () => {
        console.log("changed")
        for (const btn of document.querySelectorAll('.vote')) {
            btn.addEventListener('click', event => {
                event.currentTarget.classList.toggle('on');
            });
        }
    }

    const updateSongValue = (props) => {
        console.log("changed")
        for (const btn of document.querySelectorAll('.vote')) {
            btn.addEventListener('click', event => {
                event.currentTarget.classList.toggle('on');
            });
        }
        let songName = props.target.id
        console.log(songName)
        const toSend = {
            increased: songName
        }
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        axios.post(
            "http://localhost:4567/rankings",
            toSend,
            config
        )
            .then(response => {
            })
            .catch(function (error) {
                console.log(error);
            });
    }

    return (
        <div className="App"><center>
            <h2>Queue</h2>
            <br></br>
            {props.songQueue[0] && (
                <table id="table" className="table" border="1px" table-layour="fixed" bordercolor="black">
                    <tbody>
                    {props.songQueue.map(item =>
                        <tr>
                            <td align="center">
                                <span className="vote" onClick={updateSongValue}>
                                  <svg width="36" height="36">
                                    <path d="M2 26h32L18 10 2 26z" fill="currentColor" id={item.name}></path>
                                  </svg>
                                </span>
                                <span className="vote" onClick={changeColor}>
                                  <svg width="36" height="36">
                                    <path d="M2 10h32L18 26 2 10z" fill="currentColor"></path>
                                  </svg>
                                </span>
                            </td>
                            <td align="center"><img src={item.artwork} width="50" align="center"/></td>
                            <td align="center" style={{fontSize: 13, padding: 10}}>{item.name}</td>
                        </tr>
                    )
                    }
                    </tbody>
                </table>
            )}
        </center>
            <br></br>
        </div>
    );
}

export default Queue;