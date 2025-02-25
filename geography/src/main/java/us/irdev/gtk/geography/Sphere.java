package us.irdev.gtk.geography;

import us.irdev.gtk.xyw.Tuple;

public class Sphere {
    public static final double EARTH_RADIUS_MILES = (3963.1906 + 3949.9028) / 2.0;

    private final double radius;

    public Sphere(double radius) {
        this.radius = radius;
    }

    public Sphere() {
        this(EARTH_RADIUS_MILES);
    }

    public static double greatArcLength (Tuple ta, Tuple tb, double radius) {
        // convert degrees to radians
        double rLatA = Math.toRadians(ta.y);
        double rLonA = Math.toRadians(ta.x);
        double rLatB = Math.toRadians(tb.y);
        double rLonB = Math.toRadians(tb.x);

        // compute deltas
        double deltaLat = rLatB - rLatA;
        double deltaLon = rLonB - rLonA;

        // use the Haversine formula to compute the length of a great arc between two points
        double a = Math.pow(Math.sin(deltaLat / 2), 2) + Math.cos(rLatA) * Math.cos(rLatB) * Math.pow(Math.sin(deltaLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // compute the distance
        return radius * c;
    }

    public double greatArcLength (Tuple ta, Tuple tb) {
        return greatArcLength(ta, tb, radius);
    }
}
