package edu.brown.cs.student.spothouse;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONObject;


@WebSocket
public class LobbyWebSocket {
    private static final Gson GSON = new Gson();
    public static final Map<Integer, org.eclipse.jetty.websocket.api.Session> userToSessions = new HashMap<>();
    public static final Map<org.eclipse.jetty.websocket.api.Session, Integer> sessionsToUser = new HashMap<>();
    public static final Map<Integer, Integer> userToLobby = new HashMap<>();
    public static Map<Integer, ConcurrentLinkedQueue<Session>>
            lobbyToSessions = new HashMap<>();

    private static int nextId = 0;

    private static enum MESSAGE_TYPE {
        CONNECT,
        UPDATE,
        SEND,
    }

    @OnWebSocketConnect
    public void connected(Session session) throws Exception, IOException {
            // TODO build CONNECT message
            JsonObject json = new JsonObject();
            json.addProperty("type", MESSAGE_TYPE.CONNECT.ordinal());
            json.addProperty("id", nextId);
            userToSessions.put(nextId, session);
            sessionsToUser.put(session, nextId);
//            if (nextId != 0) {
//                lobby.addUser(new Guest("userName", nextId));
//            }
            nextId++;

            json.addProperty("queue", Main.lobbies.get(1).getQueueAsJson());

            // Hint: can use ordinal to get the number position of an enum, MESSAGE_TYPE.CONNECT.ordinal());

            // TODO send message to session
            session.getRemote().sendString(GSON.toJson(json));
            // Hint: session.getRemote().sendString({message})
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        // TODO remove session from sessions
        System.out.println("Closed: " + reason);
        lobbyToSessions.get(userToLobby.get(sessionsToUser.get(session))).remove(session);
        userToLobby.remove(sessionsToUser.get(session));
        userToSessions.remove(sessionsToUser.get(session));
        sessionsToUser.remove(session);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        // TODO convert message to JsonObject
        // Hint GSON.fromJson
        System.out.println(message);
        JsonObject messageJson = GSON.fromJson(message, JsonObject.class);

        String messageType = messageJson.getAsJsonPrimitive("MessageType").getAsString();
        int userID = messageJson.getAsJsonPrimitive("userID").getAsInt();
        int lobbyID = messageJson.getAsJsonPrimitive("lobbyID").getAsInt();

        String returnQueue;

        System.out.println(messageType);

        switch (messageType) {
            case "request": returnQueue = this.request(userID, lobbyID, messageJson);
                break;
            case "remove": returnQueue = this.removeSong(userID, lobbyID, messageJson);
                break;
            case "upvote": returnQueue = this.upVote(userID, lobbyID, messageJson);
                break;
            case "downvote": returnQueue = this.downVote(userID, lobbyID, messageJson);
                break;
            case "removeUpvote": returnQueue = this.removeUpVote(userID, lobbyID, messageJson);
                break;
            case "removeDownvote": returnQueue = this.removeDownVote(userID, lobbyID, messageJson);
                break;
            default: returnQueue = this.defaultQueue(lobbyID);
        }

        // TODO build UPDATE message
        JsonObject updateJson = new JsonObject();
        updateJson.addProperty("type", MESSAGE_TYPE.UPDATE.ordinal());
        updateJson.addProperty("queue", returnQueue);

        // TODO send UPDATE message to each connected client.
        userToLobby.put(userID, 1);
        lobbyToSessions.get(lobbyID).add(session);
        ConcurrentLinkedQueue<Session> sessions = lobbyToSessions.get(lobbyID);
        //Set<Integer> sessionUserSet = new HashSet<>();

        for (Session sesh : sessions) {
            //sessionUserSet.add(sessionsToUser.get(sesh));
            sesh.getRemote().sendString(updateJson.toString());
        }
//        for (int seshUser : sessionUserSet) {
//            userToSessions.get(seshUser).getRemote().sendString(updateJson.toString());
//        }
    }

