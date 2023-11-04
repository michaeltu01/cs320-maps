package edu.brown.cs.student.main.server.handlers.json_handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import edu.brown.cs.student.main.server.Server;
import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import okio.Buffer;
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
            //System.out.println(Server.getSharedJson());
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

    public Map<String, Object> parseJson(String filePath) throws DatasourceException {
        Moshi moshi = new Moshi.Builder().build();
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
        Map<String, Object> json = new HashMap<String, Object>();

        try (Buffer buff = new Buffer()) {
            json = adapter.fromJson(buff.readFrom(new FileInputStream(new File(filePath))));
        } catch (IOException e) {
            throw new DatasourceException(e.getMessage(), e.getCause());
        }

        return json;
    }
    
}
