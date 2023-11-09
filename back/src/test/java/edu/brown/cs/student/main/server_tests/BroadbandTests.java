package edu.brown.cs.student.main.server_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.census.MockAPIDataSource;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.BroadbandHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/** Testing class for our broadband handler. */
public class BroadbandTests {

  /** Sets up the port. */
  @BeforeAll
  public static void setupOnce() {
    // Pick an arbitrary free port
    Spark.port(0);
    // Eliminate logger spam in console for test suite
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
  }

  /**
   * Helps to set up the broadband handler and connects it to the MockAPIDataSource.
   *
   * @throws DatasourceException
   */
  @BeforeEach
  public void setup() throws DatasourceException {
    // In fact, restart the entire Spark server for every test!

    //    Spark.get("/broadband", new BroadbandHandler(new ACSAPIDataSource()));
    Spark.get("/broadband", new BroadbandHandler(new MockAPIDataSource()));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  /** Tears down the broadband handler after use. */
  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/broadband");
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

  /**
   * Uses a mock API to test the Broadband Handler interface
   *
   * @throws IOException
   */
  @Test
  public void testBroadbandHandlerInterface() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("broadband?state=California&county=Orange%20County");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", body.get("type"));
    assertEquals(46.7, body.get("broadband"));
  }

  /**
   * Uses a mock API to test the Broadband Handler interface
   *
   * @throws IOException
   */
  @Test
  public void testBadBroadbandRequest() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband?county=Orange%20County");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Map<String, Object> responseMap = new HashMap<String, Object>();
    responseMap.put("type", "error");
    responseMap.put("error_type", "error_bad_request");
    responseMap.put("details", "'state' parameter missing");
    assertEquals(responseMap.toString(), body.toString());

    clientConnection = tryRequest("broadband?state=Virginia");
    assertEquals(200, clientConnection.getResponseCode());

    body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    responseMap = new HashMap<String, Object>();
    responseMap.put("type", "error");
    responseMap.put("error_type", "error_bad_request");
    responseMap.put("details", "'county' parameter missing");
    assertEquals(responseMap.toString(), body.toString());
  }
}
