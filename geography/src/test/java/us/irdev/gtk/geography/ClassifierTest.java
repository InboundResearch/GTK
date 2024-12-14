package us.irdev.gtk.geography;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;

import static us.irdev.gtk.xyw.Tuple.PT;

import static org.junit.jupiter.api.Assertions.*;

public class ClassifierTest {
    @Test
    public void testClassifier() {
        var geoJsons = Feature.readGeoJsonFile(Paths.get("data", "USA-ADM1.geojson.gz").toString());
        var ringArrays = new ArrayList<RingArray> ();
        for (var geoJson : geoJsons) {
            ringArrays.addAll (geoJson.ringArrays);
        }
        var classifier = new Classifier (ringArrays);

        classifier.toSvg("classifier");

        var properties = classifier.getAt (PT (-95.3701, 29.7601));
        assertEquals ("Harris", properties.getString ("shapeName"));
    }
}
