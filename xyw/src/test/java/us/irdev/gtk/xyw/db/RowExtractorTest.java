package us.irdev.gtk.xyw.db;

import org.junit.jupiter.api.Test;
import us.irdev.gtk.xyw.Tuple;

import java.util.HashMap;

import static us.irdev.gtk.xyw.Helper.assertSimilar;
import static us.irdev.gtk.xyw.Tuple.PT;

public class RowExtractorTest {
  @Test
  public void testRowExtractor () {
    var extractor = new  RowExtractor ("x", "y", "v");
    var entry = new HashMap<String, String> ();
    entry.put("x", "1.5"); entry.put ("y", "3.6"); entry.put ("v", "36.5");
    Row row = extractor.fromEntry(entry);
    assertSimilar(PT(1.5, 3.6), row.xy);
    assertSimilar(36.5, row.value);
  }
}
