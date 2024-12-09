package us.irdev.gtk.geography;

import org.junit.jupiter.api.Test;
import us.irdev.gtk.functional.ListFunc;
import us.irdev.gtk.io.Utility;
import us.irdev.gtk.svg.Axis;
import us.irdev.gtk.svg.Frame;
import us.irdev.gtk.svg.Traits;
import us.irdev.gtk.xyw.Assertions;
import us.irdev.gtk.xyw.Domain;
import us.irdev.gtk.xyw.SegmentsPair;
import us.irdev.gtk.xyw.Tuple;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static us.irdev.gtk.xyw.Assertions.assertSimilar;
import static us.irdev.gtk.xyw.Tuple.PT;

public class GeoJsonTest {
  private void drawSvg(String name, List<GeoJson> geoJsonList) {
    var domain = ListFunc.reduce(geoJsonList, new Domain(), (geoJson, dom) -> Domain.union (geoJson.domain, dom));
    var frame = new Frame(domain)
            .begin (new Traits(0.1, "#888", "none"))
            .element(new Axis());

    for (var geoJson : geoJsonList) {
      for (var ringArray : geoJson.ringArrays) {
        frame
                .begin (new Traits (0.01, "#007", "none"))
                .poly (ringArray.boundary);
        for (var hole : ringArray.holes) {
          frame
                  .begin(new Traits(0.01, "#700", "none"))
                  .poly(hole);
        }
      }
    }

    String svg = frame.emitSvg(name, 800);
    Utility.writeFile(Paths.get("output", name + ".svg").toString(), svg);
  }

  @Test
  public void testSimple() {
    var geoJsonList = GeoJson.read (Paths.get("data", "simple.json").toString());
    drawSvg ("simple", geoJsonList);
    assertEquals(1, geoJsonList.size());
    var geoJson = geoJsonList.get(0);
    assertEquals ("Simple", geoJson.properties.getString("name"));
    Domain domain = new Domain ()
            .add(PT (-20, 20))
            .add(PT (-10, 10));
    Assertions.assertSimilar (domain, geoJson.domain);

    // check some sample points for containment
    assertTrue (geoJson.contains (PT (-15, 15)));
    assertFalse (geoJson.contains (PT (-5, 15)));
    assertFalse (geoJson.contains (PT (-25, 15)));
    assertFalse (geoJson.contains (PT (-15, 5)));
    assertFalse (geoJson.contains (PT (-15, 25)));
  }

  @Test
  public void testSimpleWithHole() {
    var geoJsonList = GeoJson.read (Paths.get("data", "simple_with_hole.json").toString());
    drawSvg ("simple_with_hole", geoJsonList);
    assertEquals(1, geoJsonList.size());
    var geoJson = geoJsonList.get(0);
    assertEquals ("Simple With Hole", geoJson.properties.getString("name"));
    Domain domain = new Domain ()
            .add(PT (-20, 20))
            .add(PT (-10, 10));
    Assertions.assertSimilar (domain, geoJson.domain);

    // check some sample points for containment
    assertTrue (geoJson.contains (PT (-17, 15)));
    assertTrue (geoJson.contains (PT (-15, 17)));
    assertTrue (geoJson.contains (PT (-15, 13)));
    assertTrue (geoJson.contains (PT (-13, 15)));

    assertFalse (geoJson.contains (PT (-15, 15)));
    assertFalse (geoJson.contains (PT (-5, 15)));
    assertFalse (geoJson.contains (PT (-25, 15)));
    assertFalse (geoJson.contains (PT (-15, 5)));
    assertFalse (geoJson.contains (PT (-15, 25)));
  }

  @Test
  public void testTexas() {
    var geoJsonList = GeoJson.read (Paths.get("data", "texas.json").toString());
    drawSvg ("texas", geoJsonList);
    assertEquals(1, geoJsonList.size());
    var geoJson = geoJsonList.get(0);
    assertEquals ("Texas", geoJson.properties.getString("name"));

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
  }

  @Test
  public void testUsa() {
    var geoJsonList = GeoJson.read (Paths.get("data", "usa.json").toString());
    drawSvg ("usa", geoJsonList);
  }

  @Test
  public void testStates() {
    var geoJsonList = GeoJson.read (Paths.get("data", "gz_2010_us_states.json").toString());
    drawSvg ("states", geoJsonList);
  }

  @Test
  public void testBadData() {
    var geoJsonList = GeoJson.read (Paths.get("data", "bad_data.json").toString());
    drawSvg ("bad_data", geoJsonList);
  }
}
