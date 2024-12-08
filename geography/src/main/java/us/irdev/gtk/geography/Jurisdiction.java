package us.irdev.gtk.geography;

import java.util.List;

public class Jurisdiction {
    private GeoJson geo;
    private List<Jurisdiction> children;

    public Jurisdiction(GeoJson geo) {
        this.geo = geo;
    }


    // future design features:
    // - hierarchy... read a state, and counties, and cites, and districts, etc.
    // - precache grid feature to make containment checking fast, each grid cell contains a list of
    //   geojson objects in the next level of the hierarchy that span that cell
}
