package us.irdev.gtk.xyw;

import static us.irdev.gtk.xyw.Tuple.PT;

public class Domain {
  public Tuple min;
  public Tuple max;

  /**
   * default constructor - start with a deliberately empty domain (contains nothing)
   */
  public Domain () {
    min = PT (Double.MAX_VALUE, Double.MAX_VALUE);
    max = PT (-Double.MAX_VALUE, -Double.MAX_VALUE);
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
    this (PT (minX, minY), PT (maxX, maxY));
  }

  /**
   * add a point to the domain, expanding the boundaries as needed
   * @param pt - the point to add to the domain
   * @return self-reference so this call can be chained
   */
  public Domain add(Tuple pt) {
    min = PT (Math.min(min.x, pt.x), Math.min(min.y, pt.y));
    max = PT (Math.max(max.x, pt.x), Math.max(max.y, pt.y));
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
   * constant bitflags for qualifying a point in the domain - primarily of use to the cohen-
   * sutherland clipping algorithm
   */
  public static final int INSIDE = 0; // 0b0000
  public static final int LEFT   = 1; // 0b0001
  public static final int RIGHT  = 2; // 0b0010
  public static final int BELOW  = 4; // 0b0100
  public static final int ABOVE  = 8; // 0b1000

  /**
   * the Cohen-Sutherland clipping algorithm classifies points into one of nine quadrants around a
   * rectangular domain:
   *     0b1001  |  0b1000  |  0b1010
   *   ----------+----------+----------
   *     0b0001  |  0b0000  |  0b0010
   *   ----------+----------+----------
   *     0b0101  |  0b0100  |  0b0110
   */
  public int codePoint (Tuple point) {
    int code = INSIDE;
    if (point.x < min.x) code |= LEFT;
    if (point.x > max.x) code |= RIGHT;
    if (point.y < min.y) code |= BELOW;
    if (point.y > max.y) code |= ABOVE;
    return code;
  }

  /**
   * @param point - the point to check for containment
   * @return true if the point is within the bounds
   */
  public boolean contains (Tuple point) {
    return (codePoint(point) == INSIDE);
  }

  /**
   * @return true if any part of the segment is within the domain, roughly following the structure
   * of a cohen-sutherland style clipping algorithm - we use this formulation because it handles
   * trivial cases quickly and efficiently answers the containment question.
   * @param segment - the segment to test for containment
   */
  public boolean contains (Segment segment) {
    // classify the two points
    Tuple a = segment.a;
    Tuple b = segment.b;
    int cA = codePoint(a);
    int cB = codePoint(b);

    // loop until we succeed or fail, the pathological case being the two points in opposite corner
    // quadrants, but not intersecting the domain. this case would present 4 possible segment
    // intersections with the domain boundaries, but in the worst case we would only need to
    // evaluate three of them until we found a shared outside zone.
    while (true) {
      // compute a combination code to evaluate
      int cC = (cA << 4) | cB;
      switch (cC) {
        // trivial inclusion cases - any case with one point inside, or the 4 spanning cases:
        //
        //   0b1001  |  0b1000  |  0b1010
        // ----------+----------+----------
        //   0b0001  |  0b0000  |  0b0010
        // ----------+----------+----------
        //   0b0101  |  0b0100  |  0b0110

        case 0b00001001:   case 0b00001000:   case 0b00001010:
        case 0b00000001:   case 0b00000000:   case 0b00000010:
        case 0b00000101:   case 0b00000100:   case 0b00000110:

        case 0b10010000:   case 0b10000000:   case 0b10100000:
        case 0b00010000:   /*   0b00000000 */ case 0b00100000:
        case 0b01010000:   case 0b01000000:   case 0b01100000:

        case 0b10000100:   case 0b01001000:   case 0b00010010:   case 0b00100001:
          return true;

        // trivial exclusion cases - cases where both points share an external flag:
        //
        //   0b1001  |  0b1000  |  0b1010
        // ----------+----------+----------
        //   0b0001  |  0b0000  |  0b0010
        // ----------+----------+----------
        //   0b0101  |  0b0100  |  0b0110

        case 0b10011001:   case 0b10001000:   case 0b10101010:
        case 0b00010001:                      case 0b00100010:
        case 0b01010101:   case 0b01000100:   case 0b01100110:

        case 0b10011010:   case 0b10101001:   case 0b10011000:   case 0b10001001:   case 0b10001010:   case 0b10101000:
        case 0b01010110:   case 0b01100101:   case 0b01010100:   case 0b01000101:   case 0b01000110:   case 0b01100100:
        case 0b10010001:   case 0b00011001:   case 0b10010101:   case 0b01011001:   case 0b00010101:   case 0b01010001:
        case 0b10100010:   case 0b00101010:   case 0b10100110:   case 0b01101010:   case 0b00100110:   case 0b01100010:
          return false;

        // complex cases, type 1 - both points are outside on only one axis:
        // we move a to the boundary it's outside of, and the next step should trivially accept or
        // reject the segment.
        //
        //   0b1001  |  0b1000  |  0b1010
        // ----------+----------+----------
        //   0b0001  |  0b0000  |  0b0010
        // ----------+----------+----------
        //   0b0101  |  0b0100  |  0b0110

        case 0b10000001:   case 0b10000010:
          // a is above, move it to the top edge of the domain and re-classify
          a = a.add(b.subtract(a).scale((max.y - a.y) / (b.y - a.y)));
          cA = codePoint (a);
          break;
        case 0b01000001:   case 0b01000010:
          // a is below, move it to the bottom edge of the domain and re-classify
          a = a.add(b.subtract(a).scale((min.y - a.y) / (b.y - a.y)));
          cA = codePoint (a);
          break;
        case 0b00011000:   case 0b00010100:
          // a is left, move it to the left edge of the domain and re-classify
          a = a.add(b.subtract(a).scale((min.x - a.x) / (b.x - a.x)));
          cA = codePoint (a);
          break;
        case 0b00101000:   case 0b00100100:
          // a is right, move it to the right edge of the domain and re-classify
          a = a.add(b.subtract(a).scale((max.x - a.x) / (b.x - a.x)));
          cA = codePoint (a);
          break;

        // complex cases, type 2 - one points is outside on only one axis, the other is in a corner
        // quadrant:
        // we move the non-corner point to the boundary it's outside of, and the next step should
        // trivially accept or reject the segment/
        //
        //   0b1001  |  0b1000  |  0b1010
        // ----------+----------+----------
        //   0b0001  |  0b0000  |  0b0010
        // ----------+----------+----------
        //   0b0101  |  0b0100  |  0b0110

        // a is the corner
        case 0b01011000:   case 0b01101000:
          // b is above, move b to the top edge and reclassify
          b = b.add(a.subtract(b).scale((max.y - b.y) / (a.y - b.y)));
          cB = codePoint (b);
          break;
        case 0b10010100:   case 0b10100100:
          // b is below, move b to the bottom edge and reclassify
          b = b.add(a.subtract(b).scale((min.y - b.y) / (a.y - b.y)));
          cB = codePoint (b);
          break;
        case 0b10100001:   case 0b01100001:
          // b is left, move b to the left edge and reclassify
          b = b.add(a.subtract(b).scale((min.x - b.x) / (a.x - b.x)));
          cB = codePoint (b);
          break;
        case 0b10010010:   case 0b01010010:
          // b is right, move b to the right edge and reclassify
          b = b.add(a.subtract(b).scale((max.x - b.x) / (a.x - b.x)));
          cB = codePoint (b);
          break;

        // b is the corner
        case 0b10000101:   case 0b10000110:
          // a is above, move it to the top edge of the domain and re-classify
          a = a.add(b.subtract(a).scale((max.y - a.y) / (b.y - a.y)));
          cA = codePoint (a);
          break;

        case 0b01001001:   case 0b01001010:
          // a is below, move it to the bottom edge of the domain and re-classify
          a = a.add(b.subtract(a).scale((min.y - a.y) / (b.y - a.y)));
          cA = codePoint (a);
          break;

        case 0b00011010:   case 0b00010110:
          // a is left, move it to the left edge of the domain and re-classify
          a = a.add(b.subtract(a).scale((min.x - a.x) / (b.x - a.x)));
          cA = codePoint (a);
          break;

        case 0b00101001:   case 0b00100101:
          // a is right, move it to the right edge of the domain and re-classify
          a = a.add(b.subtract(a).scale((max.x - a.x) / (b.x - a.x)));
          cA = codePoint (a);
          break;

        // complex cases, type 3 - both end points are in corner quadrants:
        // we move a to the horizontal boundary it's outside of, and the next step might either
        // move a again from a different case, or be able to trivially accept or reject the segment.
        //
        //   0b1001  |  0b1000  |  0b1010
        // ----------+----------+----------
        //   0b0001  |  0b0000  |  0b0010
        // ----------+----------+----------
        //   0b0101  |  0b0100  |  0b0110

        case 0b10010110: case 0b10100101:
          // a is above, move it to the top edge of the domain and re-classify
          a = a.add(b.subtract(a).scale((max.y - a.y) / (b.y - a.y)));
          cA = codePoint (a);
          break;
        case 0b01101001: case 0b01011010:
          // a is below, move it to the bottom edge of the domain and re-classify
          a = a.add(b.subtract(a).scale((min.y - a.y) / (b.y - a.y)));
          cA = codePoint (a);
          break;

        default:
          System.err.printf ("Invalid code: 0b%8b%n", cC);
          return false;
      }
    }
  }

  /**
   * @ return true if the domain contains a region of space
   */
  public boolean valid () {
    return (max.x >= min.x) && (max.y >= min.y);
  }

  /**
   * @return a copy of the domain scaled by the requested factor around the center of the domain
   */
  public Domain scale (double factor) {
    Tuple center = center ();
    Tuple half = max.subtract(center).scale (factor);
    return new Domain (center.subtract(half), center.add(half));
  }
}
