package us.irdev.gtk.xy;

import org.junit.jupiter.api.Test;

import static java.lang.Math.sqrt;
import static org.junit.jupiter.api.Assertions.*;
import static us.irdev.gtk.xy.Helper.assertSimilar;

public class SegmentTest {

  @Test
  public void testToString() {
    assertEquals(new Segment (1.2, 1, 1.1, 5).toString(), "[(1.200000, 1.000000), (1.100000, 5.000000)]");
  }

  @Test
  public void testEquals() {
    assertSimilar(new Segment (1.2, 1, 1.1, 5), new Segment (1.2, 1, 1.1, 5));
    assertSimilar(new Segment (1.2, 1, 1.1, 5), new Segment (1.1, 5, 1.2, 1));
    assertSimilar(new Segment (1.1, 5, 1.2, 1), new Segment (1.2, 1, 1.1, 5));
  }

  @Test
  public void testIntersection() {
    Segment segment = new Segment (new Tuple (2, 0), new Tuple(0, 2));
    Segment ray = new Segment (new Tuple (0, 0), new Tuple (2, 2));
    Tuple pt = Segment.intersect (ray, segment);
    Helper.assertSimilar (new Tuple (1, 1), pt);
    assertTrue(segment.pointIsInSegment (pt));
    assertTrue(ray.pointIsInSegment (pt));
  }

  @Test
  public void testLineEquation() {
    Tuple a = new Tuple (0, 2);
    Tuple b = new Tuple (1, 1);

    Segment segment = new Segment (a, b);
    Tuple origin = segment.line.origin();
    assertTrue(segment.pointIsInSegment (origin));

    Helper.assertSimilar (new Tuple (-1, -1).normalized (), segment.line.n);

    assertTrue(segment.line.pointIsOnLine (a));
    assertTrue(segment.line.pointIsOnLine (b));
    assertTrue(segment.line.pointIsOnLine (origin));

    assertSimilar (segment.line.distanceToPoint (new Tuple (0, 0)), sqrt (2));

    assertTrue(segment.pointIsInSegment (new Tuple(0.5, 1.5)));
    assertTrue(segment.line.pointIsOnLine (new Tuple(1.5, 0.5)));
    assertFalse(segment.pointIsInSegment (new Tuple(1.5, 0.5)));
    assertTrue(segment.line.pointIsOnLine (new Tuple(-0.5, 2.5)));
    assertFalse(segment.pointIsInSegment (new Tuple(-0.5, 2.5)));
  }

  @Test
  public void testMidLerp () {
    assertSimilar(new Tuple (1, 1), new Segment (new Tuple (0, 0), new Tuple (2, 2)).mid());
    assertSimilar(new Tuple (-1, -1), new Segment (new Tuple (0, 0), new Tuple (-2, -2)).mid());

    assertSimilar(new Tuple (1, 1), new Segment (new Tuple (0, 0), new Tuple (2, 2)).lerp(0.5));
    assertSimilar(new Tuple (-1, -1), new Segment (new Tuple (0, 0), new Tuple (-2, -2)).lerp(0.5));

    assertSimilar(new Tuple (0.5, 0.5), new Segment (new Tuple (0, 0), new Tuple (2, 2)).lerp(0.25));
    assertSimilar(new Tuple (-0.5, -0.5), new Segment (new Tuple (0, 0), new Tuple (-2, -2)).lerp(0.25));
  }
}
