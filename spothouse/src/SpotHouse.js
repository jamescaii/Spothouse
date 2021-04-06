import React from 'react'
import { SpotifyApiContext } from 'react-spotify-api'
import Cookies from 'js-cookie'
import { SpotifyAuth, Scopes } from 'react-spotify-auth'
import 'react-spotify-auth/dist/index.css' // if using the included styles

/**
 * A functional component to create the routing features
 * @return the Route element
 */
 function SpotHouse() {
  const token = Cookies.get('spotifyAuthToken')
    // return the route
    return (
      <div className="SpotHouse">
        <h1>hello</h1>        
        {token ? (
          <SpotifyApiContext.Provider value={token}>
            {/* Your Spotify Code here */}
            <p>You are authorized with token: {token}</p>
          </SpotifyApiContext.Provider>
        ) : (
          // Display the login page
          <SpotifyAuth
            redirectUri='http://localhost:3000/callback'
            clientID='41b5d9c762d04b6182549771690cf81c'
            scopes={[Scopes.userReadPrivate, 'user-read-email']} // either style will work
          />
        )}
      </div>
    );

}

export default SpotHouse;