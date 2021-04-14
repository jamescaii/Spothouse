package edu.brown.cs.student.spothouse;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LobbyGUI implements TemplateViewRoute, Route {
    @Override
    public ModelAndView handle(Request request, Response response) throws Exception {
        System.out.println(request.params(":LobbyID"));
        int lobbyID = Integer.parseInt(request.params(":LobbyID"));
        if (Main.getLobbies().get(lobbyID) != null) {
//            Main.getLobbies().get(lobbyID).getLobbyWebSocket().;
            Map<String, Object> variables = ImmutableMap.<String, Object>builder().put("lobbyID", lobbyID).build();
            request.session().attribute("lobbyID", lobbyID);
            return new ModelAndView(variables, "index.ftl");
        }
        else {
            response.redirect("/");
            return null;
        }
    }
}
