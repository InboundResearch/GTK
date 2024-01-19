package us.irdev.gtk.xyw;

import org.junit.jupiter.api.Test;

import static us.irdev.gtk.xyw.Helper.assertSimilar;
import static us.irdev.gtk.xyw.Tuple.*;
import static org.junit.jupiter.api.Assertions.*;

public class LineTest {

  @Test
  public void testLineConstructors() {
    Line line = new Line (0.0, 1.0, 0.0);
    assertSimilar (new Tuple (0, 1, 0), line.abc);
    assertSimilar (0.0, line.c());
    assertSimilar(0.0, line.m());
    assertSimilar(0.0, line.b());
    assertSimilar(line.origin(), PT (0, 0));

    line = new Line (0.0, -1.0, 2.0);
    assertSimilar (new Tuple (0, -1, 2.0), line.abc);
    assertSimilar (2.0, line.c());
    assertSimilar(0.0, line.m());
    assertSimilar(2.0, line.b());
    assertSimilar(line.origin(), PT (0, 2));

    // verify the line from two points
    line = Line.fromTwoPoints (PT (0, 2), PT (1, 2));
    assertSimilar (new Tuple (0, -1, 2.0), line.abc);
    assertSimilar (2.0, line.c());
    assertSimilar(0.0, line.m());
    assertSimilar(2.0, line.b());
    assertSimilar(line.origin(), PT (0, 2));

    line = Line.fromTwoPoints (PT (1, 2), PT (0, 2));
    assertSimilar (new Tuple(0, 1, -2.0), line.abc);
    assertSimilar (-2.0, line.c());
    assertSimilar(0.0, line.m());
    assertSimilar(2.0, line.b());
    assertSimilar(line.origin(), PT (0, 2));

    // verify the line from a point and direction
    line = Line.fromPointVector (PT (0, 2), VEC (1, 0));
    assertSimilar (new Tuple (0, -1, 2.0), line.abc);
    assertSimilar (2.0, line.c());
    assertSimilar(0.0, line.m());
    assertSimilar(2.0, line.b());
    assertSimilar(line.origin(), PT (0, 2));

    line = Line.fromPointVector (PT (1, 2), VEC (-1, 0));
    assertSimilar (new Tuple (0, 1, -2.0), line.abc);
    assertSimilar (-2.0, line.c());
    assertSimilar(0.0, line.m());
    assertSimilar(2.0, line.b());
    assertSimilar(line.origin(), PT (0, 2));

    // test the slope/intercept form
    line = Line.fromSlopeIntercept (0.0, 2);
    assertSimilar (new Tuple (0, -1, 2.0), line.abc);
    assertSimilar (2.0, line.c());
    assertSimilar(0.0, line.m());
    assertSimilar(2.0, line.b());
    assertSimilar(line.origin(), PT (0, 2));

    line = Line.fromSlopeIntercept (0.5, 2);
    assertSimilar(0.5, line.m());
    assertSimilar(2.0, line.b());
    double theta = Math.atan(line.m());
    double c = line.b() * Math.cos(theta);
    assertSimilar (VEC (1, -2).normalized().add(new Tuple (0, 0, c)), line.abc);
    assertSimilar (c, line.c());
    theta += Math.PI * 0.5;
    assertSimilar(line.origin(), ORIGIN.add(VEC (Math.cos(theta), Math.sin(theta)).scale (c)));

    line = Line.fromSlopeIntercept(Double.POSITIVE_INFINITY, 3);
    assertSimilar (new Tuple (1, 0, -3.0), line.abc);
    assertSimilar (-3.0, line.c());
    assertSimilar (PT (3, 0), line.origin());
    assertEquals(Double.POSITIVE_INFINITY, line.m());
    assertSimilar(3.0, line.b());

    line = Line.fromSlopeIntercept(Double.NEGATIVE_INFINITY, 3.0);
    assertEquals(Double.NEGATIVE_INFINITY, line.m());
    assertSimilar(3.0, line.b());
    assertSimilar (new Tuple (-1, 0, 3.0), line.abc);
    assertSimilar (3.0, line.c());
    assertSimilar (PT (3, 0), line.origin());

    // test horizontal and perpendicular
    line = Line.verticalUp (3.0);
    assertEquals(Double.POSITIVE_INFINITY, line.m());
    assertSimilar(3.0, line.b());
    assertSimilar (new Tuple (1, 0, -3.0), line.abc);
    assertSimilar (-3.0, line.c());
    assertSimilar (PT (3, 0), line.origin());

    line = Line.verticalUp (-3.0);
    assertEquals(Double.POSITIVE_INFINITY, line.m());
    assertSimilar(-3.0, line.b());
    assertSimilar (new Tuple (1, 0, 3.0), line.abc);
    assertSimilar (3.0, line.c());
    assertSimilar (PT (-3, 0), line.origin());

    line = Line.horizontalRight (3.0);
    assertSimilar(0.0, line.m());
    assertSimilar(3.0, line.b());
    assertSimilar (new Tuple (0, -1, 3.0), line.abc);
    assertSimilar (3.0, line.c());
    assertSimilar (PT (0, 3), line.origin());

    line = Line.horizontalRight (-3.0);
    assertSimilar(0.0, line.m());
    assertSimilar(-3.0, line.b());
    assertSimilar (new Tuple (0, -1, -3.0), line.abc);
    assertSimilar (-3.0, line.c());
    assertSimilar (PT (0, -3), line.origin());
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
    assertSimilar (PT (0.0, 1.0), Line.intersect (l1, l2));

    // test harder intersection
    l2 = Line.fromSlopeIntercept(-0.5, 2.0);
    assertSimilar (PT (1.0, 1.5), Line.intersect (l1, l2));
  }

  @Test
  public void testDistances() {
    Line line = Line.fromSlopeIntercept (0.5, 2);
    assertTrue(line.pointIsOnLine (line.origin ()));
    double theta = Math.atan(line.m());
    double c = line.b() * Math.cos(theta);
    assertSimilar(c, line.distanceToPoint (ORIGIN));

    line = Line.verticalUp (5.0);
    assertEquals(Line.Classification.BACK, line.classifyPoint (PT (4.0, 1.0)));
    assertEquals(Line.Classification.ON, line.classifyPoint (PT (5.0, 1.0)));
    assertEquals(Line.Classification.FRONT, line.classifyPoint (PT (6.0, 1.0)));

    line = Line.verticalUp (-5.0);
    assertEquals(Line.Classification.FRONT, line.classifyPoint (PT (-4.0, 1.0)));
    assertEquals(Line.Classification.ON, line.classifyPoint (PT (-5.0, 1.0)));
    assertEquals(Line.Classification.BACK, line.classifyPoint (PT (-6.0, 1.0)));
  }
}
