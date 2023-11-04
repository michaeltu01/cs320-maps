package edu.brown.cs.student.main.server_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.csv_handlers.LoadCSVHandler;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/**
 * Testing class for our loadCSVHandler.
 */
public class LoadCSVTests {

  private LoadCSVHandler loadCSVHandler = new LoadCSVHandler();
  Moshi moshi;
  JsonAdapter<Map<String, Object>> adapter;
  // private Server server;
  // private final MockAPIDataSource mockAPIDataSource = new MockAPIDataSource();
  // private final int port = 3232;

  /**
   * Sets up the port.
   */
  @BeforeAll
  public static void setupOnce() {
    // // Pick an arbitrary free port
    // Spark.port(0);
    // Eliminate logger spam in console for test suite
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
  }

  /**
   * Sets up the loadCSVHandler.
   */
  @BeforeEach
  public void setup() {
    // In fact, restart the entire Spark server for every test!

    Spark.get("/loadcsv", loadCSVHandler);
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    // this.server = new Server(mockAPIDataSource);

    moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

  }

  /**
   * Tears down the loadCSV after use.
   */
  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/loadcsv");
    Spark.awaitStop(); // don't proceed until the server is stopped
    // server.tearDown();
  }


  /**
   * Connects the query request to the API.
   * @param apiCall
   * @return
   * @throws IOException
   */
  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send a request yet)
    URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    // The request body contains a Json object
    clientConnection.setRequestProperty("Content-Type", "application/json");
    // We're expecting a Json object in the response body
    clientConnection.setRequestProperty("Accept", "application/json");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Makes sure that LoadCSV works when there are headers included in the query.
   * @throws IOException
   * @throws DatasourceException
   */
  @Test
  public void loadCSVTestsWithHeaders() throws IOException, DatasourceException {

    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=data/test/simple-for-tests&headers=true");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    showDetailsIfError(body);
    assertEquals("success", body.get("type"));

    List<List<String>> body_data = loadCSVHandler.getReadOnlyCSVData();
    List<String> header_data = loadCSVHandler.getReadOnlyHeaders();

    //testing that it properly returns the indicated lists
    assert body_data.equals(List.of(List.of("d", "e", "f"), List.of("g", "h", "i")));
    assert header_data.equals(List.of("a", "b", "c"));

    //checking if mutation fails
    try {
      body_data.get(0).add("x");
      header_data.add("x");
    } catch (Exception e){
      System.err.println("Attempt to mutate the immutable data has failed.");
    }

  }

  /**
   * Makes sure that LoadCSV works when headers are not included in the query.
   * @throws IOException
   * @throws DatasourceException
   */
  @Test
  public void loadCSVTestsWithoutHeaders() throws IOException, DatasourceException {

    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=data/test/simple-for-tests&headers=false");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    showDetailsIfError(body);
    assertEquals("success", body.get("type"));

    List<List<String>> body_data = loadCSVHandler.getReadOnlyCSVData();
    List<String> header_data = loadCSVHandler.getReadOnlyHeaders();

    //testing that it properly returns the indicated lists
    assert body_data.equals(List.of(List.of("a", "b", "c"),List.of("d", "e", "f"), List.of("g", "h", "i")));
    assert header_data == null;

    //checking if mutation fails
    try {
      body_data.get(0).add("x");
    } catch (Exception e){
      System.err.println("Attempt to mutate the immutable data has failed.");
    }
  }

  /**
   * Test error response for loading a bad CSV
   */
  @Test
  public void testLoadBadCSV() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=simple-for-tests&headers=false");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error", body.get("type"));
    assertEquals("error_datasource", body.get("error_type"));
//    assertEquals("File path not found: C:\\Code\\CS320\\server-louietanmay-michaeltu01\\data\\simple-for-tests", body.get("details"));
    assertEquals("File path not found: simple-for-tests", body.get("details"));
  }

  /**
   * Makes sure that loadCSV throws the appropriate error when a filename is not provided in the query.
   * @throws IOException
   * @throws DatasourceException
   */
  @Test
  public void loadCSVTestsNoFileName() throws IOException, DatasourceException {

    HttpURLConnection clientConnection = tryRequest("loadcsv?headers=false");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    Map<String, Object> body = adapter.fromJson(
        new Buffer().readFrom(clientConnection.getInputStream()));
    showDetailsIfError(body);

    assertEquals("error", body.get("type"));
    assertEquals("error_bad_request", body.get("error_type"));

  }

  /**
   * Makes sure that loadCSV still works when a file is loaded more than one time.
   * @throws IOException
   * @throws DatasourceException
   */
  @Test
  public void loadMoreThanOneCSV() throws IOException, DatasourceException {

    HttpURLConnection clientConnectionBefore = tryRequest("loadcsv?filename=data/test/simple-for-tests&headers=true");
    assertEquals(200, clientConnectionBefore.getResponseCode());

    HttpURLConnection clientConnection = tryRequest("loadcsv?filename=data/test/simple-for-tests-2&headers=true");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    Map<String, Object> body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    showDetailsIfError(body);
    assertEquals("success", body.get("type"));

    List<List<String>> body_data = loadCSVHandler.getReadOnlyCSVData();
    List<String> header_data = loadCSVHandler.getReadOnlyHeaders();

    //testing that it properly returns the indicated lists
    assert body_data.equals(List.of(List.of("m", "n", "o"), List.of("p", "q", "r")));
    assert header_data.equals(List.of("j", "k", "l"));

  }

  /**
   * Helper method which shows the details if there happens to be an error.
   * @param body
   */
  private void showDetailsIfError(Map<String, Object> body) {
    if(body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body.toString());
    }
  }
}
