package edu.brown.cs.student.main.server.json_classes;

import java.util.List;

public record Geometry(String type, List<List<List<List<Double>>>> coordinates) {}
