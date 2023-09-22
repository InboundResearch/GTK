package us.irdev.gtk.xy.db;

import java.util.Map;

import static java.lang.Double.parseDouble;

public class RowExtractor {
  protected String x, y, v;

  public RowExtractor (String x, String y, String v) {
    this.x = x; this.y = y; this.v = v;
  }

  public Row fromEntry (Map<String, String> entry) {
    return new Row(parseDouble(entry.get(x)), parseDouble(entry.get(y)), parseDouble(entry.get(v)));
  }
}
