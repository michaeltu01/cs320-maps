package edu.brown.cs.student.main.json_tests;

import static org.junit.jupiter.api.Assertions.*;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.json_handlers.LoadJsonHandler;
import edu.brown.cs.student.main.server.handlers.json_handlers.SearchJsonHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
  private JsonAdapter<Map<String, Object>> adapter;
  private Map<String, Object> responseMap;
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
    adapter = moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    responseMap = new HashMap<String, Object>();
  }

  @AfterAll
  public static void shutdown() throws InterruptedException {
    Spark.stop();
    Thread.sleep(3000);
  }

  /** Tears down the searchjson handler after use. */
  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/searchjson");
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

  // tests when searchjson is missing
  @Test
  public void testMissingSearchValue() throws IOException {

    HttpURLConnection clientConnection = tryRequest("searchjson");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // check json response
    responseMap.put("type", "error");
    responseMap.put("error_type", "unloaded json");

    assertEquals(responseMap, body);
  }

  // tests when we search without loading json
  @Test
  public void testUnloadedJson() throws IOException {
    HttpURLConnection clientConnection = tryRequest("searchjson?search=birmingham");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // check json response
    responseMap.put("type", "error");
    responseMap.put("error_type", "unloaded json");

    assertEquals(responseMap, body);
  }

  // tests a search that works
  @Test
  public void testWorkingSearch() throws IOException {
    // Load JSON before the search
    HttpURLConnection loadJsonConnection =
            tryRequest("loadjson");
    assertEquals(200, loadJsonConnection.getResponseCode());

    // Read and discard the response for the loadjson request
    new Buffer().readFrom(loadJsonConnection.getInputStream());

    HttpURLConnection clientConnection =
        tryRequest("searchjson?search=adjacent%20to%20Central%20Park-%20good%20transportation");
    //assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    Object result = body.get("result");

    // Object properties = result.get("properties");

  }
}
