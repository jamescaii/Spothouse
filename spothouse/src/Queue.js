import React, { useState, useEffect } from 'react';

const Queue = props => {
  return (
    <div className="App"><center>
      <h2>Queue</h2>
      <br></br>
      {props.songQueue[0] && (
      <table id="table" className="table" border="1px" table-layour="fixed">

      <tbody>
        
        {props.songQueue.map(item => 
          <tr>
            <td align="center"><img src={item.artwork} width="50" align="center"/></td>
            <td align="center" style={{fontSize: 15, padding: 10}}>{item.name}</td>
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