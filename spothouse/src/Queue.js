import React, { useRef } from 'react';
import axios from 'axios';
import './Queue.css';
import Button from 'react-bootstrap/Button'

const Queue = props => {
    const clickedMap = useRef([])
    let roomCode = props.roomCode

    const handleUpvote = async (event) => {
        let songName = event.target.id
        let found = false;
        for (let i = 0; i < clickedMap.current.length; i++) {
            if (clickedMap.current[i].name === songName) {
                console.log(clickedMap.current[i].name)
                clickedMap.current[i].upboolean = !clickedMap.current[i].upboolean
                found = true
            }
        }
        console.log(clickedMap.current)
        if (!found) {
            clickedMap.current.push({name: songName, upboolean: true})
        }
        const toSend = {
            toChange: songName,
            isIncrease: true,
            isReset: false,
            rCode: roomCode,
        }
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        await axios.post(
            "/rankings",
            toSend,
            config
        )
            .then(response => {
            })
            .catch(function (error) {
                console.log(error);
            });

    }
    

    const handleDownvote = async (event) => {
        let songName = event.target.id
        let found = false;
        for (let i = 0; i < clickedMap.current.length; i++) {
            if (clickedMap.current[i].name === songName) {
                console.log(clickedMap.current[i].name)
                clickedMap.current[i].downboolean = !clickedMap.current[i].downboolean
                found = true
            }
        }
        console.log(clickedMap.current)
        if (!found) {
            clickedMap.current.push({name: songName, downboolean: true})
        }
        const toSend = {
            toChange: songName,
            isIncrease: false,
            isReset: false,
            rCode: roomCode,
        }
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        await axios.post(
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
    
    function Clicked(songName, isUp) {        
        for (let i = 0; i < clickedMap.current.length; i++) {
            if (clickedMap.current[i].name === songName) {
                //console.log(clickedMap.current[i].name, songName)
                if (isUp)
                    return clickedMap.current[i].upboolean
                else
                    return clickedMap.current[i].downboolean
            }
        }
        return false
    }

    function getColorUp(songName) {
        //console.log(songName)
        if (!Clicked(songName, true)) {
            //console.log("not clicked")
            return { fill: "#687074" }
        }
        else {            
            //console.log("clicked")
            return { fill: "#f48024" }
        }
    }
    
    function getColorDown(songName) {
        //console.log(songName)
        if (!Clicked(songName, false)) {
            //console.log("not clicked")
            return { fill: "#687074" }
        }
        else {            
            //console.log("clicked")
            return { fill: "#f48024" }
        }
    }
    function renderRemove(uri) {
        if (props.isHost) {
            return (
            <td align="center">
                <Button className="btn btn--remove" size="sm" onClick={() => removeFromBackend(uri)}>
                X
                </Button>{' '}
            </td>
            );
        }
    }
    function removeFromBackend(uri) {
      const toSend = {
        songUri: uri,
        code: roomCode
      }
      let config = {
        headers: {
          "Content-Type": "application/json",
          'Access-Control-Allow-Origin': '*',
        }
      }
      axios.post(
          "http://localhost:4567/remove",
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
                        <tr key={item.name}>
                            {renderRemove(item.uri)}
                            <td align="center">
                                <span className="voteup" onClick={handleUpvote}>
                                  <svg width="36" height="36" >
                                    <path d="M2 26h32L18 10 2 26z" style={getColorUp(item.name)} id={item.name} ></path>
                                  </svg>
                                </span>
                                <span className="votedown" onClick={handleDownvote}>
                                  <svg width="36" height="36">
                                    <path d="M2 10h32L18 26 2 10z" style={getColorDown(item.name)} id={item.name}></path>
                                  </svg>
                                </span>
                            </td>
                            <td align="center"><img src={item.artwork} width="50" align="center" alt="album art"/></td>
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