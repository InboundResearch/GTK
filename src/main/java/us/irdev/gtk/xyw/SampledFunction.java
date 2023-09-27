package us.irdev.gtk.xyw;

import us.irdev.gtk.xyw.bb.BoundaryBehavior;
import us.irdev.gtk.xyw.db.Row;
import us.irdev.gtk.xyw.db.Rows;

import java.util.ArrayList;
import java.util.List;

import static us.irdev.gtk.xyw.Tuple.PT;

/**
 * A sampled function in a 2D domain, assumes samples are taken on a regular grid and the entire
 * grid is supplied.
 */
public class SampledFunction {
  private final Domain domain;
  private final Tuple interval;
  private final BoundaryBehavior bbX, bbY;
  private final int dimX, dimY;
  private final double[] samples;

  /**
   * Constructor - this raw constructor provides a set of 0 samples
   * @param domain - min and max bounds of the function samples
   * @param interval - space between samples
   * @param bbX - boundary behavior for the x-axis
   * @param bbY - boundary behavior for the y-axis
   */
  public SampledFunction (Domain domain, Tuple interval, BoundaryBehavior bbX, BoundaryBehavior bbY) {
    // copy the parameters
    this.domain = domain;
    this.interval = interval;
    this.bbX = bbX;
    this.bbY = bbY;

    // compute the actual array dimensions
    Tuple size = domain.size();
    this.dimX = 1 + (int) Math.round (size.x / interval.x);
    this.dimY = 1 + (int) Math.round (size.y / interval.y);

    // we trust that the interval neatly divides the domain because we computed it (probably), but
    // verify the neatness to be sure it's computed correctly
    assert (Math.abs(((dimX - 1) * interval.x) - size.x) < Numerics.TOLERANCE);
    assert (Math.abs(((dimY - 1) * interval.y) - size.y) < Numerics.TOLERANCE);

    // different boundary behaviors may expand the domain max, so we compute that change here
    domain.max = PT (
    domain.max.x + bbX.adjustDomainMax(domain.min.x, domain.max.x, interval.x),
    domain.max.y + bbY.adjustDomainMax(domain.min.y, domain.max.y, interval.y)
    );

    // create the samples array
    samples = new double[dimX * dimY];
  }

  /**
   * Constructor - this raw constructor provides a set of 0 samples
   * @param domain - min and max bounds of the function samples
   * @param interval - space between samples
   * @param bb - boundary behavior for both the x-axis and y-axis
   */
  public SampledFunction (Domain domain, Tuple interval, BoundaryBehavior bb) {
    this (domain, interval, bb, bb);
  }

  /**
   * a helper function to create a populated SampledFunction from a "database"
   * @param rows - an array of numbers representing (x, y, value) triples for each sample
   *                location
   * @param bbX - boundary behavior for the x-axis
   * @param bbY - boundary behavior for the y-axis
   * @return a new SampledFunction instance
   */
  public static SampledFunction fromDatabase (Rows rows, BoundaryBehavior bbX, BoundaryBehavior bbY) {
    // set up the sampled function
    SampledFunction function = new SampledFunction (rows.getDomain(), rows.getInterval (), bbX, bbY);

    // loop over the input database to put the samples
    for (Row row : rows.getRows()) {
      function.putSample (row.xy, row.value);
    }

    // return the created function
    return function;
  }

  /**
   * a helper function to create a populated SampledFunction from a "database"
   * @param rows - an array of numbers representing (x, y, value) triples for each sample
   *                location
   * @param bb - boundary behavior for both the x-axis and y-axis
   * @return a new SampledFunction instance
   */
  public static SampledFunction fromDatabase (Rows rows, BoundaryBehavior bb) {
    return fromDatabase(rows, bb, bb);
  }

  /**
   * @return the domain representing the min and max values represented by samples
   */
  public Domain getDomain() {
    return domain;
  }

  /**
   * @return the tuple representing the regular spacing between samples in the domain
   */
  public Tuple getInterval () {
    return interval;
  }

  /**
   * @param x - x-index to fetch in array space (range is [0..xDim))
   * @param y - y-index to fetch in array space (range is [0..yDim))
   * @return the 1-dimensional array index in the samples array for the 2-dimensional index (x, y)
   */
  private int coord (int x, int y) {
    return (y * dimX) + x;
  }

  /**
   * @param x - x-index to fetch in array space (range is [0..xDim))
   * @param y - y-index to fetch in array space (range is [0..yDim))
   * @return the sample value for the 2-dimensional index (x, y)
   */
  private double getSample (int x, int y) {
    return samples[coord(x, y)];
  }

