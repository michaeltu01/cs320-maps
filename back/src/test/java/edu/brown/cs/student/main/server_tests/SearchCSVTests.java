package edu.brown.cs.student.main.server_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.csv_handlers.LoadCSVHandler;
import edu.brown.cs.student.main.server.handlers.csv_handlers.SearchCSVHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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

/** Testing class for testing integration of /searchcsv endpoint */
public class SearchCSVTests {
  private Moshi moshi;
  private JsonAdapter<Map<String, Object>> adapter;
  private Map<String, Object> responseMap;
  private final String removeQuotes = "\"(.*?)\"";

  // mock rows for testing
  private final List<String> riHeaders =
      new ArrayList<>(
          List.of(
              "City/Town", "Median Household Income", "Median Family Income", "Per Capita Income"));

  private final List<List<String>> riData =
      new ArrayList<>(
          List.of(
              new ArrayList<>(List.of("Rhode Island", "74,489.00", "95,198.00", "39,603.00")),
              new ArrayList<>(List.of("Barrington", "130,455.00", "154,441.00", "69,917.00"))));

  private final List<List<String>> moreData =
      new ArrayList<>(
          List.of(
              new ArrayList<>(List.of("1998", "27", "9991")),
              new ArrayList<>(List.of("1997", "17", "9925")),
              new ArrayList<>(List.of("1998", "28", "10491"))));

  /** Sets up the port. */
  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    // Don't try to remember it. Spark won't actually give Spark.port() back
    // until route mapping has started. Just get the port number later. We're using
    // a random _free_ port to remove the chances that something is already using a
    // specific port on the system used for testing.

    // Remove the logging spam during tests
    //   This is surprisingly difficult. (Notes to self omitted to avoid complicating things.)

