package edu.brown.cs.student.main.json_tests;

import static org.junit.jupiter.api.Assertions.*;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.json_handlers.LoadJsonHandler;
import edu.brown.cs.student.main.server.handlers.json_handlers.SearchJsonHandler;
import edu.brown.cs.student.main.server.server_responses.ServerFailureResponse;
import edu.brown.cs.student.main.server.server_responses.SuccessGeoJsonResponse;
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

public class SearchJsonTests {
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
    Spark.get("/searchjson", new SearchJsonHandler());
    Spark.get("/loadjson", new LoadJsonHandler());
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

  /** Tears down the searchjson handler after use. */
  @AfterEach
  public void tearDown() throws InterruptedException {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/searchjson");
    Spark.unmap("/loadjson");
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

  // tests when we search without loading json
  @Test
  public void testUnloadedJson() throws IOException {
    HttpURLConnection clientConnection = tryRequest("searchjson?search=birmingham");
    assertEquals(200, clientConnection.getResponseCode());

    ServerFailureResponse body =
            failureAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // check json response
    assertEquals("error", body.type());
    assertEquals("error_datasource", body.error_type());
    assertEquals("No file loaded", body.details());
  }


  // tests when searchjson is missing
  @Test
  public void testMissingSearchValue() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadjson");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("searchjson?");
    assertEquals(200, clientConnection.getResponseCode());

    ServerFailureResponse body =
        failureAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // check json response
    assertEquals("error", body.type());
    assertEquals("error_bad_request", body.error_type());
  }

  // tests a search that works
  @Test
  public void testWorkingSearch() throws IOException {
    // Load JSON before the search
    HttpURLConnection clientConnection = tryRequest("loadjson");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection =
        tryRequest("searchjson?search=adjacent%20to%20Central%20Park-%20good%20transportation");
    // assertEquals(200, clientConnection.getResponseCode());

    SuccessGeoJsonResponse body =
        successAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", body.type());
    assertEquals("NY", body.result().features().get(0).properties().state());
    assertEquals("Manhattan", body.result().features().get(0).properties().city());
  }

  @Test
  public void testEmptySearchResults() throws IOException {
    // Load JSON before the search
    HttpURLConnection clientConnection = tryRequest("loadjson");
    assertEquals(200, clientConnection.getResponseCode());

    clientConnection = tryRequest("searchjson?search=asdfasdfasdfasdfasdfasdfasdfa");
    // assertEquals(200, clientConnection.getResponseCode());

    ServerFailureResponse body =
        failureAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error", body.type());
    assertEquals("error_bad_json", body.error_type());
    assertEquals(
        "No areas found with the given keyword: asdfasdfasdfasdfasdfasdfasdfa", body.details());
  }
}
