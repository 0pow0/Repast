package util;

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
}

