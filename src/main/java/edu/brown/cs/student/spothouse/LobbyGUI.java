package edu.brown.cs.student.spothouse;

import spark.*;

import java.util.HashMap;
import java.util.Map;

public class LobbyGUI implements TemplateViewRoute, Route {
  @Override
  public ModelAndView handle(Request request, Response response) throws Exception {
    Map<String, String> lobby = new HashMap<>();
    return new ModelAndView(lobby, "index.ftl");
  }
}
