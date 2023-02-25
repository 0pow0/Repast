package util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;

public class Utils {
  public static double calcDistance(double lat1, double lng1,
    double lat2, double lng2) {
    SpatialContext ctx = SpatialContext.GEO;
    Point startPoint = ctx.getShapeFactory().pointLatLon(lat1, lng1);
    Point endPoint = ctx.getShapeFactory().pointLatLon(lat2, lng2);
    double distanceInDegrees = ctx.calcDistance(startPoint, endPoint);
    double distanceInKm = distanceInDegrees * DistanceUtils.DEG_TO_KM;
    return distanceInKm * 1000.0;
  }

  // Returns all binary combinations of length n with k 1s
  public static ArrayList<String> getCombinations(int n, int k) {
    ArrayList<String> result = new ArrayList<>();
    StringBuilder combination = new StringBuilder();
    generateCombinations(n, k, 0, combination, result);
    return result;
  }

  private static void generateCombinations(int n, int k, int index,
    StringBuilder combination, ArrayList<String> result) {
    if (combination.length() == n) {
      if (k == 0)
        result.add(combination.toString());
      return;
    }

    if (n - index < k)
      return;
    
    combination.append("0");
    generateCombinations(n, k, index + 1, combination, result);
    combination.deleteCharAt(combination.length() - 1);
    if (k > 0) {
      combination.append("1");
      generateCombinations(n, k - 1, index + 1, combination, result);
      combination.deleteCharAt(combination.length() - 1);
    }
  }
}

