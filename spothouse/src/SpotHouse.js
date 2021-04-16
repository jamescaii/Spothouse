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
      hostToken: null,
      inRoom: false,
      isCreated: false,
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
      topTracks: [],
      count: 0,
      added: false,
      code: 0,
      numberQuery: "",
      userQuery: "",
      userList: [],
      usernameSet: false,
    };

    this.getCurrentlyPlaying = this.getCurrentlyPlaying.bind(this);
    this.tick = this.tick.bind(this);
  }

  changeQuery(param) {
    this.setState({ searchQuery: param })
  }

  changeNumberQuery(param) {
    this.setState({ numberQuery: param })
  }

  changeUserQuery(param) {
    this.setState({ userQuery: param })
  }

  componentDidMount() {
    window.songqueue = []
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
    this.interval = setInterval(() => this.tick(), 250);
  }

  componentWillUnmount() {
    // clear the interval to save resources
    clearInterval(this.interval);
  }



  tick() {
    if (this.state.token) {
      this.getCurrentlyPlaying(this.state.hostToken);
      if (this.state.inRoom) {
        console.log(window.songqueue)
        this.retrieveBackendQueue();
        this.getUsersList();
        if (this.state.progress_ms / this.state.item.duration_ms > .95 & !this.state.added) {
          this.setState({ added: true })
          console.log("almost done")
          if (window.songqueue.length > 0) {
            let songUri = window.songqueue.shift().uri;
            console.log(window.songqueue)
            this.removeFromBackend(songUri)    
            this.retrieveBackendQueue();
            if (this.state.isCreated) {
              this.addToSpotifyQueue(this.state.token, songUri);   
            }
  
          }
        }
      }
    }
  }
  
  retrieveBackendQueue = () => {
    const toSend = {
      roomCode: this.state.code
    }
    let config = {
      headers: {
        "Content-Type": "application/json",
        'Access-Control-Allow-Origin': '*',
      }
    }
    axios.post(
        "http://localhost:4567/getBackQueue",
        toSend,
        config
    )
        .then(response => {
          window.songqueue = response.data["songList"]
        })
        .catch(function (error) {
          console.log(error);
        });
  }


  addBackendQueue = () => {
    let orderedList = []
    console.log(window.songqueue)
    const toSend = {
      songs: window.songqueue,
      roomCode: this.state.code
    }
    let config = {
      headers: {
        "Content-Type": "application/json",
        'Access-Control-Allow-Origin': '*',
      }
    }
    axios.post(
        "http://localhost:4567/add",
        toSend,
        config
    )
        .then(response => {
          orderedList = response.data["songList"]
          window.songqueue = orderedList
        })
        .catch(function (error) {
          console.log(error);
        });
  }

  addToSpotifyQueue = (token, songUri) => {
    let toAdd = encodeURIComponent(songUri.trim())
    // Make a call using the token
    $.ajax({
      url: "https://api.spotify.com/v1/me/player/queue?uri=" + toAdd,
      type: "POST",
      beforeSend: xhr => {
        xhr.setRequestHeader("Authorization", "Bearer " + token);
      },
      success: data => {
        console.log("Song URI", songUri)
      }
    });
  }

  removeFromBackend = (token) => {
    const toSend = {
      songUri: token,
      code: this.state.code
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
          console.log(response)
        })
        .catch(function (error) {
          console.log(error);
        });
  }

  scrollToBottom = () => {
    this.endPage.scrollIntoView({ behavior: "smooth" });
  }
  componentDidUpdate(prevProps, prevState) {
    if (prevState.count !== this.state.count)
      this.scrollToBottom();
    if (prevState.item.name !== this.state.item.name)
      this.setState({ added: false })
  }
  getSearch(token, searchQuery) {
    // parse searchQuery
    let searchQueryParameter = encodeURIComponent(searchQuery.trim())
    // Make a call using the token
    $.ajax({
      url: "https://api.spotify.com/v1/search?q=" + searchQueryParameter + "&type=track&limit=10",
      type: "GET",
      beforeSend: xhr => {
        xhr.setRequestHeader("Authorization", "Bearer " + token);
      },
      success: data => {
        //console.log(data)
        // Checks if the data is not empty
        if (data) {
          console.log(data)
          this.setState({
            searchResults: data.tracks.items.map((item) => ({
              name: item.name,
              artist: item.artists[0].name,
              uri: item.uri,
              artwork: item.album.images[0].url
            }
            )
            )
          });
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
        if (!data) {
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
      url: "https://api.spotify.com/v1/me/top/tracks?time_range=short_term&limit=10",
      type: "GET",
      beforeSend: xhr => {
        xhr.setRequestHeader("Authorization", "Bearer " + token);
      },
      success: data => {
        // Checks if the data is not empty
        if (!data) {
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
      }
    });
  }

  clickResult(e) {
    let clickedArtist = e.currentTarget.textContent.split(" -, ")[0]
    let clickedName = e.currentTarget.textContent.split(" -, ")[1]
    let clickedURI = e.currentTarget.textContent.split(" -, ")[2]
    let clickedArt = e.currentTarget.textContent.split(" -, ")[3]
    this.setState({ clickedSongURI: clickedURI })
    var joined = window.songqueue.concat({
      name: clickedName,
      artist: clickedArtist,
      artwork: clickedArt,
      uri: clickedURI,
    });
    if (joined) {
      window.songqueue = joined
      this.addBackendQueue()
    }

    this.setState({ count: this.state.count + 1 })
    console.log("added!")
  }

  getUsersList = () => {
    let tempUsers = []
    const toSend = {
      roomCode: this.state.code
    }
    let config = {
      headers: {
        "Content-Type": "application/json",
        'Access-Control-Allow-Origin': '*',
      }
    }
    axios.post(
        "http://localhost:4567/users",
        toSend,
        config
    )
        .then(response => {
          tempUsers = response.data["userList"]
          this.setState({userList: tempUsers})
        })
        .catch(function (error) {
          console.log(error);
        });
  }

  setUpRoom(val) {
    console.log(this.state.userQuery)
    const toSend = {
      roomCode: val,
      hostName: this.state.userQuery,
      hostToken: this.state.token
    }
    let config = {
      headers: {
        "Content-Type": "application/json",
        'Access-Control-Allow-Origin': '*',
      }
    }
    axios.post(
        "http://localhost:4567/setup",
        toSend,
        config
    )
        .then(response => {
          console.log("USERLIST", response.data["userList"])
        })
        .catch(function (error) {
          console.log(error);
        });
  }

  createRoom() {
    let randomCode = Math.floor(10000 + Math.random() * (99999 - 10000));
    this.setState({code: randomCode})
    this.setState({isCreated: true})
    this.setState({inRoom: true})
    this.setState({hostToken: this.state.token})
    this.setUpRoom(randomCode)
  }

  joinRoom(numberQuery) {
    let orderedList = []
    let newQueue = []
    const toSend = {
      query: numberQuery,
      guestName: this.state.userQuery
    }
    let config = {
      headers: {
        "Content-Type": "application/json",
        'Access-Control-Allow-Origin': '*',
      }
    }
    axios.post(
        "http://localhost:4567/join",
        toSend,
        config
    )
        .then(response => {
          console.log("USERLIST", response.data["userList"])
          orderedList = response.data["backendSongs"]
          this.setState({hostToken: response.data["hostToken"]})
          let cVal = response.data["code"]
          this.setState({code: cVal})
          window.songqueue = orderedList
          this.setState({isCreated: false})
          this.setState({inRoom: true})
        })
        .catch(function (error) {
          console.log(error);
        });
  }

  render() {
    return (
      <>
      <div className="App">
        <header className="App-header">
          {!this.state.token && !this.state.inRoom && !this.state.usernameSet && (
            <a
              className="btn btn--loginApp-link"
              href={`${authEndpoint}?client_id=${clientId}&redirect_uri=${redirectUri}&scope=${scopes.join(
                "%20"
              )}&response_type=token&show_dialog=true`}
            >
              Login to Spotify
            </a>
          )}
          
          {this.state.token && !this.state.inRoom && !this.state.usernameSet && (
            <>
                
              <TextBox label="Enter Username:" force={this.state.userQuery} onChange={this.changeUserQuery.bind(this)} />
              <hr style={{ height: 10, visibility: "hidden" }} />
              <AwesomeButton type="primary" className="btn btn--search" onPress={() => {
                this.setState({usernameSet: true})
              }}>Submit Username</AwesomeButton>
              <br></br>
          </>
          )}
          {this.state.token && !this.state.inRoom && this.state.usernameSet && (
            <>

              <AwesomeButton type="primary" className="btn btn--search" onPress={() => { this.createRoom() }}>Create Room</AwesomeButton>
              <br></br>
                <AwesomeButton type="primary" className="btn btn--search" onPress={() => {
                  this.joinRoom(this.state.numberQuery)
                }}>Join Room</AwesomeButton>
                <hr style={{ height: 10, visibility: "hidden" }} />
                <TextBox label="Enter Room Number:" style={{fontSize: "large"}} force={this.state.numberQuery} onChange={this.changeNumberQuery.bind(this)} />
              <br></br>
            </>
          )
          }
            {this.state.token && this.state.inRoom && this.state.isCreated && (
              <>
                <div class="row">
                  <div class="widercolumn">
                    <h3 className="roomcode" style={{fontSize: "large"}}>Room Code: {this.state.code} </h3><h3 className="username" style={{fontSize: "large"}}>Username: {this.state.userQuery} </h3>
                    <br></br>
                  </div>
                  <div class="widercolumn">
                    <h3 className="userslist" style={{fontSize: "large"}}>
                    Users List:</h3>
                    {this.state.userList.map(item => <p className="userslist" style={{fontSize: "large"}}>{item.username}</p>)}
                  </div>
                </div>
                <br></br>
                <TextBox label="Search for a song:" force={this.state.searchQuery} onChange={this.changeQuery.bind(this)} />
                <hr style={{ height: 10, visibility: "hidden" }} />
                <AwesomeButton type="primary" className="btn btn--search" onPress={() => {
                  this.getSearch(this.state.token, this.state.searchQuery)
                }}>Submit</AwesomeButton>
                <br></br>
                <p style={{ fontSize: "small" }}>Click on a song to add it to the queue!</p>
                <br></br>
                <div class="row">
                  <div class="column">
                    {this.state.token && !this.state.no_top_data && (
                      <>
                        <h4>Your top tracks:</h4>
                        <br></br>
                        {this.state.topTracks.map(item => <p className="search" onClick={item => this.clickResult(item)}>
                          {item.artist} -<span style={{ display: "none" }}>,</span> {item.name}<div style={{ display: "none" }}> -, {item.uri} -, {item.artwork}</div></p>)}
                        <br></br>
                      </>
                    )}
                  </div>
                  <div class="column">
                    <hr width="300" style={{ visibility: "hidden" }} />
                    {this.state.searchResults[0].name && (
                      <>
                        <h4>Search results:</h4>
                        <br></br>
                        {this.state.searchResults.map(item => <p className="search" onClick={item => this.clickResult(item)}>
                          {item.artist} -<span style={{ display: "none" }}>,</span> {item.name}<div style={{ display: "none" }}> -, {item.uri} -, {item.artwork}</div></p>)}
                      </>
                    )}
                  </div>
                </div>
              </>
            )
            }    
            
            {this.state.token && this.state.inRoom && !this.state.isCreated && (
              <>
              <div class="row">
                <div class="widercolumn">
                  <h3 className="roomcode" style={{fontSize: "large"}}>Room Code: {this.state.code} </h3><h3 className="username" style={{fontSize: "large"}}>Username: {this.state.userQuery} </h3>
                  <br></br>
                </div>
                <div class="widercolumn">
                  <h3 className="userslist" style={{fontSize: "large"}}>
                  Users List:</h3>
                  {this.state.userList.map(item => <p className="userslist" style={{fontSize: "large"}}>{item.username}</p>)}
                </div>
              </div>
                <br></br>
                <TextBox label="Search for a song:" force={this.state.searchQuery} onChange={this.changeQuery.bind(this)} />
                <hr style={{ height: 10, visibility: "hidden" }} />
                <AwesomeButton type="primary" className="btn btn--search" onPress={() => {
                  this.getSearch(this.state.token, this.state.searchQuery)
                }}>Submit</AwesomeButton>
                <br></br>
                <p style={{ fontSize: "small" }}>Click on a song to add it to the queue!</p>
                <br></br>
                <div class="row">
                  <div class="column">
                    {this.state.token && !this.state.no_top_data && (
                      <>
                        <h4>Your top tracks:</h4>
                        <br></br>
                        {this.state.topTracks.map(item => <p className="search" onClick={item => this.clickResult(item)}>
                          {item.artist} -<span style={{ display: "none" }}>,</span> {item.name}<div style={{ display: "none" }}> -, {item.uri} -, {item.artwork}</div></p>)}
                        <br></br>
                      </>
                    )}
                  </div>
                  <div class="column">
                    <hr width="300" style={{ visibility: "hidden" }} />
                    {this.state.searchResults[0].name && (
                      <>
                        <h4>Search results:</h4>
                        <br></br>
                        {this.state.searchResults.map(item => <p className="search" onClick={item => this.clickResult(item)}>
                          {item.artist} -<span style={{ display: "none" }}>,</span> {item.name}<div style={{ display: "none" }}> -, {item.uri} -, {item.artwork}</div></p>)}
                      </>
                    )}

                  </div>
                </div>
              </>
            )
            }    
        </header>
        <br></br>
        {this.state.token && !this.state.no_data && this.state.item && this.state.inRoom && (
          <>
            <Player
              item={this.state.item}
              is_playing={this.state.is_playing}
              progress_ms={this.state.progress_ms}
            />

            <Queue
              songQueue={window.songqueue}
              roomCode={this.state.code}
            />
            <br></br>
          </>
        )}
        {this.state.no_data && this.state.inRoom && (
          <>

            <Queue
              songQueue={window.songqueue}
              roomCode={this.state.code}
            />
            <br></br>

          </>
        )}
        <div style={{ float: "left", clear: "both" }}
          ref={(el) => { this.endPage = el; }}>
        </div>
      </div>
      </>
    );
  }
}

export default SpotHouse;