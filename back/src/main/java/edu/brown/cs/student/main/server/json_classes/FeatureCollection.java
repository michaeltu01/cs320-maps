package edu.brown.cs.student.main.server.json_classes;

import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import java.util.ArrayList;
import java.util.List;

// public record FeatureCollection(
//   @Json(name = "type") String type,
//   @Json(name = "features") List<GeoJsonFeature> features) {

//     public record GeoJsonFeature(
//       @Json(name = "type") String type,
//       @Json(name = "geometry") Geometry geometry,
//       @Json(name = "properties") Map<String, Object> properties) {

//         public record Geometry(
//           @Json(name = "type") String type,
//           @Json(name = "coordinates") List<List<List<List<Double>>>> coordinates) {}

//       }
//   }

public record FeatureCollection(String type, List<Feature> features) {
  public FeatureCollection filterByBoundaryBox(BoundaryBox bbox) throws DatasourceException {
    ArrayList<Feature> filteredFeatures = new ArrayList<Feature>();

    List<Feature> featureCollection = this.features();
    if (featureCollection == null) {
      throw new DatasourceException("Feature collection is null.");
    }
    try {
      for (Feature feature : featureCollection) {
        if (feature.geometry() != null) {
          List<List<List<List<Double>>>> coordinatesProperty = feature.geometry().coordinates();
          if (coordinatesProperty != null) {
            if (allLieInBoundaryBox(coordinatesProperty, bbox)) {
              filteredFeatures.add(feature);
            }
          }
        }
      }
      return new FeatureCollection(this.type(), filteredFeatures);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      throw e;
    }
  }

  private boolean allLieInBoundaryBox(
      List<List<List<List<Double>>>> coordinatesList4D, BoundaryBox bbox) {
    for (List<List<List<Double>>> coordinatesList3D : coordinatesList4D) {
      for (List<List<Double>> coordinatesList2D : coordinatesList3D) {
        for (List<Double> coordinate : coordinatesList2D) {
          if (!bbox.contains(coordinate)) {
            return false;
          }
        }
      }
    }
    return true;
  }
}
