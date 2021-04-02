import React, { Component } from 'react';
import './App.css';
import SpotHouse from './SpotHouse.js';

/**
 * Main component, for the website and logo.
 */
class App extends Component {
    render() {
    return (
      <div className="App">
        <div className="App-header">
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
