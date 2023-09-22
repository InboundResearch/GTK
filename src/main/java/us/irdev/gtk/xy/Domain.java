package us.irdev.gtk.xy;

import java.util.ArrayList;
import java.util.List;

public class Domain {
  public Tuple min;
  public Tuple max;

  /**
   * default constructor - start with a deliberately empty domain (contains nothing)
   */
  public Domain () {
    min = new Tuple (Double.MAX_VALUE);
    max = new Tuple (-Double.MAX_VALUE);
  }

  /**
   * constructor from some points
   * @param pts - variable length argument list of points to add
   */
  public Domain (Tuple ... pts) {
    this();
    for (Tuple pt: pts) {
      add (pt);
    }
  }

  /**
   * constructor from min and max values
   * @param minX, maxX, minY, maxY - the four boundaries of the domain
   */
  public Domain (double minX, double maxX, double minY, double maxY) {
    this (new Tuple (minX, minY), new Tuple (maxX, maxY));
  }

  /**
   * add a point to the domain, expanding the boundaries as needed
   * @param pt - the point to add to the domain
   * @return self-reference so this call can be chained
   */
  public Domain add(Tuple pt) {
    min = new Tuple (Math.min(min.x, pt.x), Math.min(min.y, pt.y));
    max = new Tuple (Math.max(max.x, pt.x), Math.max(max.y, pt.y));
    return this;
  }

  /**
   * @return the vector size of the domain
   */
  public Tuple size () {
    return max.subtract(min);
  }

  /**
   * @return the scalar size of the domain
   */
  public double span () {
    return size().norm();
  }

  /**
   * @return the midpoint, center, or centroid of the domain
   */
  public Tuple center() {
    return min.add(max).scale(0.5);
  }

  public double top() {
    return max.y;
  }

  public double bottom() {
    return min.y;
  }

  public double left() {
    return min.x;
  }

  public double right() {
    return max.x;
  }

  /**
   * compare two domains for equivalency
   * @param d1  - the first domain of the pair
   * @param d2 - the second domain of the pair
   * @return true if the two domains are suubstantively similar
   */
  public static boolean similar(Domain d1, Domain d2) {
    return (d1 != null) && (d2 != null) && Tuple.similar (d1.min, d2.min) && Tuple.similar (d1.max, d2.max);
  }

  // -----------------------------------------------------------------------------------------------
  // bounds-like methods
  // -----------------------------------------------------------------------------------------------

  /**
   * compute the union of two domains
   * @param d1 - the first domain in the union
   * @param d2 - the second domain in the union
   * @return the domain that resents d1 || d2
   */
  public static Domain union(Domain d1, Domain d2) {
    return new Domain (
            Math.min(d1.min.x, d2.min.x),
            Math.max(d1.max.x, d2.max.x),
            Math.min(d1.min.y, d2.min.y),
            Math.max(d1.max.y, d2.max.y)
    );
  }

  /**
   * compute the intersection of two domains
   * @param d1 - the first domain in the intersection
   * @param d2 - the second domain in the intersection
   * @return the domain that resents d1 && d2
   */
  public static Domain intersection(Domain d1, Domain d2) {
    return new Domain (
            Math.max(d1.min.x, d2.min.x),
            Math.min(d1.max.x, d2.max.x),
            Math.max(d1.min.y, d2.min.y),
            Math.min(d1.max.y, d2.max.y)
    );
  }

  /**
   * @param point - the point to check for containment
   * @return true if the point is within the bounds
   */
  public boolean contains (Tuple point) {
    return (point.x >= min.x) && (point.x <= max.x) && (point.y >= min.y) && (point.y <= max.y);
  }

  /**
   * @return a copy of the domain scaled by the requested factor around the center of the domain
   */
  public Domain scale (double factor) {
    Tuple center = center ();
    Tuple half = max.subtract(center).scale (factor);
    return new Domain (center.subtract(half), center.add(half));
  }

  /**
   * clip a segment to the parts that are inside axis-aligned edges of the domain
   * @param segment - the input segment to clip
   * @return the clipped segment or null if no part of the segment is within the domain
   */
  public Segment clip (Segment segment) {
    /*
    // clip to each edge in turn...
    segment = clip (segment, min.x, a -> a >= min.x, t -> t.x);
    segment = clip (segment, min.y, a -> a >= min.y, t -> t.y);
    segment = clip (segment, max.x, a -> a <= max.x, t -> t.x);
    segment = clip (segment, max.y, a -> a <= max.y, t -> t.y);
    return segment;
    */
    //segment =
    return segment;
  }

  /**
   * clip a list of segments to yield a new list
   * @param segments - list of segments to clip
   * @return a list of potentially clipped segments that are within the domain
   */
  public List<Segment> clip (List<Segment> segments) {
    List<Segment> result = new ArrayList<>();
    for (Segment segment: segments) {
      if ((segment = clip (segment)) != null) {
        result.add (segment);
      }
    }
    return (result.size() > 0) ? result : null;
  }

  public static List<Tuple> intersect(List<Segment> s1, List<Segment> s2) {
    return null;
  }
}
