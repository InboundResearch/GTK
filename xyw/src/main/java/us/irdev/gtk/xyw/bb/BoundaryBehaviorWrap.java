package us.irdev.gtk.xyw.bb;

import us.irdev.gtk.xyw.Numerics;

/**
 * remap coordinates outside the domain to the opposite side of the domain in circular fashion, like
 * a clock (modular arithmetic).
 */
public class BoundaryBehaviorWrap extends BoundaryBehavior {

  @Override
  public double condition (double val, double min, double max) {
    assert((val > Double.NEGATIVE_INFINITY) && (val < Double.POSITIVE_INFINITY));
    assert(! Numerics.similar (min, max));
    assert(max > min);
    double delta = max - min;
    // we typically assume the input coordinate value is close to the domain, so this loop will not
    // execute many times - if that's not true it may be more useful to implement the modular
    // arithmetic
    while (val < min) val += delta;
    while (val >= max) val -= delta;
    return val;
  }

  @Override
  public double adjustDomainMax (double min, double max, double increment) {
    return increment;
  }
}
