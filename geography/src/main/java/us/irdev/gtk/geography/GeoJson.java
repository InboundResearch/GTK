package us.irdev.gtk.geography;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import us.irdev.bedrock.bag.BagArray;
import us.irdev.bedrock.bag.BagObject;
import us.irdev.bedrock.bag.BagObjectFrom;
import us.irdev.gtk.xyw.*;
import static us.irdev.gtk.xyw.Tuple.PT;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class GeoJson {
    private static final Logger log = LogManager.getLogger(GeoJson.class);

    public final String name;
    public final List<RingArray> ringArrays;
    public final Domain domain;

    private static Tuple tupleFrom (BagArray tupleArray) {
        return PT (tupleArray.getFloat(0), tupleArray.getFloat(1));
    }

    private static Segments ringFrom (BagArray ring) {
        var tuples = new ArrayList<Tuple>();
        // geojson transmits the last point the same as the first, so we don't need to grab the last
        // one, but it doesn't seem to protect against duplicate tuples...
        var lastTuple = PT(1.0e9, 1.0e9);
        for (int i = 0, count = ring.getCount() - 1; i < count; ++i) {
            var tuple = tupleFrom (ring.getBagArray(i));
            if (! Tuple.similar (tuple, lastTuple)) {
                lastTuple = tuple;
                tuples.add(tuple);
            }
        }
        return new Segments(new PolyLine (tuples, true).toSegments());
    }

    private static RingArray ringArrayFrom (BagArray ringArray) {
        var result = new ArrayList<Segments>();
        for (int i = 0, count = ringArray.getCount(); i < count; ++i) {
            result.add(ringFrom (ringArray.getBagArray(i)));
        }
        return new RingArray (result);
    }

    private static List<RingArray> ringArraysFrom (BagArray ringArrays) {
        var result = new ArrayList<RingArray>();
        for (int i = 0, count = ringArrays.getCount(); i < count; ++i) {
            result.add(ringArrayFrom (ringArrays.getBagArray(i)));
        }
        return result;
    }

    public GeoJson (BagObject bagObject) {
        this.name = bagObject.getString("properties/name");
        var geometry = bagObject.getBagObject("geometry");
        if (geometry.getString("type").equals("MultiPolygon")) {
            // coordinates is an array of ring arrays
            // a ring array is an array of rings, where the first ring is the boundary and the rest are holes
            // a ring is an array of coordinates
            // a coordinate is an array of floats (a Tuple)
            ringArrays = ringArraysFrom (geometry.getBagArray("coordinates"));
            log.info("Ring count: {}", ringArrays.size());

            // compute the domain
            var computedDomain = new Domain ();
            for (var ringArray : ringArrays) {
                // holes must be fully contained within the boundary, so we can skip them here
                computedDomain = Domain.union (computedDomain, ringArray.boundary.domain);
            }
            domain = computedDomain;
        } else {
            ringArrays = null;
            domain = null;
        }
    }

    public static GeoJson read(String filename) {
        return new GeoJson (BagObjectFrom.file (new File(filename)));
    }

    public boolean contains (Tuple pt) {
        // create a segments object with a single line segment from the pt to the right side of the
        // input domains
        var right = domain.right() + 1;
        var segment = new Segment (pt, PT (domain.right() + 1, pt.y));
        var segments = new Segments (List.of(segment));

        // reduce and compute the intersections
        for (var ringArray : ringArrays) {
            if (ringArray.contains (segments)) {
                return true;
            }
        }

        return false;
    }
}
