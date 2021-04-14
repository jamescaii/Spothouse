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
        int lobbyID = Integer.parseInt(request.splat()[0]);
        if (Main.getLobbies().get(lobbyID) != null) {
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
