package edu.brown.cs.student.spothouse;

import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LobbyGUI implements TemplateViewRoute, Route {
    @Override
    public ModelAndView handle(Request request, Response response) throws Exception {
        String id = request.params(":lobbyID");
        if (id.isEmpty() || id.equals(" ")) {
            Random rand = new Random();
            id = Integer.toString(rand.nextInt(9999));
        }
        Map<String, String> lobby = new HashMap<>();
        lobby.put("content", id);
        return new ModelAndView(lobby, "index.ftl");
    }
}
