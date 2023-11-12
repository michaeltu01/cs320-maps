package edu.brown.cs.student.main.json_tests;

import static org.junit.jupiter.api.Assertions.*;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.json_handlers.FilterJsonHandler;

import edu.brown.cs.student.main.server.handlers.json_handlers.LoadJsonHandler;
import edu.brown.cs.student.main.server.json_classes.Feature;
import edu.brown.cs.student.main.server.server_responses.ServerFailureResponse;
import edu.brown.cs.student.main.server.server_responses.SuccessGeoJsonResponse;
import edu.brown.cs.student.main.server.json_classes.FeatureCollection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;


public class FilterJsonTests {
    private Moshi moshi;
    private JsonAdapter<SuccessGeoJsonResponse> successAdapter;
    private JsonAdapter<ServerFailureResponse> failureAdapter;
  /** Sets up the port. */
  @BeforeAll
  public static void setupOnce() {
    // Pick an arbitrary free port
    Spark.port(0);
    // Eliminate logger spam in console for test suite
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
  }

  /**
   * Helps to set up the json search handler.
   *
   * @throws DatasourceException
   */
  @BeforeEach
  public void setup() throws DatasourceException, IOException {
    // In fact, restart the entire Spark server for every test!
    Spark.get("/loadjson", new LoadJsonHandler());
    Spark.get("/filterjson", new FilterJsonHandler());
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    moshi = new Moshi.Builder().build();
    successAdapter = moshi.adapter(SuccessGeoJsonResponse.class);
    failureAdapter = moshi.adapter(ServerFailureResponse.class);
  }

  @AfterAll
  public static void shutdown() throws InterruptedException {
    Spark.stop();
    Thread.sleep(3000);
  }

  /** Tears down the filterjson handler after use. */
  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/loadjson");
    Spark.unmap("/filterjson");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  /**
   * Helper method which connects our query request to the API.
   *
   * @param apiCall
   * @return
   * @throws IOException
   */
  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send a request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    // The request body contains a Json object
    clientConnection.setRequestProperty("Content-Type", "application/json");
    // We're expecting a Json object in the response body
    clientConnection.setRequestProperty("Accept", "application/json");

