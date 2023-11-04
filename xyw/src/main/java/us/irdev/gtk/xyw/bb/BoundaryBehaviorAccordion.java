package us.irdev.gtk.xyw.bb;

import us.irdev.gtk.xyw.Numerics;

/**
 * remap coordinates outside the domain to the edge of the domain
 */
public class BoundaryBehaviorAccordion extends BoundaryBehavior {
  @Override
  public double condition (double val, double min, double max) {
    if (min == 0) {
      assert((val > Double.NEGATIVE_INFINITY) && (val < Double.POSITIVE_INFINITY));
      assert(! Numerics.similar (min, max));
      assert(max > 0);
      double wrap = max * 2;
      val = (val < 0) ? -val : val;
      // we typically assume the input coordinate value is close to the domain, so this loop will not
      // execute many times - if that's not true it may be more useful to implement the modular
      // arithmetic
      while (val > wrap) val -= wrap;
      val = (val > max) ? wrap - val : val;
    } else {
      val = condition (val - min, 0, max - min) + min;
    }
    return val;
  }
}
