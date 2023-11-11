package edu.brown.cs.student.main.json_tests;

import static org.junit.jupiter.api.Assertions.*;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.json_handlers.LoadJsonHandler;
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

public class LoadJsonTests {
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
  public void setup() throws DatasourceException {
    // In fact, restart the entire Spark server for every test!

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

  // tests that when we do not put anything in for load, the default json is loaded successfully
  @Test
  public void testDefaultLoad() throws Exception {

    HttpURLConnection clientConnection = tryRequest("loadjson");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // check json response

//    responseMap.put("filepath", "/Users/isaacyi/Desktop/CSCI0320/maps-iyi3-mstu/back/data/geodata/fullDownload.json");
    responseMap.put("filepath", "C:\\Code\\CS320\\maps-iyi3-mstu\\back\\data\\geodata\\fullDownload.json");
    responseMap.put("type", "success");
    responseMap.put("details", "file loaded successfully");
    System.out.println(body);

    assertEquals(responseMap, body);
  }

  @Test
  public void testLoadingAnotherJson() throws Exception {
    HttpURLConnection clientConnection = tryRequest("loadjson?filepath=src/test/java/edu/brown/cs/student/jsons/exampleGeoJson.txt");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // check json response
    responseMap.put("filepath", "src/test/java/edu/brown/cs/student/jsons/exampleGeoJson.txt");
    responseMap.put("type", "success");
    responseMap.put("details", "file loaded successfully");
    System.out.println(body);

    assertEquals(responseMap, body);
  }

  @Test
  public void testInvalidFilepath() throws Exception {
    HttpURLConnection clientConnection = tryRequest("loadjson?filepath=adfasdfa");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // check json response
    responseMap.put("type", "error");
    responseMap.put("error_type", "error_datasource");
    responseMap.put("details", "Invalid file path: adfasdfa");
    System.out.println(body);

    assertEquals(responseMap, body);
  }

  @Test
  public void testInvalidJsonFormat() throws Exception {
    HttpURLConnection clientConnection = tryRequest("loadjson?filepath=src/test/java/edu/brown/cs/student/jsons/json2.txt");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // check json response
    responseMap.put("type", "error");
    responseMap.put("error_type", "error_datasource");
    responseMap.put("details", "src/test/java/edu/brown/cs/student/jsons/json2.txt");
    System.out.println(body);

    assertEquals(responseMap, body);
  }
}
