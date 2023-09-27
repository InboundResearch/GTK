package us.irdev.gtk.xyw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static us.irdev.gtk.xyw.Helper.assertSimilar;
import static us.irdev.gtk.xyw.Tuple.PT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SegmentsTest {

  private Segment clipToDomain(Domain domain, Segment segment) {
    List<Segment> segments = new ArrayList<>(1);
    segments.add(segment);
    segments = Segments.clipToDomain (segments, domain);
    return (segments != null) ? segments.get(0) : null;
  }

  @Test
  public void testClip() {
    Domain domain = new Domain (1, 3, 1, 3);

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

  interface TupleFunc {
    Tuple value(double x);
  }
  private Segments makeSegments (double low, double high, double step, TupleFunc tupleFunc) {
    // generate a bunch of points in the tuplefunc
    List<Tuple> points = new ArrayList<>();
    int steps = (int) Math.round (((high - low) / step)) + 1;
    assert (steps > 1);
    for (int i = 0; i < steps; ++i) {
      points.add (tupleFunc.value(low + (i * step)));
    }

    // make a list of segments from the points
    int segmentListSize = points.size() - 1;
    List<Segment> segments = new ArrayList<>(segmentListSize);
    for (int i = 0; i < segmentListSize; ++i) {
      segments.add (new Segment (points.get(i), points.get (i + 1)));
    }

    // return the result
    return (segments.size() > 0) ? new Segments (segments) : null;
  }

  @Test
  public void testBounds() {
    // generate a bunch of points in a sinusoidal line
    Segments segments = makeSegments (-10, 10, 0.1, x -> PT(x, Math.sin (x * Math.PI) * 2));
    assertEquals(200, segments.segments.size());
    assertSimilar(new Domain (-10, 10, -2, 2), segments.domain);
  }

  @Test
  public void testIntersection() {
    // generate a bunch of points in a sinusoidal line
    Segments a = makeSegments (-10, 10, 0.1, x -> PT(x, x * 0.1));
    Segments b = makeSegments (-10, 10, 0.1, x -> PT(x * 0.1, x + 2));

    // precompute a line intersection for comparison
    Tuple testPoint = Line.intersect (
            Line.fromTwoPoints (a.segments.get(0).a, a.segments.get(10).b),
            Line.fromTwoPoints (b.segments.get(0).a, b.segments.get(10).b)
    );
    List<Tuple> result = Segments.intersect (a, b);
  }

  @Test
  public void testIntersectionPathological() {
    // generate a bunch of points in a spiral line
    Segments a = makeSegments (1, 3, 0.05, x -> PT(Math.cos(x * 2 * Math.PI) * x, Math.sin(x * 2 * Math.PI) * x));
    Segments b = makeSegments (-10, 10, 0.1, x -> PT(x * 0.1, x + 2));

    //List<Tuple> result = Segments.intersect (a, b);
  }
}