    clientConnection.connect();
    return clientConnection;
  }

  ////////////////
  // Tests!! /////
  ////////////////

  // tests that a filter json works
  @Test
  public void testWorkingFilter() throws Exception {
    HttpURLConnection clientConnection =
        tryRequest("loadjson");
    assertEquals(200, clientConnection.getResponseCode());

    // http://localhost:3232/filterjson?minlong=-71.37&minlat=41.7&maxlong=-71&maxlat=41.88
    clientConnection =
        tryRequest("filterjson?minlong=-71.37&minlat=41&maxlong=-71&maxlat=42");
    assertEquals(200, clientConnection.getResponseCode());

    SuccessGeoJsonResponse body =
        successAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

      // check json response
    assertEquals(body.type(), "success");

    Feature singleResult = body.result().features().get(0);
    assertEquals(singleResult.type(), "Feature");
    assertEquals(singleResult.geometry().type(), "MultiPolygon");
    assertTrue(singleResult.geometry().coordinates() != null);
    assertEquals(singleResult.properties().state(), "RI");
    assertEquals(singleResult.properties().city(), "Pawtucket & Central Falls");
  }

  @Test
  public void testEmptyFilter() throws Exception {
    HttpURLConnection clientConnection =
        tryRequest("loadjson");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection =
        tryRequest("filterjson?minlong=-71.37&minlat=-71.37&maxlong=-71&maxlat=-71");
    assertEquals(200, clientConnection.getResponseCode());

    SuccessGeoJsonResponse body =
        successAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // check json response
    assertEquals(body.type(), "success");
    assertTrue(body.result().features().isEmpty());
  }

  @Test
  public void testNoFileLoaded() throws Exception {
    HttpURLConnection clientConnection =
        tryRequest("filterjson?minlong=-71.37&minlat=-71.37&maxlong=-71&maxlat=-71");
    assertEquals(200, clientConnection.getResponseCode());

    ServerFailureResponse body =
        failureAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // check json response
    assertEquals(body.type(), "error");
    assertEquals(body.error_type(), "error_datasource");
    assertEquals(body.details(), "edu.brown.cs.student.main.server.exceptions.DatasourceException: No file loaded");
  }

  @Test
  public void testMissingParameters() throws Exception {
    HttpURLConnection clientConnection =
        tryRequest("loadjson");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection =
        tryRequest("filterjson?minlat=-71.37&maxlong=-71&maxlat=-71");
    assertEquals(200, clientConnection.getResponseCode());

    ServerFailureResponse body =
        failureAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // check json response
    assertEquals(body.type(), "error");
    assertEquals(body.error_type(), "error_bad_request");
    assertEquals(body.details(),
        "You are missing a parameter(s). Make sure you entered a value for all of the following parameters: minlong, minlat, maxlong, maxlat.");

    clientConnection =
        tryRequest("filterjson?minlong=-100&maxlong=-71&maxlat=-71");
    assertEquals(200, clientConnection.getResponseCode());

    body =
        failureAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // check json response
    assertEquals(body.type(), "error");
    assertEquals(body.error_type(), "error_bad_request");
    assertEquals(body.details(),
        "You are missing a parameter(s). Make sure you entered a value for all of the following parameters: minlong, minlat, maxlong, maxlat.");

    clientConnection =
        tryRequest("filterjson?minlong=-100&minlat=-71.37&maxlat=-71");
    assertEquals(200, clientConnection.getResponseCode());

    body =
        failureAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // check json response
    assertEquals(body.type(), "error");
    assertEquals(body.error_type(), "error_bad_request");
    assertEquals(body.details(),
        "You are missing a parameter(s). Make sure you entered a value for all of the following parameters: minlong, minlat, maxlong, maxlat.");

    clientConnection =
        tryRequest("filterjson?minlong=-100&minlat=-71.37&maxlat=-71");
    assertEquals(200, clientConnection.getResponseCode());

    body =
        failureAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // check json response
    assertEquals(body.type(), "error");
    assertEquals(body.error_type(), "error_bad_request");
    assertEquals(body.details(),
        "You are missing a parameter(s). Make sure you entered a value for all of the following parameters: minlong, minlat, maxlong, maxlat.");
  }

  @Test
  public void testBadParameters() throws Exception {
    HttpURLConnection clientConnection =
        tryRequest("loadjson");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection =
        tryRequest("filterjson?minlong=100&minlat=-71.37&maxlong=-71&maxlat=-71");
    assertEquals(200, clientConnection.getResponseCode());

    ServerFailureResponse body =
        failureAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // check json response
    assertEquals(body.type(), "error");
    assertEquals(body.error_type(), "error_bad_request");
    assertEquals(body.details(), "'minlong' parameter needs to be less than or equal to 'maxlong' parameter");

    clientConnection =
        tryRequest("filterjson?minlong=-100&minlat=100&maxlong=-71&maxlat=-71.37");
    assertEquals(200, clientConnection.getResponseCode());

    body =
        failureAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // check json response
    assertEquals(body.type(), "error");
    assertEquals(body.error_type(), "error_bad_request");
    assertEquals(body.details(), "'minlat' parameter needs to be less than or equal to 'maxlat' parameter");
  }

  @Test
  public void testCaching() throws Exception {
    HttpURLConnection clientConnection =
        tryRequest("loadjson");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection =
        tryRequest("filterjson?minlong=-87&maxlong=-86&minlat=33&maxlat=34");
    assertEquals(200, clientConnection.getResponseCode());

    SuccessGeoJsonResponse body = successAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    String dateTime = body.date_time();

    clientConnection =
        tryRequest("filterjson?minlong=-30&maxlong=0&minlat=20&maxlat=40");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("filterjson?minlong=0&maxlong=40&minlat=-40&maxlat=0");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection =
        tryRequest("filterjson?minlong=-87&maxlong=-86&minlat=33&maxlat=34");
    assertEquals(200, clientConnection.getResponseCode());
    body = successAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals(dateTime, body.date_time());
  }


}
