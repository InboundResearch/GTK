package us.irdev.gtk.xyw;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static us.irdev.gtk.xyw.Helper.assertSimilar;
import static us.irdev.gtk.xyw.Tuple.PT;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SegmentsPairTest {
  @Test
  public void testReduce() {
    // generate a bunch of points in a pair of straight lines
    Segments a = Segments.fromTupleFunction (-10, 10, 0.1, x -> PT(x, x * 0.1));
    Segments b = Segments.fromTupleFunction (-10, 10, 0.1, x -> PT(x * 0.1, x + 2));

    // precompute a line intersection for comparison
    Tuple testPoint = Line.intersect (
            Line.fromTwoPoints (a.segments.get(0).a, a.segments.get(10).b),
            Line.fromTwoPoints (b.segments.get(0).a, b.segments.get(10).b)
    );

    // do the reduction and verify the found segments contain our intersection point
    SegmentsPair sp = SegmentsPair.reduce(a, b);
    assertNotNull(sp);
    Tuple intersectionPoint = Segment.intersect(sp.a.segments.get(0), sp.b.segments.get(0));
    assertNotNull(intersectionPoint);
    Helper.assertSimilar(testPoint, intersectionPoint);
  }

  private SegmentsPair rawPathological () {
    final double stepSize = 0.005;
    Segments a = Segments.fromTupleFunction (1, 12, stepSize, x -> PT(1.0 + (Math.cos(x * 2 * Math.PI) * x), 0.5 + (Math.sin(x * 2 * Math.PI) * x)));
    Segments b = Segments.fromTupleFunction (1, 5, stepSize, x -> PT(-4.5 + (Math.cos(x * 2 * Math.PI) * x), -3.0 + (Math.sin(x * 2 * Math.PI) * x)));
    return new SegmentsPair(a, b);
  }

  @Test
  public void testIntersectionPathological() {
    SegmentsPair raw = rawPathological();
    assertNotNull (raw);
    List<Tuple> result = raw.reduce().intersections ();
  }

  @Test
  public void testIntersectionCoincidence() {
    // a really strange case is when I have multiple line segments that share an endpoint, and
    // another line we are testing for intersection would go right through that endpoint. we'll get
    // multiple intersections.
    var a = new Segments(List.of(
            new Segment (PT(0, 0), PT (1, 1)),
            new Segment (PT (1, 1), PT (2, 2))
    ));
    var b = new Segments(List.of(
            new Segment (PT (0, 1), PT (2, 1))
    ));
    var pair = new SegmentsPair (a, b);
    var result = pair.reduce().intersections ();
    assertEquals(2, result.size ());
    assertEquals(PT(1, 1), result.get(0));
  }
}
