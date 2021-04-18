import React, { Component } from "react";
import * as $ from "jquery";
import { authEndpoint, clientId, redirectUri, scopes, toDeploy } from "./Config";
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
      go: false,
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
    }

    // set interval for polling every .5 seconds
    this.interval = setInterval(() => this.tick(), 250);
  }

  componentWillUnmount() {
    // clear the interval to save resources
    clearInterval(this.interval);
  }



  async tick() {
    if (this.state.token) {
      if (this.state.inRoom) {
        this.getCurrentlyPlaying(this.state.hostToken);
        this.retrieveBackendQueue();
        this.getUsersList();
        console.log(this.state.added)
        if (this.state.progress_ms / this.state.item.duration_ms < .2) {
          this.setState({ added: false })
        }
        if (this.state.progress_ms / this.state.item.duration_ms > .98 & !this.state.added) {
          console.log("almost done!")
          this.setState({ added: true })
          if (window.songqueue.length > 0) {
            let songUri = window.songqueue.shift().uri;
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
    let listUser = []
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
          listUser = response.data["userList"]
          this.setState({userList: listUser})
          //console.log(this.state.userList)
        })
        .catch(function (error) {
          console.log(error);
        });
  }


  addBackendQueue = () => {
    let orderedList = []
    const toSend = {
      songs: window.songqueue,
      roomCode: this.state.code,
      user: this.state.userQuery
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

  addToSpotifyQueue = async (token, songUri) => {
    let toAdd = encodeURIComponent(songUri.trim())
    // Make a call using the token
    await $.ajax({
      url: "https://api.spotify.com/v1/me/player/queue?uri=" + toAdd,
      type: "POST",
      beforeSend: xhr => {
        xhr.setRequestHeader("Authorization", "Bearer " + token);
      },
      success: data => {
        this.setState({go: true})
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
        })
        .catch(function (error) {
          console.log(error);
        });
  }

  scrollToBottom = () => {
    this.endPage.scrollIntoView({ behavior: "smooth" });
  }
  componentDidUpdate(prevProps, prevState) {
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
        // Checks if the data is not empty
        // TODO: make this better
        if (data) {
          this.setState({searchResults: []})
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
    let found = false
    for (let i = 0; i < window.songqueue.length; i++) {
      if (window.songqueue[i].uri === clickedURI) {
        found = true
        alert("song already added!")
        break
      }
    }
    if (!found) {
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
    let username = this.state.userQuery.trim()
    if (username === "") {      
      let randomCode = Math.floor(10000 + Math.random() * (99999 - 10000));
      username = "host#" + randomCode
      this.setState({userQuery: username})
    }
    const toSend = {
      roomCode: val,
      hostName: username,
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
          //console.log("USERLIST", response.data["userList"])
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
  timeout(delay) {
    return new Promise( res => setTimeout(res, delay) );
  }
  async skipSong() {
    if (window.songqueue.length > 0 && this.state.isCreated) {
      let songUri = window.songqueue.shift().uri;
      this.addToSpotifyQueue(this.state.token, songUri)
      this.setState({ added: false })
      this.removeFromBackend(songUri)
      await this.timeout(500); // .5s delay
      if (this.state.go) this.skipCurrentlyPlaying(this.state.token)
      this.setState({go: false})
    }
  }

  async skipCurrentlyPlaying(token) {
    // Make a call using the token
    await $.ajax({
      url: "https://api.spotify.com/v1/me/player/next",
      type: "POST",
      beforeSend: xhr => {
        xhr.setRequestHeader("Authorization", "Bearer " + token);
      },
      success: data => {
      }
    });

  }

  joinRoom(numberQuery) {
    let orderedList = []
    let username = this.state.userQuery.trim()
    if (username === "") {      
      let randomCode = Math.floor(10000 + Math.random() * (99999 - 10000));
      username = "guest#" + randomCode
      this.setState({userQuery: username})
    }
    const toSend = {
      query: numberQuery,
      guestName: username
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
          alert("Invalid room code!")
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
              <p style={{fontSize: "small"}}>(leave blank for random name)</p>
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
                <TextBox label="" style={{fontSize: "large"}} force={this.state.numberQuery} onChange={this.changeNumberQuery.bind(this)} />
              <br></br>
            </>
          )
          }
            {this.state.token && this.state.inRoom && this.state.isCreated && (
              <>
                <div className="row">
                  <div className="widercolumn">
                    <h3 className="roomcode" style={{fontSize: "large"}}>Room Code: {this.state.code} </h3>
                    <br></br>
                    <h3 className="username" style={{fontSize: "large"}}>{this.state.userQuery} </h3>
                    <br></br>
                  </div>
                  <div className="widercolumn">
                    <h3 className="userslist" style={{fontSize: "large", textAlign: "right"}}>
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
                <p style={{ fontSize: "small" }}>Click on a song to add it to the queue! (duplicates not allowed)</p>
                <br></br>
                <div className="row">
                  <div className="column">
                    {this.state.token && !this.state.no_top_data && (
                      <>
                        <h4>Your top tracks:</h4>
                        <br></br>
                        <table className="tablebtn" border="1px" table-layour="fixed" bordercolor="black">
                          <tbody>
                          {this.state.topTracks.map(item =>
                            <tr id="tableresult" className="trbtn" key={item.name} onClick={item => this.clickResult(item)}><p className="search">
                            {item.artist} -<span style={{ display: "none" }}>,</span> {item.name}<div style={{ display: "none" }}> -, {item.uri} -, {item.artwork}</div></p>
                              </tr>)}

                          </tbody>
                        </table>
                        <br></br>
                      </>
                    )}
                  </div>
                  <div className="column">
                    <hr width="300" style={{ visibility: "hidden" }} />
                    {this.state.searchResults[0].name && (
                      <>
                        <h4>Search results:</h4>
                        <br></br>
                        <table className="tablebtn" border="1px" table-layour="fixed" bordercolor="black">
                          <tbody>
                            {this.state.searchResults.map(item => <tr id="tableresult" className="trbtn" key={item.name}><p className="search" onClick={item => this.clickResult(item)}>
                              {item.artist} -<span style={{ display: "none" }}>,</span> {item.name}<div style={{ display: "none" }}> -, {item.uri} -, {item.artwork}</div></p></tr>)}

                          </tbody>
                        </table>
                      </>
                    )}
                  </div>
                </div>
              </>
            )
            }    
            
            {this.state.token && this.state.inRoom && !this.state.isCreated && (
              <>
                <div className="row">
                  <div className="widercolumn">
                    <h3 className="roomcode" style={{fontSize: "large"}}>Room Code: {this.state.code} </h3>
                    <br></br>
                    <h3 className="username" style={{fontSize: "large"}}>{this.state.userQuery} </h3>
                    <br></br>
                  </div>
                  <div className="widercolumn">
                    <h3 className="userslist" style={{fontSize: "large", textAlign: "right"}}>
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
                <p style={{ fontSize: "small" }}>Click on a song to add it to the queue! (duplicates not allowed)</p>
                <br></br>
                <div className="row">
                  <div className="column">
                    {this.state.token && !this.state.no_top_data && (
                      <>
                        <h4>Your top tracks:</h4>
                        <br></br>
                        <table className="tablebtn" border="1px" table-layour="fixed" bordercolor="black">
                          <tbody>
                          {this.state.topTracks.map(item =>
                            <tr id="tableresult" className="trbtn" key={item.name} onClick={item => this.clickResult(item)}><p className="search">
                            {item.artist} -<span style={{ display: "none" }}>,</span> {item.name}<div style={{ display: "none" }}> -, {item.uri} -, {item.artwork}</div></p>
                              </tr>)}

                          </tbody>
                        </table>
                        <br></br>
                      </>
                    )}
                  </div>
                  <div className="column">
                    <hr width="300" style={{ visibility: "hidden" }} />
                    {this.state.searchResults[0].name && (
                      <>
                        <h4>Search results:</h4>
                        <br></br>
                        <table className="tablebtn" border="1px" table-layour="fixed" bordercolor="black">
                          <tbody>
                            {this.state.searchResults.map(item => <tr id="tableresult" className="trbtn" key={item.name}><p className="search" onClick={item => this.clickResult(item)}>
                              {item.artist} -<span style={{ display: "none" }}>,</span> {item.name}<div style={{ display: "none" }}> -, {item.uri} -, {item.artwork}</div></p></tr>)}

                          </tbody>
                        </table>
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
              {this.state.isCreated && (
                <>
                <AwesomeButton type="primary" className="btn btn--skip" onPress={() => {
                  this.skipSong()
                }}>≫</AwesomeButton>
                <br></br>
                <br></br>
              </>
              )}
            <Queue
              songQueue={window.songqueue}
              roomCode={this.state.code}
              user={this.state.userQuery}
              isHost={this.state.isCreated}
            />
            <br></br>
          </>
        )}
        {this.state.no_data && this.state.inRoom && (
          <>

            <Queue
              songQueue={window.songqueue}
              roomCode={this.state.code}
              user={this.state.userQuery}
              isHost={this.state.isCreated}
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