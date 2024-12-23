package us.irdev.gtk.xyw;

import us.irdev.gtk.functional.ListFunc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static us.irdev.gtk.xyw.Numerics.lerp;
import static us.irdev.gtk.xyw.Tuple.VEC;

// a conceptual wrapper around a Segments object that includes the notion of containment
public class Polygon {
    public final Segments segments;

    public Polygon(Segments segments) {
        this.segments = segments;
    }

    private static List<Segment> createFromTuples (List<Tuple> tuples) {
        return ListFunc.map(tuples, (i, a) -> new Segment (a, tuples.get((i + 1) % tuples.size ())));
    }

    public Polygon(Tuple[] tuples) {
        this (new Segments (createFromTuples (Arrays.asList (tuples))));
    }

    public Polygon(List<Tuple> tuples) {
        this (new Segments (createFromTuples (tuples)));
    }

    private static final Tuple ONE = VEC(1, 1);

    private static final int SKIP = 0;  // a trivial no
    private static final int TEST = 1;  // requires further investigation

    // the x-classification is aggregated into a set of bits that we use to quickly determine what
    // to do, in particular skipping out on conditions that will always be trivially rejectable
    private static final int[] xflags = {
            // 0b0000, 0b0001, 0b0010, 0b0011
               SKIP,   SKIP,   TEST,   SKIP,
            // 0b0100, 0b0101, 0b0110, 0b0111
               SKIP,   SKIP,   TEST,   SKIP,
            // 0b1000, 0b1001, 0b1010, 0b1011
               TEST,   TEST,   TEST,   SKIP,
            // 0b1100, 0b1101, 0b1110, 0b1111
               SKIP,   SKIP,   SKIP,   SKIP,
    };

    private static final int HALF = 1;
    private static final int FULL = 2;

    // the y-classification is aggregated into a set of bits that we use to quickly determine what
    // to do, and how much to contribute to the winding number for each condition
    private static final int[] yflags = {
            // 0b0000, 0b0001, 0b0010, 0b0011
               SKIP,   +HALF,  +FULL,  SKIP,
            // 0b0100, 0b0101, 0b0110, 0b0111
               -HALF,  SKIP,   +HALF,  SKIP,
            // 0b1000, 0b1001, 0b1010, 0b1011
               -FULL,  -HALF,  SKIP,   SKIP,
            // 0b1100, 0b1101, 0b1110, 0b1111
               SKIP,   SKIP,   SKIP,   SKIP,
    };

    static final int ON_BOUNDARY = 3;
    static int classifySegment(Tuple pt, Segment segment) {
        // brutally test for a boundary condition
        if (segment.contains(pt)) return ON_BOUNDARY;

        // compute the classification values
        var a = segment.a.subtract (pt).signum().add(ONE);
        var b = segment.b.subtract (pt).signum().add(ONE);

        // compute the x classification aggregate, and see if that's a testable situation
        var x = ((int)a.x << 2) | ((int)b.x);
        if (xflags[x] == TEST) {
            // compute the y classification aggregate, the y-flag value, and see if that's a
            // testable situation
            var y = ((int) a.y << 2) | ((int) b.y);
            var yFlag = yflags[y];
            if (yFlag != SKIP) {
                // compute the intersection, if it's actually to the right, return the y-flag for
                // this segment
                var xIntersection = lerp (segment.a.x, segment.b.x, (pt.y - segment.a.y) / (segment.b.y - segment.a.y)) - pt.x;
                if (xIntersection > 0) return yFlag;
            }
        }
        // no conditions were met, this segment adds nothing to the winding number
        return 0;
    }

    public boolean contains (Tuple pt) {
        // a line intersecting a closed polygon (convex or concave) will have an even number of
        // intersection points. for a segment starting inside the polygon, the count will be odd.
        // the winding number test, which counts edge crossings of a horizontal line segment
        // extending left from the test point, is a simple way to verify this.

        // start with a zero winding number
        var windingNumber = 0;

        // loop over all the segments
        for (var segment : segments.segments) {
            // brutally test if the point is on this segment, the boundary of the polygon
            // XXX this needs to be more efficient
            if (segment.contains(pt)) return true;

            // compute the classification values
            var a = segment.a.subtract (pt).signum().add(ONE);
            var b = segment.b.subtract (pt).signum().add(ONE);

            // compute the x classification aggregate, and see if that's a testable situation
            var x = ((int)a.x << 2) | ((int)b.x);
            if (xflags[x] == TEST) {
                // compute the y classification aggregate, the y-flag value, and see if that's a
                // testable situation
                var y = ((int) a.y << 2) | ((int) b.y);
                var yFlag = yflags[y];
                if (yFlag != SKIP) {
                    // compute the intersection, if it's actually to the right, return the y-flag for
                    // this segment
                    var xIntersection = lerp (segment.a.x, segment.b.x, (pt.y - segment.a.y) / (segment.b.y - segment.a.y)) - pt.x;
                    if (xIntersection > 0) windingNumber += yFlag;
                }
            }
        }

        // a properly closed polygon should never end up with an odd number of crossings
        assert (windingNumber % 2 == 0);
        return windingNumber != 0;
    }

    public Domain domain () {
        return segments.domain;
    }

    public static enum Classification {
        NO_INTERSECTION, CONTAINS_DOMAIN, NON_TRIVIAL_INTERSECTION;
    }

    public Classification classify (Domain domain) {
        // check if the polygon domains overlap
        if (Domain.intersection(segments.domain, domain).valid()) {
            // classify the polygon wrt to the domain, returning one of:
            // 1) domain and polygon do not have any intersection
            // 2) domain is fully contained in the polygon
            // 3) the polygon and the domain have some complex intersection

            // start by doing a simple trim of the polygon to segments that actually touch the domain
            if (segments.trimToDomain(domain) != null) {
                // there are segments that cross the domain or are contained within it, including the
                // possibility the entire polygon is wholly contained. regardless, this means the
                // intersection is not trivially decided for any given point within the domain.
                return Classification.NON_TRIVIAL_INTERSECTION;
            } else {
                // there are no segments crossing or contained within the domain, so the domain could be
                // wholly contained by the polygon or there is no overlap at all. we distinguish these
                // cases by checking the center of the domain for containment in the polygon.
                return contains(domain.center()) ? Classification.CONTAINS_DOMAIN : Classification.NO_INTERSECTION;
            }
        }
        return Classification.NO_INTERSECTION;
    }

    // should implement:
    // clipToDomain - create a new whole polygon
    // clip to polygon - create a new whole polygon
    // intersect, union, difference
}