  /**
   * @param xy - a tuple with coordinates in the domain
   * @param sample - the value to store at this sample location
   */
  public void putSample (Tuple xy, double sample) {
    // map from the domain to the coordinate space
    xy = xy.subtract(domain.min).hquotient (interval).round();
    samples[coord((int) xy.x, (int) xy.y)] = sample;
  }

  // -----------------------------------------------------------------------------------------------
  // computing f(xy) and dxydy(xy)
  // -----------------------------------------------------------------------------------------------
  /**
   * A simple class to hold the results of a functional evaluation: f(x) and the derivatives with
   * respect to x and y
   */
  public static class At {
    public double fxy;
    public Tuple dxdy;
    public At(double fxy, double dx, double dy) {
      this.fxy = fxy;
      this.dxdy = PT (dx, dy);
    }
  }

  /**
   * @param xy - a tuple with coordinates in the domain
   * @return an At, containing the interpolated value of the function and the 1st derivatives at xy
   */
  public At f (Tuple xy) {
    // condition the input coordinate
    Tuple cxy = PT (bbX.condition(xy.x, domain.min.x, domain.max.x), bbY.condition(xy.y, domain.min.y, domain.max.y));

    // map the input coordinate to 4 sampled locations [(x0, y0), (x1, y0), (x0, y1), (x1, y1)] in
    // the sample array space[[0..dimX),[0..dimY)], and compute the interpolators, which should be
    // in the range [0..1)
    xy = cxy.subtract(domain.min).hquotient (interval);

    int x0 = (int) Math.floor(xy.x);
    int x1 = (x0 + 1) % dimX;
    double xInterpolant = xy.x - x0;

    int y0 = (int) Math.floor(xy.y);
    int y1 = (y0 + 1) % dimY;
    double yInterpolant = xy.y - y0;

    // get the 4 sample values
    double a = getSample(x0, y0);
    double b = getSample(x1, y0);
    double c = getSample(x0, y1);
    double d = getSample(x1, y1);

    // compute the interpolations and return the result. it doesn't matter what order we perform
    // this operation. we can interpolate the AC, BD to get MN; or AB, CD...
    //
    //   C--- P -----D
    //   |    |      |
    //   M--- R -----N
    //   |    |      |
    //   |    |      |
    //   A--- O -----B
    double m = Numerics.lerp (a, c, yInterpolant);
    double n = Numerics.lerp (b, d, yInterpolant);
    double o = Numerics.lerp (a, b, xInterpolant);
    double p = Numerics.lerp (c, d, xInterpolant);
    double r = Numerics.lerp (m, n, xInterpolant);
    return new At (r, n - m, p - o);
  }

  /**
   * @param x - x-coordinate to evaluate in the domain
   * @param y - y-coordinate to evaluate in the domain
   * @return the value of the function and the 1st derivatives at (x, y)
   */
  public At f (double x, double y) {
    return f(PT (x, y));
  }

  /**
   * @param xy - a tuple with coordinates in the domain
   * @return the value of the function at xy
   */
  public double fxy (Tuple xy) {
    return f(xy).fxy;
  }

  /**
   * @param x - x-coordinate to evaluate in the domain
   * @param y - y-coordinate to evaluate in the domain
   * @return the value of the function at (x, y)
   */
  public double fxy (double x, double y) {
    return f(PT (x, y)).fxy;
  }

  /**
   * @param xy - a tuple with coordinates in the domain
   * @return the 1st derivatives of the function at xy
   */
  public Tuple dxdy (Tuple xy) {
    return f(xy).dxdy;
  }

  /**
   * @param x - x-coordinate to evaluate in the domain
   * @param y - y-coordinate to evaluate in the domain
   * @return the 1st derivatives of the function at (x, y))
   */
  public Tuple dxdy (double x, double y) {
    return f(PT (x, y)).dxdy;
  }

  // -----------------------------------------------------------------------------------------------
  // compute iso contours
  // -----------------------------------------------------------------------------------------------

