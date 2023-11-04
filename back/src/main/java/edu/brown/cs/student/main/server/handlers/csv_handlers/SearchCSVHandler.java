package edu.brown.cs.student.main.server.handlers.csv_handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import edu.brown.cs.student.main.server.exceptions.BadJsonException;
import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import edu.brown.cs.student.main.csv.searcher.Searcher;

/**
 * SearchCSVHandler class which enables the end user to query for specific information in the
 * loaded CSV.
 */
public class SearchCSVHandler implements Route {

  LoadCSVHandler loadCSVHandler;
  List<List<String>> parsedImmutable;
  List<String> headerImmutable;

  private final String removeQuotes = "\"(.*?)\"";

  /**
   * Constructor which saves a reference to the LoadCSVHandler object.
   * @param loadCSVHandler reference to the loadCSVHandler object
   */
  public SearchCSVHandler(LoadCSVHandler loadCSVHandler){
    this.loadCSVHandler = loadCSVHandler;
  }

  /**
   * Constructor made for testing
   * @param immutableHeader immutable header object
   * @param immutableData immutable data object
   */
  public SearchCSVHandler(List<String> immutableHeader, List<List<String>> immutableData) {
    this.loadCSVHandler = null;
    this.headerImmutable = immutableHeader;
    this.parsedImmutable = immutableData;
  }

  /**
   * Handler for Search CSV which takes in the query data of value, index, and column and returns the
   * sought data.
   * @param request the value, index, and column of the search
   * @param response the result of the search
   * @return
   */
  @Override
  public Object handle(Request request, Response response) {
    // Grab parameters from API request
    String searchVal = request.queryParams("value"); // required; expects a String
    String index = request.queryParams("index"); // optional; expects an 0-based integer
    String colName = request.queryParams("column"); // optional; expects a String

    // Set up JSON adapters for response
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    try {
      if (this.loadCSVHandler != null) {
        this.parsedImmutable = this.loadCSVHandler.getReadOnlyCSVData();
        this.headerImmutable = this.loadCSVHandler.getReadOnlyHeaders();
      }

      // Verify input values
      if (searchVal == null) {
        throw new BadRequestException("'value' parameter missing");
      }
      // Search for the value
      Searcher searcher = new Searcher();
      List<List<String>> searchResults = new ArrayList<>();
      if (index == null && colName != null) {  // search by column name
        if (!headerExists(colName)) {
          throw new BadRequestException("Column name not found: " + colName
            + ". Here are the possible column names: " + headerImmutable.toString());
        }
        int colIndex = headerImmutable.indexOf(colName);
        searchResults = searcher.search(this.parsedImmutable, searchVal, colIndex);
      } else if (index != null && colName == null) { // search by column index
        int colIndex = Integer.parseInt(index);
        if (colIndex < 0 || colIndex >= parsedImmutable.get(0).size()) { // if index is out of bounds
          throw new BadRequestException("Column index out of bounds. The file has " + parsedImmutable.get(0).size() + " columns.");
        }
        searchResults = searcher.search(this.parsedImmutable, searchVal, colIndex);
      } else if (index != null && colName != null) { // user specified both (search by column index)
        int colIndex = Integer.parseInt(index);
        if (headerExists(colName) && colIndex != headerImmutable.indexOf(colName)) {
          throw new BadRequestException("Index (" + index + ") and column name (" + colName + ") does not match. "
              + "There are " + headerImmutable.size() + " columns: " + headerImmutable.toString());
        }
        searchResults = searcher.search(this.parsedImmutable, searchVal, colIndex);
      } else { // search the entire CSV file
        searchResults = searcher.search(this.parsedImmutable, searchVal);
      }

      if (searchResults.isEmpty() || searchResults == null) {
        throw new BadJsonException("No matching rows found.");
      }

      // Add the header row to the results
      if(this.headerImmutable != null && !this.headerImmutable.isEmpty()) {
        searchResults.add(0, this.headerImmutable);
      }

      // Produce success response
      responseMap.put("type", "success");
      responseMap.put("value", searchVal);
      if (index != null) {responseMap.put("index", index);}
      if (colName != null) {responseMap.put("column", colName);}
      //responseMap.put("results", searchResults.toString().replaceAll(removeQuotes, "$1"));
      responseMap.put("data", searchResults);
      //responseMap.put("data", this.parsedImmutable.toString().replaceAll(removeQuotes, "$1"));
      return adapter.toJson(responseMap);
    } catch (BadRequestException e) {
      responseMap.put("type", "error");
      responseMap.put("error_type", "error_bad_request");
      responseMap.put("details", e.getMessage());
      return adapter.toJson(responseMap);
    } catch (NumberFormatException e) {
      responseMap.put("type", "error");
      responseMap.put("error_type", "error_bad_request");
      responseMap.put("details", "Column index invalid: " + index);
      return adapter.toJson(responseMap);
    } catch (DatasourceException e) {
      responseMap.put("type", "error");
      responseMap.put("error_type", "error_datasource");
      responseMap.put("details", e.getMessage());
      return adapter.toJson(responseMap);
    } catch (BadJsonException e) {
      responseMap.put("type", "error");
      responseMap.put("error_type", "error_bad_json");
      responseMap.put("details", e.getMessage());
      return adapter.toJson(responseMap);
    }
  }

  /**
   * A private helper method that checks whether the given header exists in the loaded CSV
   * @return true, if the header exists; false, otherwise
   */
  private boolean headerExists(String header) throws BadRequestException {
    // Check if a header row even exists
    if (this.headerImmutable == null || this.headerImmutable.isEmpty()) {
      throw new BadRequestException("Loaded CSV does not contain any headers.");
    }
    for (String str : headerImmutable) {
      if (str.toLowerCase().equals(header.toLowerCase())) {
        return true;
      }
    }
    return false;
  }

}
