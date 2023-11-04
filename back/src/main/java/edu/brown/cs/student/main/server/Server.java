package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.census.ACSAPIDataSource;
import edu.brown.cs.student.main.server.census.CensusDataSource;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.BroadbandHandler;
import edu.brown.cs.student.main.server.handlers.ClearCSVHandler;
import edu.brown.cs.student.main.server.handlers.LoadCSVHandler;
import edu.brown.cs.student.main.server.handlers.SearchCSVHandler;
import edu.brown.cs.student.main.server.handlers.ViewCSVHandler;
import spark.Spark;

/**
 * Server class which is responsible for setting up our server by instantiating each of
 * our handlers and hooking them up to the API.
 */
public class Server {

  static final int port = 3232;
  private final CensusDataSource state;

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
