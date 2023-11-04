package us.irdev.gtk.symbolic_math;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionTest {
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

  @Test
  public void testNamedConstant() {
    String pi = "π";
    NamedConstant nc = (NamedConstant) NamedConstant.get(pi);
    assert nc != null;
    assertNotEquals(null, nc);
    assertEquals(nc, NamedConstant.make(pi, Math.PI));
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
    Map<String, Double> at = new HashMap<>();
    at.put("x", 7.0);
    assertEquals(7.0, vx.n(at));
    assertEquals(7.0, vx.lastValue);
    assertEquals("(x:7.0)", vx.lastSignature);
    assertEquals(Constant.ONE, vx.d());
  }

  @Test
  public void testExpression() {
    Expression expr = Product.make (Constant.make (3), NamedConstant.get("π"), Power.make (Variable.make ("x"), Constant.make (2)));
    assertEquals (expr.toString (), "(3×π×(x^2))");
    assertEquals (expr.prettyString (), "3πx²");

    Set<String> dependencies = expr.getDependencies();
    assertEquals(1, dependencies.size());
    assertTrue(dependencies.contains ("x"));

    Expression der = expr.d();
    assertEquals ("((0×π×(x^2))+(0×3×(x^2))+(3×π×(2×(x^(2-1)))))", der.toString ());
    assertEquals ("0πx² + (0 × 3 × x²) + 3π2(x^(2 - 1))", der.prettyString ());

    Expression sim = der.simplify ();
    assertEquals (sim.prettyString (), "6πx");

    Expression der2 = sim.d().simplify();
    assertEquals (der2.prettyString (), "6π");
  }
}
