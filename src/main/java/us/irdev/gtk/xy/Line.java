package us.irdev.gtk.xy;

public class Line {
  public final Tuple n;
  public final double c;

  /**
   * constructor from the normal vector and distance to the origin. this is the literal expression
   * of the line equation ax + by + c where n = (a, b)
   * @param n - the normal vector for the line (perpendicular to the line) the vector (a, b) where
   *            a and b are the coefficients to the line equation ax + by + c = 0
   * @param c - the negative of the distance from the origin to the line along n, from the line
   *            equation ax + by + c = 0
   */
  public Line(Tuple n, double c) {
    // normalize the line equation so that the normal vector (n) points from the origin to the line
    // and c is the positive distance along n to the line from the origin. note this means the line
    // does not capture any sense of direction
    if (c < 0) {
      this.n = n.scale(-1.0);
      this.c = -c;
    } else {
      this.n = n;
      this.c = c;
    }
  }

  /**
   * @param x - the x value for a vertical line
   * @return the line object constructed from a normal and offset
   */
  public static Line vertical(double x) {
    return new Line (new Tuple (0, 1).perpendicular(), -x);
  }

  /**
   * @param y - the y value for a horizontal line
   * @return the line object constructed from a normal and offset
   */
  public static Line horizontal(double y) {
    return new Line (new Tuple (1, 0).perpendicular(), y);
  }

  /**
   * @param point - a point on the line
   * @param direction - the general direction of the line from the point (it is assumed to go both
   *                    along and against the direction)
   * @return the Line object constructed from the point and direction
   */
  public static Line fromPointVector(Tuple point, Tuple direction) {
    assert(direction.normSq() > 0);
    Tuple n = direction.perpendicular().normalized();
    return new Line (n, -point.dot(n));
  }

  /**
   * @param a - a point on the line
   * @param b - a second point on the line
   * @return the Line object constructed from the two points
   */
  public static Line fromTwoPoints(Tuple a, Tuple b) {
    assert(!Tuple.similar (a, b));
    return fromPointVector (a, b.subtract(a));
  }

  /**
   * @param slope - the slope of the line from the form y = mx + b, if m is infinite, we assume this
   *                means a vertical line
   * @param intercept - the y-intercept of the line from the form y = mx + b, unless the slope is
   *                    infinite, in which case we use this as the x-intercept
   * @return the Line object constructed from the slope and intercept
   */
  public static Line fromSlopeIntercept (double slope, double intercept) {
    if (slope == Double.POSITIVE_INFINITY) {
      return fromPointVector(new Tuple (intercept, 0), new Tuple (0, 1));
    }
    if (slope == Double.NEGATIVE_INFINITY) {
      return fromPointVector(new Tuple (intercept, 0), new Tuple (0, -1));
    }
    return fromTwoPoints (new Tuple (-1, -slope + intercept), new Tuple (1, slope + intercept));
  }

  /**
   * @return the slope of the line for the y = mx + b slope/intercept form
   */
  public double m () {
    return n.x / -n.y;
  }

  /**
   * @return the y-intercept of the line for the y = mx + b slope/intercept form. if m is infinite,
   * we return the x-intercept.
   */
  public double b () {
    return Numerics.similar(n.y, 0) ? c : c / -n.y;
  }

  /**
   * @return the point on the line that is closest to the origin
   */
  public Tuple origin () {
    return n.scale(-c);
  }

  /**
   * @param p - a point in space
   * @return the shortest distance from the line to the point p along n, the "normal" vector
   */
  public double distanceToPoint (Tuple p) {
    return p.dot(n) + c;
  }

  /**
   * @param p - a point to test for being on the line
   * @return true if the point is on the line, false otherwise
   */
  public boolean pointIsOnLine(Tuple p) {
    return Numerics.similar (0, distanceToPoint(p));
  }

  /**
   * compute the intersection point between two lines
   * @param l1 - one of the pair of lines to test for intersection
   * @param l2 - the second line
   * @return the point where the lines cross, or null if they do not intersect
   */
  public static Tuple intersect(Line l1, Line l2) {
    // using the homogenous coordinate formulation, representing the line equation ax + by + cz = 0
    // as the vector (a, b, c). the cross-product of the two line vectors gives the intersection
    // point:
    //     P' = (ai,bi,ci) = (a1, b1, c1) x (a2, b2, c2) = (b1c2-b2c1, a2c1-a1c2, a1b2-a2b1)
    // if ci is close to 0, the lines are parallel and no intersection is found
    // the 2d reprojection of the intersection point is: P(ai / ci, bi / ci)
    // see also: https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection
    double a1 = l1.n.x, b1 = l1.n.y, c1 = l1.c,
            a2 = l2.n.x, b2 = l2.n.y, c2 = l2.c;
    double z = (a1 * b2) - (a2 * b1); // also l1.n.cross(l2.n);
    return Numerics.similar(0.0, z) ?
            null :
            new Tuple (((b1 * c2) - (b2 * c1)) / z, ((a2 * c1) - (a1 * c2)) / z);
  }

  public static class Classification {
    public static final int BACK =  0b0010;
    public static final int ON =    0b0011;
    public static final int FRONT = 0b0001;
  }

  /**
   * classify a point with respect to what side of the line it is on. the normal vector for the line
   * equation points from the line towards the origin, and the "front" side is between the line and
   * the origin.
   */
  public int classifyPoint(Tuple p) {
    double distance = distanceToPoint(p);
    return Numerics.similar(distance, 0) ? Classification.ON :
            (distance < 0) ? Classification.BACK : Classification.FRONT;
  }

  /**
   * @param a - a line to compare
   * @param b - a line to compare
   * @return true if the two lines are similar
   */
  public static boolean similar(Line a, Line b) {
    return (a != null) && (b != null) && Tuple.similar (a.n, b.n) && Numerics.similar (a.c, b.c);
  }
}
