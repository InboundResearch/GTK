package us.irdev.gtk.xyw;

import static java.lang.Math.abs;

public class Numerics {
  /**
   * @param a - f(0)
   * @param b - f(1)
   * @param x - value in the range [0..1]
   * @return the linear interpolation of a = f(0) and b = f(1) at f(x)
   */
  public static double lerp (double a, double b, double x) {
    assert ((x >= 0) && (x <= 1));
    return (b * x) + (a * (1.0 - x));
  }

  /**
   * @param a - coordinate value a
   * @param fa - f(a)
   * @param b - coordinate value b
   * @param fb - f(b)
   * @param x - value in the range [a..b]
   * @return the linear interpolation of a = f(a) and b = f(b) at f(x)
   */
  public static double lerp (double a, double fa, double b, double fb, double x) {
    // recast x into the range [0..1]
    return lerp(a, b, (x - a) / (b - a));
  }

  /**
   * dual of lerp, fx rests between fa = f(a) and fb = f(b), we want to know x such that f(x) = fx
   * @param fx - f(x)
   * @param a - coordinate value a
   * @param fa - f(a)
   * @param b - coordinate value b
   * @param fb - f(b)
   * @return x
   */
  public static double where (double fx, double a, double fa, double b, double fb) {
    return lerp (a, b, (fx - fa) / (fb - fa));
  }

  /**
   * ---- floating-point comparisons ------
   * floating-point arithmetic using limited storage space results in very small errors. we use two
   * different strategies to cope with these errors when comparing two numbers:
   *  - discretized tolerances with the expectation our numbers are not near extremal values, and
   *  - error tolerances relative to the range of the numbers being compared
   *
   * note that we do not want to base our comparisons against the *machine* epsilon, or the smallest
   * value a double-precision floating point number can represent.
   * /

  /**
   * for tolerance computations, we use a number typically referred to as epsilon, a small allowed
   * difference in value that prevents us running up against numerical accuracy errors after a chain
   * of computations in floating-point arithmetic.
   *
   * in a geodetic coordinate space, the following tolerances are sufficient to track ground
   * locations to:
   *
   *      1.0e-9:  40mm
   *      1.0e-10: 4mm
   *      1.0e-11: 400um
   *      1.0e-12: 40um
   *
   * note that tolerance tests with double-precision math really starts to break down around 15
   * orders of magnitude difference between the values, so setting the tolerance to 1.0e-12 and
   * adding tolerance to numbers with only 3 orders of magnitude between them will not pass
   * tolerance tests (e.g > 1.0e3).
   */
  public static final int TOLERANCE_ORDER_OF_MAGNITUDE = -10;
  public static final double TOLERANCE = Math.pow(10, TOLERANCE_ORDER_OF_MAGNITUDE);

  /**
   * compare two numbers for equality, when a and b are similar magnitude and not extremal values.
   *
   * @param a - first value to compare
   * @param b - second value to compare
   * @param tolerance - space allowed to consider a == b
   * @return true if (a == b) within the given tolerance
   */
  public static boolean withinTolerance(double a, double b, double tolerance) {
    return (abs(a - b) <= tolerance);
  }

  /**
   * compare two numbers for equality, when a and b are similar magnitude and not extremal values.
   *
   * @param a - first value to compare
   * @param b - second value to compare
   * @return true if (a == b) within the default tolerance
   */
  public static boolean withinTolerance(double a, double b) {
    return withinTolerance(a, b, TOLERANCE);
  }

  /**
   * when doing epsilon testing, we want to make sure we are well above the numerical precision of
   * the double type if the values being compared are very small, we add just a little bit of buffer
   */
  public static final double A_LITTLE_BIT = 1.0;

  /**
   * compare two numbers for equality, ignoring that last few bits of precision.
   *
   * @param a - first value to compare
   * @param b - second value to compare
   * @return true if (a == b) within our tolerances
   */
  public static boolean similar (double a, double b) {
    return (a == b) || (abs (a - b) <= ((abs (a) + abs (b) + A_LITTLE_BIT) * TOLERANCE));
  }
}
