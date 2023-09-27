package us.irdev.gtk.xyw.bb;

public abstract class BoundaryBehavior {
  public abstract double condition(double val, double min, double max);
  public double adjustDomainMax (double min, double max, double increment) {
    return 0;
  }
}
