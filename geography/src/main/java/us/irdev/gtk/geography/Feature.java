package us.irdev.gtk.geography;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import us.irdev.bedrock.bag.BagArray;
import us.irdev.bedrock.bag.BagObject;
import us.irdev.bedrock.bag.BagObjectFrom;
import us.irdev.gtk.svg.Frame;
import us.irdev.gtk.xyw.*;

import static us.irdev.gtk.xyw.Tuple.PT;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;


public class Feature {
    private static final Logger log = LogManager.getLogger(Feature.class);

    public final BagObject properties;
    public final List<RingArray> ringArrays;

    // a coordinate is an array of floats (maybe 2 or 3)
    private static Tuple coordinateFrom(BagArray tupleArray) {
        return PT(tupleArray.getFloat(0), tupleArray.getFloat(1));
    }

    // a ring is an array of coordinates, it may be closed (polygon) or open (polyline)
    private static Polygon ringFrom(BagArray ring) {
        var tuples = new ArrayList<Tuple>();
        // geojson transmits the last point the same as the first, so we don't need to grab the last
        // one, but it doesn't seem to protect against duplicate tuples...
        var lastTuple = PT(1.0e9, 1.0e9);
        for (int i = 0, count = ring.getCount() - 1; i < count; ++i) {
            var tuple = coordinateFrom(ring.getBagArray(i));
            if (!Tuple.similar(tuple, lastTuple)) {
                lastTuple = tuple;
                tuples.add(tuple);
            }
        }

        // the last tuple could still be identical to the first tuple in particularly ugly cases, so
        // we walk back the end of the array to remove such degeneracies
        if (!tuples.isEmpty()) {
            var firstElement = tuples.get(0);

            // iterate from the end of the list
            for (int i = tuples.size() - 1; i > 0; i--) {
                if (Tuple.similar(tuples.get(i), firstElement)) {
                    tuples.remove(i);
                } else {
                    break;
                }
            }
        }

        // join the tuples list into a list of segments, then return a new segments object with that
        return new Polygon(tuples);
    }

    // a ring array is an array of rings, they may be closed (polygons) - in which case the first
    // ring is taken to be the boundary and subsequent rings are holes (and could probably be cut
    // into the parent polygon somehow), or open (polylines)
    private static RingArray ringArrayFrom(BagArray ringArray, BagObject properties) {
        var result = new ArrayList<Polygon>();
        for (int i = 0, count = ringArray.getCount(); i < count; ++i) {
            result.add(ringFrom(ringArray.getBagArray(i)));
        }
        return new RingArray(result, properties);
    }

    private static List<RingArray> ringArraysFrom(BagArray ringArrays, BagObject properties) {
        var result = new ArrayList<RingArray>();
        for (int i = 0, count = ringArrays.getCount(); i < count; ++i) {
            result.add(ringArrayFrom(ringArrays.getBagArray(i), properties));
        }
        return result;
    }

    public Feature(BagObject bagObject) {
        assert (bagObject.getString("type").equals("Feature"));
        properties = bagObject.getBagObject("properties");
        var geometry = bagObject.getBagObject("geometry", () -> BagObject.open("type", "None"));
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
                log.warn("No geometry read, found type: {}", geometry.getString("type"));
                ringArrays = new ArrayList<RingArray>();
                break;
        }
    }

    private static InputStream getFileOrResource(String source) throws Exception {
        // attempt to load as a file
        File file = new File(source);
        if (file.exists() && file.isFile()) {
            return Files.newInputStream(file.toPath());
        }

        // attempt to load as a resource using the context class loader
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            InputStream resourceStream = contextClassLoader.getResourceAsStream(source);
            if (resourceStream != null) {
                return resourceStream;
            }
        }

        // fallback to using the library class's class loader
        InputStream resourceStream = Feature.class.getClassLoader().getResourceAsStream(source);
        if (resourceStream != null) {
            return resourceStream;
        }

        // If neither works, throw an exception
        throw new Exception("File or resource not found: " + source);
    }

    private static InputStream getSourceAsInputStream(String source) throws Exception {
        var inputStream = getFileOrResource (source);
        return source.endsWith(".gz") ? new GZIPInputStream(inputStream) : inputStream;
    }

    public static List<Feature> fromGeoJson(String source) throws Exception {
        BagObject root;
        if (source.startsWith("https://")) {
            root = BagObjectFrom.url(source);
        } else {
            root = BagObjectFrom.inputStream(getSourceAsInputStream(source));
        }
        log.info ("Loaded {}", source);

        // handle the geojson contents
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

    public void toSvg(Frame frame) {
        for (var ringArray : ringArrays) {
            ringArray.toSvg(frame);
        }
    }
}
