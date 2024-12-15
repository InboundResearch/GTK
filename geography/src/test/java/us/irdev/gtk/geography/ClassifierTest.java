package us.irdev.gtk.geography;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import us.irdev.gtk.xyw.Domain;

import java.util.ArrayList;
import java.util.List;

import static us.irdev.gtk.xyw.Tuple.PT;

import static org.junit.jupiter.api.Assertions.*;

public class ClassifierTest {
    private static final Logger log = LogManager.getLogger(ClassifierTest.class);

    @Test
    public void testClassifier() {
        var allFeatures = List.of(
                //Feature.fromGeoJson("https://github.com/wmgeolab/geoBoundaries/raw/9469f09/releaseData/gbOpen/USA/ADM1/geoBoundaries-USA-ADM1.geojson")
                Feature.fromGeoJson("data/USA-ADM1.geojson.gz")
                //,Feature.fromGeoJson("data/MEX-ADM1.geojson.gz")
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
        var utah = find(features, "Utah").ringArrays;
        for (var ringArray: utah) {
            var classification = ringArray.classify (domain);
            log.info ("Domain: " + classification.toString ());
            var contains = ringArray.contains (center);
            log.info ("Center: " + contains);
        }

        var classifier = new Classifier (utah);
        classifier.toSvg("utah_classifier");

        var domains = classifier.grid.enumerate ();

    }
}
