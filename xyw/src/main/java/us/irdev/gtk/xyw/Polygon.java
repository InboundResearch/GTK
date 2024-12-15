package us.irdev.gtk.xyw;

import java.util.List;

import static us.irdev.gtk.xyw.Tuple.PT;

// a conceptual wrapper around a Segments object that includes the notion of containment
public class Polygon {
    public final Segments segments;

    public Polygon(Segments segments) {
        this.segments = segments;
    }

    public boolean contains (Segments test) {
        SegmentsPair sp = SegmentsPair.reduce(segments, test);
        return (sp != null) && ((sp.uniqueIntersections().size() % 2) == 1);
    }

    public Segments getSegmentsForContains(Tuple pt) {
        var domain = segments.domain;
        var right = domain.right() + (domain.width() * 0.01);
        return new Segments(List.of(new Segment(pt, PT(right, pt.y))));
    }

    public boolean contains(Tuple pt) {
        return contains (getSegmentsForContains(pt));
    }

    public Domain domain () {
        return segments.domain;
    }

    public static enum Classification {
        NO_INTERSECTION, CONTAINS_DOMAIN, NON_TRIVIAL_INTERSECTION;
    }

    public Classification classify (Domain domain) {
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