    // SLF4J doesn't let us change the logging level directly (which makes sense,
    //   given that different logging frameworks have different level labels etc.)
    // Changing the JDK *ROOT* logger's level (not global) will block messages
    //   (assuming using JDK, not Log4J)
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  /**
   * Shared state for all tests. We need to be able to mutate it (adding recipes etc.) but never
   * need to replace the reference itself. We clear this state out after every test runs.
   */
  @BeforeEach
  public void setup() {
    moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));
    responseMap = new HashMap<String, Object>();
  }

  /** Tears down the /searchcsv endpoint after use. */
  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on endpoints
    Spark.unmap("/searchcsv");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * @param apiCall the call string, including endpoint (NOTE: this would be better if it had more
   *     structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    // clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  ////////////////
  // Tests!! /////
  ////////////////

  /**
   * Test for error message when a user doesn't specify a null value
   *
   * @throws IOException
   */
  @Test
  public void testMissingSearchValue() throws IOException {
    // In fact, restart the entire Spark server for every test!
    Spark.get("/searchcsv", new SearchCSVHandler(riHeaders, riData));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    // Make API request
    HttpURLConnection clientConnection = tryRequest("searchcsv");
    assertEquals(200, clientConnection.getResponseCode());

    // Check JSON response
    responseMap.put("type", "error");
    responseMap.put("error_type", "error_bad_request");
    responseMap.put("details", "'value' parameter missing");

    assertEquals(
        responseMap.toString(),
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream())).toString());
  }

  /**
   * Test for error message when column index is out of bounds
   *
   * @throws IOException
   */
  @Test
  public void testIndexOutOfBounds() throws IOException {
    // In fact, restart the entire Spark server for every test!
    Spark.get("/searchcsv", new SearchCSVHandler(riHeaders, riData));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    // Make API request
    HttpURLConnection clientConnection = tryRequest("searchcsv?value=idk&index=-1");
    assertEquals(200, clientConnection.getResponseCode());

    // Check JSON response
    responseMap.put("type", "error");
    responseMap.put("error_type", "error_bad_request");
    responseMap.put("details", "Column index out of bounds. The file has 4 columns.");

    assertEquals(
        responseMap.toString(),
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream())).toString());

    // Make API request
    clientConnection = tryRequest("searchcsv?value=idk&index=4");
    assertEquals(200, clientConnection.getResponseCode());

    // Check JSON response
    responseMap = new HashMap<String, Object>();
    responseMap.put("type", "error");
    responseMap.put("error_type", "error_bad_request");
    responseMap.put("details", "Column index out of bounds. The file has 4 columns.");

    assertEquals(
        responseMap.toString(),
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream())).toString());
  }

  /**
   * Test for error message when column index is a non-integer value
   *
   * @throws IOException
   */
  @Test
  public void testNonIntegerIndex() throws IOException {
    // In fact, restart the entire Spark server for every test!
    Spark.get("/searchcsv", new SearchCSVHandler(riHeaders, riData));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    // Make API request
    HttpURLConnection clientConnection = tryRequest("searchcsv?value=idk&index=asdf");
    assertEquals(200, clientConnection.getResponseCode());

    // Check JSON response
    responseMap.put("type", "error");
    responseMap.put("error_type", "error_bad_request");
    responseMap.put("details", "Column index invalid: asdf");

    assertEquals(
        responseMap.toString(),
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream())).toString());

    // Make API request
    clientConnection = tryRequest("searchcsv?value=idk&index=1.0");
    assertEquals(200, clientConnection.getResponseCode());

    // Check JSON response
    responseMap = new HashMap<String, Object>();
    responseMap.put("type", "error");
    responseMap.put("error_type", "error_bad_request");
    responseMap.put("details", "Column index invalid: 1.0");

    assertEquals(
        responseMap.toString(),
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream())).toString());
  }

  /**
   * Test for error message when column name is not found
   *
   * @throws IOException
   */
  @Test
  public void testColumnNameNotFound() throws IOException {
    // In fact, restart the entire Spark server for every test!
    Spark.get("/searchcsv", new SearchCSVHandler(riHeaders, riData));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    // Make API request
    HttpURLConnection clientConnection = tryRequest("searchcsv?value=idk&column=idk");
    assertEquals(200, clientConnection.getResponseCode());

    // Check JSON response
    responseMap.put("type", "error");
    responseMap.put("error_type", "error_bad_request");
    responseMap.put(
        "details",
        "Column name not found: idk. Here are the possible column names: " + riHeaders.toString());

    assertEquals(
        responseMap.toString(),
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream())).toString());
  }

  /**
   * Test for error message when searching by column name when there are no headers
   *
   * @throws IOException
   */
  @Test
  public void testColumnNameWhenNoHeaders() throws IOException {
    // In fact, restart the entire Spark server for every test!
    Spark.get("/searchcsv", new SearchCSVHandler(new ArrayList<String>(), riData));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    // Make API request
    HttpURLConnection clientConnection = tryRequest("searchcsv?value=idk&column=something");
    assertEquals(200, clientConnection.getResponseCode());

    // Check JSON response
    responseMap.put("type", "error");
    responseMap.put("error_type", "error_bad_request");
    responseMap.put("details", "Loaded CSV does not contain any headers.");

    assertEquals(
        responseMap.toString(),
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream())).toString());
  }

  /**
   * Test for searching by column index when there are no headers
   *
   * @throws IOException
   */
  @Test
  public void testSearchIndexWhenNoHeaders() throws IOException {
    // In fact, restart the entire Spark server for every test!
    Spark.get("/searchcsv", new SearchCSVHandler(new ArrayList<String>(), riData));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    // Make API request
    HttpURLConnection clientConnection = tryRequest("searchcsv?value=Rhode%20Island&index=0");
    assertEquals(200, clientConnection.getResponseCode());

    // Check JSON response
    responseMap.put("type", "success");
    responseMap.put("value", "Rhode Island");
    responseMap.put("index", 0);
    // responseMap.put("results",
    //     new ArrayList<>(List.of(riData.get(0))).toString().replaceAll(removeQuotes, "$1"));
    responseMap.put(
        "data", new ArrayList<>(List.of(riData.get(0))).toString().replaceAll(removeQuotes, "$1"));

    assertEquals(
        responseMap.toString(),
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream())).toString());
  }

  /**
   * Test for searching the entire CSV file
   *
   * @throws IOException
   */
  @Test
  public void testSearchEntireFile() throws IOException {
    // In fact, restart the entire Spark server for every test!
    Spark.get("/searchcsv", new SearchCSVHandler(null, riData));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    // Make API request
    HttpURLConnection clientConnection = tryRequest("searchcsv?value=Rhode%20Island");
    assertEquals(200, clientConnection.getResponseCode());

    // Check JSON response
    responseMap.put("type", "success");
    responseMap.put("value", "Rhode Island");
    // responseMap.put("results",
    //     new ArrayList<>(List.of(riData.get(0))).toString().replaceAll(removeQuotes, "$1"));
    responseMap.put(
        "data", new ArrayList<>(List.of(riData.get(0))).toString().replaceAll(removeQuotes, "$1"));

    assertEquals(
        responseMap.toString(),
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream())).toString());
  }

  /**
   * Test for multiple rows of output
   *
   * @throws IOException
   */
  @Test
  public void testOutputMultipleRows() throws IOException {
    // In fact, restart the entire Spark server for every test!
    Spark.get("/searchcsv", new SearchCSVHandler(null, moreData));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    // Make API request
    HttpURLConnection clientConnection = tryRequest("searchcsv?value=1998");
    assertEquals(200, clientConnection.getResponseCode());

    // Check JSON response
    responseMap.put("type", "success");
    responseMap.put("value", "1998");
    // responseMap.put("results", new ArrayList<>(List.of(moreData.get(0),
    // moreData.get(2))).toString()
    //     .replaceAll(removeQuotes, "$1"));
    responseMap.put(
        "data",
        new ArrayList<>(List.of(moreData.get(0), moreData.get(2)))
            .toString()
            .replaceAll(removeQuotes, "$1"));

    assertEquals(
        responseMap.toString(),
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream())).toString());
  }

  /**
   * Test for searching by column index
   *
   * @throws IOException
   */
  @Test
  public void testSearchByColumnIndex() throws IOException {
    // In fact, restart the entire Spark server for every test!
    Spark.get("/searchcsv", new SearchCSVHandler(null, moreData));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    // Make API request
    HttpURLConnection clientConnection = tryRequest("searchcsv?value=9991&index=2");
    assertEquals(200, clientConnection.getResponseCode());

    // Check JSON response
    responseMap.put("type", "success");
    responseMap.put("value", "9991");
    responseMap.put("index", 2);
    // responseMap.put("results",
    //     new ArrayList<>(List.of(moreData.get(0))).toString().replaceAll(removeQuotes, "$1"));
    responseMap.put(
        "data",
        new ArrayList<>(List.of(moreData.get(0))).toString().replaceAll(removeQuotes, "$1"));

    assertEquals(
        responseMap.toString(),
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream())).toString());
  }

  /**
   * Test for searching by column name
   *
   * @throws IOException
   */
  @Test
  public void testSearchByColumnName() throws IOException {
    // In fact, restart the entire Spark server for every test!
    Spark.get("/searchcsv", new SearchCSVHandler(riHeaders, riData));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    // Make API request
    HttpURLConnection clientConnection =
        tryRequest("searchcsv?value=130,455.00&column=Median%20Household%20Income");
    assertEquals(200, clientConnection.getResponseCode());

    // Check JSON response
    responseMap.put("type", "success");
    responseMap.put("value", "130,455.00");
    responseMap.put("column", "Median Household Income");
    // responseMap.put("results",
    //     new ArrayList<>(List.of(riData.get(1))).toString().replaceAll(removeQuotes, "$1"));
    responseMap.put(
        "data",
        new ArrayList<>(List.of(riHeaders, riData.get(1)))
            .toString()
            .replaceAll(removeQuotes, "$1"));

    assertEquals(
        responseMap.toString(),
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream())).toString());

    // Make API request
    clientConnection = tryRequest("searchcsv?value=Rhode%20Island&column=City/Town");
    assertEquals(200, clientConnection.getResponseCode());

    // Check JSON response
    responseMap.put("type", "success");
    responseMap.put("value", "Rhode Island");
    responseMap.put("column", "City/Town");
    // responseMap.put("results",
    //     new ArrayList<>(List.of(riData.get(0))).toString().replaceAll(removeQuotes, "$1"));
    responseMap.put(
        "data",
        new ArrayList<>(List.of(riHeaders, riData.get(0)))
            .toString()
            .replaceAll(removeQuotes, "$1"));

    assertEquals(
        responseMap.toString(),
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream())).toString());
  }

  /**
   * Test for no output queries
   *
   * @throws IOException
   */
  @Test
  public void testNoOutput() throws IOException {
    // In fact, restart the entire Spark server for every test!
    Spark.get("/searchcsv", new SearchCSVHandler(riHeaders, riData));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    // Make API request
    HttpURLConnection clientConnection = tryRequest("searchcsv?value=130,455.00&column=City/Town");
    assertEquals(200, clientConnection.getResponseCode());

    // Check JSON response
    responseMap.put("type", "error");
    responseMap.put("error_type", "error_bad_json");
    responseMap.put("details", "No matching rows found.");

    assertEquals(
        responseMap.toString(),
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream())).toString());
  }

  /**
   * Test for searching by both column name and index (matching)
   *
   * @throws IOException
   */
  @Test
  public void testSearchBothColumnAndIndex() throws IOException {
    // In fact, restart the entire Spark server for every test!
    Spark.get("/searchcsv", new SearchCSVHandler(riHeaders, riData));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    // Make API request
    HttpURLConnection clientConnection =
        tryRequest("searchcsv?value=Rhode%20Island&column=City/Town&index=0");
    assertEquals(200, clientConnection.getResponseCode());

    // Check JSON response
    responseMap.put("type", "success");
    responseMap.put("value", "Rhode Island");
    responseMap.put("column", "City/Town");
    responseMap.put("index", 0);
    // responseMap.put("results",
    //     new ArrayList<>(List.of(riData.get(0).toString().replaceAll(removeQuotes, "$1"))));
    responseMap.put(
        "data",
        new ArrayList<>(
            List.of(riHeaders, riData.get(0).toString().replaceAll(removeQuotes, "$1"))));

    assertEquals(
        responseMap.toString(),
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream())).toString());
  }

  /**
   * Test for error when column name and index provided don't match
   *
   * @throws IOException
   */
  @Test
  public void testSearchColumnAndIndexDontMatch() throws IOException {
    // In fact, restart the entire Spark server for every test!
    Spark.get("/searchcsv", new SearchCSVHandler(riHeaders, riData));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    // Make API request
    HttpURLConnection clientConnection =
        tryRequest("searchcsv?value=Rhode%20Island&column=City/Town&index=2");
    assertEquals(200, clientConnection.getResponseCode());

    // Check JSON response
    responseMap.put("type", "error");
    responseMap.put("error_type", "error_bad_request");
    responseMap.put(
        "details",
        "Index (2) and column name (City/Town) does not match. There are 4 columns: "
            + riHeaders.toString());

    assertEquals(
        responseMap.toString(),
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream())).toString());
  }

  /**
   * Test for error when no CSV has been loaded
   *
   * @throws IOException
   */
  @Test
  public void testSearchWhenNoCSVHasBeenLoaded() throws IOException {
    // In fact, restart the entire Spark server for every test!
    LoadCSVHandler loadCSVHandler = new LoadCSVHandler();
    Spark.get("/loadcsv", loadCSVHandler);
    Spark.get("/searchcsv", new SearchCSVHandler(loadCSVHandler));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    // Make API request
    HttpURLConnection clientConnection =
        tryRequest("searchcsv?value=Rhode%20Island&column=City/Town&index=2");
    assertEquals(200, clientConnection.getResponseCode());

    // Check JSON response
    responseMap.put("type", "error");
    responseMap.put("error_type", "error_datasource");
    responseMap.put("details", "No file has been loaded.");

    assertEquals(
        responseMap.toString(),
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream())).toString());

    Spark.unmap("/loadcsv");
  }
}
