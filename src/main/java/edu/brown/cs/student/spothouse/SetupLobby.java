package edu.brown.cs.student.spothouse;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.annotation.concurrent.Immutable;
import java.util.Map;

public class SetupLobby implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        QueryParamsMap vars = request.queryMap();
        int lobbyID = Integer.parseInt(vars.value("lobbyID"));
        boolean success = Main.getLobbies().get(lobbyID) != null;
            Map<String, Object> variables = ImmutableMap.<String,Object>builder()
                    .put("lobbyID", success? lobbyID: -1).put("success", success).build();
            return new Gson().toJson(variables);
    }
}
