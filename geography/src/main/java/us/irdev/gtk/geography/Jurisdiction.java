package us.irdev.gtk.geography;

import us.irdev.bedrock.bag.BagObject;
import us.irdev.gtk.xyw.Grid;
import us.irdev.gtk.xyw.Tuple;

import java.util.List;

public class Jurisdiction {
    private class JurisdictionContainer {
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
    // they are not. in order to give a little more granularity, we buffer the n a bit, at the
    // possible expense of creating a few more redundant cells. this value is an arm's length
    // estimate, not computed in any way.
    private final int MULTIPLIER = 3;

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
            // XXX how do we use the "trivial" value?
        } else {
            grid = null;
        }
    }

    public Jurisdiction(GeoJson geo) {
        this (geo, null);
    }

    public void enumerate (Tuple pt, List<Jurisdiction> results, boolean trivialAccept) {
        if (grid != null) {
            var children = grid.getAt(pt);
            for (var child : children) {
                child.jurisdiction.enumerate(pt, results, child.trivialAccept);
            }
            if (!results.isEmpty()) {
                results.add(0, this);
            }
        } else if (trivialAccept || (geo.contains (pt))) {
            results.add(0, this);
        }
    }

    public BagObject properties () {
        return geo.properties;
    }
}
