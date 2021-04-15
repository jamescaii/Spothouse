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
      currentQueue: [],
      topTracks: [],
      count: 0,
      added: false,
      code: 0
    };

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
    this.interval = setInterval(() => this.tick(), 250);
  }

  componentWillUnmount() {
    // clear the interval to save resources
    clearInterval(this.interval);
  }

  tick() {
    console.log(this.state.code)
    if (this.state.token) {
      this.getCurrentlyPlaying(this.state.token);
      this.updateBackendQueue();
      if (this.state.progress_ms / this.state.item.duration_ms > .95 & !this.state.added) {
        if (this.state.currentQueue.length > 0)
          this.addToSpotifyQueue(this.state.token);
      }
    }
  }

  updateBackendQueue = () => {
    let current = []
    let orderedList = []
    let newQueue = []
    for (let i = 0; i < this.state.currentQueue.length; i++) {
      let name = this.state.currentQueue[i].name
      current.push(name)
    }
    const toSend = {
      songs: current,
      roomCode: this.state.code
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
        for (let i = 0; i < orderedList.length; i++) {
          let songName = orderedList[i].name
          for (let j = 0; j < this.state.currentQueue.length; j++) {
            if (this.state.currentQueue[j].name === songName) {
              newQueue.push(this.state.currentQueue[j])
            }
          }
        }
        this.setState({ currentQueue: newQueue })
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
        this.setState({ added: true })
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
      this.setState({ added: false })
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
      url: "https://api.spotify.com/v1/me/top/tracks?time_range=short_term&limit=20",
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
        console.log(this.state.topTracks)
      }
    });
  }

  async clickResult(e) {
    let clickedArtist = e.currentTarget.textContent.split(" -, ")[0]
    let clickedName = e.currentTarget.textContent.split(" -, ")[1]
    let clickedURI = e.currentTarget.textContent.split(" -, ")[2]
    let clickedArt = e.currentTarget.textContent.split(" -, ")[3]
    await this.setState({ clickedSongURI: clickedURI })
    var joined = this.state.currentQueue.concat({
      name: clickedName,
      artist: clickedArtist,
      artwork: clickedArt,
      uri: clickedURI,
      upbutton: "<span className=\"voteup\" id = {" + { clickedName } + "} onClick={handleUpvote}> <svg width=\"36\" height=\"36\"> <path d=\"M2 26h32L18 10 2 26z\" fill=\"currentColor\" id={item.name}></path></svg></span>",
      downbutton: "<span className=\"votedown\" id = {" + { clickedName } + "} onClick={handleDownvote}> <svg width=\"36\" height=\"36\"> <path d=\"M2 26h32L18 10 2 26z\" fill=\"currentColor\" id={item.name}></path></svg></span>"
    });
    await this.setState({ currentQueue: joined })

    this.setState({ count: this.state.count + 1 })
  }

  setUpRoom(val) {
    const toSend = {
      roomCode: val
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
          console.log(response)
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
    this.setUpRoom(randomCode)
  }

  joinRoom() {    
    this.setState({isCreated: false})
    this.setState({inRoom: true})
  }

  render() {
    return (
      <div className="App">
        <header className="App-header">
          {!this.state.token && !this.state.inRoom && (
            <a
              className="btn btn--loginApp-link"
              href={`${authEndpoint}?client_id=${clientId}&redirect_uri=${redirectUri}&scope=${scopes.join(
                "%20"
              )}&response_type=token&show_dialog=true`}
            >
              Login to Spotify
            </a>
          )}
          {this.state.token && !this.state.inRoom && (
            <>
              <AwesomeButton type="primary" className="btn btn--search" onPress={() => { this.createRoom() }}>Create Room</AwesomeButton>
              <br></br>
              <AwesomeButton type="primary" className="btn btn--search" onPress={() => { this.joinRoom() }}>Join Room</AwesomeButton>
              <br></br>
            </>
          )
          }
            {this.state.token && this.state.inRoom && this.state.isCreated && (
              <>
                <h4>Code: {this.state.code} </h4>
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
              songQueue={this.state.currentQueue}
              roomCode={this.state.code}
            />
            <br></br>
          </>
        )}
        {this.state.no_data && this.state.inRoom && (
          <>

            <Queue
              songQueue={this.state.currentQueue}
              roomCode={this.state.code}
            />
            <br></br>

          </>
        )}
        <div style={{ float: "left", clear: "both" }}
          ref={(el) => { this.endPage = el; }}>
        </div>
      </div>
    );
  }
}

export default SpotHouse;