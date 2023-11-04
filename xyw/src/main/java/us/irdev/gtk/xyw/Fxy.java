package us.irdev.gtk.xyw;

/**
 * A general interface defining a function in a 2d coordinate space.
 */
public interface Fxy {
  /**
   * @param xy a tuple (x, y) of where to provide the functional evaluation
   * @return the value of the function at the requested xy location
   */
  double f (Tuple xy);
}
