package us.irdev.gtk.xyw.db;

import us.irdev.gtk.xyw.Tuple;

import static us.irdev.gtk.xyw.Tuple.PT;

/**
 * a row in a database of sampled function values.
 */
public class Row {
  /**
   * the XY coordinate values for the sampled data
   */
  public final Tuple xy;

  /**
   * the sampled data value at the specified coordinate
   */
  public final double value;

  /**
   * constructor
   * @param xy the xy coordinate for the sample
   * @param value the value of the sampled function at the coordinate
   */
  public Row (Tuple xy, double value) {
    this.xy = xy;
    this.value = value;
  }

  /**
   * constructor
   * @param x the x coordinate for the sample
   * @param y the y coordinate for the sample
   * @param value the value of the sampled function at the coordinate
   */
  public Row (double x, double y, double value) {
    this(PT (x, y), value);
  }

  /**
   * generate the parsable text format for the row (so we can round-trip files)
   */
  @Override
  public String toString () {
    return String.format ("%.06f,%.06f,%.06f", xy.x, xy.y, value);
  }
}
