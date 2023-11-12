package edu.brown.cs.student.main.server.server_responses;

import edu.brown.cs.student.main.server.json_classes.FeatureCollection;

public record SuccessGeoJsonResponse(String type, FeatureCollection result, String date_time) {}
