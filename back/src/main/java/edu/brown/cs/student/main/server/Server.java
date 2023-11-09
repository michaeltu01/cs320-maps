package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;

import edu.brown.cs.student.main.server.census.ACSAPIDataSource;
import edu.brown.cs.student.main.server.census.CensusDataSource;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.BroadbandHandler;
import edu.brown.cs.student.main.server.handlers.csv_handlers.ClearCSVHandler;
import edu.brown.cs.student.main.server.handlers.csv_handlers.LoadCSVHandler;
import edu.brown.cs.student.main.server.handlers.csv_handlers.SearchCSVHandler;
import edu.brown.cs.student.main.server.handlers.csv_handlers.ViewCSVHandler;
import edu.brown.cs.student.main.server.handlers.json_handlers.FilterJsonHandler;
import edu.brown.cs.student.main.server.handlers.json_handlers.LoadJsonHandler;
import edu.brown.cs.student.main.server.handlers.json_handlers.SearchJsonHandler;
import edu.brown.cs.student.main.server.json_classes.FeatureCollection;
import spark.Spark;

/**
 * Server class which is responsible for setting up our server by instantiating each of
 * our handlers and hooking them up to the API.
 */
public class Server {

  static final int port = 3232;
  private final CensusDataSource state;

  private static FeatureCollection sharedJson;

  public static FeatureCollection getSharedJson() {
    return sharedJson; // Return the sharedjson variable
  }

  public static void setSharedJson(FeatureCollection json) {
    sharedJson = json;
  }

  /**
   * Server constructor which sets up the port as well as each of our handlers. LoadCSVHandler
   * is passed into SearchCSVHandler and ViewCSVHandler so that they have references.
   * @param toUse the Data source that is being used by the BroadbandHandler to retrieve information
   */
  public Server(CensusDataSource toUse){

    state = toUse;

    Spark.port(port);

    after((request, response) -> {
      response.header("Access-Control-Allow-Origin", "*");
      response.header("Access-Control-Allow-Methods", "*");
    });

    LoadCSVHandler loadCSVHandler = new LoadCSVHandler();

    Spark.get("/loadcsv", loadCSVHandler);
    Spark.get("/searchcsv", new SearchCSVHandler(loadCSVHandler));
    Spark.get("/viewcsv", new ViewCSVHandler(loadCSVHandler));
    Spark.get("/broadband", new BroadbandHandler(state));
    Spark.get("/clearcsv", new ClearCSVHandler(loadCSVHandler));
    Spark.get("/loadjson", new LoadJsonHandler());
    Spark.get("/filterjson", new FilterJsonHandler());
    Spark.get("/searchjson", new SearchJsonHandler());
    Spark.init();
    Spark.awaitInitialization();
  }

  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/loadcsv");
    Spark.unmap("/searchcsv");
    Spark.unmap("/viewcsv");
    Spark.unmap("/broadband");
    Spark.unmap("/clearcsv");
    Spark.unmap("/loadjson");
    Spark.unmap("/filterjson");
    Spark.unmap("/searchjson");

    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  /**
   * Main method which instantiates our server object as well as a DataSource object.
   * @param args
   */
  public static void main(String args[]) {
    try {
      Server server = new Server(new ACSAPIDataSource());
      System.out.println("Server started on http://localhost:" + port + "/");
    } catch (DatasourceException e) {
      System.err.println("Server failed to start: " + e.getMessage());
    }

  }
}
