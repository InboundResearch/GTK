package us.irdev.gtk.xy;

import static us.irdev.gtk.xy.Numerics.TOLERANCE;

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
    this (new Tuple(x1, y1), new Tuple(x2, y2));
  }

  public double lengthSq() {
    return v.normSq ();
  }

  public double length() {
    return v.norm ();
  }

  public boolean pointIsInSegment(Tuple point) {
    assert (line.pointIsOnLine (point));
    Tuple delta = point.subtract(a).hquotient(v);
    return (delta.x > -TOLERANCE) && (delta.y >= -TOLERANCE) && (delta.x <= 1 + TOLERANCE) && (delta.y <= 1 + TOLERANCE);
  }

  public Tuple mid() {
    return a.add(b).scale(0.5);
  }

  public Tuple lerp(double i) {
    return new Tuple (Numerics.lerp(a.x, b.x, i), Numerics.lerp(a.y, b.y, i));
  }

  /**
   * compute the intersection point between two segments
   * @param s1 - one of the pair of segments to test for intersection
   * @param s2 - the second segments
   * @return the point where the segments cross, or null if they do not intersect (within their
   *         own lengths)
   */
  public static Tuple intersect(Segment s1, Segment s2) {
    // compute the cosine of the closing angle between the lines times the length of the segment
    double vCosTheta = -(s1.v.dot (s2.line.n));

    // check if the two lines are not parallel
    if (Numerics.similar (vCosTheta, 0)) {
      return null;
    }

    // compute the "time" of intersection as a scalar on v, and return the segment origin plus the
    // scaled version of v
    double t = s2.line.distanceToPoint(s1.a) / vCosTheta;
    return s1.a.add (s1.v.scale(t));
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
   * @param clipper - the line to clip the segment against
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



/*
  public interface Access {
    double get (Tuple tuple);
  }
  public interface Compare {
    boolean keep (double a);
  }

  public int classifyPoint(Tuple p) {
    double distance = distanceToPoint(p);
    return Numerics.similar(distance, 0) ? Line.PointClassification.ON :
            (distance < 0) ? Line.PointClassification.LEFT : Line.PointClassification.RIGHT;
  }


  public static Segment clipToAxis (Segment segment, double clip, Compare cmp, Access acc) {
    // note this is a simplified version of the more general clip
    if (segment != null) {
      // get the actual values we'll be comparing
      double a = acc.get (segment.a);
      double b = acc.get (segment.b);

      // there are four possible scenarios for the two points of the segment:
      int code = (cmp.keep (a) ? 0b0001 : 0b0000) | (cmp.keep (b) ? 0b0010 : 0b0000);
      switch (code) {
        default:
        case 0b0000:
          // * both out -> return null (the whole segment is clipped away)
          return null;

        case 0b011:
          // * both in -> keep the original segment
          return segment;

        case 0b0001:
          // * a in, b out -> return new segment ac, where c is the computed intersection point
          return new Segment (segment.a, segment.lerp ((clip - a) / (b - a)));

        case 0b0010:
          // * a out, b in -> return new segment cb, where c is the computed intersection point
          return new Segment (segment.lerp ((clip - a) / (b - a)), segment.b);
      }
    }
    return null;
  }
  */

  @Override
  public String toString () {
    return String.format("[%s, %s]", a.toString(), b.toString());
  }

}