    public String request(int userID, int lobbyID, JsonObject requestJson) {
        Main.lobbies.get(lobbyID).addVotable(new Song(
                requestJson.getAsJsonPrimitive("name").toString(),
                requestJson.getAsJsonPrimitive("artist").toString(),
                requestJson.getAsJsonPrimitive("art").toString(),
                requestJson.getAsJsonPrimitive("uri").toString(),
                Main.lobbies.get(lobbyID).getUserByID(userID)));
        Main.lobbies.get(lobbyID).updateUserScores();
        return Main.lobbies.get(lobbyID).getQueueAsJson();
    }

    public String removeSong(int userID, int lobbyID, JsonObject requestJson) {
        Main.lobbies.get(lobbyID).removeVotable(requestJson.getAsJsonPrimitive("name").toString(),
                requestJson.getAsJsonPrimitive("artist").toString(),
                requestJson.getAsJsonPrimitive("art").toString(),
                requestJson.getAsJsonPrimitive("uri").toString());
        Main.lobbies.get(lobbyID).updateUserScores();
        return Main.lobbies.get(lobbyID).getQueueAsJson();
    }

    public String upVote(int userID, int lobbyID, JsonObject requestJson) {
        Main.lobbies.get(lobbyID).getVotable(requestJson.getAsJsonPrimitive("name").toString(),
                requestJson.getAsJsonPrimitive("artist").toString(),
                requestJson.getAsJsonPrimitive("art").toString(),
                requestJson.getAsJsonPrimitive("uri").toString())
                .addPositiveVote(Main.lobbies.get(lobbyID).getUserByID(userID));
        Main.lobbies.get(lobbyID).updateUserScores();
        return Main.lobbies.get(lobbyID).getQueueAsJson();
    }

    public String downVote(int userID, int lobbyID, JsonObject requestJson) {
        Main.lobbies.get(lobbyID).getVotable(requestJson.getAsJsonPrimitive("name").toString(),
                requestJson.getAsJsonPrimitive("artist").toString(),
                requestJson.getAsJsonPrimitive("art").toString(),
                requestJson.getAsJsonPrimitive("uri").toString())
                .addNegativeVote(Main.lobbies.get(lobbyID).getUserByID(userID));
        Main.lobbies.get(lobbyID).updateUserScores();
        return Main.lobbies.get(lobbyID).getQueueAsJson();
    }

    public String removeUpVote(int userID, int lobbyID, JsonObject requestJson) {
        Main.lobbies.get(lobbyID).getVotable(requestJson.getAsJsonPrimitive("name").toString(),
                requestJson.getAsJsonPrimitive("artist").toString(),
                requestJson.getAsJsonPrimitive("art").toString(),
                requestJson.getAsJsonPrimitive("uri").toString())
                .removePositiveVote(Main.lobbies.get(lobbyID).getUserByID(userID));
        Main.lobbies.get(lobbyID).updateUserScores();
        return Main.lobbies.get(lobbyID).getQueueAsJson();
    }

    public String removeDownVote(int userID, int lobbyID, JsonObject requestJson) {
        Main.lobbies.get(lobbyID).getVotable(requestJson.getAsJsonPrimitive("name").toString(),
                requestJson.getAsJsonPrimitive("artist").toString(),
                requestJson.getAsJsonPrimitive("art").toString(),
                requestJson.getAsJsonPrimitive("uri").toString())
                .removeNegativeVote(Main.lobbies.get(lobbyID).getUserByID(userID));
        Main.lobbies.get(lobbyID).updateUserScores();
        return Main.lobbies.get(lobbyID).getQueueAsJson();
    }

    public String defaultQueue(int lobbyID) {
        Main.lobbies.get(lobbyID).updateUserScores();
        return Main.lobbies.get(lobbyID).getQueueAsJson();
    }
//    @OnWebSocketError
//    public void error(Session session, Throwable error) {
//        System.out.println(error.toString());
//    }
}
