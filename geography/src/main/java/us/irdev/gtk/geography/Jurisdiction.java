package us.irdev.gtk.geography;

import us.irdev.gtk.xyw.Tuple;

import java.util.List;

public class Jurisdiction {
    public final GeoJson geo;
    public final List<Jurisdiction> children;

    public Jurisdiction(GeoJson geo, List<Jurisdiction> children) {
        this.geo = geo;
        this.children = children;

        // XXX create the grid hash
    }

    public void enumerate (Tuple pt, List<Jurisdiction> results) {
    }




    // future design features:
    // - hierarchy... read a state, and counties, and cites, and districts, etc.
    // - precache grid feature to make containment checking fast, each grid cell contains a list of
    //   geojson objects in the next level of the hierarchy that span that cell
}
