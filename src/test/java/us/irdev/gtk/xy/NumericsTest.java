package us.irdev.gtk.xy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static us.irdev.gtk.xy.Numerics.*;

public class NumericsTest {
  @Test
  public void testInterpolate() {
    assertEquals(0.5, lerp (0, 1, 0.5));
    assertEquals(0.5, lerp (0, 2, 0.25));
    assertEquals(0.5, lerp (-1, 1, 0.75));
  }

  @Test
  public void testWhere() {
    assertEquals(0.5, where(0.5, 0, 0, 1, 1));
    assertEquals(0.5, where(0.5, 0, 0, 2, 2));
    assertEquals(0.5, where(0.5, -1, -1, 1, 1));
    assertEquals(0.75, where(0.5, 0, -1, 1, 1));
  }

  private void interpolateWhere (double a, double aVal, double b, double bVal, double interpolant) {
    assertTrue(Math.abs(where (lerp (aVal, bVal, interpolant), a, aVal, b, bVal) - lerp (a, b, interpolant)) < 1.0e-6);
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
    assertFalse(withinTolerance(1, 1 + 1e-3));
    assertFalse(withinTolerance(1, 1 + 1e-4));
    assertFalse(withinTolerance(1, 1 + 1e-5));
    assertFalse(withinTolerance(1, 1 + 1e-6));
    assertFalse(withinTolerance(1, 2));
    assertFalse(withinTolerance(1 + (10 * TOLERANCE), 1));
    assertFalse(withinTolerance(1000 + (10 * TOLERANCE), 1000));
    assertFalse(withinTolerance(1 + (2 * TOLERANCE), 1));
    assertFalse(withinTolerance(1000 + (2 * TOLERANCE), 1000));

    // things that should be true
    assertTrue(withinTolerance(1, 1));
    assertTrue(withinTolerance(1 - (TOLERANCE * 0.975), 1));
    assertTrue(withinTolerance(1 + (TOLERANCE * 0.975), 1));
    assertTrue(withinTolerance(1000 + (TOLERANCE * 0.975), 1000));

    // some interesting cases that should fail with the tolerances approach
    assertFalse(withinTolerance(1 - (TOLERANCE * 1.025), 1));
    assertFalse(withinTolerance(1 + (TOLERANCE * 1.025), 1));
    int delta15 = -15 - TOLERANCE_ORDER_OF_MAGNITUDE;
    double num = Math.pow(10, -delta15);
    assertFalse(withinTolerance(num - TOLERANCE, num));
    assertFalse(withinTolerance(num + TOLERANCE, num));

    // things that should not be true
    assertFalse(similar (1, 1 + 1e-3));
    assertFalse(similar (1, 1 + 1e-4));
    assertFalse(similar (1, 1 + 1e-5));
    assertFalse(similar (1, 1 + 1e-6));
    assertFalse(similar (1, 2));
    assertFalse(similar (1 + (10 * TOLERANCE), 1));
    assertFalse(similar (1000 + (3000 * TOLERANCE), 1000));

    // things that should be true
    assertTrue(similar (1, 1));
    assertTrue(similar (1 - TOLERANCE, 1));
    assertTrue(similar (1 + TOLERANCE, 1));
    assertTrue(similar (1000 + TOLERANCE, 1000));
    assertTrue(similar (10 + TOLERANCE, 10));
    assertTrue(similar (10 + (10 * TOLERANCE), 10));
    assertTrue(similar (100 + (100 * TOLERANCE), 100));
    assertTrue(similar (1000 + (1000 * TOLERANCE), 1000));
  }

}
