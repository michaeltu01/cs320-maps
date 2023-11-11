package edu.brown.cs.student.main.server.handlers.json_handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.BBoxCache;
import edu.brown.cs.student.main.server.Server;
import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.json_classes.BBoxCacheResponse;
import edu.brown.cs.student.main.server.json_classes.BoundaryBox;
import edu.brown.cs.student.main.server.json_classes.FeatureCollection;
import edu.brown.cs.student.main.server.server_responses.ServerFailureResponse;
import edu.brown.cs.student.main.server.server_responses.SuccessGeoJsonResponse;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class FilterJsonHandler implements Route {

  private final FeatureCollection json;
  private BBoxCache cache;

  public FilterJsonHandler() {
    this.json = Server.getSharedJson();
    this.cache = new BBoxCache(10, 10);
  }

  @Override
  public Object handle(Request request, Response response) {

    // format: /loadjso?minlong=_&minlat=_&maxlong=_&maxlat
    Double minLong = Double.parseDouble(request.queryParams("minlong")); // expects a Double
    Double minLat = Double.parseDouble(request.queryParams("minlat")); // expects a Double
    Double maxLong = Double.parseDouble(request.queryParams("maxlong")); // expects a Double
    Double maxLat = Double.parseDouble(request.queryParams("maxlat")); // expects a Double

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<SuccessGeoJsonResponse> successAdapter = moshi.adapter(SuccessGeoJsonResponse.class);
    JsonAdapter<ServerFailureResponse> failureAdapter = moshi.adapter(ServerFailureResponse.class);
    Map<String, Object> responseMap = new HashMap<>();

    try {
      // Check for null parameters
      ArrayList<Double> params = new ArrayList<Double>(List.of(minLong, minLat, maxLong, maxLat));
      boolean allParamsNonNull = params.stream().allMatch(param -> param != null);
      if (!allParamsNonNull) {
        throw new BadRequestException(
            "You are missing a parameter(s). Make sure you entered a value for all of the following parameters: minlong, minlat, maxlong, maxlat.");
      }

      // Filter JSON for areas with coordinates that all fall within the specified "boundary box"
      BoundaryBox bbox = new BoundaryBox(minLong, minLat, maxLong, maxLat);
      BBoxCacheResponse cacheResponse = this.cache.search(bbox);
      FeatureCollection filteredJson = cacheResponse.filteredJSON();
      String dateTime = cacheResponse.dateTime();

//      responseMap.put("type", "success");
//      responseMap.put("result", filteredJson);
//      responseMap.put("date_time", dateTime);
      return successAdapter.toJson(new SuccessGeoJsonResponse("success", filteredJson, dateTime));
    } catch (BadRequestException e) {
//      responseMap.put("type", "error");
//      responseMap.put("error_type", "error_bad_request");
//      responseMap.put("details", e.getMessage());
      return failureAdapter.toJson(new ServerFailureResponse("error", "error_bad_request", e.getMessage()));
    } catch (DatasourceException e) {
//      responseMap.put("type", "error");
//      responseMap.put("error_type", "error_datasource");
//      responseMap.put("details", e.getMessage());
      System.err.println(e.getCause());
      return failureAdapter.toJson(new ServerFailureResponse("error", "error_datasource", e.getMessage()));
    }
  }
}