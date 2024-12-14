package us.irdev.gtk.geography;

import us.irdev.bedrock.bag.BagObject;
import us.irdev.gtk.functional.ListFunc;
import us.irdev.gtk.io.Utility;
import us.irdev.gtk.xyw.Domain;
import us.irdev.gtk.xyw.Grid;
import us.irdev.gtk.xyw.Polygon;
import us.irdev.gtk.xyw.Polygon.Classification;
import us.irdev.gtk.xyw.Tuple;
import us.irdev.gtk.svg.*;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static us.irdev.gtk.xyw.Polygon.Classification.*;
import static us.irdev.gtk.xyw.Tuple.VEC;

public class Jurisdiction {
    private static class JurisdictionContainer {
        public final Jurisdiction jurisdiction;
        public final boolean trivialAccept;
        public JurisdictionContainer (Jurisdiction jurisdiction, boolean trivialAccept) {
            this.jurisdiction = jurisdiction;
            this.trivialAccept = trivialAccept;
        }
    }
    public final GeoJson geo;
    private final Grid<JurisdictionContainer> grid;

    // the grid sizing is based on the sqrt (n) where n is the number of children. this is fine if
    // the children are uniformly distributed, but at some levels of the jurisdictional hierarchy
    // they are not. in order to give more granularity, we buffer the n with this multiplier, at the
    // possible expense of creating a few more redundant cells. this value is an arm's length
    // estimate, not computed in any way.
    private final static int MULTIPLIER = 100;

    public Jurisdiction(GeoJson geo, List<Jurisdiction> subordinates) {
        assert (geo != null);
        this.geo = geo;
        if (subordinates != null) {
            grid = new Grid<>(geo.domain, subordinates.size() * MULTIPLIER);

            // fill the grid with the characterized children, this is not efficient
            for (var domain : grid.enumerate()) {
                for (var subordinate : subordinates) {
                    switch (subordinate.geo.classify (domain)) {
                        case NO_INTERSECTION:
                            break;
                        case CONTAINS_DOMAIN:
                            grid.putAt (domain.center(), new JurisdictionContainer(subordinate, true));
                            break;
                        case NON_TRIVIAL_INTERSECTION:
                            grid.putAt (domain.center(), new JurisdictionContainer(subordinate, false));
                            break;
                    }
                }
            }
        } else {
            grid = null;
        }
    }

    public Jurisdiction(GeoJson geo) {
        this (geo, null);
    }

    public List<Jurisdiction> enumerate (Tuple pt, boolean trivialAccept) {
        var results = new ArrayList<Jurisdiction>();
        if (grid != null) {
            var children = grid.getAt(pt);
            for (var child : children) {
                results.addAll (child.jurisdiction.enumerate(pt, child.trivialAccept));
            }
            if (!results.isEmpty()) {
                results.add(0, this);
            }
        } else if (trivialAccept || (geo.contains (pt))) {
            results.add(0, this);
        }
        return results;
    }

    public List<Jurisdiction> enumerate (Tuple pt) {
        // the hierarchy is assumed to have a jurisdiction with no geo, that is the "universe" or "world"
        return enumerate (pt, true);
    }

    public BagObject properties () {
        return geo.properties;
    }

    private Classification classify (Set<JurisdictionContainer> subordinates) {
        return switch (subordinates.size()) {
            case 0 -> NO_INTERSECTION;
            case 1 -> subordinates.iterator().next().trivialAccept ? CONTAINS_DOMAIN : NON_TRIVIAL_INTERSECTION;
            default -> NON_TRIVIAL_INTERSECTION;
        };
    }

    public void toSvg (String name) {
        var frame = new Frame(grid.domain)
                .begin (new Traits(0.05, "#bbb", "none"))
                .element(new us.irdev.gtk.svg.Grid(grid.spacing, grid.domain.min))
                .begin (new Traits(0.1, "#444", "none"))
                .element(new Axis());

        var colors = Map.of (CONTAINS_DOMAIN, "#ccf", NON_TRIVIAL_INTERSECTION, "#fcc");
        for (var domain : grid.enumerate()) {
            var subordinates = grid.getAt (domain.center());
            var classification = classify (subordinates);
            if (classification != NO_INTERSECTION) {
                frame
                        .begin (new Traits(0.05, "none", colors.get (classification), 0.75))
                        .box (domain);
            }
        }

        /*
        for (var geoJson : geoJsonList) {
            for (var ringArray : geoJson.ringArrays) {
                frame
                        .begin (new Traits (0.05, "#00a", "none"))
                        .poly (ringArray.boundary);
                for (var hole : ringArray.holes) {
                    frame
                            .begin(new Traits(0.03, "red", "none"))
                            .poly(hole);
                }
            }
        }
        */

        String svg = frame.emitSvg(name, 800);
        Utility.writeFile(Paths.get("output", name + ".svg").toString(), svg);
    }
}
