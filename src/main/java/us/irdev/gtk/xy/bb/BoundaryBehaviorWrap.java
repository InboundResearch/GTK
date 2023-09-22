package us.irdev.gtk.xy.bb;

import static us.irdev.gtk.xy.Numerics.similar;

public class BoundaryBehaviorWrap extends BoundaryBehavior {

  @Override
  public double condition (double val, double min, double max) {
    assert((val > Double.NEGATIVE_INFINITY) && (val < Double.POSITIVE_INFINITY));
    assert(! similar (min, max));
    double delta = max - min;
    while (val < min) val += delta;
    while (val >= max) val -= delta;
    return val;
  }

  @Override
  public double adjustDomainMax (double min, double max, double increment) {
    return increment;
  }
}
