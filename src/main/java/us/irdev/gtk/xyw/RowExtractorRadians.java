package us.irdev.gtk.xyw;

import us.irdev.gtk.xyw.db.Row;
import us.irdev.gtk.xyw.db.RowExtractor;

import java.util.Map;

import static java.lang.Double.parseDouble;

public class RowExtractorRadians extends RowExtractor {
  public RowExtractorRadians (String x, String y, String v) {
    super (x, y, v);
  }

  @Override
  public Row fromEntry (Map<String, String> entry) {
    return new Row(
            Math.toRadians(parseDouble(entry.get(x))),
            Math.toRadians(parseDouble(entry.get(y))),
            Math.toRadians(parseDouble(entry.get(v)))
    );
  }
}
