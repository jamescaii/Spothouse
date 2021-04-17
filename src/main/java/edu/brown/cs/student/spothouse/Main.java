package edu.brown.cs.student.spothouse;

import java.io.*;

import freemarker.template.Configuration;
import java.util.*;

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

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  private static final Map<Integer, ArrayList<Song>> songs = new HashMap<>();
  private static final Map<Integer, ArrayList<User>> users = new HashMap<>();
  private static final int DEFAULT_PORT = 4567;
  private static final Gson GSON = new Gson();
  private static final Map<Integer, HashSet<String>> songSetMap = new HashMap<>();
  private static String hostToken = new String();

  /**
   * The initial method called when execution begins.
   *
   * @param args = An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private final String[] args;

  /**
   * Constructor for Main.
   */
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

  static int getHerokuAssignedPort() {
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (processBuilder.environment().get("PORT") != null) {
      return Integer.parseInt(processBuilder.environment().get("PORT"));
    }
    return DEFAULT_PORT;
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
    FreeMarkerEngine freeMarker = createEngine();
    Spark.get("/", new LobbyGUI(), freeMarker);
    Spark.post("/add", new AddHandler());
    Spark.post("/rankings", new RankingHandler());
    Spark.post("/setup", new SetupHandler());
    Spark.post("/join", new JoinHandler());
    Spark.post("/remove", new RemoveHandler());
    Spark.post("/getBackQueue", new GetQueueHandler());
    Spark.post("/users", new UsersHandler());
  }

  /**
   * Display an error page when an exception occurs in the server.
   *
   * @author jj
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

  private static class RemoveHandler implements Route {
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject((request.body()));
      String songUri = data.getString("songUri");
      String roomCode = data.getString("code");
      int code = Integer.parseInt(roomCode);
      songSetMap.get(code).remove(songUri);
      ArrayList<Song> tempList = new ArrayList<>();
      for (Song s: songs.get(code)) {
        if (!s.getUri().equals(songUri)) {
          tempList.add(s);
        }
      }
      songs.put(code, tempList);
      System.out.println("Song removed!");
      System.out.println(songs.get(code));
      Map<String, Object> variables = ImmutableMap.of("songSet", songSetMap.get(code));
      return GSON.toJson(variables);
    }
  }

  private static class GetQueueHandler implements Route {
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject((request.body()));
      String roomCode = data.getString("roomCode");
      int code = Integer.parseInt(roomCode);
      ArrayList<User> tempList = users.get(code);
      Collections.sort(tempList);
      int listLength = tempList.size();
      if (listLength >= 4) {
        int topUsersLength = listLength / 4;
        int start = 0;
        for (User u: tempList) {
          if (start < topUsersLength) {
            u.setOnFire(true);
          } else {
            u.setOnFire(false);
          }
          start++;
        }
      }
      users.put(code, tempList);
      Map<String, Object> variables = ImmutableMap.of("songList", songs.get(code), "userList", users.get(code));
      return GSON.toJson(variables);
    }
  }

  private static class AddHandler implements Route {
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject((request.body()));
      JSONArray songsJSON = data.getJSONArray("songs");
      String roomCode = data.getString("roomCode");
      int code = Integer.parseInt(roomCode);
      String userName = data.getString("user");
      System.out.println("Username is: " + userName);
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
      for (ArrayList<String> x : tempSongList) {
        if (!songSetMap.get(code).contains(x.get(3))) {
          System.out.println("Song added!");
          Song newSong = new Song(x.get(0), x.get(1), x.get(2), x.get(3), userName, 0);
          songs.get(code).add(newSong);
          System.out.println(songs.get(code));
          songSetMap.get(code).add(x.get(3));
        }
      }
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

  private static class UsersHandler implements Route {
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject((request.body()));
      String roomCode = data.getString("roomCode");
      int code = Integer.parseInt(roomCode);
      Map<String, Object> variables = ImmutableMap.of("userList", users.get(code));
      return GSON.toJson(variables);
    }
  }

  private static class RankingHandler implements Route {
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject((request.body()));
      String toChange = data.getString("toChange");
      String roomCode = data.getString("rCode");
      String userName = data.getString("user");
      int numAdd = Integer.parseInt(data.getString("numAdd"));
      int code = Integer.parseInt(roomCode);
      boolean isIncrease = Boolean.parseBoolean(data.getString("isIncrease"));
      Result r = RankingAlgorithm.updateRankings(toChange, userName, numAdd, isIncrease,
              songs.get(code), users.get(code));
      songs.put(code, r.getSongList());
      users.put(code, r.getUserList());
      ArrayList<Song> tempList = songs.get(code);
      Collections.sort(tempList);
      songs.put(code, tempList);
      Map<String, Object> variables = ImmutableMap.of("songList", songs.get(code), "name", toChange, "userList", users.get(code));
      return GSON.toJson(variables);
    }
  }

  private static class SetupHandler implements Route {
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject((request.body()));
      String roomCode = data.getString("roomCode");
      int code = Integer.parseInt(roomCode);
      String hostName = data.getString("hostName");
      hostToken = data.getString("hostToken");
      System.out.println("new room created: " + roomCode + " with host " + hostName);
      User newUser = new User(hostName, true);
      ArrayList<User> tempList = new ArrayList<>();
      tempList.add(newUser);
      users.put(code, tempList);
      ArrayList<Song> queue = new ArrayList<>();
      songs.put(code, queue);
      HashSet<String> songSet = new HashSet<>();
      songSetMap.put(code, songSet);
      Map<String, Object> variables = ImmutableMap.of("songList", "", "name", "", "userList", users.get(code));
      return GSON.toJson(variables);
    }
  }

  private static class JoinHandler implements Route {
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject((request.body()));
      String joinCode = data.getString("query");
      int code = Integer.parseInt(joinCode);
      String guestName = data.getString("guestName");
      User newUser = new User(guestName, false);
      ArrayList<User> tempList = users.get(code);
      tempList.add(newUser);
      users.put(code, tempList);
      System.out.println(users.get(code).get(1).getUsername());
      Map<String, Object> variables = ImmutableMap.of("name", "", "hostToken", hostToken, "backendSongs",
              songs.get(code), "code", code, "userList", users.get(code));
      return GSON.toJson(variables);
    }
  }
}
