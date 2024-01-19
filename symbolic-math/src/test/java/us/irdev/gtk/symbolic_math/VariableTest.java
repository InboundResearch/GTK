package us.irdev.gtk.symbolic_math;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VariableTest {
  @Test
  public void testVariable() {
    String x = "x";
    Variable vx = (Variable) Variable.make (x);
    assertNotNull(vx);
    assertEquals(x, vx.name);
    assertEquals(x, vx.toString());
    assertEquals(x, vx.prettyString());
    Set<String> dependencies = vx.getDependencies();
    assertFalse(dependencies.isEmpty ());
    assertEquals(1, dependencies.size());
    assertTrue(dependencies.contains(x));
    Map<String, Double> at = new HashMap<> ();
    at.put("x", 7.0);
    assertEquals(7.0, vx.n(at));
    assertEquals(7.0, vx.lastValue);
    assertEquals("(x:7.0)", vx.lastSignature);
    assertEquals(Constant.ONE, vx.d());
  }

}
