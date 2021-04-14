package edu.brown.cs.student.spothouse;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

@WebSocket
public class LobbyWebSocket<T extends Votable> {
    private static final Gson GSON = new Gson();
    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
    private final int maxUsers;
    private static int nextId = 0;
    private final Lobby<T> lobby;

    public LobbyWebSocket(Lobby<T> lobby, int maxUsers) {
        this.lobby = lobby;
        this.maxUsers = maxUsers;
    }

    private static enum MESSAGE_TYPE {
        CONNECT,
        UPDATE,
        SEND,
    }

    @OnWebSocketConnect
    public void connected(Session session) throws Exception, IOException {
        if (sessions.size() < maxUsers) {
            sessions.add(session);

            // TODO build CONNECT message
            JsonObject json = new JsonObject();
            json.addProperty("type", MESSAGE_TYPE.CONNECT.ordinal());
            json.addProperty("id", nextId);
            if (nextId != 0) {
                lobby.addUser(new Guest("userName", nextId));
            }
            nextId++;
            // make sure to send a unique id!
            // Hint: can use ordinal to get the number position of an enum, MESSAGE_TYPE.CONNECT.ordinal());

            // TODO send message to session
            session.getRemote().sendString(GSON.toJson(json));
            // Hint: session.getRemote().sendString({message})
        }
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        // TODO remove session from sessions
        sessions.remove(session);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        // TODO convert message to JsonObject
        // Hint GSON.fromJson
        JsonObject messageJson = GSON.fromJson(message, JsonObject.class);

        // TODO build UPDATE message
        messageJson.addProperty("type", MESSAGE_TYPE.UPDATE.ordinal());

        // TODO send UPDATE message to each connected client.
        for (Session sesh : sessions) {
            sesh.getRemote().sendString(messageJson.getAsString());
        }
    }
}
