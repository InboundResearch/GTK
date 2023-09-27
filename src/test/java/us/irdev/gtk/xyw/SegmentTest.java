package us.irdev.gtk.xyw;

import org.junit.jupiter.api.Test;

import static us.irdev.gtk.xyw.Helper.assertSimilar;
import static us.irdev.gtk.xyw.Tuple.PT;
import static us.irdev.gtk.xyw.Tuple.VEC;
import static java.lang.Math.sqrt;
import static org.junit.jupiter.api.Assertions.*;

public class SegmentTest {

  @Test
  public void testToString() {
    assertEquals(new Segment (1.2, 1, 1.1, 5).toString(), "[(1.200000, 1.000000, 1.000000), (1.100000, 5.000000, 1.000000)]");
  }

  @Test
  public void testEquals() {
    assertSimilar(new Segment (1.2, 1, 1.1, 5), new Segment (1.2, 1, 1.1, 5));
    assertSimilar(new Segment (1.2, 1, 1.1, 5), new Segment (1.1, 5, 1.2, 1));
    assertSimilar(new Segment (1.1, 5, 1.2, 1), new Segment (1.2, 1, 1.1, 5));
  }

  @Test
  public void testIntersection() {
    Segment segment = new Segment (PT (2, 0), PT (0, 2));
    Segment ray = new Segment (PT (0, 0), PT (2, 2));
    Tuple pt = Segment.intersect (ray, segment);
    assertSimilar (PT (1, 1), pt);
    assertTrue(segment.pointIsInSegment (pt));
    assertTrue(ray.pointIsInSegment (pt));
  }

  @Test
  public void testLineEquation() {
    Tuple a = PT (0, 2);
    Tuple b = PT (1, 1);

    Segment segment = new Segment (a, b);
    Tuple origin = segment.line.origin();
    assertTrue(segment.pointIsInSegment (origin));

    assertSimilar (VEC (-1, -1).normalized (), segment.line.n());

    assertTrue(segment.line.pointIsOnLine (a));
    assertTrue(segment.line.pointIsOnLine (b));
    assertTrue(segment.line.pointIsOnLine (origin));

    assertSimilar (segment.line.distanceToPoint (PT (0, 0)), sqrt (2));

    assertTrue(segment.pointIsInSegment (PT (0.5, 1.5)));
    assertTrue(segment.line.pointIsOnLine (PT (1.5, 0.5)));
    assertFalse(segment.pointIsInSegment (PT (1.5, 0.5)));
    assertTrue(segment.line.pointIsOnLine (PT (-0.5, 2.5)));
    assertFalse(segment.pointIsInSegment (PT (-0.5, 2.5)));
  }

  @Test
  public void testMidLerp () {
    assertSimilar(PT (1, 1), new Segment (PT (0, 0), PT (2, 2)).mid());
    assertSimilar(PT (-1, -1), new Segment (PT (0, 0), PT (-2, -2)).mid());

    assertSimilar(PT (1, 1), new Segment (PT (0, 0), PT (2, 2)).lerp(0.5));
    assertSimilar(PT (-1, -1), new Segment (PT (0, 0), PT (-2, -2)).lerp(0.5));

    assertSimilar(PT (0.5, 0.5), new Segment (PT (0, 0), PT (2, 2)).lerp(0.25));
    assertSimilar(PT (-0.5, -0.5), new Segment (PT (0, 0), PT (-2, -2)).lerp(0.25));
  }
}