  /**
   * extract an iso-line from the sampled function for a target value. the contour represents where
   * the function has the target value. (https://en.wikipedia.org/wiki/Contour_line)
   * @param targetValue - the value to extract iso-contours for
   * @return - a list of segments representing piecewise linear approximations to the iso-contour.
   * the linear segments can be refined using the refineSegments method.
   */
  public List<Segment> iso (double targetValue) {
    // use a marching squares variant to extract iso-contours from the sampled function in
    // the form of a set of line segments. our sampled data has different boundary behaviors that
    // complicate things, so this is not the most efficient variant in the sense that a lot of
    // redundant calculations are not re-used as they would be in the traditional implementation.
    List<Segment> output = new ArrayList<>();

    Tuple size = domain.size ();
    Tuple end = size.hquotient (interval).floor ();
    for (int iy = 0, iyEnd = (int) end.y; iy < iyEnd; iy++) {
      double y = domain.min.y + (iy * interval.y);
      double y1 = y + interval.y;
      int iy1 = (iy + 1) % dimY;
      for (int ix = 0, ixEnd = (int) end.x; ix < ixEnd; ix++) {
        double x = domain.min.x + (ix * interval.x);
        double x1 = x + interval.x;
        int ix1 = (ix + 1) % dimX;

        // get the 4 samples for the corners of each 2x2 sample square and compute a code based on
        // whether each corner is above the targetValue (as a threshold)
        //
        //      c - d
        //      |   |
        //      a - b

        double a = getSample(ix, iy), b = getSample (ix1, iy), c = getSample (ix, iy1), d = getSample (ix1, iy1);
        boolean ath = (a >= targetValue), bth = (b >= targetValue), cth = (c >= targetValue), dth = (d >= targetValue);
        int code = (ath ? 1 : 0) | (bth ? 2 : 0) | (cth ? 4 : 0) | (dth ? 8 : 0);

        // handle the line output for this quad based on the code
        switch (code) {
          case 0: case 15:
            // all 4 corners of this quad are above or below the targetValue, no edges have endpoints
            break;
          case 1: case 14: {
            // bottom left corner is different, bottom and left have endpoints
            Tuple left = PT (x, Numerics.where(targetValue, y, a, y1, c));
            Tuple bottom = PT (Numerics.where(targetValue, x, a, x1, b), y);
            output.add (new Segment (left, bottom));
          } break;
          case 2: case 13: {
            // bottom right corner is different, bottom and right have endpoints
            Tuple right = PT (x1, Numerics.where(targetValue, y, b, y1, d));
            Tuple bottom = PT (Numerics.where(targetValue, x, a, x1, b), y);
            output.add (new Segment (right, bottom));
          } break;
          case 4: case 11: {
            // top left corner is different, top and left have endpoints
            Tuple left = PT (x, Numerics.where(targetValue, y, a, y1, c));
            Tuple top = PT (Numerics.where(targetValue, x, c, x1, d), y1);
            output.add (new Segment (left, top));
          } break;
          case 8: case 7: {
            // top right corner is different, top and right have endpoints
            Tuple top = PT (Numerics.where(targetValue, x, c, x1, d), y1);
            Tuple right = PT (x1, Numerics.where(targetValue, y, b, y1, d));
            output.add (new Segment (right, top));
          } break;
          case 3: case 12: {
            // two bottom corners are different from the two top corners, left and right have endpoints
            Tuple left = PT (x, Numerics.where(targetValue, y, a, y1, c));
            Tuple right = PT (x1, Numerics.where(targetValue, y, b, y1, d));
            output.add (new Segment (left, right));
          } break;
          case 5: case 10: {
            // two left corners are different from the two right corners, bottom and top have endpoints
            Tuple bottom = PT (Numerics.where(targetValue, x, a, x1, b), y);
            Tuple top = PT (Numerics.where(targetValue, x, c, x1, d), y1);
            output.add (new Segment (bottom, top));
          } break;
          case 6: case 9: {
            // saddle point, all 4 edges have endpoints. we have to decide which direction to do 2 lines.
            Tuple left = PT (x, Numerics.where(targetValue, y, a, y1, c));
            Tuple right = PT (x1, Numerics.where(targetValue, y, b, y1, d));
            Tuple bottom = PT (Numerics.where(targetValue, x, a, x1, b), y);
            Tuple top = PT (Numerics.where(targetValue, x, c, x1, d), y1);

            // a good heuristic to resolve the ambiguity here is to choose the shortest combined pair
            Segment leftTop = new Segment (left, top);
            Segment leftBottom = new Segment (left, bottom);
            Segment rightTop = new Segment (right, top);
            Segment rightBottom = new Segment (right, bottom);

            if ((leftTop.lengthSq() + rightBottom.lengthSq()) < (leftBottom.lengthSq() + rightTop.lengthSq())) {
              output.add (leftTop);
              output.add (rightBottom);
            } else {
              output.add (leftBottom);
              output.add (rightTop);
            }
          } break;
        }
      }
    }

    // return our unordered list of segments
    return output;
  }

