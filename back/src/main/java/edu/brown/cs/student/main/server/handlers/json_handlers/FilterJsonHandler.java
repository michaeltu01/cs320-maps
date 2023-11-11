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

  private BBoxCache cache;

  public FilterJsonHandler() {
    this.cache = new BBoxCache(10, 10);
  }

  @Override
  public Object handle(Request request, Response response) {

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<SuccessGeoJsonResponse> successAdapter = moshi.adapter(SuccessGeoJsonResponse.class);
    JsonAdapter<ServerFailureResponse> failureAdapter = moshi.adapter(ServerFailureResponse.class);
    Map<String, Object> responseMap = new HashMap<>();

    try {
      // format: /loadjso?minlong=_&minlat=_&maxlong=_&maxlat
      String minLongStr = request.queryParams("minlong");
      String minLatStr = request.queryParams("minlat");
      String maxLongStr = request.queryParams("maxlong");
      String maxLatStr = request.queryParams("maxlat");

      // Check for null parameters
      ArrayList<String> params = new ArrayList<String>();
      params.add(minLongStr);
      params.add(minLatStr);
      params.add(maxLatStr);
      params.add(maxLongStr);

      boolean allParamsNonNull = params.stream().allMatch(param -> param != null);
      if (!allParamsNonNull) {
        throw new BadRequestException (
            "You are missing a parameter(s). Make sure you entered a value for all of the following parameters: minlong, minlat, maxlong, maxlat.");
      }

      Double minLong = Double.parseDouble(minLongStr); // expects a Double
      Double minLat = Double.parseDouble(minLatStr); // expects a Double
      Double maxLong = Double.parseDouble(maxLongStr); // expects a Double
      Double maxLat = Double.parseDouble(maxLatStr); // expects a Double

      if (minLong > maxLong) {
        throw new BadRequestException("'minlong' parameter needs to be less than or equal to 'maxlong' parameter");
      }
      if (minLat > maxLat) {
        throw new BadRequestException("'minlat' parameter needs to be less than or equal to 'maxlat' parameter");
      }

      // Filter JSON for areas with coordinates that all fall within the specified "boundary box"
      BoundaryBox bbox = new BoundaryBox(minLong, minLat, maxLong, maxLat);
      BBoxCacheResponse cacheResponse = this.cache.search(bbox);
      FeatureCollection filteredJson = cacheResponse.filteredJSON();
      String dateTime = cacheResponse.dateTime();

      return successAdapter.toJson(new SuccessGeoJsonResponse("success", filteredJson, dateTime));
    } catch (NumberFormatException e) {
      return failureAdapter.toJson(new ServerFailureResponse("error", "error_datasource", "Parameter must be a double"));
    } catch (BadRequestException e) {
      return failureAdapter.toJson(new ServerFailureResponse("error", "error_bad_request", e.getMessage()));
    } catch (DatasourceException e) {
      System.err.println(e.getCause());
      return failureAdapter.toJson(new ServerFailureResponse("error", "error_datasource", e.getMessage()));
    }
  }
}