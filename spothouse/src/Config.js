export const authEndpoint = "https://accounts.spotify.com/authorize";

export const clientId = "41b5d9c762d04b6182549771690cf81c";
//export const redirectUri = "https://spothouse-app.herokuapp.com/";
export const redirectUri = "http://localhost:3000/callback";
export const scopes = [
    "user-top-read",
    "user-read-currently-playing",
    "user-read-playback-state",
    "user-modify-playback-state",
];