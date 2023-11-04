package edu.brown.cs.student.main.server_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.LoadCSVHandler;
import edu.brown.cs.student.main.server.handlers.ViewCSVHandler;
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
 * Testing class for our ViewCSVHandler.
 */
public class ViewCSVTests {

  private LoadCSVHandler loadCSVHandler = new LoadCSVHandler();

  /**
   * Sets up the port.
   */
  @BeforeAll
  public static void setupOnce() {
    // Pick an arbitrary free port
    Spark.port(0);
    // Eliminate logger spam in console for test suite
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
  }

  /**
   * Sets up the LoadCSVHandler and the ViewCSVHandler
   */
  @BeforeEach
  public void setup() {
    // In fact, restart the entire Spark server for every test!

    Spark.get("/loadcsv", loadCSVHandler);
    Spark.get("/viewcsv", new ViewCSVHandler(loadCSVHandler));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

  }

  /**
   * Tears down the loadCSV and viewCSV after each use.
   */
  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/loadcsv");
    Spark.unmap("/viewcsv");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }


  /**
   * Helper method which sets up the connection to the API.
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
   * Confirms that the ViewCSV method indeed works after the appropriate file has been loaded.
   * @throws IOException
   * @throws DatasourceException
   */
  @Test
  public void ViewCSVAfterLoading() throws IOException, DatasourceException {

    HttpURLConnection clientConnectionLoad = tryRequest("loadcsv?filename=data/test/simple-for-tests&headers=false");
    assertEquals(200, clientConnectionLoad.getResponseCode());

    HttpURLConnection clientConnectionView = tryRequest("viewcsv");
    assertEquals(200, clientConnectionView.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    Map<String, Object> body = adapter.fromJson(new Buffer().readFrom(clientConnectionView.getInputStream()));
    showDetailsIfError(body);
    assertEquals("success", body.get("type"));

    List<List<String>> correct_data = List.of(List.of("a", "b", "c"),List.of("d", "e", "f"), List.of("g", "h", "i"));

    //testing that it properly returns the indicated lists
    assertEquals(body.get("data").toString(), correct_data.toString().replaceAll("\"(.*?)\"", "$1"));

  }

  /**
   * Confirms that viewCSV fails if it is called before any file has been loaded.
   * @throws IOException
   * @throws DatasourceException
   */
  @Test
  public void ViewCSVBeforeLoading() throws IOException, DatasourceException {

    HttpURLConnection clientConnectionView = tryRequest("viewcsv");
    assertEquals(200, clientConnectionView.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    Map<String, Object> body = adapter.fromJson(new Buffer().readFrom(clientConnectionView.getInputStream()));
    showDetailsIfError(body);

    assertEquals("error", body.get("type"));
    assertEquals("error_datasource", body.get("error_type"));

  }

  /**
   * Helper method which shows the details if there is an error.
   * @param body
   */
  private void showDetailsIfError(Map<String, Object> body) {
    if(body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body.toString());
    }
  }

}
