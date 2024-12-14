package us.irdev.gtk.geography;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import us.irdev.bedrock.bag.BagArray;
import us.irdev.bedrock.bag.BagObject;
import us.irdev.bedrock.bag.BagObjectFrom;
import us.irdev.gtk.functional.ListFunc;
import us.irdev.gtk.xyw.*;

import static us.irdev.gtk.xyw.Polygon.Classification;
import static us.irdev.gtk.xyw.Polygon.Classification.*;
import static us.irdev.gtk.xyw.Tuple.PT;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;


public class Feature {
    private static final Logger log = LogManager.getLogger(Feature.class);

    public final BagObject properties;
    public final List<RingArray> ringArrays;
    public final Domain domain;

    // a coordinate is an array of floats (maybe 2 or 3)
    private static Tuple coordinateFrom(BagArray tupleArray) {
        return PT (tupleArray.getFloat(0), tupleArray.getFloat(1));
    }

    // a ring is an array of coordinates, it may be closed (polygon) or open (polyline)
    private static Segments ringFrom (BagArray ring) {
        var tuples = new ArrayList<Tuple>();
        // geojson transmits the last point the same as the first, so we don't need to grab the last
        // one, but it doesn't seem to protect against duplicate tuples...
        var lastTuple = PT(1.0e9, 1.0e9);
        for (int i = 0, count = ring.getCount() - 1; i < count; ++i) {
            var tuple = coordinateFrom(ring.getBagArray(i));
            if (! Tuple.similar (tuple, lastTuple)) {
                lastTuple = tuple;
                tuples.add(tuple);
            }
        }

        // the last tuple could still be identical to the first tuple in particularly uglycases, so
        // we walk back the end of the array to remove such degeneracies
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

        // join the tuples list into a list of segments, then return a new segments object with that
        return new Segments(new PolyLine (tuples, true).toSegments());
    }

    // a ring array is an array of rings, they may be closed (polygons) - in which case the first
    // ring is taken to be the boundary and subsequent rings are holes (and could probably be cut
    // into the parent polygon somehow), or open (polylines)
    private static RingArray ringArrayFrom (BagArray ringArray, BagObject properties) {
        var result = new ArrayList<Segments>();
        for (int i = 0, count = ringArray.getCount(); i < count; ++i) {
            result.add(ringFrom (ringArray.getBagArray(i)));
        }
        return new RingArray (result, properties);
    }

    private static List<RingArray> ringArraysFrom (BagArray ringArrays, BagObject properties) {
        var result = new ArrayList<RingArray>();
        for (int i = 0, count = ringArrays.getCount(); i < count; ++i) {
            result.add(ringArrayFrom (ringArrays.getBagArray(i), properties));
        }
        return result;
    }

    public Feature(BagObject bagObject) {
        assert (bagObject.getString("type").equals("Feature"));
        properties = bagObject.getBagObject("properties");
        var geometry = bagObject.getBagObject("geometry", () -> BagObject.open ("type", "None"));
        switch (geometry.getString("type")) {
            case "MultiPolygon":
                ringArrays = ringArraysFrom(geometry.getBagArray("coordinates"), properties);
                break;
            case "Polygon":
                ringArrays = List.of(ringArrayFrom(geometry.getBagArray("coordinates"), properties));
                break;

            // the remainders, including our special "None" case
            case "None":
            default:
                log.warn ("Unsupported geometry type {}", geometry.getString("type"));
                ringArrays = new ArrayList<RingArray> ();
                break;
        }

        // compute the domain, holes must be fully contained within the boundary, so we can skip them here
        log.info("Ring count for {}: {}", properties.getString ("name", () -> "Unknown"), ringArrays.size());
        var computedDomain = ListFunc.reduce(ringArrays, new Domain(), (ringArray, dom) -> Domain.union (ringArray.domain (), dom));
        domain = computedDomain.valid() ? computedDomain : new Domain (-180., 180., -90., 90.);
    }

    public static List<Feature> readGeoJsonFile(String filename) {
        // read the input file, some may be gzipped
        BagObject root;
        if (filename.endsWith(".gz")) {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(filename))) {
                root = BagObjectFrom.inputStream (gzipInputStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            root = BagObjectFrom.file (new File(filename));
        }

        // handle the file contents
        switch (root.getString("type")) {
            case "Feature": {
                return List.of(new Feature(root));
            }
            case "FeatureCollection": {
                var result = new ArrayList<Feature>();
                var features = root.getBagArray("features");
                for (int i = 0, count = features.getCount(); i < count; ++i) {
                    var node = features.getBagObject(i);
                    if (node.getString("type").equals("Feature")) {
                        result.add(new Feature(node));
                    }
                }
                return result;
            }
            default:
                return new ArrayList<Feature>();
        }
    }

    public boolean contains (Tuple pt) {
        if (domain.contains (pt)) {
            // create a segments object with a single line segment from the pt to the right side of
            // the input domains
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

    public Classification classify (Domain domain) {
        for (var ringArray : ringArrays) {
            switch (ringArray.classify (domain)) {
                case CONTAINS_DOMAIN:
                    return CONTAINS_DOMAIN;
                case NO_INTERSECTION:
                    break;
                case NON_TRIVIAL_INTERSECTION:
                    return NON_TRIVIAL_INTERSECTION;
            }
        }
        return NO_INTERSECTION;
    }
}
