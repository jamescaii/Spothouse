import React, { Component } from "react";
import * as $ from "jquery";
import { authEndpoint, clientId, redirectUri, scopes } from "./Config";
import { AwesomeButton } from "react-awesome-button";
import Queue from "./Queue"
import hash from "./hash";
import "./App.css";
import TextBox from './TextBox';
import Player from './Player';
import axios from "axios";

class SpotHouse extends Component {
  constructor() {
    super();
    this.state = {
      token: null,
      item: {
        album: {
          images: [{ url: "" }]
        },
        name: "",
        artists: [{ name: "" }],
        duration_ms: 0
      },
      is_playing: "Paused",
      progress_ms: 0,
      no_data: false,
      no_top_data: false,
      searchQuery: "",
      searchResults: [
        {
          name: "",
          artist: "",
          uri: "",
          artwork: "",
        }
      ],
      clickedSongURI: "",
      conn: null,
      lobbyID: -1,
      myID: -1,
      currentQueue: [],
      currentQueueRequesters: [],
      topTracks: [],
      count: 0,
      added: false
    };

    this.setup_lobby();
    this.setup_websocket();
    this.getCurrentlyPlaying = this.getCurrentlyPlaying.bind(this);
    this.tick = this.tick.bind(this);
  }

  changeQuery(param) {
    this.setState({ searchQuery: param })
    console.log(param)
  }

  componentDidMount() {
    // Set token
    let _token = hash.access_token;

    if (_token) {
      // Set token
      this.setState({
        token: _token
      });
      this.getTopTracks(_token);
      this.getCurrentlyPlaying(_token);
    }

    // set interval for polling every .5 seconds
    this.interval = setInterval(() => this.tick(), 500);
  }

  componentWillUnmount() {
    // clear the interval to save resources
    clearInterval(this.interval);
  }

  tick() {
    if(this.state.token) {
      this.getCurrentlyPlaying(this.state.token);
      this.updateBackendQueue();
      if (this.state.progress_ms/this.state.item.duration_ms > .95 & !this.state.added) {
        if (this.state.currentQueue.length > 0)
          this.addToSpotifyQueue(this.state.token);
      }
    }
  }

  MESSAGE_TYPE = {
    CONNECT: 0,
    UPDATE: 1,
    SEND: 2
  };

  setup_lobby = () => {
    const toSend = {
    }
    let config = {
      headers: {
        "Content-Type": "application/json",
        'Access-Control-Allow-Origin': '*',
      }
    }
    axios.post(
        "/setupGUI",
        toSend,
        config
    )
        .then(response => {
          // console.log("THIS IS THE BACKEND QUEUE", response)
          this.setState({lobbyID: response.data["lobbyID"]})
        })
        .catch(function (error) {
          console.log(error);
        });
  }

  setup_websocket = () => {
    // TODO create WebSocket listening at and sending to ws://localhost:4567/message
    //const conn = new WebSocket("https://spothouse-app.herokuapp.com/callback#");
    const conn = new WebSocket("ws://localhost:4567/0");

    // TODO set up client response to messages received from server
    conn.onmessage = msg => {
      const data = JSON.parse(msg.data);
      switch (data.type) {
          // default can go anywhere in the switch order ^_^
        default:
          console.log('Unknown message type!', data.type);
          break;
        case this.MESSAGE_TYPE.CONNECT:
          // We get this message when the server recognizes that we (the client) have connected and sends back our unique id
          this.myId = data.getProperty("id");
          break;
        case this.MESSAGE_TYPE.UPDATE:
          // We get this message when some client broadcast a message to all clients
          // TODO append to the message board unordered list (in chat.ftl) the received message
          this.currentQueue.push(data.getProperty("songID"));
          break;
      }
    };
  }

// When a user hits the Send! button this method gets called (chat.ftl)
  sendRequest = () => {
    // TODO create a new message of type SEND
    let req = {
      uri: this.clickedSongURI
    }
    // TODO use conn to send the message to the server
    this.conn.send(req);
  }
  
