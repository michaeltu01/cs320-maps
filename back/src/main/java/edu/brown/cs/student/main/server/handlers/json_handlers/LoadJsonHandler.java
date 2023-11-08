package edu.brown.cs.student.main.server.handlers.json_handlers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import edu.brown.cs.student.main.server.Server;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.json_classes.FeatureCollection;
import okio.BufferedSource;
import okio.Okio;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadJsonHandler implements Route{

    @Override
    public Object handle(Request request, Response response) {
        String filepath = request.queryParams("filepath");

        Moshi moshi = new Moshi.Builder().build();
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
        Map<String, Object> responseMap = new HashMap<>();

        try {
            if (filepath == null) {
                filepath = "back/data/geodata/fullDownload.json";
                Server.setSharedJson(parseJson(filepath));
            } else {
                Server.setSharedJson(parseJson(filepath));
            }
            responseMap.put("type", "success");
            responseMap.put("filepath", filepath);
            responseMap.put("details", "file loaded successfully");
            return adapter.toJson(responseMap);
        } catch (DatasourceException e) {
            responseMap.put("type", "error");
            responseMap.put("error_type", "error_datasource");
            responseMap.put("details", e.getMessage());
            return adapter.toJson(responseMap);
        }
    }

    public FeatureCollection parseJson(String filePath) throws DatasourceException {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<FeatureCollection> adapter = moshi.adapter(FeatureCollection.class);
        FeatureCollection json = null;
        try {
            BufferedSource source = Okio.buffer(Okio.source(new File(filePath)));
            json = adapter.fromJson(source);
            source.close();
        } catch (IOException e) {
            throw new DatasourceException(e.getMessage(), e.getCause());
        } catch (JsonDataException e) {
            System.err.println(e.getMessage());
            throw e;
        }

        if (json == null || json.features() == null || json.type() == null) {
            throw new DatasourceException("JSON (or one of its fields) parsed to null");
        }

        return json;
    }
    
}