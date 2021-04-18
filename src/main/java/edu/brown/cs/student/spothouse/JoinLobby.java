package edu.brown.cs.student.spothouse;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import spark.*;

import java.util.Map;

public class JoinLobby implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        QueryParamsMap vars = request.queryMap();
//        int lobbyID = Integer.parseInt(vars.value("lobbyID"));
//        boolean success = Main.getLobbies().get(lobbyID) != null;
//            Map<String, Object> variables = ImmutableMap.<String,Object>builder()
//                    .put("lobbyID", success? lobbyID: -1).put("success", success)
//                    .put("newQueue", Main.getLobbies().get(lobbyID).getQueue()).build();
//            return new Gson().toJson(variables);
        int userID = -1;
        System.out.println("is this working: " + vars.value("userID"));
        try {
            userID = Integer.parseInt(vars.value("userID"));
        } catch (NumberFormatException e) {
            System.out.println("It's the parseInt");
            userID = 0;
        }
        System.out.println(userID);
        LobbyWebSocket.userToLobby.put(userID, Integer.parseInt(request.params("lobbyID")));

        System.out.println(userID);
        System.out.println("!");
//        //Session userSession = LobbyWebSocket.userToSessions.get(userID);
//        if (userSession == null) {
//            System.out.println("Session Exists!");
//        } else {
//            System.out.println("anything?");
//        }
        System.out.println("!!");

        int lobbyID = Integer.parseInt(request.params("lobbyID"));

        System.out.println("!!!");

        if (Main.getLobbies().get(lobbyID) != null) {
            System.out.println("!!!!");
            System.out.println("Lobby Exists!");
            LobbyWebSocket.userToLobby.put(userID, lobbyID);
            LobbyWebSocket.lobbyToSessions.get(lobbyID).add(LobbyWebSocket.userToSessions.get(userID));
            boolean userAlreadyInLobby = false;
            for (User u : Main.lobbies.get(lobbyID).getUsers()) {
                if (u.getUserID() == userID) {
                    userAlreadyInLobby = true;
                }
            }
            if (!userAlreadyInLobby) {
                Main.lobbies.get(lobbyID).addUser(new Guest("userName", userID)); //TODO: Get USERNAME!!
            }
            Map<String, Object> variables = ImmutableMap.<String, Object>builder().put("lobbyID", lobbyID)
                    .build();
                    //.put("content", Main.getLobbies().get(lobbyID).getQueue())
            request.session().attribute("lobbyID", lobbyID);
            return new Gson().toJson(variables);
        }
        else {
            System.out.println("ERROR: Lobby does not exist.");
            response.redirect("/");
            return null;
        }
    }
}
