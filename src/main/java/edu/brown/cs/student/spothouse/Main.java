package edu.brown.cs.student.spothouse;

import freemarker.template.Configuration;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.json.JSONArray;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import org.json.JSONObject;
import spark.template.freemarker.FreeMarkerEngine;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  private static final Map<Integer, ArrayList<Song>> songs = new HashMap<>();
  private static final Map<Integer, ArrayList<User>> users = new HashMap<>();
  private static final Map<Integer, HashSet<String>> songSetMap = new HashMap<>();
  private static final int DEFAULT_PORT = 4567;
  private static final Gson GSON = new Gson();
  private static String hostToken = "";

  /**
   * The initial method called when execution begins.
   *
   * @param args
   *          An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private final String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
            .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);

    runSparkServer((int) options.valueOf("port"));
    InputStreamReader stream = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(stream);
    String input;
    try {
      while (true) {
        input = reader.readLine();
        if (input == null) {
          break;
        }
      }
    } catch (IOException e) {
      System.out.println("FAILED");
    }
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable to use %s for template loading.%n", templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    Spark.port(getHerokuAssignedPort());
    Spark.externalStaticFileLocation("src/main/resources");

    Spark.options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");

      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });

    Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
    Spark.exception(Exception.class, new ExceptionPrinter());

    // call all handlers for requests
    FreeMarkerEngine freeMarker = createEngine();
    Spark.get("/", new LobbyGUI(), freeMarker);
    Spark.post("/setup", new SetupHandler());
    Spark.post("/join", new JoinHandler());
    Spark.post("/users", new UsersHandler());
    Spark.post("/add", new AddHandler());
    Spark.post("/remove", new RemoveHandler());
    Spark.post("/rankings", new RankingHandler());
    Spark.post("/getBackQueue", new GetQueueHandler());
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * get the specific port number that server and client can communicate with.
   * @return port number
   */
  static int getHerokuAssignedPort() {
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (processBuilder.environment().get("PORT") != null) {
      return Integer.parseInt(processBuilder.environment().get("PORT"));
    }
    return DEFAULT_PORT;
  }
  
  /**
   * initial setup handler.
   */
  private static class SetupHandler implements Route {

    /**
     * sets up the initial variables for the room.
     * @param request request for initial room and host info.
     * @param response response
     * @return JSON object of the setup data strcutures.
     * @throws Exception JSON Exception.
     */
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject((request.body()));
      String roomCode = data.getString("roomCode");
      int code = Integer.parseInt(roomCode);
      String hostName = data.getString("hostName");
      hostToken = data.getString("hostToken");
      System.out.println("new room created: " + roomCode + " with host " + hostName);
      // create new user as host
      User newUser = new User(hostName, true);
      ArrayList<User> tempList = new ArrayList<>();
      tempList.add(newUser);
      users.put(code, tempList);
      // new songs list
      ArrayList<Song> queue = new ArrayList<>();
      songs.put(code, queue);
      // map of the set song
      HashSet<String> songSet = new HashSet<>();
      songSetMap.put(code, songSet);
      Map<String, Object> variables = ImmutableMap.of("songList", "", "name", "", "userList", users.get(code));
      return GSON.toJson(variables);
    }
  }

  /**
   * handler to set up a new user that joins a room
   */
  private static class JoinHandler implements Route {

    /**
     * sets up a new user for the room.
     * @param request request for guest info.
     * @param response response
     * @return JSON object of the new user.
     * @throws Exception JSON exceptions.
     */
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject((request.body()));
      String joinCode = data.getString("query");
      int code = Integer.parseInt(joinCode);
      String guestName = data.getString("guestName");
      User newUser = new User(guestName, false);
      // update user list
      ArrayList<User> tempList = users.get(code);
      tempList.add(newUser);
      users.put(code, tempList);
      System.out.println(users.get(code).get(1).getUsername());
      Map<String, Object> variables = ImmutableMap.of("name", "", "hostToken", hostToken, "backendSongs",
          songs.get(code), "code", code, "userList", users.get(code));
      return GSON.toJson(variables);
    }
  }

  /**
   * Handler to get user infos
   */
  private static class UsersHandler implements Route {

    /**
     * get the users of the room.
     * @param request JSON object that contains users info.
     * @param response response.
     * @return JSON object of users.
     * @throws Exception JSON exceptions
     */
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject((request.body()));
      String roomCode = data.getString("roomCode");
      int code = Integer.parseInt(roomCode);
      Map<String, Object> variables = ImmutableMap.of("userList", users.get(code));
      return GSON.toJson(variables);
    }
  }

  /**
   * Handler to add a song.
   */
  private static class AddHandler implements Route {

    /**
     * adds new songs to the queue.
     * @param request JSON object that contains song info.
     * @param response response.
     * @return JSON object of the updated songs.
     * @throws Exception JSON exceptions
     */
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject((request.body()));
      JSONArray songsJSON = data.getJSONArray("songs");
      String roomCode = data.getString("roomCode");
      int code = Integer.parseInt(roomCode);
      String userName = data.getString("user");
      System.out.println("Username is: " + userName);
      // temporary song list with the newly added songs
      ArrayList<ArrayList<String>> tempSongList = new ArrayList<>();
      for (int i = 0; i < songsJSON.length(); i++) {
        ArrayList<String> temp = new ArrayList<>();
        JSONObject jsonobject = songsJSON.getJSONObject(i);
        String name = (String) jsonobject.get("name");
        String artist = (String) jsonobject.get("artist");
        String artwork = (String) jsonobject.get("artwork");
        String uri = (String) jsonobject.get("uri");
        temp.add(name);
        temp.add(artist);
        temp.add(artwork);
        temp.add(uri);
        tempSongList.add(temp);
      }
      // update the map with new songs
      for (ArrayList<String> x : tempSongList) {
        if (!songSetMap.get(code).contains(x.get(3))) {
          System.out.println("Song added!");
          Song newSong = new Song(x.get(0), x.get(1), x.get(2), x.get(3), userName, 0);
          songs.get(code).add(newSong);
          System.out.println(songs.get(code));
          songSetMap.get(code).add(x.get(3));
        }
      }
      // update the map with any repeated songs.
      Set<String> repeated = new HashSet<>();
      ArrayList<Song> noRepeats = new ArrayList<>();
      for (Song element: songs.get(code)) {
        if (!repeated.contains(element.getName())) {
          noRepeats.add(element);
          repeated.add(element.getName());
        }
      }
      songs.put(code, noRepeats);
      Map<String, Object> variables = ImmutableMap.of("songList", songs.get(code));
      return GSON.toJson(variables);
    }
  }

  /**
   * Handler for removing a song request.
   */
  private static class RemoveHandler implements Route {

    /**
     * removes a song that is about to play or deleted on request.
     * @param request JSON object that contains song/room info.
     * @param response response.
     * @return JSON object of the set song.
     * @throws Exception JSON exceptions
     */
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject((request.body()));
      String songUri = data.getString("songUri");
      String roomCode = data.getString("code");
      int code = Integer.parseInt(roomCode);
      // update a set song
      songSetMap.get(code).remove(songUri);
      ArrayList<Song> tempList = new ArrayList<>();
      for (Song s: songs.get(code)) {
        if (!s.getUri().equals(songUri)) {
          tempList.add(s);
        }
      }
      // update the list.
      songs.put(code, tempList);
      System.out.println("Song removed!");
      System.out.println(songs.get(code));
      Map<String, Object> variables = ImmutableMap.of("songSet", songSetMap.get(code));
      return GSON.toJson(variables);
    }
  }

  /**
   * Handler to update the song rankings.
   */
  private static class RankingHandler implements Route {

    /**
     * gets the current songs and update the rankings
     * @param request request for the current queue info.
     * @param response response.
     * @return JSON object of users.
     * @throws Exception JSON exceptions
     */
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject((request.body()));
      String toChange = data.getString("toChange");
      String roomCode = data.getString("rCode");
      String userName = data.getString("user");
      int numAdd = Integer.parseInt(data.getString("numAdd"));
      int code = Integer.parseInt(roomCode);
      boolean isIncrease = Boolean.parseBoolean(data.getString("isIncrease"));
      // run the algorithm
      Result r = RankingAlgorithm.updateRankings(toChange, userName, numAdd, isIncrease,
          songs.get(code), users.get(code));
      // update the lists
      songs.put(code, r.getSongList());
      users.put(code, r.getUserList());
      ArrayList<Song> tempList = songs.get(code);
      Collections.sort(tempList);
      songs.put(code, tempList);
      Map<String, Object> variables = ImmutableMap.of("songList", songs.get(code), "name", toChange, "userList", users.get(code));
      return GSON.toJson(variables);
    }
  }

  /**
   * Handler to get the current queue on the front end
   */
  private static class GetQueueHandler implements Route {

    /**
     * receives the current queue info and updates the user info.
     * @param request JSON object that contains queue info.
     * @param response response.
     * @return JSON object of the updated lists.
     * @throws Exception JSON exceptions
     */
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject((request.body()));
      String roomCode = data.getString("roomCode");
      int code = Integer.parseInt(roomCode);
      ArrayList<User> tempList = users.get(code);
      Collections.sort(tempList);
      int listLength = tempList.size();
      // update onFire status.
      if (listLength >= 4) {
        tempList = RankingAlgorithm.addOnFire(tempList, listLength);
      }
      users.put(code, tempList);
      Map<String, Object> variables = ImmutableMap.of("songList", songs.get(code), "userList", users.get(code));
      return GSON.toJson(variables);
    }
  }

}
