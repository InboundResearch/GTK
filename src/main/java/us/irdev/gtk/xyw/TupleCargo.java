package us.irdev.gtk.xyw;

import java.util.HashMap;
import java.util.Map;

public class TupleCargo extends Tuple {
  private final Map<String, Object> cargo;

  public TupleCargo (double x, double y, double w) {
    super (x, y, w);
    cargo = new HashMap<>();
  }

  public TupleCargo (Tuple xyw) {
    this (xyw.x, xyw.y, xyw.w);
  }

  public TupleCargo put(String name, Object value) {
    cargo.put(name, value);
    return this;
  }

  public Object get(String name, Object defaultValue) {
    return cargo.getOrDefault(name, defaultValue);
  }

  public Object get(String name) {
    return get (name, null);
  }

  public double getDouble (String name) {
    return (Double) get (name);
  }

  public static TupleCargo PTC(double x, double y) {
    return new TupleCargo(x, y, 1);
  }

  public static TupleCargo PTC(Tuple xy) {
    // you probably don't mean to truncate an unprojected tuple
    assert(Numerics.similar (0, xy.w) || Numerics.similar (1, xy.w));
    return new TupleCargo(xy.x, xy.y, 1);
  }

  public static TupleCargo VECC (double x, double y) {
    return new TupleCargo (x, y, 0);
  }

  public static TupleCargo VECC (Tuple xy) {
    // you probably don't mean to truncate an unprojected tuple
    assert(Numerics.similar (0, xy.w) || Numerics.similar (1, xy.w));
    return new TupleCargo (xy.x, xy.y, 1);
  }

  public static TupleCargo TC(Tuple xyw) {
    return new TupleCargo(xyw);
  }
}
