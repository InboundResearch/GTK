package us.irdev.gtk.xy.bb;

public class BoundaryBehaviorClamp extends BoundaryBehavior {
  @Override
  public double condition (double val, double min, double max) {
    if (val > max) val = max;
    if (val < min) val = min;
    return val;
  }
}
