import React, { Component } from 'react';
import logo from './comp.png';
import './App.css';
import Route from './SpotHouse.js';

/**
 * Main component, for the website and logo.
 */
class App extends Component {
    render() {
    return (
      <div className="App">
        <div className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h2>SpotHouse</h2>
        </div>
        <div>
            <SpotHouse/>
        </div>
      </div>
    );
  }
}

export default App;
