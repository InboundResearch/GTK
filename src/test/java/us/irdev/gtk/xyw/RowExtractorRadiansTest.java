package us.irdev.gtk.xyw;

import us.irdev.gtk.xyw.db.Row;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static us.irdev.gtk.xyw.Helper.assertSimilar;
import static us.irdev.gtk.xyw.Tuple.PT;

public class RowExtractorRadiansTest {
  @Test
  public void testRowExtractorRadians () {
    RowExtractorRadians extractor = new RowExtractorRadians ("x", "y", "v");
    Map<String, String> entry = new HashMap<String, String>();
    entry.put("x", "1.5"); entry.put ("y", "3.6"); entry.put ("v", "36.5");
    Row row = extractor.fromEntry(entry);
    assertSimilar(PT (Math.toRadians(1.5), Math.toRadians(3.6)), row.xy);
    assertSimilar(Math.toRadians(36.5), row.value);
  }
}
