import React, { Component } from 'react';
import './App.css';
import SpotHouse from './SpotHouse.js';
import logo from './logo.png';

/**
 * Main component, for the website and logo.
 */
class App extends Component {
    render() {
    return (
      <div className="App">
        <div className="App-header">
          <img src={logo} width="80px"></img>
          <br></br>
          <h2>SpotHouse!</h2>
          <br></br>
        </div>
        <div>
            <SpotHouse/>
        </div>
      </div>
    );
  }
}

export default App;
