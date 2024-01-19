package us.irdev.gtk.xyw;

import static us.irdev.gtk.xyw.Tuple.PT;

/**
 * A general base class for a function with derivative in a 2d coordinate space.
 */
public abstract class Function implements Fxy {
  /**
   * @param xy a tuple with coordinates in the domain
   * @return an At, containing the value of the function and the 1st derivatives at xy
   */
  abstract public At at (Tuple xy);

  /**
   * @param x x-coordinate to evaluate in the domain
   * @param y y-coordinate to evaluate in the domain
   * @return the value of the function and the 1st derivatives at (x, y)
   */
  public At at (double x, double y) {
    return at (Tuple.PT (x, y));
  }

  /**
   * @param xy a tuple with coordinates in the domain
   * @return the value of the function at xy
   */
  public double f (Tuple xy) {
    return at (xy).f;
  }

  /**
   * @param x x-coordinate to evaluate in the domain
   * @param y y-coordinate to evaluate in the domain
   * @return the value of the function at (x, y)
   */
  public double f (double x, double y) {
    return at (Tuple.PT (x, y)).f;
  }

  /**
   * @param xy a tuple with coordinates in the domain
   * @return the 1st derivatives of the function at xy
   */
  public Tuple dxdy (Tuple xy) {
    return at (xy).dxdy;
  }

  /**
   * @param x x-coordinate to evaluate in the domain
   * @param y y-coordinate to evaluate in the domain
   * @return the 1st derivatives of the function at xy
   */
  public Tuple dxdy (double x, double y) {
    return at (Tuple.PT (x, y)).dxdy;
  }
}
