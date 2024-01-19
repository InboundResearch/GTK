package us.irdev.gtk.symbolic_math;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionTest {
  @Test
  public void testExpression() {
    Expression expr = Product.make (Constant.make (3), NamedConstant.get("π"), Power.make (Variable.make ("x"), Constant.make (2)));
    assertEquals (expr.toString (), "(3*π*(x^2))");
    assertEquals (expr.prettyString (), "3πx²");

    Set<String> dependencies = expr.getDependencies();
    assertEquals(1, dependencies.size());
    assertTrue(dependencies.contains ("x"));

    Expression der = expr.d();
    assertEquals ("((0*π*(x^2))+(0*3*(x^2))+(3*π*(2*(x^(2-1)))))", der.toString ());
    assertEquals ("0πx² + (0 × 3 × x²) + 3π2(x^(2 - 1))", der.prettyString ());

    Expression sim = der.simplify ();
    assertEquals (sim.prettyString (), "6πx");

    Expression der2 = sim.d().simplify();
    assertEquals (der2.prettyString (), "6π");
  }
}
