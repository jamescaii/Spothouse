package edu.brown.cs.student.spothouse;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SetupLobby implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        //int lobbyID = (int) (Math.random() * 100000000);
        int lobbyID = 1;

        System.out.println("LobbyID in Setup: " + lobbyID);

        //QueryParamsMap vars = request.queryMap();

        if (!Main.getLobbies().containsKey(lobbyID)) {
            Host host = new Host("Richard", 0);
            Lobby<Song> lobby = new Lobby<>(new Host("Richard", 0), lobbyID,
                    100);
            Main.getLobbies().put(lobbyID, lobby);
            LobbyWebSocket.userToLobby.put(0, lobbyID);
            LobbyWebSocket.lobbyToSessions.put(lobbyID, new ConcurrentLinkedQueue<Session>());
        }

        Map<String, Object> variables = ImmutableMap.<String, Object>builder().put("lobbyID", lobbyID).build();
        return new Gson().toJson(variables);
    }
}
