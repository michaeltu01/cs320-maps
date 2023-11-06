package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.Cache;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.census.CensusData;
import edu.brown.cs.student.main.server.census.CensusDataSource;
import edu.brown.cs.student.main.server.exceptions.BadJsonException;
import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * BroadbandHandler class which returns the broadband data for the specified county and state.
 */
public class BroadbandHandler implements Route {

  private final CensusDataSource state;
  private Cache cache;

  /**
   * Constructor which stores an object of the CensusDataSource.
   * @param state an object of the data source for retrieval
   */
  public BroadbandHandler(CensusDataSource state){
    this.state = state;
    this.cache = new Cache(10, 10, this.state);

  }

  /**
   * Handle method which takes in a state and county name and returns the broadband percentange
   * of the specified location.
   * @param request state name and county name
   * @param response broadband percentage
   * @return
   */
  @Override
  public Object handle(Request request, Response response) {

    //Get the state and the county from the request
    String state = request.queryParams("state");
    String county = request.queryParams("county");

    // Prepare to send a reply
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    try {
      if (state == null) {
        throw new BadRequestException("'state' parameter missing");
      }
      if (county == null) {
        throw new BadRequestException("'county' parameter missing");
      }
      CensusData data = this.cache.search(state, county);

      responseMap.put("type", "success");
      responseMap.put("broadband", data.broadbandPct());
      responseMap.put("date/time", data.dateTime());
      responseMap.put("county/state", county + ", " + state);
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
    } catch (BadRequestException e) {
      responseMap.put("type", "error");
      responseMap.put("error_type", "error_bad_request");
      responseMap.put("details", e.getMessage());
      return adapter.toJson(responseMap);
    }
  }
}
