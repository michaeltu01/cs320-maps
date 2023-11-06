package edu.brown.cs.student.main.server.json_classes;

import java.util.List;

public record FeatureCollection(String type, List<Feature> featureCollection) {}
