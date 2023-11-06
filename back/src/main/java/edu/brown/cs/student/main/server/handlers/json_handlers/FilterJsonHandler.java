package edu.brown.cs.student.main.server.handlers.json_handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.xml.crypto.dsig.spec.XPathType.Filter;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import edu.brown.cs.student.main.server.Server;
import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.json_classes.BoundaryBox;
import edu.brown.cs.student.main.server.json_classes.Feature;
import edu.brown.cs.student.main.server.json_classes.FeatureCollection;
import spark.Request;
import spark.Response;
import spark.Route;

public class FilterJsonHandler implements Route{

    private final FeatureCollection json;
    
    public FilterJsonHandler() {
        this.json = Server.getSharedJson();
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
            System.out.println("params: " + params.toString());
            boolean allParamsNonNull = params.stream().allMatch(param -> param != null);
            if (!allParamsNonNull) {
                throw new BadRequestException("You are missing a parameter(s). Make sure you entered a value for all of the following parameters: minlong, minlat, maxlong, maxlat.");
            }

            // Filter JSON for areas with coordinates that all fall within the specified "boundary box"
            FeatureCollection json = Server.getSharedJson();
            System.out.println(json);
            BoundaryBox bbox = new BoundaryBox(minLong, minLat, maxLong, maxLat);
            FeatureCollection filteredJson = filterByBoundaryBox(json, bbox);
            System.out.println(filteredJson);

            responseMap.put("type", "success");
            responseMap.put("geojson", filteredJson);
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
            return adapter.toJson(responseMap);
        }
    }
    
    private FeatureCollection filterByBoundaryBox(FeatureCollection json, BoundaryBox bbox) throws DatasourceException {
        ArrayList<Feature> filteredFeatures = new ArrayList<Feature>();

        List<Feature> featureCollection = json.features();
        if (featureCollection == null) {
            throw new DatasourceException("Feature collection is null.");
        }
        for (Feature feature : featureCollection) {
            List<List<List<List<Double>>>> coordinatesProperty = feature.geometry().coordinates();
            if (coordinatesProperty.size() != 1 && coordinatesProperty.get(0).size() != 1) {
                throw new DatasourceException("Cannot parse GeoJSON: invalid structure of feature coordinates");
            }
            List<List<Double>> coordinatesList = feature.geometry().coordinates().get(0).get(0);
            if (allLieInBoundaryBox(coordinatesList, bbox)) {
                filteredFeatures.add(feature);
            }
        }
        return new FeatureCollection(json.type(), filteredFeatures);
    }

    private boolean allLieInBoundaryBox(List<List<Double>> coordinates, BoundaryBox bbox) {
        for (List<Double> coordinate : coordinates) {
            if (!bbox.contains(coordinate)) {
                return false;
            }
        }
        return true;
    }
}
