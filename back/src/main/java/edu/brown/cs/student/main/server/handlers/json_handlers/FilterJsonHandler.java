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

import javax.xml.crypto.dsig.spec.XPathType.Filter;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import edu.brown.cs.student.main.server.Server;
import spark.Request;
import spark.Response;
import spark.Route;

public class FilterJsonHandler implements Route{

    private final Map<String, Object> json;
    
    public FilterJsonHandler() {
        this.json = Server.getSharedJson();
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {

        // format: /loadjson?minlong=_&minlat=_&maxlong=_&maxlat
        String minlong = request.queryParams("minlong"); // expects a Double
        String minlat = request.queryParams("minlat"); // expects a Double
        String maxlong = request.queryParams("maxlong"); // expects a Double
        String maxlat = request.queryParams("maxlat"); // expects a Double

        Moshi moshi = new Moshi.Builder().build();
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
        Map<String, Object> responseMap = new HashMap<>();

        for 


        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }    
}
