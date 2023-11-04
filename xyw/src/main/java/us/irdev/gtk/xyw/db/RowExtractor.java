package us.irdev.gtk.xyw.db;

import java.util.Map;

import static java.lang.Double.parseDouble;

public class RowExtractor {
  protected final String x, y, v;

  public RowExtractor (String x, String y, String v) {
    this.x = x; this.y = y; this.v = v;
  }

  public Row fromEntry (Map<String, String> entry) {
    try {
      String xv = entry.get (x);
      String yv = entry.get (y);
      String vv = entry.get (v);
      if ((xv != null) && (yv != null) && (vv != null)) {
        return new Row (parseDouble (xv), parseDouble (yv), parseDouble (vv));
      }
    } catch (Exception ignored) {}
    return null;
  }
}
