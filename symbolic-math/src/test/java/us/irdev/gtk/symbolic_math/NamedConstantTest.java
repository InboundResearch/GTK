package us.irdev.gtk.symbolic_math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NamedConstantTest {
  @Test
  public void testNamedConstant() {
    String pi = "π";
    NamedConstant nc = (NamedConstant) NamedConstant.get(pi);
    assert nc != null;
    assertNotEquals(null, nc);
    assertEquals(nc, NamedConstant.make(pi, "pi", Math.PI));
    assertEquals(Math.PI, nc.value);
    assertEquals(Math.PI, nc.n (null));
    assertEquals(pi, nc.toString ());
    assertEquals(pi, nc.prettyString ());
    assertTrue(nc.getDependencies().isEmpty ());
    assertEquals(Constant.ZERO, nc.d ());
    assertFalse(nc.isInteger);

    nc = (NamedConstant) NamedConstant.get("junk");
    assertNull (nc);
  }

}
