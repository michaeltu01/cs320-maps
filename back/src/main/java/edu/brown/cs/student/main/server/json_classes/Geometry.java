package edu.brown.cs.student.main.server.json_classes;

import java.util.ArrayList;

public record Geometry(String type, ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> coordinates) {}
