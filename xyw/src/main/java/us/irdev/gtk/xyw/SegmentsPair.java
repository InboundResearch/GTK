package us.irdev.gtk.xyw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * a utility class for computing intersections between pairs of Segments objects
 */
public class SegmentsPair {
  public final Segments a;
  public final Segments b;
  public final Domain domain;

  /**
   * constructor from two segments
   */
  public SegmentsPair(Segments a, Segments b) {
    this.a = a;
    this.b = b;
    domain = Domain.union (a.domain, b.domain);
  }

  public static SegmentsPair reduce (Segments a, Segments b) {
    // to efficiently compute the intersections, we want to bound the area where the intersections
    // occur as much as possible. if there is only one intersection and the segments are relatively
    // small, this will probably be a very tight bound indeed. the performance will degrade in a
    // linear fashion as the number of intersections increases, up to the pathological scenario that
    // results if all segments intersect each other (an asterisk type of arrangement, for instance)

    // start by making sure the input segments are valid and there is at least some overlap
    if ((a == null) || (b == null) || (!(Domain.intersection(a.domain, b.domain).valid())))
      return null;

    // loop until we can't reduce the segments further - we use a for-loop to track how many
    // iterations this typically takes
    Domain lastDomain = null;
    int i;
    for (i = 0; !Domain.similar(b.domain, lastDomain); ++i) {
      // save the current domain for b so we can see if it changed since the last iteration
      lastDomain = b.domain;

      // first trim a to the b domain, then b to the new a domain. if either of these fail, we
      // don't have any overlap, so early out.
      if ((a = a.trimToDomain (b.domain)) == null)
        return null;
      if ((b = b.trimToDomain (a.domain)) == null)
        return null;
    }

    // if we got all the way out here, there's at least one intersection between the two segments,
    // so we package up the results
    return new SegmentsPair (a, b);
  }

  public SegmentsPair reduce () {
    return reduce (a, b);
  }

  public List<SegmentsPair> partition() {
    List<SegmentsPair> output = new ArrayList<>();
    List<Segments> lA = a.partition();
    List<Segments> lB = b.partition();

    for (Segments sA: lA) {
      for (Segments sB: lB) {
        SegmentsPair segmentsPair = SegmentsPair.reduce(sA, sB);
        if (segmentsPair != null) {
          output.add(segmentsPair);
        }
      }
    }

    return output;
  }

  public List<Tuple> intersections () {
    var result = new ArrayList<Tuple>();
    for (var sa: a.segments) {
      for (var sb: b.segments) {
        var intersection = Segment.intersect (sa, sb);
        if (intersection != null) {
          result.add(intersection);
        }
      }
    }
    return result.isEmpty() ? null : result;
  }

  public static List<Tuple> intersections(List<SegmentsPair> pairs) {
    List<Tuple> result = new ArrayList<>();
    for (SegmentsPair pair: pairs) {
      List<Tuple> pairIntersections = pair.intersections();
      if (pairIntersections != null) {
        result.addAll (pairIntersections);
      }
    }
    return result.isEmpty() ? null : result;
  }
}
