package edu.brown.cs.student.main.json_tests;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.handlers.json_handlers.FilterJsonHandler;
import edu.brown.cs.student.main.server.handlers.json_handlers.LoadJsonHandler;
import edu.brown.cs.student.main.server.handlers.json_handlers.SearchJsonHandler;
import edu.brown.cs.student.main.server.json_classes.Feature;
import edu.brown.cs.student.main.server.server_responses.ServerFailureResponse;
import edu.brown.cs.student.main.server.server_responses.SuccessGeoJsonResponse;
import okio.Buffer;
import org.junit.jupiter.api.*;
import spark.Spark;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegrationTests {
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
        Spark.get("/searchjson", new SearchJsonHandler());
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

    @Test
    public void BigTest() throws IOException {
        // searching an api without loading
        HttpURLConnection clientConnection = tryRequest("searchjson?search=birmingham");
        assertEquals(200, clientConnection.getResponseCode());

        ServerFailureResponse body =
                failureAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        // check json response
        assertEquals("error", body.type());
        assertEquals("error_datasource", body.error_type());
        assertEquals("No file loaded", body.details());

        // load a working json
        HttpURLConnection clientConnection2 = tryRequest("loadjson");
        assertEquals(200, clientConnection2.getResponseCode());
        SuccessGeoJsonResponse body2 =
                successAdapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));
        assertEquals("success", body2.type());

        // search the working json
        HttpURLConnection clientConnection3 =
                tryRequest("searchjson?search=adjacent%20to%20Central%20Park-%20good%20transportation");
        assertEquals(200, clientConnection3.getResponseCode());

        SuccessGeoJsonResponse body3 =
                successAdapter.fromJson(new Buffer().readFrom(clientConnection3.getInputStream()));

        assertEquals("success", body3.type());
        assertEquals("NY", body3.result().features().get(0).properties().state());
        assertEquals("Manhattan", body3.result().features().get(0).properties().city());

        // load a different json
        HttpURLConnection clientConnection4 = tryRequest("loadjson?filepath=/Users/isaacyi/Desktop/CSCI0320/maps-iyi3-mstu/back/data/geodata/fullDownload.json");
        assertEquals(200, clientConnection4.getResponseCode());
        SuccessGeoJsonResponse body4 =
                successAdapter.fromJson(new Buffer().readFrom(clientConnection4.getInputStream()));
        assertEquals("success", body4.type());

        // search a different search
        HttpURLConnection clientConnection5 =
                tryRequest("searchjson?search=a%20low%20cost%20housing%20project%20owned");
        assertEquals(200, clientConnection5.getResponseCode());

        SuccessGeoJsonResponse body5 =
                successAdapter.fromJson(new Buffer().readFrom(clientConnection5.getInputStream()));

        assertEquals("success", body5.type());
        assertEquals("NY", body5.result().features().get(0).properties().state());
        assertEquals("Bronx", body5.result().features().get(0).properties().city());

        // filter the json
        HttpURLConnection clientConnection6 = tryRequest("filterjson?minlong=-71.37&minlat=41&maxlong=-71&maxlat=42");
        assertEquals(200, clientConnection6.getResponseCode());

        // check json response

        SuccessGeoJsonResponse body6 =
                successAdapter.fromJson(new Buffer().readFrom(clientConnection6.getInputStream()));

        assertEquals(body6.type(), "success");

        Feature singleResult = body6.result().features().get(0);
        assertEquals(singleResult.type(), "Feature");
        assertEquals(singleResult.geometry().type(), "MultiPolygon");
        assertTrue(singleResult.geometry().coordinates() != null);
        assertEquals(singleResult.properties().state(), "RI");
        assertEquals(singleResult.properties().city(), "Pawtucket & Central Falls");

        // make a bad filter
        clientConnection = tryRequest("filterjson?minlong=100&minlat=-71.37&maxlong=-71&maxlat=-71");
        assertEquals(200, clientConnection.getResponseCode());

        ServerFailureResponse body7 =
                failureAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        // check json response
        assertEquals(body7.type(), "error");
        assertEquals(body7.error_type(), "error_bad_request");
        assertEquals(
                body7.details(),
                "'minlong' parameter needs to be less than or equal to 'maxlong' parameter");
    }
}