package us.irdev.gtk.geography;

import us.irdev.bedrock.bag.BagObject;
import us.irdev.gtk.svg.Frame;
import us.irdev.gtk.svg.Traits;
import us.irdev.gtk.xyw.*;
import us.irdev.gtk.functional.ListFunc;
import static us.irdev.gtk.xyw.Polygon.Classification;
import static us.irdev.gtk.xyw.Polygon.Classification.*;

import java.util.List;

// the name ring array is taken from the GeoJSON spec discussing polygons
public class RingArray {
    public final Polygon boundary;
    public final List<Polygon> holes;
    public final BagObject properties;

    public RingArray(Polygon boundary, List<Polygon> holes, BagObject properties) {
        this.boundary = boundary;
        this.holes = holes;
        this.properties = properties;
    }

    public RingArray(List<Polygon> polygonList, BagObject properties) {
        this (polygonList.get(0), polygonList.subList (1, polygonList.size()), properties);
    }

    public boolean contains(Tuple pt) {
        return ListFunc.reduce (holes, boundary.contains (pt), (hole, value) -> value && !hole.contains (pt));
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

    public void toSvg (Frame frame) {
        frame
                .begin(new Traits(0.01, "#008", "none"))
                .poly(boundary);
        for (var hole : holes) {
            frame
                    .begin(new Traits(0.005, "#080", "none"))
                    .poly(hole);
        }
    }
}
