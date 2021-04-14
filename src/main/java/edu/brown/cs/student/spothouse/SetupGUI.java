package edu.brown.cs.student.spothouse;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import java.util.Map;

public class SetupGUI implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        //int lobbyID = (int) (Math.random() * 100000000);
        int lobbyID = 1;
        QueryParamsMap vars = request.queryMap();

//        Lobby<Song> lobby = new Lobby<>(new Host(vars.value("host"), 0),
//                Integer.parseInt(vars.value("maxCapacity")));
//        Main.getLobbies().put(lobbyID, lobby);

        Lobby<Song> lobby = new Lobby<>(new Host("Richard", 0),
                10);
        Main.getLobbies().put(lobbyID, lobby);
        Map<String, Object> variables = ImmutableMap.<String, Object>builder().put("lobbyID", lobbyID).build();
        return new Gson().toJson(variables);
    }
}