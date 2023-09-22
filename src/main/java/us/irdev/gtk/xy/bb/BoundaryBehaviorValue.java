package us.irdev.gtk.xy.bb;

public class BoundaryBehaviorValue extends BoundaryBehavior {
  @Override
  public double condition (double val, double min, double max) {
    return val;
  }
}
