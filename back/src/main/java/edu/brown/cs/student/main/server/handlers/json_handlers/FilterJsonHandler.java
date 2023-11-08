package edu.brown.cs.student.main.server.handlers.json_handlers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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
import spark.Request;
import spark.Response;
import spark.Route;

public class FilterJsonHandler implements Route{

    private final FeatureCollection json;
    private BBoxCache cache;
    
    public FilterJsonHandler() {
        this.json = Server.getSharedJson();
        this.cache = new BBoxCache(10, 10);
    }

    @Override
    public Object handle(Request request, Response response) {

        // format: /loadjson?minlong=_&minlat=_&maxlong=_&maxlat
        Double minLong = Double.parseDouble(request.queryParams("minlong")); // expects a Double
        Double minLat = Double.parseDouble(request.queryParams("minlat")); // expects a Double
        Double maxLong = Double.parseDouble(request.queryParams("maxlong")); // expects a Double
        Double maxLat = Double.parseDouble(request.queryParams("maxlat")); // expects a Double

        Moshi moshi = new Moshi.Builder().build();
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
        Map<String, Object> responseMap = new HashMap<>();

        try {
            // Check for null parameters
            ArrayList<Double> params = new ArrayList<Double>(List.of(minLong, minLat, maxLong, maxLat));
            boolean allParamsNonNull = params.stream().allMatch(param -> param != null);
            if (!allParamsNonNull) {
                throw new BadRequestException("You are missing a parameter(s). Make sure you entered a value for all of the following parameters: minlong, minlat, maxlong, maxlat.");
            }

            // Filter JSON for areas with coordinates that all fall within the specified "boundary box"
            BoundaryBox bbox = new BoundaryBox(minLong, minLat, maxLong, maxLat);
            BBoxCacheResponse cacheResponse = this.cache.search(bbox);
            FeatureCollection filteredJson = cacheResponse.filteredJSON();
            String dateTime = cacheResponse.dateTime();

            responseMap.put("type", "success");
            responseMap.put("geojson", filteredJson);
            responseMap.put("date/time", dateTime);
            return adapter.toJson(responseMap);
        } catch (BadRequestException e) {
            responseMap.put("type", "error");
            responseMap.put("error_type", "error_bad_request");
            responseMap.put("details", e.getMessage());
            return adapter.toJson(responseMap);
        } catch (DatasourceException e) {
            responseMap.put("type", "error");
            responseMap.put("error_type", "error_datasource");
            responseMap.put("details", e.getMessage());
            System.err.println(e.getCause());
            return adapter.toJson(responseMap);
        }
    }
    
    // private FeatureCollection filterByBoundaryBox(FeatureCollection json, BoundaryBox bbox) throws DatasourceException {
    //     ArrayList<Feature> filteredFeatures = new ArrayList<Feature>();

    //     List<Feature> featureCollection = json.features();
    //     if (featureCollection == null) {
    //         throw new DatasourceException("Feature collection is null.");
    //     }
    //     try {
    //         for (Feature feature : featureCollection) {
    //             if (feature.geometry() != null) {
    //                 List<List<List<List<Double>>>> coordinatesProperty = feature.geometry().coordinates();
    //                 if (coordinatesProperty != null) {
    //                     if (allLieInBoundaryBox(coordinatesProperty, bbox)) {
    //                         filteredFeatures.add(feature);
    //                     }
    //                 }
    //             }
    //         }
    //         return new FeatureCollection(json.type(), filteredFeatures);
    //     } catch (Exception e) {
    //         System.err.println(e.getMessage());
    //         throw e;
    //     }
    // }

    // private boolean allLieInBoundaryBox(List<List<List<List<Double>>>> coordinatesList4D, BoundaryBox bbox) {
    //     for (List<List<List<Double>>> coordinatesList3D : coordinatesList4D) {
    //         for (List<List<Double>> coordinatesList2D : coordinatesList3D) {
    //             for (List<Double> coordinate : coordinatesList2D) {
    //                 if (!bbox.contains(coordinate)) {
    //                     return false;
    //                 }
    //             }
    //         }
    //     }
    //     return true;
    // }
}
