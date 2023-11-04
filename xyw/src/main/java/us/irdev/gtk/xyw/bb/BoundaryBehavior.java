package us.irdev.gtk.xyw.bb;

/**
 * an abstract base class for determining the behavior of the coordinate system at the edges of
 * the domain of a given function. coordinate values outside the domain are (possibly) remapped into
 * the domain in the conditioning process.
 */
public abstract class BoundaryBehavior {
  /**
   * @return the (possibly modified) coordinate value to use
   * @param val the input coordinate value
   * @param min the bottom of the domain
   * @param max the top of the domain
   */
  public abstract double condition(double val, double min, double max);

  /**
   * @return the adjusted value to be used as the top of the domain
   * @param min the bottom of the domain
   * @param max the top of the domain
   * @param increment the grid spacing between samples in a sampled function
   */
  public double adjustDomainMax (double min, double max, double increment) {
    return 0;
  }
}
