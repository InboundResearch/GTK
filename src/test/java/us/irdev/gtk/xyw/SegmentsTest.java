package us.irdev.gtk.xyw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static us.irdev.gtk.xyw.Helper.assertSimilar;
import static us.irdev.gtk.xyw.Tuple.PT;
import static us.irdev.gtk.xyw.TupleCargo.TC;

public class SegmentsTest {
  private Segment clipToDomain(Domain domain, Segment segment) {
    var segments = Segments.clipToDomain (List.of(segment), domain);
    return (segments != null) ? segments.get(0) : null;
  }

  @Test
  public void testClip() {
    var domain = new  Domain (1, 3, 1, 3);

    // all outside
    assertNull(clipToDomain(domain, new Segment(PT (0, 0), PT (0.5, 0.5))));
    assertNull(clipToDomain(domain, new Segment(PT (1.5, 0), PT (2.5, 0.5))));
    assertNull(clipToDomain(domain, new Segment(PT (3.5, 0), PT (4.5, 0.5))));

    assertNull(clipToDomain(domain, new Segment(PT (0, 2), PT (0.5, 2.5))));
    assertNull(clipToDomain(domain, new Segment(PT (4, 0), PT (4.5, 2.5))));

    assertNull(clipToDomain(domain, new Segment(PT (0, 4), PT (0.5, 4.5))));
    assertNull(clipToDomain(domain, new Segment(PT (1.5, 4),PT (2.5, 4.5))));
    assertNull(clipToDomain(domain, new Segment(PT (3.5, 4),PT (4.5, 4.5))));

    // all inside
    assertSimilar(new Segment (PT (1.5, 1.5),PT (2.5, 2.5)), clipToDomain(domain, new Segment (PT (1.5, 1.5),PT (2.5, 2.5))));

    // spanning one edge
    assertSimilar(new Segment (PT (1, 2),PT (1.5, 2.5)), clipToDomain(domain, new Segment (PT (0.5, 1.5),PT (1.5, 2.5))));
    assertSimilar(new Segment (PT (2, 1),PT (2.5, 1.5)), clipToDomain(domain, new Segment (PT (1.5, 0.5),PT (2.5, 1.5))));
    assertSimilar(new Segment (PT (2.5, 1.5),PT (3, 2)), clipToDomain(domain, new Segment (PT (2.5, 1.5),PT (3.5, 2.5))));
    assertSimilar(new Segment (PT (1.5, 2.5),PT (2, 3)), clipToDomain(domain, new Segment (PT (1.5, 2.5),PT (2.5, 3.5))));

    // spanning two edges
    assertSimilar(new Segment (PT (1, 1.625),PT (3, 2.125)), clipToDomain(domain, new Segment (PT (0.5, 1.5),PT (4.5, 2.5))));
  }

  public static Segments makeSegments (double low, double high, double step, TupleFunc tupleFunc) {
    // generate a bunch of points in the tuplefunc
    var points = new  ArrayList<Tuple>();
    var steps = (int) Math.round (((high - low) / step)) + 1;
    assert (steps > 1);
    for (var i = 0; i < steps; ++i) {
      var x = low + (i * step);
      var tpc = TC(tupleFunc.value(x)).put("x", x);
      points.add (tpc);
    }

    // make a list of segments from the points
    var segmentListSize = points.size() - 1;
    var segments = new  ArrayList<Segment>(segmentListSize);
    for (var i = 0; i < segmentListSize; ++i) {
      segments.add (new Segment (points.get(i), points.get (i + 1)));
    }

    // return the result
    return (!segments.isEmpty()) ? new Segments (segments) : null;
  }

  @Test
  public void testBounds() {
    // generate a bunch of points in a sinusoidal line
    var segments = makeSegments (-10, 10, 0.1, x -> PT(x, Math.sin (x * Math.PI) * 2));
    assertNotNull(segments);
    assertEquals(200, segments.segments.size());
    assertSimilar(new Domain (-10, 10, -2, 2), segments.domain);
  }

}
