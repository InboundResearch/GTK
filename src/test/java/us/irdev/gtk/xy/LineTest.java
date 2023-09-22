package us.irdev.gtk.xy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static us.irdev.gtk.xy.Helper.assertSimilar;

public class LineTest {

  @Test
  public void testLineConstructors() {
    Line line = new Line (new Tuple (0.0, 1.0), 0.0);
    assertSimilar (new Tuple (0, 1), line.n);
    assertSimilar (0.0, line.c);
    assertSimilar(0.0, line.m());
    assertSimilar(0.0, line.b());
    assertSimilar(line.origin(), new Tuple (0, 0));

    line = new Line (new Tuple (0.0, -1.0), 2.0);
    assertSimilar (new Tuple (0, -1), line.n);
    assertSimilar (2.0, line.c);
    assertSimilar(0.0, line.m());
    assertSimilar(2.0, line.b());
    assertSimilar(line.origin(), new Tuple (0, 2));

    // verify the line from two points no longer contains implicit direction information
    line = Line.fromTwoPoints (new Tuple (0, 2), new Tuple (1, 2));
    assertSimilar (new Tuple (0, -1), line.n);
    assertSimilar (2.0, line.c);
    assertSimilar(0.0, line.m());
    assertSimilar(2.0, line.b());
    assertSimilar(line.origin(), new Tuple (0, 2));

    line = Line.fromTwoPoints (new Tuple (1, 2), new Tuple (0, 2));
    assertSimilar (new Tuple (0, -1), line.n);
    assertSimilar (2.0, line.c);
    assertSimilar(0.0, line.m());
    assertSimilar(2.0, line.b());
    assertSimilar(line.origin(), new Tuple (0, 2));

    // verify the line from a point and direction no longer contains implicit direction information
    line = Line.fromPointVector (new Tuple (0, 2), new Tuple (1, 0));
    assertSimilar (new Tuple (0, -1), line.n);
    assertSimilar (2.0, line.c);
    assertSimilar(0.0, line.m());
    assertSimilar(2.0, line.b());
    assertSimilar(line.origin(), new Tuple (0, 2));

    line = Line.fromPointVector (new Tuple (1, 2), new Tuple (-1, 0));
    assertSimilar (new Tuple (0, -1), line.n);
    assertSimilar (2.0, line.c);
    assertSimilar(0.0, line.m());
    assertSimilar(2.0, line.b());
    assertSimilar(line.origin(), new Tuple (0, 2));

    // test the slope/intercept form
    line = Line.fromSlopeIntercept (0.0, 2);
    assertSimilar (new Tuple (0, -1), line.n);
    assertSimilar (2.0, line.c);
    assertSimilar(0.0, line.m());
    assertSimilar(2.0, line.b());
    assertSimilar(line.origin(), new Tuple (0, 2));

    line = Line.fromSlopeIntercept (0.5, 2);
    assertSimilar(0.5, line.m());
    assertSimilar(2.0, line.b());
    assertSimilar (new Tuple (1, -2).normalized(), line.n);
    double theta = Math.atan(line.m());
    double c = line.b() * Math.cos(theta);
    assertSimilar (c, line.c);
    theta += Math.PI * 0.5;
    assertSimilar(line.origin(), new Tuple (Math.cos(theta), Math.sin(theta)).scale (c));

    line = Line.fromSlopeIntercept(Double.POSITIVE_INFINITY, 3);
    assertSimilar(Double.POSITIVE_INFINITY, line.m());
    assertSimilar(3.0, line.b());
    assertSimilar (new Tuple (-1, 0), line.n);
    assertSimilar (3.0, line.c);
    assertSimilar (new Tuple (3, 0), line.origin());

    line = Line.fromSlopeIntercept(Double.NEGATIVE_INFINITY, 3.0);
    assertSimilar(Double.POSITIVE_INFINITY, line.m());
    assertSimilar(3.0, line.b());
    assertSimilar (new Tuple (-1, 0), line.n);
    assertSimilar (3.0, line.c);
    assertSimilar (new Tuple (3, 0), line.origin());

    // test horizontal and perpendicular
    line = Line.vertical(3.0);
    assertSimilar(Double.POSITIVE_INFINITY, line.m());
    assertSimilar(3.0, line.b());
    assertSimilar (new Tuple (-1, 0), line.n);
    assertSimilar (3.0, line.c);
    assertSimilar (new Tuple (3, 0), line.origin());

    line = Line.vertical(-3.0);
    assertSimilar(Double.POSITIVE_INFINITY, line.m());
    assertSimilar(3.0, line.b());
    assertSimilar (new Tuple (1, 0), line.n);
    assertSimilar (3.0, line.c);
    assertSimilar (new Tuple (-3, 0), line.origin());

    line = Line.horizontal(3.0);
    assertSimilar(0.0, line.m());
    assertSimilar(3.0, line.b());
    assertSimilar (new Tuple (0, -1), line.n);
    assertSimilar (3.0, line.c);
    assertSimilar (new Tuple (0, 3), line.origin());

    line = Line.horizontal(-3.0);
    assertSimilar(0.0, line.m());
    assertSimilar(-3.0, line.b());
    assertSimilar (new Tuple (0, 1), line.n);
    assertSimilar (3.0, line.c);
    assertSimilar (new Tuple (0, -3), line.origin());
  }

  @Test
  public void testIntersect() {
    // test no intersection (parallel)
    Line l1 = Line.fromSlopeIntercept(0.5, 1.0);
    Line l2 = Line.fromSlopeIntercept (0.5, 2.0);
    assertNull (Line.intersect (l1, l2));

    // test no intersection (coincident)
    l2 = Line.fromSlopeIntercept(0.5, 1.0);
    assertNull (Line.intersect (l1, l2));

    // test easy intersection
    l2 = Line.fromSlopeIntercept(-0.5, 1.0);
    assertSimilar (new Tuple (0.0, 1.0), Line.intersect (l1, l2));

    // test harder intersection
    l2 = Line.fromSlopeIntercept(-0.5, 2.0);
    assertSimilar (new Tuple (1.0, 1.5), Line.intersect (l1, l2));
  }

  @Test
  public void testDistances() {
    Line line = Line.fromSlopeIntercept (0.5, 2);
    assertTrue(line.pointIsOnLine (line.origin ()));
    double theta = Math.atan(line.m());
    double c = line.b() * Math.cos(theta);
    assertSimilar(c, line.distanceToPoint (Tuple.ORIGIN));

    line = Line.vertical(5.0);
    assertEquals(Line.Classification.FRONT, line.classifyPoint (new Tuple (4.0, 1.0)));
    assertEquals(Line.Classification.ON, line.classifyPoint (new Tuple (5.0, 1.0)));
    assertEquals(Line.Classification.BACK, line.classifyPoint (new Tuple (6.0, 1.0)));

    line = Line.vertical(-5.0);
    assertEquals(Line.Classification.FRONT, line.classifyPoint (new Tuple (-4.0, 1.0)));
    assertEquals(Line.Classification.ON, line.classifyPoint (new Tuple (-5.0, 1.0)));
    assertEquals(Line.Classification.BACK, line.classifyPoint (new Tuple (-6.0, 1.0)));
  }
}
