package us.irdev.gtk.xyw.db;

import us.irdev.gtk.xyw.Tuple;

import static us.irdev.gtk.xyw.Tuple.PT;

public class Row {
  public Tuple xy;
  public double value;

  public Row (Tuple xy, double value) {
    this.xy = xy;
    this.value = value;
  }

  public Row (double x, double y, double value) {
    this(PT (x, y), value);
  }

  @Override
  public String toString () {
    return String.format ("%.06f,%.06f,%.06f", xy.x, xy.y, value);
  }
}
