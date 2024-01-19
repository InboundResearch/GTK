package us.irdev.gtk.symbolic_math;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExpressionsTest {
  @Test
  public void testTerm () {
    Expression expr = Expressions.add(Expressions.term(3, "x", 2), Expressions.term(2, "x"), Expressions.term (5)).simplify ();
    assertEquals("((3*(x^2))+(2*x)+5)", expr.toString ());
    assertEquals("3x² + 2x + 5", expr.prettyString ());
    Set<String> deps = expr.getDependencies();
    assertEquals(1, deps.size());
    assertTrue(deps.contains("x"));

    Map<String, Double> at = new HashMap<> ();
    at.put("x", 2.0);
    double eval = expr.n(at);
    assertEquals(21, eval);
    eval = expr.n(at);
    assertEquals(21, eval);

    Expression d = expr.d().simplify ();
    eval = d.n(at);
    assertEquals(14, eval);
    eval = d.n(at);
    assertEquals(14, eval);
  }

  @Test
  public void test1() {
    String x = "x";
    Expression expr = Expressions.div (Expressions.add (Expressions.term(x, 2), Expressions.term (3, x)), Expressions.add (Expressions.term(x), Expressions.term (4)));
    Expression d = expr.d().simplify();

    Set<String> deps = expr.getDependencies();
    assertEquals(1, deps.size());
    assertTrue(deps.contains(x));

    deps = d.getDependencies();
    assertEquals(1, deps.size());
    assertTrue(deps.contains(x));

    assertEquals ("(((x^2)+(3*x))/(x+4))", expr.toString());
    assertEquals ("(x² + 3x) / (x + 4)", expr.prettyString());
    assertEquals ("(x² + 8x + 12) / (x² + 8x + 16)", d.prettyString());

    Expression dExpected = Expressions.div (Expressions.mul(Expressions.add(Expressions.term(x), Expressions.term(2)), Expressions.add(Expressions.term(x), Expressions.term(6))), Expressions.sq(Expressions.add(Expressions.term(x), Expressions.term(4))));

    Map<String, Double> at = new HashMap<> ();
    at.put("x", 2.0);
    double eval = expr.n(at);
    assertEquals(5.0 / 3.0, eval);
    eval = expr.n(at);
    assertEquals(5.0 / 3.0, eval);

    eval = d.n(at);
    assertEquals(8.0 / 9.0, eval);
    eval = d.n(at);
    assertEquals(8.0 / 9.0, eval);

    assertEquals(dExpected.n(at), d.n(at));
  }

}
