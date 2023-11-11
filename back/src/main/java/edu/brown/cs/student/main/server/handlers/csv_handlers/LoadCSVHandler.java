package edu.brown.cs.student.main.server.handlers.csv_handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.csv.parser.CSVParser;
import edu.brown.cs.student.main.csv.parser.ListCreator;
import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class for handling the loading of CSV data */
public class LoadCSVHandler implements Route {

  private ListCreator listCreator;
  private FileReader fileReader;
  private String CSVFileName;
  private List<List<String>> parsedCSVData;
  private List<String> headers = null;

  public LoadCSVHandler() {}

  /**
   * Handler for the loadcsv endpoint
   *
   * @param request user request to the loadcsv endpoint
   * @param response
   * @return JSON response
   */
  @Override
  public Object handle(Request request, Response response) {
    // Grabbing parameters from user API request
    String incomingFileName = request.queryParams("filename"); // expects a String
    String hasHeaders = request.queryParams("headers"); // expects either "true" or "false"
    this.CSVFileName = incomingFileName;

    // Set up JSON response
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    try {
      // Verify parameters
      if (incomingFileName == null) {
        throw new BadRequestException("Missing 'filename' parameter");
      }
      if (hasHeaders == null) {
        throw new BadRequestException("Missing 'headers' parameter");
      }
      if (!hasHeaders.toLowerCase().equals("true") && !hasHeaders.toLowerCase().equals("false")) {
        throw new BadRequestException("Invalid header parameter: " + hasHeaders);
      }

      // Helper method that assigns values to parsedCSVData and headers
      if (hasHeaders.toLowerCase().equals("true")) {
        this.parseCSV(true);
      } else {
        this.parseCSV(false);
      }

      // Produce success response
      responseMap.put("type", "success");
      responseMap.put("filepath", this.CSVFileName);
      responseMap.put("headers", hasHeaders);
      responseMap.put("details", "file loaded successfully");
      return adapter.toJson(responseMap);
    } catch (DatasourceException e) {
      responseMap.put("type", "error");
      responseMap.put("error_type", "error_datasource");
      responseMap.put("details", e.getMessage());
      return adapter.toJson(responseMap);
    } catch (BadRequestException e) {
      responseMap.put("type", "error");
      responseMap.put("error_type", "error_bad_request");
      responseMap.put("details", e.getMessage());
      return adapter.toJson(responseMap);
    }
  }

  /**
   * A public method that returns the CSV data as an immutable list (read-only)
   *
   * @return read-only version of CSV data
   */
  public List<List<String>> getReadOnlyCSVData() throws DatasourceException {
    if (this.parsedCSVData == null) {
      throw new DatasourceException("No file has been loaded.");
    }
    return Collections.unmodifiableList(this.parsedCSVData);
  }

  /**
   * A public method that returns the headers of the CSV file as an immutable list (read-only)
   *
   * @return read-only List of CSV headers
   */
  public List<String> getReadOnlyHeaders() {
    if (this.headers == null) {
      return null;
    }
    return Collections.unmodifiableList(this.headers);
  }

  /**
   * A public method that returns the entire CSV file (parsed) with headers as an immutable list
   * (read-only)
   *
   * @return the entire CSV file with headers
   * @throws DatasourceException when CSV has not been parsed
   */
  public List<List<String>> getReadOnlyCSVFile() throws DatasourceException {
    if (this.headers == null) {
      return getReadOnlyCSVData();
    }
    if (this.parsedCSVData == null) {
      throw new DatasourceException("No file has been loaded.");
    }
    List<List<String>> csvData = new ArrayList<List<String>>(this.parsedCSVData);
    csvData.add(0, this.headers);
    return Collections.unmodifiableList(csvData);
  }

  /** This function clears the currently loaded CSV data for testing on the front end. */
  public void clear() {
    this.headers = null;
    this.parsedCSVData = null;
  }

  /**
   * A private helper method that parses the CSV into a 2D List of Strings
   *
   * @return CSV data as 2D List of Strings
   * @throws DatasourceException if filename not found
   */
  private void parseCSV(Boolean hasHeaders) throws DatasourceException {
    try {
      this.fileReader = new FileReader(this.CSVFileName); // back/data/*.csv
      this.listCreator = new ListCreator();
      CSVParser<List<String>> fileCSVParser = new CSVParser<List<String>>(fileReader, listCreator);
      this.parsedCSVData = fileCSVParser.parseCSV();
      if (hasHeaders) {
        this.headers = parsedCSVData.remove(0);
      } else {
        this.headers = null;
      }
    } catch (FileNotFoundException e) {
      System.out.println(e.getStackTrace());
      throw new DatasourceException("File path not found: " + this.CSVFileName);
    }
  }
}
