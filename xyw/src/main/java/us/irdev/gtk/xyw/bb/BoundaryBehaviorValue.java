package us.irdev.gtk.xyw.bb;

/**
 * coordinates outside the domain are unmodified
 */
public class BoundaryBehaviorValue extends BoundaryBehavior {
  @Override
  public double condition (double val, double min, double max) {
    return val;
  }
}
