package us.irdev.gtk.geography;

import us.irdev.gtk.xyw.*;
import us.irdev.gtk.functional.ListFunc;
import static us.irdev.gtk.xyw.Polygon.Classification;
import static us.irdev.gtk.xyw.Polygon.Classification.*;

import java.util.List;

public class RingArray {
    public final Polygon boundary;
    public final List<Polygon> holes;

    public RingArray(Segments boundary, List<Segments> holes) {
        this.boundary = new Polygon (boundary);
        this.holes = ListFunc.map (holes, Polygon::new);
    }

    public RingArray(List<Segments> segmentsList) {
        this (segmentsList.get(0), segmentsList.subList (1, segmentsList.size()));
    }

    public boolean contains(Segments segments) {
        // test the boundary, and if that passes, test the holes for exclusion
        return ListFunc.reduce (holes, boundary.contains (segments), (hole, value) -> value && !hole.contains (segments));
    }

    public boolean contains(Tuple pt) {
        return contains (boundary.getSegmentsForContains(pt));
    }

    public Classification classify (Domain domain) {
        // check the boundary classification, if it contains the entire domain, we need to see if
        // any of the holes have complex containment, necessitating additional testing vs. the
        // trivial accept.
        var classification = boundary.classify (domain);
        if (classification == CONTAINS_DOMAIN)  {
            for (var hole : holes) {
                switch (hole.classify (domain)) {
                    case CONTAINS_DOMAIN:
                        // the whole ringarray should be treated as no intersection
                        return NO_INTERSECTION;
                    case NO_INTERSECTION:
                        // no impact on the original classification
                        break;
                    case NON_TRIVIAL_INTERSECTION:
                        // the whole ringarray should be treated as non-trivial
                        return NON_TRIVIAL_INTERSECTION;
                }
            }
        }
        return classification;
    }

    public Domain domain () {
        return boundary.domain();
    }
}
