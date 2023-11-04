package edu.brown.cs.student.main.server.json_classes;

import java.util.Map;

public record FeatureProperties(String state, String city, String name, String holc_id, String holc_grade, int neighborhood_id, Map<String, String> areaDescriptionData) {

}
