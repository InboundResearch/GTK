package us.irdev.gtk.symbolic_math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConstantTest {
  @Test
  public void testConstant() {
    // create a constant
    Constant c = (Constant) Constant.make (0);
    // check its value, expresison as a string, and numeric evaluation
    assertEquals (0, c.value);
    assertEquals("0", c.toString ());
    assertEquals("0", c.prettyString ());
    assertEquals(0, c.n (null));
    // verify the dependencies
    assertTrue(c.getDependencies().isEmpty ());
    // check that it correctly identifies an integer
    assertTrue(c.isInteger);
    // check the id and registration mechanism
    assertEquals(Constant.ZERO.id, c.id);
    assertEquals(Constant.ZERO, c);
    // verify the derivative is correct
    assertEquals(Constant.ZERO, c.d ());

    c = (Constant) Constant.make (5);
    assertTrue (c.id > 4);
    assertEquals (5, c.value);
    assertEquals("5", c.toString ());
    assertEquals("5", c.prettyString ());
    assertEquals(5, c.n (null));
    assertTrue(c.getDependencies().isEmpty ());
    assertTrue(c.isInteger);
    assertEquals(Constant.ZERO, c.d ());
    assertEquals(c, Constant.make (5));

    c = (Constant) Constant.make (55.5);
    assertTrue (c.id > 4);
    assertEquals (55.5, c.value);
    assertEquals("55.5", c.toString ());
    assertEquals("55.5", c.prettyString ());
    assertEquals(55.5, c.n (null));
    assertTrue(c.getDependencies().isEmpty ());
    assertFalse(c.isInteger);
    assertEquals(Constant.ZERO, c.d ());
    assertEquals(c, Constant.make (55.5));
  }

}
