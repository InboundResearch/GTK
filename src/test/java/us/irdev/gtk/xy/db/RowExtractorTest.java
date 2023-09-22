package us.irdev.gtk.xy.db;

import org.junit.jupiter.api.Test;
import us.irdev.gtk.xy.Tuple;

import java.util.HashMap;
import java.util.Map;

import static us.irdev.gtk.xy.Helper.assertSimilar;

public class RowExtractorTest {
  @Test
  public void testRowExtractor () {
    RowExtractor extractor = new RowExtractor ("x", "y", "v");
    Map<String, String> entry = new HashMap<String, String> ();
    entry.put("x", "1.5"); entry.put ("y", "3.6"); entry.put ("v", "36.5");
    Row row = extractor.fromEntry(entry);
    assertSimilar(new Tuple (1.5, 3.6), row.xy);
    assertSimilar(36.5, row.value);
  }
}
