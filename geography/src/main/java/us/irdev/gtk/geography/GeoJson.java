package us.irdev.gtk.geography;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import us.irdev.bedrock.bag.BagArray;
import us.irdev.bedrock.bag.BagObject;
import us.irdev.bedrock.bag.BagObjectFrom;
import us.irdev.gtk.functional.ListFunc;
import us.irdev.gtk.xyw.*;
import static us.irdev.gtk.xyw.Tuple.PT;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class GeoJson {
    private static final Logger log = LogManager.getLogger(GeoJson.class);

    public final BagObject properties;
    public final List<RingArray> ringArrays;
    public final Domain domain;

    // a coordinate is an array of floats (maybe 2 or 3)
    private static Tuple coordinateFrom(BagArray tupleArray) {
        return PT (tupleArray.getFloat(0), tupleArray.getFloat(1));
    }

    // a ring is an array of coordinates, it may be closed (polygon) or open (polyline)
    private static Segments ringFrom (BagArray ring, boolean closed) {
        var tuples = new ArrayList<Tuple>();
        // geojson transmits the last point the same as the first, so we don't need to grab the last
        // one, but it doesn't seem to protect against duplicate tuples...
        var lastTuple = PT(1.0e9, 1.0e9);
        for (int i = 0, count = ring.getCount() - (closed ? 1 : 0); i < count; ++i) {
            var tuple = coordinateFrom(ring.getBagArray(i));
            if (! Tuple.similar (tuple, lastTuple)) {
                lastTuple = tuple;
                tuples.add(tuple);
            }
        }

        // XXX and the last tuple could still be identical to the first tuple in particularly ugly
        // XXX cases, so we walk back the end of the array
        if (!tuples.isEmpty()) {
            var firstElement = tuples.get(0);

            // iterate from the end of the list
            for (int i = tuples.size() - 1; i > 0; i--) {
                if (Tuple.similar (tuples.get(i), firstElement)) {
                    tuples.remove (i);
                } else {
                    break;
                }
            }
        }

        return new Segments(new PolyLine (tuples, closed).toSegments());
    }

    // a ring array is an array of rings, they may be closed (polygons) - in which case the first
    // ring is taken to be the boundary and subsequent rings are holes (and could probably be cut
    // into the parent polygon somehow), or open (polylines)
    private static RingArray ringArrayFrom (BagArray ringArray, boolean closed) {
        var result = new ArrayList<Segments>();
        for (int i = 0, count = ringArray.getCount(); i < count; ++i) {
            result.add(ringFrom (ringArray.getBagArray(i), closed));
        }
        return new RingArray (result);
    }

    private static List<RingArray> ringArraysFrom (BagArray ringArrays, boolean closed) {
        var result = new ArrayList<RingArray>();
        for (int i = 0, count = ringArrays.getCount(); i < count; ++i) {
            result.add(ringArrayFrom (ringArrays.getBagArray(i), closed));
        }
        return result;
    }

    public GeoJson (BagObject bagObject) {
        assert (bagObject.getString("type").equals("Feature"));
        properties = bagObject.getBagObject("properties");
        var geometry = bagObject.getBagObject("geometry");
        switch (geometry.getString("type")) {
            case "MultiPolygon":
                ringArrays = ringArraysFrom(geometry.getBagArray("coordinates"), true);
                break;
            case "Polygon":
                ringArrays = List.of(ringArrayFrom(geometry.getBagArray("coordinates"), true));
                break;

            // NOTE: we read these, but not sure what we would do with them...
            case "MultiLineString":
                log.warn ("Reading MultiLineString");
                ringArrays = ringArraysFrom(geometry.getBagArray("coordinates"), false);
                break;
            case "LineString":
                log.warn ("Reading LineString");
                ringArrays = List.of(ringArrayFrom(geometry.getBagArray("coordinates"), false));
                break;
            default:
                ringArrays = new ArrayList<RingArray> ();
                break;
        }

        // compute the domain, holes must be fully contained within the boundary, so we can skip them here
        log.info("Ring count: {}", ringArrays.size());
        domain = ListFunc.reduce(ringArrays, new Domain(), (ringArray, dom) -> Domain.union (ringArray.boundary.domain, dom));
    }

    public static List<GeoJson> read(String filename) {
        var root = BagObjectFrom.file (new File(filename));
        switch (root.getString("type")) {
            case "Feature": {
                return List.of(new GeoJson(root));
            }
            case "FeatureCollection": {
                var result = new ArrayList<GeoJson>();
                var features = root.getBagArray("features");
                for (int i = 0, count = features.getCount(); i < count; ++i) {
                    var node = features.getBagObject(i);
                    if (node.getString("type").equals("Feature")) {
                        result.add(new GeoJson(node));
                    }
                }
                return result;
            }
            default:
                return new ArrayList<GeoJson>();
        }
    }

    public boolean contains (Tuple pt) {
        if (domain.contains (pt)) {
            // create a segments object with a single line segment from the pt to the right side of
            // the input domains
            var right = domain.right() + 1;
            var segment = new Segment(pt, PT(domain.right() + 1, pt.y));
            var segments = new Segments(List.of(segment));

            // reduce and compute the intersections
            for (var ringArray : ringArrays) {
                if (ringArray.contains(segments)) {
                    return true;
                }
            }
        }
        return false;
    }
}
