package us.irdev.gtk.xyw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NumericsTest {
  @Test
  public void testInterpolate() {
    Assertions.assertEquals(0.5, Numerics.lerp (0, 1, 0.5));
    Assertions.assertEquals(0.5, Numerics.lerp (0, 2, 0.25));
    Assertions.assertEquals(0.5, Numerics.lerp (-1, 1, 0.75));
  }

  @Test
  public void testWhere() {
    Assertions.assertEquals(0.5, Numerics.where(0.5, 0, 0, 1, 1));
    Assertions.assertEquals(0.5, Numerics.where(0.5, 0, 0, 2, 2));
    Assertions.assertEquals(0.5, Numerics.where(0.5, -1, -1, 1, 1));
    Assertions.assertEquals(0.75, Numerics.where(0.5, 0, -1, 1, 1));
  }

  private void interpolateWhere (double a, double aVal, double b, double bVal, double interpolant) {
    assertTrue(Math.abs(Numerics.where (Numerics.lerp (aVal, bVal, interpolant), a, aVal, b, bVal) - Numerics.lerp (a, b, interpolant)) < 1.0e-6);
  }

  @Test
  public void testInterpolateWhere() {
    interpolateWhere(0, 0, 1, 1, 0.5);
    interpolateWhere(-1, 0, 1, 1, 0.5);
    interpolateWhere(-5, 30, 1, 27, 0.35);
  }

  @Test
  public void testComparisons() {
    // things that should not be true
    Assertions.assertFalse(Numerics.withinTolerance(1, 1 + 1e-3));
    Assertions.assertFalse(Numerics.withinTolerance(1, 1 + 1e-4));
    Assertions.assertFalse(Numerics.withinTolerance(1, 1 + 1e-5));
    Assertions.assertFalse(Numerics.withinTolerance(1, 1 + 1e-6));
    Assertions.assertFalse(Numerics.withinTolerance(1, 2));
    Assertions.assertFalse(Numerics.withinTolerance(1 + (10 * Numerics.TOLERANCE), 1));
    Assertions.assertFalse(Numerics.withinTolerance(1000 + (10 * Numerics.TOLERANCE), 1000));
    Assertions.assertFalse(Numerics.withinTolerance(1 + (2 * Numerics.TOLERANCE), 1));
    Assertions.assertFalse(Numerics.withinTolerance(1000 + (2 * Numerics.TOLERANCE), 1000));

    // things that should be true
    Assertions.assertTrue(Numerics.withinTolerance(1, 1));
    Assertions.assertTrue(Numerics.withinTolerance(1 - (Numerics.TOLERANCE * 0.975), 1));
    Assertions.assertTrue(Numerics.withinTolerance(1 + (Numerics.TOLERANCE * 0.975), 1));
    Assertions.assertTrue(Numerics.withinTolerance(1000 + (Numerics.TOLERANCE * 0.975), 1000));

    // some interesting cases that should fail with the tolerances approach
    Assertions.assertFalse(Numerics.withinTolerance(1 - (Numerics.TOLERANCE * 1.025), 1));
    Assertions.assertFalse(Numerics.withinTolerance(1 + (Numerics.TOLERANCE * 1.025), 1));
    int delta15 = -15 - Numerics.TOLERANCE_ORDER_OF_MAGNITUDE;
    double num = Math.pow(10, -delta15);
    Assertions.assertFalse(Numerics.withinTolerance(num - Numerics.TOLERANCE, num));
    Assertions.assertFalse(Numerics.withinTolerance(num + Numerics.TOLERANCE, num));

    // things that should not be true
    Assertions.assertFalse(Numerics.similar (1, 1 + 1e-3));
    Assertions.assertFalse(Numerics.similar (1, 1 + 1e-4));
    Assertions.assertFalse(Numerics.similar (1, 1 + 1e-5));
    Assertions.assertFalse(Numerics.similar (1, 1 + 1e-6));
    Assertions.assertFalse(Numerics.similar (1, 2));
    Assertions.assertFalse(Numerics.similar (1 + (10 * Numerics.TOLERANCE), 1));
    Assertions.assertFalse(Numerics.similar (1000 + (3000 * Numerics.TOLERANCE), 1000));

    // things that should be true
    Assertions.assertTrue(Numerics.similar (1, 1));
    Assertions.assertTrue(Numerics.similar (1 - Numerics.TOLERANCE, 1));
    Assertions.assertTrue(Numerics.similar (1 + Numerics.TOLERANCE, 1));
    Assertions.assertTrue(Numerics.similar (1000 + Numerics.TOLERANCE, 1000));
    Assertions.assertTrue(Numerics.similar (10 + Numerics.TOLERANCE, 10));
    Assertions.assertTrue(Numerics.similar (10 + (10 * Numerics.TOLERANCE), 10));
    Assertions.assertTrue(Numerics.similar (100 + (100 * Numerics.TOLERANCE), 100));
    Assertions.assertTrue(Numerics.similar (1000 + (1000 * Numerics.TOLERANCE), 1000));
  }

}
