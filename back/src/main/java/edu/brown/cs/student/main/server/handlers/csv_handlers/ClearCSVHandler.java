package edu.brown.cs.student.main.server.handlers.csv_handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** This is a class that handles calls to clear the currently loaded CSV file. */
public class ClearCSVHandler implements Route {

  private LoadCSVHandler loadCSVHandler;

  /**
   * Constructor for clearCSVHandler
   *
   * @param loadCSVHandler shared state so that this class can clear the loaded CSV file
   */
  public ClearCSVHandler(LoadCSVHandler loadCSVHandler) {
    this.loadCSVHandler = loadCSVHandler;
  }

  /**
   * Handler for the clearCSV endpoint
   *
   * @param request user request to the loadcsv endpoint
   * @param response
   * @return JSON response
   */
  @Override
  public Object handle(Request request, Response response) {
    // Set up JSON adapters for response
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    this.loadCSVHandler.clear();

    responseMap.put("type", "success");
    responseMap.put("details", "Loaded files cleared.");

    return adapter.toJson(responseMap);
  }
}
