package edu.brown.cs.student.spothouse;

import java.io.*;

import java.util.*;
import java.util.List;

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

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;
  private static final Gson GSON = new Gson();
  private static List<Song2> songs;
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

    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }
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

  private void runSparkServer(int port) {
    Spark.port(getHerokuAssignedPort());
    Spark.externalStaticFileLocation("src/main/resources/static");

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
    Spark.post("/queue", new QueueHandler());
    Spark.post("/rankings", new RankingHandler());
    Spark.get("/:lobbyID", new LobbyGUI());
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
      List<Song2> nonRemovedList = new ArrayList<>();
      for (Song2 song: songs) {
        if (!removedSongs.contains(song.getName())) {
          nonRemovedList.add(song);
        }
      }
      songs = nonRemovedList;
      for (String s: newAddedSongs) {
        Song2 newSong = new Song2(s, "NA", 0);
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
      System.out.println(toUpdate);
      for (Song2 s: songs) {
        if (s.getName().equals(toUpdate)) {
          s.addVote(1);
        }
      }
      Collections.sort(songs);
      Map<String, Object> variables = ImmutableMap.of("songList", songs, "name", toUpdate);
      return GSON.toJson(variables);
    }
  }
}
