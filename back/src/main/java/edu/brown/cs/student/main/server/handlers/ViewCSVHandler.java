package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * ViewCSVHandler class which enables the end user to see the CSV with a query.
 */
public class ViewCSVHandler implements Route {

  LoadCSVHandler loadCSVHandler;
  List<List<String>> parsedCSV;

  private final String removeQuotes = "\"(.*?)\"";

  /**
   * Constructor for ViewCSVHandler which stores an object of loadCSV.
   * @param loadCSVHandler
   */
  public ViewCSVHandler(LoadCSVHandler loadCSVHandler){
    this.loadCSVHandler = loadCSVHandler;
  }

  /**
   * Handler method for ViewCSVHandler which returns the CSV as a Json.
   * @param request nothing for view
   * @param response the csv file that is already loaded
   * @return
   */
  @Override
  public Object handle(Request request, Response response) {
    // Set up JSON adapters for response
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();
    try {
      this.parsedCSV = this.loadCSVHandler.getReadOnlyCSVFile();
      responseMap.put("type", "success");
      // responseMap.put("data", this.parsedImmutable.toString().replaceAll(removeQuotes, "$1"));
      responseMap.put("data", this.parsedCSV);
      return adapter.toJson(responseMap);
    } catch (DatasourceException e) {
      responseMap.put("type", "error");
      responseMap.put("error_type", "error_datasource");
      responseMap.put("details", e.getMessage());
      return adapter.toJson(responseMap);
    }
  }

}
