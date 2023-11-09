package edu.brown.cs.student.main.server.handlers.json_handlers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import edu.brown.cs.student.main.server.BBoxCache;
import edu.brown.cs.student.main.server.Server;
import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.json_classes.Feature;
import edu.brown.cs.student.main.server.json_classes.FeatureCollection;
import edu.brown.cs.student.main.server.json_classes.FeatureProperties;
import okio.BufferedSource;
import okio.Okio;
import spark.Request;
import spark.Response;
import spark.Route;


public class SearchJsonHandler implements Route {

    private FeatureCollection json;

    public SearchJsonHandler() {
        this.json = new FeatureCollection(null, null);
    }
    
    @Override
    public Object handle(Request request, Response response) {
        // format: /searchjson?search=_
        String searchVal = request.queryParams("search");

        Moshi moshi = new Moshi.Builder().build();
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
        Map<String, Object> responseMap = new HashMap<>();

        this.json = Server.getSharedJson();

        try {
            if (this.json == null) {
                throw new IOException("please load in a json before searching");
            }
            if (searchVal == null) {
                throw new BadRequestException("'value' parameter missing");
            }

            Object searches = areaQuery(searchVal);

            responseMap.put("type", "success");
            responseMap.put("result", searches);

            return adapter.toJson(responseMap);

        } catch (BadRequestException e) {
            responseMap.put("type", "error");
            responseMap.put("error_type", "missing val");
            return adapter.toJson(responseMap);
        } catch (IOException e) {
            responseMap.put("type", "error");
            responseMap.put("error_type", "unloaded json");
            return adapter.toJson(responseMap);
        }
    }

    private Object areaQuery(String keyword){
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
            System.out.println(e.getMessage());
            throw e;
        }
        return filteredFeatures;
    }
}
