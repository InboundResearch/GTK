package us.irdev.gtk.xy.bb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoundaryBehaviorTest {
  @Test
  public void testBoundaryBehaviorNone () {
    BoundaryBehavior bb = new BoundaryBehaviorValue ();

    assertEquals(0, bb.adjustDomainMax(0, 0, 1));

    assertEquals(1, bb.condition(1, 0, 0));
    assertEquals(-1, bb.condition(-1, 0, 0));
    assertEquals(0, bb.condition(0, 0, 0));
    assertEquals(-1.0e5, bb.condition(-1.0e5, 0, 0));
  }

  @Test
  public void testBoundaryBehaviorClamp () {
    BoundaryBehavior bb = new BoundaryBehaviorClamp ();

    assertEquals(0, bb.adjustDomainMax(0, 1, 1));

    assertEquals(1, bb.condition(1, 0, 2));
    assertEquals(0, bb.condition(-1, 0, 2));
    assertEquals(2, bb.condition(2, 0, 2));
    assertEquals(2, bb.condition(3, 0, 2));

    assertEquals(0, bb.condition(0, -1, 1));
    assertEquals(1, bb.condition(1, -1, 1));
    assertEquals(-1, bb.condition(-1, -1, 1));
    assertEquals(1, bb.condition(3, -1, 1));
    assertEquals(-1, bb.condition(-3, -1, 1));
  }

  @Test
  public void testBoundaryBehaviorWrap () {
    BoundaryBehavior bb = new BoundaryBehaviorWrap ();

    assertEquals(1, bb.adjustDomainMax(0, 1, 1));
    assertEquals(2, bb.adjustDomainMax(0, 6, 2));

    assertEquals(0, bb.condition(0, 0, 1));
    assertEquals(0, bb.condition(1, 0, 1));

    assertEquals(0.5, bb.condition(0.5, 0, 1));
    assertEquals(0.5, bb.condition(1.5, 0, 1));
    assertEquals(0.5, bb.condition(2.5, 0, 1));
    assertEquals(0.5, bb.condition(10.5, 0, 1));

    assertEquals(0.5, bb.condition(-0.5, 0, 1));
    assertEquals(0.5, bb.condition(-1.5, 0, 1));
    assertEquals(0.5, bb.condition(-2.5, 0, 1));
    assertEquals(0.5, bb.condition(-10.5, 0, 1));

    assertEquals(0.75, bb.condition(0.75, 0, 1));
    assertEquals(0.75, bb.condition(1.75, 0, 1));
    assertEquals(0.75, bb.condition(2.75, 0, 1));
    assertEquals(0.75, bb.condition(10.75, 0, 1));

    assertEquals(0.25, bb.condition(-0.75, 0, 1));
    assertEquals(0.25, bb.condition(-1.75, 0, 1));
    assertEquals(0.25, bb.condition(-2.75, 0, 1));
    assertEquals(0.25, bb.condition(-10.75, 0, 1));

    assertEquals(-0.75, bb.condition(-0.75, -1, 2));
    assertEquals(1.25, bb.condition(-1.75, -1, 2));
    assertEquals(0.25, bb.condition(-2.75, -1, 2));
    assertEquals(1.25, bb.condition(-10.75, -1, 2));

    assertEquals(0.75, bb.condition(0.75, -1, 2));
    assertEquals(1.75, bb.condition(1.75, -1, 2));
    assertEquals(-0.25, bb.condition(2.75, -1, 2));
    assertEquals(1.75, bb.condition(10.75, -1, 2));

    // with interval 2
    assertEquals(-0.75, bb.condition(-0.75, -1, 3));
    assertEquals(2.25, bb.condition(-1.75, -1, 3));
    assertEquals(1.25, bb.condition(-2.75, -1, 3));
    assertEquals(1.25, bb.condition(-10.75, -1, 3));

    assertEquals(0.75, bb.condition(0.75, -1, 3));
    assertEquals(1.75, bb.condition(1.75, -1, 3));
    assertEquals(2.75, bb.condition(2.75, -1, 3));
    assertEquals(2.75, bb.condition(10.75, -1, 3));

  }

  @Test
  public void testBoundaryBehaviorAccordion () {
    BoundaryBehavior bb = new BoundaryBehaviorAccordion ();

    assertEquals(0, bb.adjustDomainMax(0, 1, 1));

    assertEquals(0, bb.condition(0, 0, 1));
    assertEquals(1, bb.condition(1, 0, 1));
    assertEquals(0, bb.condition(2, 0, 1));
    assertEquals(1, bb.condition(3, 0, 1));
    assertEquals(1, bb.condition(-1, 0, 1));
    assertEquals(0, bb.condition(-2, 0, 1));
    assertEquals(1, bb.condition(-3, 0, 1));

    assertEquals(0.5, bb.condition(-0.5, 0, 1));
    assertEquals(0.5, bb.condition(0.5, 0, 1));
    assertEquals(0.5, bb.condition(1.5, 0, 1));

    assertEquals(0.25, bb.condition(-0.25, 0, 1));
    assertEquals(0.25, bb.condition(0.25, 0, 1));
    assertEquals(0.75, bb.condition(1.25, 0, 1));

    assertEquals(0, bb.condition(0, 0, 4));
    assertEquals(4, bb.condition(4, 0, 4));

    assertEquals(1, bb.condition(1, 0, 4));
    assertEquals(1, bb.condition(-1, 0, 4));

    assertEquals(3, bb.condition(5, 0, 4));
    assertEquals(3, bb.condition(-5, 0, 4));

    assertEquals(1, bb.condition(9, 0, 4));
    assertEquals(1, bb.condition(-9, 0, 4));

    assertEquals(3, bb.condition(13, 0, 4));
    assertEquals(3, bb.condition(-13, 0, 4));

    assertEquals(1, bb.condition(1, 1, 3));
    assertEquals(2, bb.condition(0, 1, 3));

    assertEquals(2.5, bb.condition(-0.5, 1, 3));
    assertEquals(1.5, bb.condition(0.5, 1, 3));
    assertEquals(1.5, bb.condition(1.5, 1, 3));

    assertEquals(2.25, bb.condition(-0.25, 1, 3));
    assertEquals(1.75, bb.condition(0.25, 1, 3));
    assertEquals(1.25, bb.condition(1.25, 1, 3));
  }
}
