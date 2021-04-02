package edu.brown.cs.student.spothouse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
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
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("traffic");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    OptionSet options = null;
    try {
      options = parser.parse(args);
    } catch (Exception e) {
      System.out.println("ERROR: " + e);
    }
    if (options == null) {
      return;
    }

    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }
  }


  private void runSparkServer(int port) {
    Spark.port(port);
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
    Spark.post("/route", new RouteHandler());
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


  /**
   * RouteHandler.
   * Handles requests made for a route.
   */
  private static class RouteHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {

    //   JSONObject data = new JSONObject(request.body());
    //   String sLat = data.getString("srclat");
    //   String sLon = data.getString("srclong");
    //   String dLat = data.getString("destlat");
    //   String dLon = data.getString("destlong");
    //   M.routeFinder(new String[]{"route", sLat, sLon, dLat, dLon});
    //   List<Edge<MapNode>> route = M.getRouteTest();

    //   // construct the string to return
    //   StringBuilder toRet = new StringBuilder();
    //   StringBuilder toRet2 = new StringBuilder();
    //   double distance = 0;

    //   for (Edge<MapNode> e : route) {
    //     toRet.append(e.toString()).append("\n");
    //     // build the map of exact nodes and dests
    //     if (toRet.toString().startsWith("ERROR")
    //         || toRet.toString().startsWith("Sorry")) {
    //       toRet2.setLength(0);
    //       break;
    //     }
    //     // cast to a MapWay for usage
    //     MapWay z = (MapWay) e;
    //     toRet2.append(z.getSourceLat()).append(",").append(z.getSourceLon()).append(",");
    //     toRet2.append(z.getDestLat()).append(",").append(z.getDestLon());
    //     toRet2.append("\n");
    //     // calculate the route distance
    //     distance += z.getWeight();
    //   }

    //   // return the variables
      Map<String, Object> variables;
      variables = ImmutableMap.of("route", "hello");
    //   } else {
    //     variables = ImmutableMap.of("route", M.getErrorMSG(),
    //         "routeMap", toRet2.toString(), "dist", String.valueOf(distance));
    //   }
      return GSON.toJson(variables);
    }
  }


}
