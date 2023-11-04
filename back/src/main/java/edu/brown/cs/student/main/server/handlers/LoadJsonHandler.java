package edu.brown.cs.student.main.server.handlers;

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

import okio.Buffer;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadJsonHandler implements Route{

    @Override
    public Object handle(Request request, Response response) throws Exception {
        // [TODO] Implement this handler once an endpoint is served
        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }

    public Map<String, Object> parseJson(String filePath) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
        Map<String, Object> json = new HashMap<String, Object>();

        try (Buffer buff = new Buffer()) {
            json = adapter.fromJson(buff.readFrom(new FileInputStream(new File(filePath))));
        } catch (IOException e) {
            System.err.println(e.getStackTrace());
            throw e;
        }

        return json;
    }
    
}