  // -----------------------------------------------------------------------------------------------
  // refine sample locations
  // -----------------------------------------------------------------------------------------------

  /**
   * each refinement step should halve the error (give or take). the exact value to use will depend
   * on how close the original estimate is, but 3-4 steps is typically enough to get within the
   * numerical tolerance we want
   */
  private static final int REFINE_SAMPLE_LOCATION_STEPS = 3;

  /**
   * given an estimated coordinate in the domain to use as a starting point, follow the gradient of
   * the function to improve the estimate. this is based on newton's method, which typically assumes
   * C1 continuity. the bi-linear interpolation defines a hyperbolic paraboloid, which satisfies
   * this requirement, but continuity is not guaranteed between sampled cells (basically: be aware).
   * @param xy -
   * @param targetValue -
   * @param steps -
   * @return the improved estimate
   */
  public Tuple refineSampleLocation (Tuple xy, double targetValue, int steps) {
    for (int i = 0; i < steps; ++i) {
      // get the value at the current location
      At at = f (xy);

      // compute the half-delta from the target value, and then a scaled version of the derivative
      // to add to the current location. note that we scale by the inverse of delta, because we want
      // to remove the error. we choose half steps to asymptotically converge on the result, rather
      // than risk overshooting due to bad behavior from discontinuity between sampled cells.
      double delta = (at.fxy - targetValue) * 0.5;
      xy = xy.add (interval.hproduct (at.dxdy.hinverse ().scale(-delta)));
    }
    // return the refined location
    return xy;
  }

  /**
   * given an estimated coordinate in the domain to use as a starting point, follow the gradient of
   * the function to improve the estimate.
   * @param x -
   * @param y -
   * @param targetValue -
   * @return the improved estimate
   */
  public Tuple refineSampleLocation (double x, double y, double targetValue) {
    return refineSampleLocation(x, y, targetValue, REFINE_SAMPLE_LOCATION_STEPS);
  }

  /**
   * given an estimated coordinate in the domain to use as a starting point, follow the gradient of
   * the function to improve the estimate.
   * @param x -
   * @param y -
   * @param targetValue -
   * @param steps -
   * @return the improved estimate
   */
  public Tuple refineSampleLocation (double x, double y, double targetValue, int steps) {
    return refineSampleLocation(PT (x, y), targetValue, steps);
  }

  /**
   * given an estimated coordinate in the domain to use as a starting point, follow the gradient of
   * the function to improve the estimate.
   * @param xy -
   * @param targetValue -
   * @return the improved estimate
   */
  public Tuple refineSampleLocation (Tuple xy, double targetValue) {
    return refineSampleLocation(xy, targetValue, REFINE_SAMPLE_LOCATION_STEPS);
  }

  // -----------------------------------------------------------------------------------------------
  // refine segments into curves
  // -----------------------------------------------------------------------------------------------

  /**
   * improve the error bounds on an iso-line by splitting it in the middle and refining the middle
   * point
   * @param segment -
   * @param targetValue -
   * @param maxSegmentLength -
   * @param output -
   */
  public void refineSegment(Segment segment, double targetValue, double maxSegmentLength, List<Segment> output) {
    // depth-first bisection and refinement of each new vertex
    if (segment.length() > maxSegmentLength) {
      // split the segment into two halves at the mid-point, refine the midpoint, then recur on the
      // two halves...
      Tuple mid = refineSampleLocation(segment.mid(), targetValue);
      refineSegment(new Segment(segment.a, mid), targetValue, maxSegmentLength, output);
      refineSegment(new Segment(mid, segment.b), targetValue, maxSegmentLength, output);
    } else {
      // the segment is within the maximum length requirement, so send it to output
      output.add (segment);
    }
  }

  // XXX does this belong in "Segments"?
  /**
   *
   */
  public List<Segment> refineSegments (List<Segment> segments, double targetValue, double maxSegmentLength) {
    List<Segment> output = new ArrayList<>();
    for (Segment segment: segments) {
      refineSegment(segment, targetValue, maxSegmentLength, output);
    }
    return output;
  }

  // -----------------------------------------------------------------------------------------------
  // compute iso-crossings
  // -----------------------------------------------------------------------------------------------
  public static class Crossing {
    public double x;
    public Tuple loc;
    public double value;
    public boolean above;

