package us.irdev.gtk.xyw;

import static us.irdev.gtk.xyw.Tuple.PT;
import static us.irdev.gtk.xyw.Tuple.VEC;

public class Line {
  public final Tuple abc;

  /**
   * constructor from the line equation tuple
   * @param abc the coefficients to the line equation ax + by + c = 0 stored as a tuple
   */
  public Line(Tuple abc) {
    this.abc = abc;
  }

  /**
   * constructor from the line equation components: ax + by + c = 0
   */
  public Line(double a, double b, double c) {
    // normalize the line equation so that the normal vector (n) points from the origin to the line
    // and c is the positive distance along n to the line from the origin. note this means the line
    // does not capture any sense of direction
    this (new Tuple (a, b, c));
  }

  /**
   * @return the normal vector (n), perpendicular to the line
   */
  public Tuple n() {
    return Tuple.VEC (abc.x, abc.y);
  }

  /**
   * @return the negative of the distance to the origin from the line along the normal vector (n)
   */
  public double c() {
    return abc.w;
  }

  /**
   * @return a line normalized so c is positive and the vector ab points towards the origin
   */
  public Line normalized () {
    return (abc.w < 0) ? new Line (abc.scale(-1)) : this;
  }

  /**
   * @param point a point on the line
   * @param direction the general direction of the line from the point (it is assumed to go both
   *                    along and against the direction)
   * @return the Line object constructed from the point and direction
   */
  public static Line fromPointVector(Tuple point, Tuple direction) {
    assert(direction.normSq() > 0);
    Tuple n = direction.perpendicular().normalized();
    return new Line (new Tuple (n.x, n.y, -point.dot(n)));
  }

  /**
   * @param x the x value for a vertical line proceeding up
   * @return the line object constructed from a normal and offset
   */
  public static Line verticalUp (double x) {
    return fromPointVector (PT(x, 0), Tuple.VEC (0, 1));
  }

  /**
   * @param x the x value for a vertical line proceeding down
   * @return the line object constructed from a normal and offset
   */
  public static Line verticalDown (double x) {
    return fromPointVector (PT(x, 0), Tuple.VEC (0, -1));
  }

  /**
   * @param y the y value for a horizontal line proceeding to the right
   * @return the line object constructed from a normal and offset
   */
  public static Line horizontalRight (double y) {
    return fromPointVector (PT(0, y), Tuple.VEC (1, 0));
  }

  /**
   * @param y the y value for a horizontal line proceeding to the left
   * @return the line object constructed from a normal and offset
   */
  public static Line horizontalLeft (double y) {
    return fromPointVector (PT(0, y), Tuple.VEC (-1, 0));
  }

  /**
   * @param a a point on the line
   * @param b a second point on the line
   * @return the Line object constructed from the two points
   */
  public static Line fromTwoPoints(Tuple a, Tuple b) {
    assert(!Tuple.similar (a, b));
    return fromPointVector (a, b.subtract(a));
  }

  /**
   * @param slope the slope of the line from the form y = mx + b, if m is infinite, we assume this
   *                means a vertical line
   * @param intercept the y-intercept of the line from the form y = mx + b, unless the slope is
   *                    infinite, in which case we use this as the x-intercept
   * @return the Line object constructed from the slope and intercept
   */
  public static Line fromSlopeIntercept (double slope, double intercept) {
    return (slope == Double.POSITIVE_INFINITY) ? verticalUp(intercept) :
           (slope == Double.NEGATIVE_INFINITY) ? verticalDown(intercept) :
            fromTwoPoints (PT(-1, -slope + intercept), PT(1, slope + intercept));
  }

  /**
   * @return the slope of the line for the y = mx + b slope/intercept form
   */
  public double m () {
    return Numerics.similar(abc.y, 0) ?
            (abc.x < 0) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY :
            -abc.x / abc.y;
  }

  /**
   * @return the y-intercept of the line for the y = mx + b slope/intercept form. if m is infinite,
   * we return the x-intercept.
   */
  public double b () {
    return -abc.w / (Numerics.similar(abc.y, 0) ? abc.x : abc.y);
  }

  /**
   * @return the point on the line that is closest to the origin
   */
  public Tuple origin () {
    return PT(n().scale(-c()));
  }

  /**
   * @param p a point in space
   * @return the shortest distance from the line to the point p along n, the "normal" vector
   */
  public double distanceToPoint (Tuple p) {
    return p.dot(abc);
  }

  /**
   * @param p a point to test for being on the line
   * @return true if the point is on the line, false otherwise
   */
  public boolean pointIsOnLine(Tuple p) {
    return Numerics.similar (0, distanceToPoint(p));
  }

  /**
   * compute the intersection point between two lines
   * @param l1 one of the pair of lines to test for intersection
   * @param l2 the second line
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
    return l1.abc.cross(l2.abc).projectToPoint();
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
   * @param a a line to compare
   * @param b a line to compare
   * @return true if the two lines are similar
   */
  public static boolean similar(Line a, Line b) {
    return (a != null) && (b != null) && Tuple.similar (a.abc, b.abc);
  }
}
