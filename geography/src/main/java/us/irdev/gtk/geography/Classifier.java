package us.irdev.gtk.geography;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.irdev.bedrock.bag.BagObject;
import us.irdev.gtk.functional.ListFunc;
import us.irdev.gtk.io.Utility;
import us.irdev.gtk.svg.Axis;
import us.irdev.gtk.svg.Frame;
import us.irdev.gtk.svg.Traits;
import us.irdev.gtk.xyw.*;

import java.nio.file.Paths;
import java.util.*;

import static us.irdev.gtk.xyw.Polygon.Classification;
import static us.irdev.gtk.xyw.Polygon.Classification.*;
import static us.irdev.gtk.xyw.Tuple.VEC;

public class Classifier {
    private static final Logger log = LogManager.getLogger(Classifier.class);

    public record Container(boolean trivialAccept, RingArray ringArray) {}
    public final Grid<Container> grid;

    // the grid sizing is based on the sqrt (n) where n is the number of children. this is fine if
    // the children are uniformly distributed, but at some levels of the jurisdictional hierarchy
    // they are not. in order to give more granularity, we buffer the n with this multiplier, at the
    // possible expense of creating a few more redundant cells. this value is an arm's length
    // estimate, not computed in any way.
    private final static int MULTIPLIER = 16;

    public Classifier (List<RingArray> ringArrays) {
        /*
        var computedDomain = ListFunc.reduce(ringArrays, new Domain(), (ringArray, dom) -> Domain.union (ringArray.domain (), dom));
        var gridDomain = computedDomain.valid() ? computedDomain : new Domain (-180., 180., -90., 90.);
        grid = new Grid<> (gridDomain, ringArrays.size() * MULTIPLIER);
        */
        grid = new Grid<> (new Domain (-180., 180., -90., 90.), VEC(0.25, 0.25));

        // populate the grid, this is not particularly efficient
        log.info ("Populating {} Ring Arrays", ringArrays.size());
        for (var ringArray : ringArrays) {
            var domains = grid.enumerate (ringArray.domain());
            //log.info ("Populating into {} domains", domains.size());
            for (var domain : domains) {
                switch (ringArray.classify (domain)) {
                    case NO_INTERSECTION:
                        break;
                    case CONTAINS_DOMAIN:
                        grid.putAt (domain.center(), new Container(true, ringArray));
                        break;
                    case NON_TRIVIAL_INTERSECTION:
                        grid.putAt (domain.center(), new Container(false, ringArray));
                        break;
                }
            }
        }

    }

    public BagObject getAt (Tuple pt) {
        var children = grid.getAt(pt);
        for (var child : children) {
            if (child.trivialAccept || child.ringArray.contains (pt)) {
                return child.ringArray.properties;
            }
        }
        return null;
    }

    private Classification classifyChildren (Set<Container> children) {
        return switch (children.size()) {
            case 0 -> NO_INTERSECTION;
            case 1 -> children.iterator().next().trivialAccept ? CONTAINS_DOMAIN : NON_TRIVIAL_INTERSECTION;
            default -> NON_TRIVIAL_INTERSECTION;
        };
    }

    public void toSvg (String name) {
        var frame = new Frame(grid.domain)
                .begin (new Traits(0.01, "#bbb", "none"))
                .element(new us.irdev.gtk.svg.Grid(grid.spacing, grid.domain.min))
                .begin (new Traits(0.02, "#444", "none"))
                .element(new Axis());

        var colors = Map.of (CONTAINS_DOMAIN, "#ccf", NON_TRIVIAL_INTERSECTION, "#fcc");
        for (var domain : grid.enumerate()) {
            var children = grid.getAt (domain.center());

            // loop over the children
            var classification = classifyChildren (children);
            if (classification != NO_INTERSECTION) {
                frame
                        .begin (new Traits(0.05, "none", colors.get (classification), 0.75))
                        .box (domain, "domain [min:" + domain.min.toString () + ", max:" + domain.max.toString () + "]");
            }
        }

        // add in the polygons
        var renderedSet = new HashSet<RingArray>();
        for (var domain : grid.enumerate()) {
            var children = grid.getAt (domain.center());
            for (var child : children) {
                if (! renderedSet.contains (child.ringArray)) {
                    renderedSet.add (child.ringArray);
                    child.ringArray.toSvg (frame);
                }
            }
        }
        String svg = frame.emitSvg(name, 800);
        Utility.writeFile(Paths.get("output", name + ".svg").toString(), svg);
    }
}
