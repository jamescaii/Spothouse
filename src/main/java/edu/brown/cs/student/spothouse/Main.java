package edu.brown.cs.student.spothouse;

import java.io.*;

import freemarker.template.Configuration;
import java.util.*;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
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

  private static final int DEFAULT_PORT = 4567;
  private static final Gson GSON = new Gson();
  private static final Map<Integer, Lobby<Song>> lobbies = new HashMap<>();
  private static List<Song> songs;
  private static Set<String> songSet = new HashSet<>();

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
    songs = new ArrayList<>();
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
    Spark.webSocket("/message", WebSocket.class);
    Spark.post("/queue", new QueueHandler());
    Spark.post("/rankings", new RankingHandler());
    Spark.post("/setup", new SetupGUI());
    Spark.post("/setupLobby", new SetupLobby());
    //Spark.get("/", new LoginGUI());
    Spark.get("/*", new LobbyGUI(), freeMarker);

  }

  public static Map<Integer, Lobby<Song>> getLobbies() {
    return lobbies;
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

  private static class QueueHandler implements Route {
    public Object handle(Request request, Response response) throws Exception {
//      JSONObject data = new JSONObject((request.body()));
//      JSONArray songsJSON = data.getJSONArray("songs");
//      JSONArray requestersJSON = data.getJSONArray("requesters");
//      List<String> songList = new ArrayList<>();
//      List<String> requesterList = new ArrayList<>();
//      Set<String> frontSongSet = new HashSet<>();
//
//      for (int i = 0; i < songsJSON.length(); i++) {
//        songList.add(songsJSON.getString(i));
//        frontSongSet.add(songsJSON.getString(i));
//      }
//      Set<String> removedSongs = new HashSet<>(songSet);
//      removedSongs.removeAll(frontSongSet);
//
//      Set<String> newAddedSongs = new HashSet<>(frontSongSet);
//
//      for (int i = 0; i < newAddedSongs.size(); i++) {
//        if (songSet.contains(newAddedSongs.get(i))) {
//          newAddedSongs.remove();
//          requesterList.removeAtIndex(i);
//        }
//      }
//      newAddedSongs.removeAll(songSet);
//
//
//      Set<String> tempSongSet = new HashSet<>(newAddedSongs);
//      Set<String> intersectionSongs = new HashSet<>(frontSongSet);
//      intersectionSongs.retainAll(songSet);
//      tempSongSet.addAll(intersectionSongs);
//      songSet = tempSongSet;
//      List<Song> nonRemovedList = new ArrayList<>();
//      for (Song song: songs) {
//        if (!removedSongs.contains(song.getName())) {
//          nonRemovedList.add(song);
//        }
//      }
//
//      songs = nonRemovedList;
//      for (String s: newAddedSongs) {
//
//        Song newSong = new Song(s, new Guest("NA", 0));
//        songs.add(newSong);
//      }
//      Map<String, Object> variables = ImmutableMap.of("songList", songs);
//      return GSON.toJson(variables);
      JSONObject data = new JSONObject((request.body()));
      JSONArray songsJSON = data.getJSONArray("songs");
      List<String> songList = new ArrayList<>();
      Set<String> frontSongSet = new HashSet<>();
      for (int i = 0; i < songsJSON.length(); i++) {
        songList.add(songsJSON.getString(i));
        frontSongSet.add(songsJSON.getString(i));
      }
      Set<String> removedSongs = new HashSet<>(songSet);
      removedSongs.removeAll(frontSongSet);
      Set<String> newAddedSongs = new HashSet<>(frontSongSet);
      newAddedSongs.removeAll(songSet);
      Set<String> tempSongSet = new HashSet<>(newAddedSongs);
      Set<String> intersectionSongs = new HashSet<>(frontSongSet);
      intersectionSongs.retainAll(songSet);
      tempSongSet.addAll(intersectionSongs);
      songSet = tempSongSet;
      List<Song> nonRemovedList = new ArrayList<>();
      for (Song song: songs) {
        if (!removedSongs.contains(song.getName())) {
          nonRemovedList.add(song);
        }
      }
      songs = nonRemovedList;
      for (String s: newAddedSongs) {
        Song newSong = new Song(s, new Guest("NA", 0));
        songs.add(newSong);
      }
      Map<String, Object> variables = ImmutableMap.of("songList", songs);
      return GSON.toJson(variables);
    }
  }

  private static class RankingHandler implements Route {
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject((request.body()));
      String toUpdate = data.getString("increased");
      int voterID = data.getInt("voterID");
      Lobby<Song> lobby = lobbies.get(0); //todo: change!!!
      User voter = lobby.getUserByID(voterID);

      System.out.println(toUpdate);
      for (Song s: songs) {
        if (s.getName().equals(toUpdate)) {
          s.addPositiveVote(voter);
        }
      }
      Collections.sort(songs);
      Map<String, Object> variables = ImmutableMap.of("songList", songs, "name", toUpdate);
      return GSON.toJson(variables);
    }
  }
}