  updateBackendQueue = () => {
    let current = []
    let currentRequesters = []
    let orderedList = []
    let orderedListRequesters = []
    let newQueue = []
    let newQueueRequesters = []
    for (let i = 0; i < this.state.currentQueue.length; i++) {
        let name = this.state.currentQueue[i].name
        let requester = this.state.currentQueueRequesters[i].name
        current.push(name)
        currentRequesters.push(requester)
    }
      const toSend = {
          songs: current,
          users:currentRequesters
      }
      let config = {
          headers: {
              "Content-Type": "application/json",
              'Access-Control-Allow-Origin': '*',
          }
      }
      axios.post(
          "/queue",
          toSend,
          config
      )
          .then(response => {
              // console.log("THIS IS THE BACKEND QUEUE", response)
              orderedList = response.data["songList"]
              orderedListRequesters = response.data["requesterList"]
              for (let i = 0; i < orderedList.length; i++) {
                  let songName = orderedList[i].name
                  let requesterName = orderedListRequesters[i].name
                  for (let j = 0; j < this.state.currentQueue.length; j++) {
                      if (this.state.currentQueue[j].name === songName) {
                          newQueue.push(this.state.currentQueue[j])
                          newQueueRequesters.push(this.state.currentQueueRequesters[j])
                      }
                  }
              }
              this.setState({currentQueue: newQueue})
          })
          .catch(function (error) {
              console.log(error);
          });
  }

  addToSpotifyQueue = (token) => {
    let toAdd = encodeURIComponent(this.state.currentQueue[0].uri.trim()) 
    // Make a call using the token
    $.ajax({
      url: "https://api.spotify.com/v1/me/player/queue?uri=" + toAdd,
      type: "POST",
      beforeSend: xhr => {
        xhr.setRequestHeader("Authorization", "Bearer " + token);
      },
      success: data => {
        this.state.currentQueue.shift();
        this.setState({added: true})
      }
    });
  }
  scrollToBottom = () => {
    this.endPage.scrollIntoView({ behavior: "smooth" });
  }
  componentDidUpdate(prevProps, prevState) {
    if (prevState.count !== this.state.count)
      this.scrollToBottom();
    if (prevState.item.name !== this.state.item.name)
      this.setState({added: false})
  }
  getSearch(token, searchQuery) {
    // parse searchQuery
    let searchQueryParameter = encodeURIComponent(searchQuery.trim()) 
    // Make a call using the token
    $.ajax({
      url: "https://api.spotify.com/v1/search?q=" + searchQueryParameter + "&type=track",
      type: "GET",
      beforeSend: xhr => {
        xhr.setRequestHeader("Authorization", "Bearer " + token);
      },
      success: data => {
        //console.log(data)
        // Checks if the data is not empty
        if(data) {
          console.log(data)
          this.setState({
            searchResults: data.tracks.items.map((item) => ({
              name: item.name,
              artist: item.artists[0].name,
              uri: item.uri,
              artwork: item.album.images[0].url
            }
          )
          )});
          console.log(this.state.searchResults)
          return;
        }

        this.setState({
          item: data.item,
        });
      }
    });
  }

  getCurrentlyPlaying(token) {
    // Make a call using the token
    $.ajax({
      url: "https://api.spotify.com/v1/me/player",
      type: "GET",
      beforeSend: xhr => {
        xhr.setRequestHeader("Authorization", "Bearer " + token);
      },
      success: data => {
        // Checks if the data is not empty
        if(!data) {
          this.setState({
            no_data: true,
          });
          return;
        }

        this.setState({
          item: data.item,
          is_playing: data.is_playing,
          progress_ms: data.progress_ms,
          no_data: false /* We need to "reset" the boolean, in case the
                            user does not give F5 and has opened his Spotify. */
        });
      }
    });
  }
  
