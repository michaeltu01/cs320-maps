package edu.brown.cs.student.main.server_tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.brown.cs.student.main.server.handlers.LoadJsonHandler;

public class LoadJsonTests {

    private LoadJsonHandler loadJsonHandler;

    @BeforeEach
    public void setup() {
        this.loadJsonHandler = new LoadJsonHandler();
    }

    @Test
    public void testGeoJson() {
        Map<String, Object> json1 = new HashMap<>();
        try {
            json1 = loadJsonHandler.parseJson("src/test/java/edu/brown/cs/student/jsons/json1.txt");
        } catch (IOException e) {
            fail(e.getMessage());
        }

        Map<String, Object> expected = new HashMap<>();
        ArrayList<Double> coordinates = new ArrayList<>(List.of(125.6, 10.1));
        expected.put("type", "Location");
        expected.put("coordinates", coordinates);
        expected.put("name", "Dinagat Islands");

        assertEquals(json1.get("type"), expected.get("type"));
        assertEquals(json1.get("coordinates"), expected.get("coordinates"));
        assertEquals(json1.get("name"), expected.get("name"));
    }

    @Test
    public void testEmptyJson() {
        Map<String, Object> json1 = new HashMap<>();
        try {
            json1 = loadJsonHandler.parseJson("src/test/java/edu/brown/cs/student/jsons/emptyJson.txt");
        } catch (IOException e) {
            fail(e.getMessage());
        }

        Map<String, Object> expected = new HashMap<>();
        assertEquals(json1, expected);
    }

    @Test
    public void testNestedJsons() {
        Map<String, Object> json1 = new HashMap<>();
        try {
            json1 = loadJsonHandler.parseJson("src/test/java/edu/brown/cs/student/jsons/nestedJson.txt");
        } catch (IOException e) {
            fail(e.getMessage());
        }

        Map<String, Object> expected = new HashMap<>();
        Map<String, Object> nestedJson = new HashMap<>();
        nestedJson.put("name", "Dinagat Islands");
        ArrayList<Double> coordinates = new ArrayList<>(List.of(125.6, 10.1));
        expected.put("coordinates", coordinates);
        expected.put("properties", nestedJson);

        assertEquals(json1.get("type"), null);
        assertEquals(json1.get("coordinates"), expected.get("coordinates"));
        assertEquals(json1.get("properties"), expected.get("properties"));
    }
}
