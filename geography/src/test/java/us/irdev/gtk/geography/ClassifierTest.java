package us.irdev.gtk.geography;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import us.irdev.bedrock.bag.BagObject;
import us.irdev.gtk.functional.ListFunc;
import us.irdev.gtk.io.Utility;
import us.irdev.gtk.xyw.Domain;
import us.irdev.gtk.xyw.Polygon;
import us.irdev.gtk.xyw.Tuple;

import java.nio.file.Paths;
import java.util.*;

import static us.irdev.gtk.xyw.Tuple.PT;

import static org.junit.jupiter.api.Assertions.*;
import static us.irdev.gtk.xyw.Tuple.VEC;

public class ClassifierTest {
    private static final Logger log = LogManager.getLogger(ClassifierTest.class);

    @Test
    public void testClassifier() {
        var allFeatures = List.of(
                //Feature.fromGeoJson("https://github.com/wmgeolab/geoBoundaries/raw/9469f09/releaseData/gbOpen/USA/ADM1/geoBoundaries-USA-ADM1.geojson")
                Feature.fromGeoJson("data/USA-ADM1.geojson.gz")
                ,Feature.fromGeoJson("data/MEX-ADM1.geojson.gz")
                //,Feature.fromGeoJson("data/CAN-ADM1.geojson.gz")
        );
        log.info("All features loaded");
        var ringArrays = new ArrayList<RingArray> ();
        for (var featureList : allFeatures) {
            log.info("Copying {} features", featureList.size());
            for (var feature : featureList) {
                ringArrays.addAll(feature.ringArrays);
            }
        }

        var classifier = new Classifier (ringArrays);

        classifier.toSvg("classifier");

        /*
        // houston          PT (-95.3701, 29.7601);
        // austin           PT (-97.7431, 30.2672);
        // san antonio      PT (-98.4946, 29.4252);
        // mesquite bay     PT (-96.83564, 28.09093);
        // new orleans      PT (-90.0758, 29.9509);
        // mexico city      PT (-99.1332, 19.4326);
        // phoenix          PT (-112.0740, 33.4484);
        // denver           PT (-104.9903, 39.7392);
        // gulf of mexico   PT (-95.31868, 27.93636);
        // baltimore        PT (-76.61198, 39.28589);
        */

        var properties = classifier.getAt (PT (-95.3701, 29.7601));
        assertEquals ("Texas", properties.getString ("shapeName"));
    }

    private Feature find (List<Feature> features, String shapeName) {
        for (var feature : features) {
            if (feature.properties.getString ("shapeName").equals(shapeName)) {
                return feature;
            }
        }
        return null;
    }

    @Test
    public void testUtah() {
        var features = Feature.fromGeoJson("data/USA-ADM1.geojson.gz");

        // set up the domain that appears to be failing to clip
        var domain = new Domain(-110, -109.5, 37.0, 37.5);
        var center = domain.center();

        // find Utah and its polygons
        var utahFeature = find(features, "Utah");
        assertNotNull(utahFeature);
        var utah = utahFeature.ringArrays;
        for (var ringArray: utah) {
            var classification = ringArray.classify (domain);
            log.info ("Domain: " + classification.toString ());
            assertEquals(Polygon.Classification.CONTAINS_DOMAIN, classification);
            var contains = ringArray.contains (center);
            log.info ("Center: " + contains);
            assertTrue(contains);
        }

        var classifier = new Classifier (utah);
        classifier.toSvg("utah_classifier");

        var domains = classifier.grid.enumerate ();

    }

    private List<RingArray> ringArraysFromGeoJson(String source) {
        var ringArrays = new ArrayList<RingArray> ();
        for (var feature : Feature.fromGeoJson(source)) {
            ringArrays.addAll(feature.ringArrays);
        }
        return ringArrays;
    }

    private void testADM2toADM1(String country) {
        var adm1 = new Classifier (ringArraysFromGeoJson("data/" + country + "-ADM1.geojson.gz"));
        var adm2 = ringArraysFromGeoJson("data/" + country + "-ADM2.geojson.gz");
        adm1.toSvg ("adm1");
        new Classifier (adm2).toSvg ("adm2");

        var seenAdm2 = new HashSet<String> ();
        var mapOfAdm2ToAdm1 = new HashMap<String, String>();
        for (var ringArray : adm2) {
            var adm2Name = ringArray.properties.getString("shapeName");
            if (! seenAdm2.contains(adm2Name)) {
                seenAdm2.add(adm2Name);
                var boundary = ringArray.boundary;

                // randomly sample the domain until we find a point inside the boundary
                boolean contained ;
                Tuple randomPt;
                var domain = boundary.domain ();
                var size = domain.size();

                var gotIt = false;
                for (int i = 0; (i < 100) && (!gotIt); ++i) {
                    do {
                        randomPt = domain.min.add(VEC(Math.random(), Math.random()).hproduct(size));
                        contained = boundary.contains(randomPt);
                    } while (!contained);

                    var adm = adm1.getAt(randomPt);
                    if (adm != null) {
                        mapOfAdm2ToAdm1.put(adm2Name, adm.getString("shapeName"));
                        gotIt = true;
                    }
                }

                if (! gotIt) {
                    log.warn("Something is terribly wrong for {}", adm2Name);
                }
            }
        }

        // now figure out the inverse mapping
        var bagObject = new BagObject();
        for (var entry : mapOfAdm2ToAdm1.entrySet()) {
            bagObject.add(entry.getValue(), entry.getKey());
        }

        // and write that to a file
        var json = bagObject.toString ();
        Utility.writeFile(Paths.get("output",  country + "-ADM2-mappings.json").toString(), json);
    }

    @Test
    public void testMappings() {
        testADM2toADM1("USA");
        testADM2toADM1("MEX");
    }
}
