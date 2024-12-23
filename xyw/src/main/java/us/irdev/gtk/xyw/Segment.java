package us.irdev.gtk.xyw;

import static us.irdev.gtk.xyw.Numerics.TOLERANCE;
import static us.irdev.gtk.xyw.Tuple.PT;

public class Segment {
  // two end points of the segment
  public final Tuple a;
  public final Tuple b;

  // common traits and the line equation
  public final Tuple v;
  public final Line line;

  public Segment (Tuple a, Tuple b) {
    this.a = a;
    this.b = b;

    // compute the line equation and traits
    v = b.subtract(a);
    line = Line.fromPointVector(a, v);
  }

  public Segment (double x1, double y1, double x2, double y2) {
    this (Tuple.PT (x1, y1), Tuple.PT (x2, y2));
  }

  public double lengthSq() {
    return v.normSq ();
  }

  public double length() {
    return v.norm ();
  }

  public boolean contains(Tuple point) {
    Tuple delta = point.subtract(a).hquotient(v);
    return (delta.x > -TOLERANCE) && (delta.y >= -TOLERANCE) && (delta.x <= 1 + TOLERANCE) && (delta.y <= 1 + TOLERANCE) && line.pointIsOnLine (point);
  }

  public Tuple mid() {
    return a.add(b).scale(0.5);
  }

  public Tuple lerp(double i) {
    //return a.add(b.subtract(a).scale(i));
    return Tuple.PT (Numerics.lerp(a.x, b.x, i), Numerics.lerp(a.y, b.y, i));
  }

  /**
   * compute the intersection point between two segments
   * @param s1 one of the pair of segments to test for intersection
   * @param s2 the second segments
   * @return the point where the segments cross, or null if they do not intersect (within their
   *         own lengths)
   */
  public static Tuple intersect(Segment s1, Segment s2) {
    // two segments intersect only if the endpoints of s1 are on opposite sides of the line s2
    if (Math.signum(s2.line.distanceToPoint (s1.a)) != Math.signum(s2.line.distanceToPoint(s1.b))) {
      // two segments intersect only if the endpoints of s2 are on opposite sides of the line s1
      if (Math.signum(s1.line.distanceToPoint (s2.a)) != Math.signum(s1.line.distanceToPoint(s2.b))) {
        // compute the actual point of intersection
        return Line.intersect(s1.line, s2.line);
      }
    }
    return null;
  }

  public static boolean similar (Segment a, Segment b) {
    return (a != null) && (b != null) && (
            (Tuple.similar(a.a, b.a) && Tuple.similar (a.b, b.b)) ||
            (Tuple.similar(a.a, b.b) && Tuple.similar (a.b, b.a))
    );
  }

  public static class Clip {
    public final Segment back;
    public final Segment on;
    public final Segment front;
    public Clip (Segment back, Segment on, Segment front) {
      this.back = back;
      this.on = on;
      this.front = front;
    }
  }

  /**
   * clip the segment according to whether it is in the back or front of the line, or both (spanning)
   * @param clipper the line to clip the segment against
   * @return a back, on, and front structure with the clipped segment parts. possible cases for the
   *         two endpoints are:
   *            - a is on, b is on         whole segment is on
   *            - a is on, b is back       whole segment is back
   *            - a is on, b is front      whole segment is front
   *
   *            - a is back, b is on       whole segment is back
   *            - a is back, b is back     whole segment is back
   *            - a is back, b is front    segment is split
   *
   *            - a is front, b is on      whole segment is front
   *            - a is front, b is back    segment is split
   *            - a is front, b is front   whole segment is front
   */
  public Clip clipToLine (Line clipper) {
    // classify each of the segment endpoints, and build a code for the clipping case
    int code = (clipper.classifyPoint(a) << 2) | clipper.classifyPoint(b);
    switch (code) {
      default:
      case 0b1111:
        // whole segment is on
        return new Clip (null, this, null);

      case 0b1110:
      case 0b1011:
      case 0b1010:
        // whole segment is back
        return new Clip (this, null, null);

      case 0b1101:
      case 0b0111:
      case 0b0101:
        // whole segment is front
        return new Clip (null, null, this);

      case 0b1001: {
        // a back, b front
        Tuple c = Line.intersect(clipper, line);
        return new Clip (new Segment(a, c), null, new Segment(c, b));
      }

      case 0b0110: {
        // a front, b back
        Tuple c = Line.intersect(clipper, line);
        return new Clip (new Segment (c, b), null, new Segment(a, c));
      }
    }
  }

  @Override
  public String toString () {
    return String.format("[%s, %s]", a.toString(), b.toString());
  }

}
