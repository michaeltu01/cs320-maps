package edu.brown.cs.student.main.server.handlers.json_handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.Server;
import edu.brown.cs.student.main.server.exceptions.BadJsonException;
import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.json_classes.Feature;
import edu.brown.cs.student.main.server.json_classes.FeatureCollection;
import edu.brown.cs.student.main.server.json_classes.FeatureProperties;
import edu.brown.cs.student.main.server.server_responses.ServerFailureResponse;
import edu.brown.cs.student.main.server.server_responses.SuccessGeoJsonResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchJsonHandler implements Route {

  private FeatureCollection json;
  private List<String> searchHistory;

  public SearchJsonHandler() {
    this.json = new FeatureCollection(null, null);
    this.searchHistory = new ArrayList<String>();
  }

  @Override
  public Object handle(Request request, Response response) {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<SuccessGeoJsonResponse> successAdapter = moshi.adapter(SuccessGeoJsonResponse.class);
    JsonAdapter<ServerFailureResponse> failureAdapter = moshi.adapter(ServerFailureResponse.class);
    Map<String, Object> responseMap = new HashMap<>();

    try {
      // format: /searchjson?search=_
      String searchVal = request.queryParams("search");
      this.json = Server.getSharedJson();

      if (this.json == null) {
        throw new DatasourceException("please load in a json before searching");
      }
      if (searchVal == null) {
        throw new BadRequestException("'value' parameter missing");
      }

      List<Feature> searches = areaQuery(searchVal);
      if (searches == null || searches.isEmpty()) {
        throw new BadJsonException("No areas found with the given keyword: " + searchVal);
      }
      FeatureCollection featureCollection = new FeatureCollection("FeatureCollection", searches);

      return successAdapter.toJson(new SuccessGeoJsonResponse("success", featureCollection, null));
    } catch (BadRequestException e) {
      return failureAdapter.toJson(new ServerFailureResponse("error", "error_bad_request", e.getMessage()));
    } catch (DatasourceException e) {
      return failureAdapter.toJson(new ServerFailureResponse("error", "error_datasource", e.getMessage()));
    } catch (BadJsonException e) {
      return failureAdapter.toJson(new ServerFailureResponse("error", "error_bad_json", e.getMessage()));
    }
  }

  private List<Feature> areaQuery(String keyword) throws BadJsonException {
    List<Feature> filteredFeatures = new ArrayList<>();
    try {
      for (Feature feature : this.json.features()) {
        if (feature.geometry() != null) {
          FeatureProperties properties = feature.properties();
          Map<String, String> area = properties.area_description_data();
          for (String val : area.values()) {
            if (val.toLowerCase().contains(keyword.toLowerCase())) {
              filteredFeatures.add(feature);
            }
          }
        }
      }
    } catch (Exception e) {
      throw new BadJsonException("Keyword not found", e.getCause());
    }
    this.searchHistory.add(keyword);
    return filteredFeatures;
  }
}
