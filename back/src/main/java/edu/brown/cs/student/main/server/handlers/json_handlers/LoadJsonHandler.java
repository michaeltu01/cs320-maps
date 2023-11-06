package edu.brown.cs.student.main.server.handlers.json_handlers;

import com.beust.jcommander.Parameterized;
import edu.brown.cs.student.main.server.json_classes.FeatureProperties;
import edu.brown.cs.student.main.server.json_classes.Geometry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import edu.brown.cs.student.main.server.Server;
import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.json_classes.Feature;
import edu.brown.cs.student.main.server.json_classes.FeatureCollection;
import okio.Buffer;
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
                System.out.println("ok ok ok");
                filepath = "back/data/geodata/fullDownload.json";
                Server.setSharedJson(parseJson(filepath));
            } else {
                Server.setSharedJson(parseJson(filepath));
            }
            responseMap.put("type", "success");
            responseMap.put("filepath", filepath);
            responseMap.put("details", "file loaded successfully");
            return adapter.toJson(responseMap);
        } /*catch (BadRequestException e) {
            responseMap.put("type", "error");
            responseMap.put("error_type", "error_bad_request");
            responseMap.put("details", e.getMessage());
            return adapter.toJson(responseMap);
        }*/ catch (DatasourceException e) {
            responseMap.put("type", "error");
            responseMap.put("error_type", "error_datasource");
            responseMap.put("details", e.getMessage());
            return adapter.toJson(responseMap);
        }
    }

    public FeatureCollection parseJson(String filePath) throws DatasourceException {
        System.out.println("entered parseJson");
        Moshi moshi = new Moshi.Builder().build();
        try {
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }
        System.out.println("making adapter");
        JsonAdapter<FeatureCollection> adapter = moshi.adapter(FeatureCollection.class);
        try {
            // JsonAdapter<List<Feature>> featureAdapter = moshi.adapter(
            //     Types.newParameterizedType(List.class, Feature.class));
            // JsonAdapter<Geometry> geometryAdapter = moshi.adapter(Geometry.class);
            // JsonAdapter<FeatureProperties> featurePropertiesAdapter = moshi.adapter(
            //     FeatureProperties.class);
            // JsonAdapter<Map<String, String>> areaDescriptionAdapter = moshi.adapter(
            //     Types.newParameterizedType(Map.class, String.class, String.class));
            // JsonAdapter<List<List<List<List<Double>>>>> coordinatesAdapter = moshi.adapter(Types.newParameterizedType(List.class, List.class, List.class, List.class, Double.class));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }
        System.out.println("making a feature collection");
        FeatureCollection json = null;
        System.out.println("try....");
        try {
            BufferedSource source = Okio.buffer(Okio.source(new File(filePath)));
            // System.out.println("parsing...");
            json = adapter.fromJson(source);
            source.close();
        } catch (IOException e) {
            // System.out.println("catching...");
            throw new DatasourceException(e.getMessage(), e.getCause());
        } catch (JsonDataException e) {
            System.err.println(e.getMessage());
            throw e;
        }

        if (json == null || json.featureCollection() == null || json.type() == null) {
            throw new DatasourceException("JSON (or one of its fields) parsed to null");
        }

        System.out.println(json);
        return json;
    }
    
}
