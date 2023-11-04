package us.irdev.gtk.xyw.db;

import us.irdev.gtk.xyw.*;

import java.util.*;
import java.util.stream.Collectors;

import static us.irdev.gtk.xyw.Tuple.PT;
import static us.irdev.gtk.xyw.Tuple.VEC;

public class Rows {
  public final List<Row> rows;
  public final Tuple interval;
  public final Domain domain;
  private final Set<Double> xValues, yValues;

  private static double computeInterval(Set<Double> values) {
    assert (values.size () > 1);
    double interval = Double.MAX_VALUE;
    double origin = values.stream().findFirst().get();
    for (Double value: values) {
      double delta = value - origin;
      if ((delta > Numerics.TOLERANCE) && (delta < interval)) interval = delta;
    }
    return interval;
  }

  public Rows (List<Row> rows) {
    assert (!rows.isEmpty ());
    this.rows = rows;

    // gather the set of values in the x and y coordinates
    xValues = new HashSet<> ();
    yValues = new HashSet<> ();
    for (Row row : rows) {
      xValues.add(row.xy.x);
      yValues.add(row.xy.y);
    }

    // compute the interval in x and y
    interval = VEC (computeInterval(xValues), computeInterval(yValues));

    // compute the domain in x and y
    domain = new Domain (
            xValues.stream().min(Double::compare).get(),
            xValues.stream().max(Double::compare).get(),
            yValues.stream().min(Double::compare).get(),
            yValues.stream().max(Double::compare).get()
    );
  }

  /**
   * a helper function to create a populated Database from a table (list of hashmaps)
   * @param table an array of map<String, String> (probably read in from a CSV file), representing
   *                a table of rows.
   * @param row instance of a row extractor class to take entries and turn them into rows
   * @return a new Database instance
   */
  public static Rows fromTable(List<Map<String, String>> table, RowExtractor row) {
    return new Rows (table.stream().map(row::fromEntry).collect(Collectors.toList()));
  }

  /**
   * a helper function to create a populated Database from a table (list of hashmaps). this method
   * provides a default extractor that simply parses doubles from the table entries.
   * @param table an array of map<String, String> (probably read in from a CSV file), representing
   *                a table of rows.
   * @param xName name of the map entry (in table) for the x-value for each row
   * @param yName  - name of the map entry (in table) for the y-value for each row
   * @param valueName  - name of the map entry (in table) for the sample-value for each row
   * @return a new Database instance
   */
  public static Rows fromTable(List<Map<String, String>> table, String xName, String yName, String valueName) {
    return fromTable(table, new RowExtractor(xName, yName, valueName));
  }

  public void addRow(double y, double value) {
    if (! yValues.contains (y)) {
      // add the row
      for (Double x : xValues) {
        Tuple xy = PT (x, y);
        domain.add(xy);
        rows.add (new Row (xy, value));
      }
    }
  }

  public void addColumn(double x, double value) {
    if (! xValues.contains (x)) {
      // add the row
      for (Double y : yValues) {
        Tuple xy = PT (x, y);
        domain.add(xy);
        rows.add (new Row (xy, value));
      }
    }
  }

  /**
   * create and fill a Database from a function
   * @param domain the domain to use for the fill
   * @param interval the spacing to use for the fill
   * @param fill the function to call for each point we want to sample
   * @return a Database with the samples for a function
   */
  public static Rows fromFxy (Domain domain, Tuple interval, Fxy fill) {
    List<Row> rows = new ArrayList<> ();
    for (double y = domain.min.y; y <= domain.max.y; y += interval.y) {
      for (double x = domain.min.x; x <= domain.max.x; x += interval.x) {
        Tuple xy = PT (x, y);
        rows.add (new Row (xy, fill.f (xy)));
      }
    }
    return new Rows (rows);
  }

  /**
   * create and fill a Database from a sampled function
   * @param sf the SampledFunction to use for the fill
   * @return a Database with the samples for a function
   */
  public static Rows fromSampledFunction (SampledFunction sf) {
    // XXX this needs to be tested that it doesn't do something strange....
    return fromFxy (sf.domain, sf.interval, sf);
  }

  /**
   * @param xName the name to print out for the x column
   * @param yName the name to print out for the y column
   * @param valueName the name to print out for the value column
   * @return a string with the database in the same CSV format we read from a table
   */
  public String toString(String xName, String yName, String valueName) {
    StringBuilder sb = new StringBuilder ();
    sb.append("# comment line with date in standard format goes here").append(System.lineSeparator());
    sb.append (xName).append(',').append(yName).append(',').append(valueName).append(System.lineSeparator());
    for (Row row: rows) {
      sb.append (row.toString()).append(System.lineSeparator());
    }
    return sb.toString();
  }
}
