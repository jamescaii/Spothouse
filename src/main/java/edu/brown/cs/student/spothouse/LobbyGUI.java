package edu.brown.cs.student.spothouse;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateViewRoute;
import java.util.HashMap;
import java.util.Map;

/**
 * Lobby GUI for GET request.
 */
public class LobbyGUI implements TemplateViewRoute, Route {

  /**
   * create a new ModelAndView for the room.
   * @param request request
   * @param response response
   * @return lobby object
   * @throws Exception exception
   */
  @Override
  public ModelAndView handle(Request request, Response response) throws Exception {
    Map<String, String> lobby = new HashMap<>();
    return new ModelAndView(lobby, "index.ftl");
  }
}
