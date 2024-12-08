package us.irdev.gtk.geography;

import org.junit.jupiter.api.Test;
import us.irdev.gtk.xyw.Assertions;
import us.irdev.gtk.xyw.Domain;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static us.irdev.gtk.xyw.Assertions.assertSimilar;
import static us.irdev.gtk.xyw.Tuple.PT;

public class GeoJsonTest {
  @Test
  public void testSimple() {
    var geoJson = GeoJson.read (Paths.get("data", "test.geojson").toString());
    assertEquals ("Test", geoJson.name);
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
  public void testTexas() {
    var geoJson = GeoJson.read (Paths.get("data", "texas.geojson").toString());
    assertEquals ("Texas", geoJson.name);
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
}
