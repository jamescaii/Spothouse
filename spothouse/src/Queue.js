import React, {useState, useEffect, useRef} from 'react';
import axios from 'axios';

const Queue = props => {
    let currentQueue = []
    let orderedList = []
    for (let i = 0; i < props.songQueue.length; i++) {
        let name = props.songQueue[i].name
        currentQueue.push(name)
    }
    let tempQueue = props.songQueue
    const newQueue = useRef([])

    const updateSongValue = (props) => {
        let songName = props.target.className
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
                orderedList = response.data["songList"]
                newQueue.current = []
                for (let i = 0; i < orderedList.length; i++) {
                    let songName = orderedList[i].name
                    for (let j = 0; j < tempQueue.length; j++) {
                        if (tempQueue[j].name === songName) {
                            newQueue.current.push(tempQueue[j])
                        }
                    }
                }
                console.log(response.data["songList"])
                console.log("NEW QUEUE", newQueue)
            })
            .catch(function (error) {
                console.log(error);
            });
    }

    const updateBackendQueue = () => {
        const toSend = {
            songs: currentQueue
        }
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        axios.post(
            "http://localhost:4567/queue",
            toSend,
            config
        )
            .then(response => {
                // console.log("THIS IS THE BACKEND QUEUE", response)
                orderedList = response.data["songList"]
                newQueue.current = []
                for (let i = 0; i < orderedList.length; i++) {
                    let songName = orderedList[i].name
                    for (let j = 0; j < tempQueue.length; j++) {
                        if (tempQueue[j].name === songName) {
                            newQueue.current.push(tempQueue[j])
                        }
                    }
                }
            })
            .catch(function (error) {
                console.log(error);
            });
    }
    updateBackendQueue()

    // console.log("Current Queue", currentQueue)
    return (
        <div className="App"><center>
            <h2>Queue</h2>
            <br></br>
            {props.songQueue[0] && (
                <table id="table" className="table" border="1px" table-layour="fixed" bordercolor="black">
                    <tbody>
                    {newQueue.current.map(item =>
                        <tr>
                            <td align="center"><input type="button" className={item.name} value="Upvote" onClick={updateSongValue}/></td>
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