package us.irdev.gtk.geography;

import org.junit.jupiter.api.Test;
import us.irdev.gtk.functional.ListFunc;
import us.irdev.gtk.io.Utility;
import us.irdev.gtk.svg.Axis;
import us.irdev.gtk.svg.Frame;
import us.irdev.gtk.svg.Grid;
import us.irdev.gtk.svg.Traits;
import us.irdev.gtk.xyw.Domain;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static us.irdev.gtk.xyw.Tuple.PT;
import static us.irdev.gtk.xyw.Tuple.VEC;

public class FeatureTest {
  private static final double SVG_GRID_DEGREES = 3.0;

  private void drawSvg(String name, List<Feature> featureList, Domain domain) {
    if (domain == null) {
      domain = ListFunc.reduce(featureList.get(0).ringArrays, new Domain(), (ringArray, dom) -> Domain.union (ringArray.boundary.domain (), dom));
      var pad = domain.span () * 0.05;
      domain = domain.pad(VEC(pad, pad));
      //domain = new Domain(-180, 0, 0, 90);
    }

    var frame = new Frame(domain)
            .begin (new Traits(0.05, "#888", "none"))
            .element(new Grid(VEC(SVG_GRID_DEGREES, SVG_GRID_DEGREES)))
            .begin (new Traits(0.1, "#444", "none"))
            .element(new Axis());

    for (var feature : featureList) {
      feature.toSvg (frame);
    }

    String svg = frame.emitSvg(name, 800);
    Utility.writeFile(Paths.get("output", name + ".svg").toString(), svg);
  }

  @Test
  public void testSimple() {
    var features = Feature.fromGeoJson(Paths.get("data", "simple.json").toString());
    drawSvg ("simple", features, new Domain(-25, -5, 5, 25));
    assertEquals(1, features.size());
    var feature = features.get(0);
    assertEquals ("Simple", feature.properties.getString("name"));

    // check some sample points for containment
    var ringArray = feature.ringArrays.get(0);
    assertTrue (ringArray.contains (PT (-15, 15)));
    assertFalse (ringArray.contains (PT (-5, 15)));
    assertFalse (ringArray.contains (PT (-25, 15)));
    assertFalse (ringArray.contains (PT (-15, 5)));
    assertFalse (ringArray.contains (PT (-15, 25)));
  }

  @Test
  public void testSimpleWithHole() {
    var features = Feature.fromGeoJson(Paths.get("data", "simple_with_hole.json").toString());
    drawSvg ("simple_with_hole", features, new Domain(-25, -5, 5, 25));
    assertEquals(1, features.size());
    var feature = features.get(0);
    assertEquals ("Simple With Hole", feature.properties.getString("name"));

    // check some sample points for containment
    var ringArray = feature.ringArrays.get(0);
    assertTrue (ringArray.contains (PT (-17, 15)));
    assertTrue (ringArray.contains (PT (-15, 17)));
    assertTrue (ringArray.contains (PT (-15, 13)));
    assertTrue (ringArray.contains (PT (-13, 15)));

    assertFalse (ringArray.contains (PT (-15, 15)));
    assertFalse (ringArray.contains (PT (-5, 15)));
    assertFalse (ringArray.contains (PT (-25, 15)));
    assertFalse (ringArray.contains (PT (-15, 5)));
    assertFalse (ringArray.contains (PT (-15, 25)));
  }

  @Test
  public void testTexas() {
    var features = Feature.fromGeoJson(Paths.get("data", "texas.json.gz").toString());
    drawSvg ("texas", features, null);
    assertEquals(1, features.size());
    var feature = features.get(0);
    assertEquals ("Texas", feature.properties.getString("name"));

    /*
    // houston
    assertTrue (geoJson.contains (PT (-95.3701, 29.7601)));
    // austin
    assertTrue (geoJson.contains (PT (-97.7431, 30.2672)));
    // san antonio
    assertTrue (geoJson.contains (PT (-98.4946, 29.4252)));
    // mesquite bay
    assertTrue (geoJson.contains (PT (-96.83564, 28.09093)));
    // new orleans
    assertFalse (geoJson.contains (PT (-90.0758, 29.9509)));
    // mexico city
    assertFalse (geoJson.contains (PT (-99.1332, 19.4326)));
    // phoenix
    assertFalse (geoJson.contains (PT (-112.0740, 33.4484)));
    // denver
    assertFalse (geoJson.contains (PT (-104.9903, 39.7392)));
    // gulf of mexico
    assertFalse (geoJson.contains (PT (-95.31868, 27.93636)));
    */
  }

  @Test
  public void testUsa() {
    var features = Feature.fromGeoJson("https://github.com/wmgeolab/geoBoundaries/raw/9469f09/releaseData/gbOpen/USA/ADM0/geoBoundaries-USA-ADM0.geojson");
    drawSvg ("usa", features, new Domain(-180, -60, 15, 75));
  }

  @Test
  public void testStates() {
    var features = Feature.fromGeoJson("https://github.com/wmgeolab/geoBoundaries/raw/9469f09/releaseData/gbOpen/USA/ADM1/geoBoundaries-USA-ADM1.geojson");
    drawSvg ("states", features, new Domain(-180, -60, 15, 75));
  }

  @Test
  public void testBadData() {
    var features = Feature.fromGeoJson(Paths.get("data", "bad_data.json").toString());
    drawSvg ("bad_data", features, null);
  }
}