  getTopTracks(token) {
    // Make a call using the token
    $.ajax({
      url: "https://api.spotify.com/v1/me/top/tracks?time_range=short_term&limit=20",
      type: "GET",
      beforeSend: xhr => {
        xhr.setRequestHeader("Authorization", "Bearer " + token);
      },
      success: data => {
        // Checks if the data is not empty
        if(!data) {
          this.setState({
            no_top_data: true,
          });
          return;
        }
        this.setState({
          topTracks: data.items.map((item) => ({
            name: item.name,
            artist: item.artists[0].name,
            uri: item.uri,
            artwork: item.album.images[0].url
          }
          ))
        });
        console.log(this.state.topTracks)
      }
    });
  }

  async clickResult(e) {
    let clickedArtist = e.currentTarget.textContent.split(" -, ")[0]
    let clickedName = e.currentTarget.textContent.split(" -, ")[1]
    let clickedURI = e.currentTarget.textContent.split(" -, ")[2]
    let clickedArt = e.currentTarget.textContent.split(" -, ")[3]
    await this.setState({clickedSongURI: clickedURI})
    var joined = this.state.currentQueue.concat({name: clickedName,
      artist: clickedArtist,
      artwork: clickedArt,
      uri: clickedURI});
    await this.setState({currentQueue: joined})
    this.sendRequest();
    this.setState({count: this.state.count + 1})
  }

  render() {
    return (
      <div className="App">
        <header className="App-header">
          {!this.state.token && (
            <a
              className="btn btn--loginApp-link"
              href={`${authEndpoint}?client_id=${clientId}&redirect_uri=${redirectUri}&scope=${scopes.join(
                "%20"
              )}&response_type=token&show_dialog=true`}
            >
              Login to Spotify
            </a>
          )}
          {this.state.token && (
            <>
            <TextBox label="Search for a song:" force={this.state.searchQuery} onChange={this.changeQuery.bind(this)}/>
            <hr style={{height:10, visibility:"hidden"}} />
            <AwesomeButton type="primary" className="btn btn--search" onPress={() => {
                this.getSearch(this.state.token, this.state.searchQuery)
            }}>Submit</AwesomeButton>
            <br></br>
            <p style={{fontSize:"small"}}>Click on a song to add it to the queue!</p>
            <br></br>
            <div class="row">
              <div class="column">
                {this.state.token && !this.state.no_top_data && (
                  <>     
                  <h4>Your top tracks:</h4>
                  <br></br>
                    {this.state.topTracks.map(item => <p className="search" onClick={item => this.clickResult(item)}>
                      {item.artist} -<span style={{display: "none"}}>,</span> {item.name}<div style={{display: "none"}}> -, {item.uri} -, {item.artwork}</div></p>)}
                  <br></br>
                  </>
                )}
              </div>
              <div class="column">
                <hr width="300" style={{visibility:"hidden"}}/>
                {this.state.searchResults[0].name && (
                  <>
                  <h4>Search results:</h4>
                  <br></br>
                  {this.state.searchResults.map(item => <p className="search" onClick={item => this.clickResult(item)}>
                    {item.artist} -<span style={{display: "none"}}>,</span> {item.name}<div style={{display: "none"}}> -, {item.uri} -, {item.artwork}</div></p>)} 
                  </>
                )}

              </div>
            </div>
            </>
          )}    
        </header>
        <br></br>
        {this.state.token && !this.state.no_data && this.state.item && (
            <>
            <Player
              item={this.state.item}
              is_playing={this.state.is_playing}
              progress_ms={this.state.progress_ms}
            />
            
            <Queue 
              songQueue = {this.state.currentQueue}
              />
            <br></br>
            </>
          )}
          {this.state.no_data && (
            <>     
            
            <Queue 
              songQueue = {this.state.currentQueue}
              />
            <br></br>
              
            </>
          )}
          <div style={{ float:"left", clear: "both" }}
               ref={(el) => { this.endPage = el; }}>
          </div>
      </div>
    );
  }
}

export default SpotHouse;