    public Crossing (double x, Tuple loc, double value, boolean above) {
      this.x = x;
      this.loc = loc;
      this.value = value;
      this.above = above;
    }

    // so we can sort a list of these by x
    public double getX () {
      return x;
    }

    public static Crossing merge (Crossing a, Crossing b) {
      // copy the "above" flag from the first point to capture whether the crossing is "from above
      // to below" or "from below to above" the iso value.
      return new Crossing ((a.x + b.x) * 0.5, a.loc.add(b.loc).scale(0.5), (a.value + b.value) * 0.5, a.above);
    }
  }

  /**
   * an interface for a function that returns the indexing tuple at some offset x. the indexing
   * tuple is then used to get the SampledFunction value. for instance, the sampled function might
   * be the depth of water, and the TupleFunctionAt might return the position of a boat by time. the
   * result is a query of the depth of water by time, and we can find the point at which the boat
   * moves into water of a certain depth.
   */
  public interface TupleFunctionAt {
    Tuple at(double x);
  }

  /**
   * compute the candidate value at x and pre-test what side of the iso this sample is on
   */
  private Crossing at (TupleFunctionAt tfa, double x, double targetValue) {
    Tuple loc = tfa.at (x);
    double magLat = fxy(loc);
    return new Crossing (x, loc, magLat, magLat > targetValue);
  }

  /**
   * compute x and f(x) where a function f crosses an iso-contour of some target value
   * @param tfa - an object that provides f(x) as a 2d location
   * @param xa - a bound on a region where an iso crossing is expected
   * @param xb  - the second bound. the crossing is expected to be at xa >= x >= xb
   * @param targetValue - the iso value we are seeking
   * @return a found crossing, with x, tfa(x), f(x), and above. above indicates if the first time
   *         window was above the target value or below.
   *         returns null if no crossing exists in the specified domain
   */
  public Crossing findCrossing(TupleFunctionAt tfa, double xa, double xb, double targetValue) {
    // initial candidates
    Crossing a = at(tfa, xa, targetValue);
    Crossing b = at(tfa, xb, targetValue);

    // a and b must be on opposite sides of the target value
    if (a.above == b.above) {
      // this is a normal case if we dice the search region, so we just do nothing
      return null;
    }

    // repeatedly bisect the search region until the two boundary points converge
    while (!Numerics.similar (a.value, b.value)) {
      // get the midpoint of the domain
      Crossing c = at (tfa, (a.x + b.x) * 0.5, targetValue);

      // shrink the domain to the midpoint for whichever point is on the same side of the
      // target value
      if (a.above == c.above) a = c; else b = c;
    }

    // the points converged, it doesn't really matter which one we return, we combine them as a last
    // hurrah
    return Crossing.merge (a, b);
  }

  /**
   * find all the crossings within a search region. this is basically a root finder, so we have to
   * know a little something about the function we are querying. the sampling strategy here is to
   * divide the search space into uniformly sized pieces. this is good if we assume the sample
   * domain is traversed with uniform velocity, such as a satellite in a circular orbit, but might
   * need some additional thought in the future to provide a sampling strategy that is more suitable
   * to say, a highly elliptical orbit, where uniform time slices might create very different sizes
   * of the projected position steps over the domain.
   * @param tfa - the trajectory function we are moving along to find roots
   * @param xa - the first bound of the x parameter passed to the tfa
   * @param xb - the second bound of the x parameter passed to the tfa
   * @param targetValue - the value we are searching for in the SampledFunction
   * @param crossings - the (estimated) number of roots we will find in the query domain
   * @return a list of the roots found, or null if none are found
   */
  public List<Crossing> findCrossings(TupleFunctionAt tfa, double xa, double xb, double targetValue, int crossings) {
    List<Crossing> output = new ArrayList<>();

    // divide the search range into smaller parts based on the crossings hint, loop over each subsegment separately
    double delta = xb - xa;
    double interval = delta / crossings;
    for (int i = 0; i < crossings; ++i) {
      // compute the search interval, then find a crossing in that interval
      double x0 = xa + (i * interval), x1 = x0 + interval;
      Crossing crossing = findCrossing(tfa, x0, x1, targetValue);

      // if we found a crossing, save it
      if (crossing != null) {
        output.add(crossing);
      }
    }

    // return what we found, null if nothing...
    return (output.size() > 0) ? output : null;
  }
}
