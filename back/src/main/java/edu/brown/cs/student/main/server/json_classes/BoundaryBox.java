package edu.brown.cs.student.main.server.json_classes;

import java.util.List;

public record BoundaryBox(Double minLong, Double minLat, Double maxLong, Double maxLat) {
  public boolean contains(List<Double> coordinate) {
    Double lng = coordinate.get(0);
    Double lat = coordinate.get(1);

    if ((minLong <= lng && lng <= maxLong) && (minLat <= lat && lat <= maxLat)) {
      return true;
    } else {
      return false;
    }
  }
}
