package us.irdev.gtk.xyw.bb;

import us.irdev.gtk.xyw.Numerics;

public class BoundaryBehaviorAccordion extends BoundaryBehavior {
  @Override
  public double condition (double val, double min, double max) {
    if (min == 0)
    {
      assert((val > Double.NEGATIVE_INFINITY) && (val < Double.POSITIVE_INFINITY));
      assert(! Numerics.similar (min, max));
      double wrap = max * 2;
      val = (val < 0) ? -val : val;
      while (val > wrap) val -= wrap;
      val = (val > max) ? wrap - val : val;
    } else {
      val = condition (val - min, 0, max - min) + min;
    }
    return val;
  }
}
