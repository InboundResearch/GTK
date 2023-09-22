package us.irdev.gtk.xy.db;

import us.irdev.gtk.xy.Tuple;

public class Row {
  public Tuple xy;
  public double value;

  public Row (Tuple xy, double value) {
    this.xy = xy;
    this.value = value;
  }

  public Row (double x, double y, double value) {
    this(new Tuple (x, y), value);
  }

  @Override
  public String toString () {
    return String.format ("%.06f,%.06f,%.06f", xy.x, xy.y, value);
  }
}
