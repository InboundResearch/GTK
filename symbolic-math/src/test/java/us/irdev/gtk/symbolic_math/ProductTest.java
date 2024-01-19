package us.irdev.gtk.symbolic_math;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static us.irdev.gtk.symbolic_math.Expressions.term;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductTest {
  @Test
  public void testBasic() {
    Expression x = Variable.make ("x");
    Expression c = Constant.make (3);
    Expression p = Product.make(x, c);
    assertEquals("(3*x)", p.toString());
  }

  @Test
  public void testProduct() {
    Expression x = Variable.make("x");
    Expression pi = NamedConstant.get("π");
    Expression c = Constant.make(4.0);
    Product p = (Product) Product.make(x, pi, c, x, c);
    assertEquals(5, p.expressions.size());
    assertEquals(c, p.expressions.get(0));
    assertEquals(c, p.expressions.get(1));
    assertEquals(pi, p.expressions.get(2));
    assertEquals(x, p.expressions.get(3));
    assertEquals(x, p.expressions.get(4));

    assertEquals("(4*4*π*x*x)", p.toString());
    assertEquals("4 × 4 × π × x × x", p.prettyString());

    Expression d = p.d();
    assertEquals("((0*4*π*x*x)+(0*4*π*x*x)+(0*4*4*x*x)+(1*4*4*π*x)+(1*4*4*π*x))", d.toString());
    assertEquals("(0 × 4 × π × x × x) + (0 × 4 × π × x × x) + (0 × 4 × 4 × x × x) + (1 × 4 × 4 × π × x) + (1 × 4 × 4 × π × x)", d.prettyString());

    Map<String, Double> at = new HashMap<> ();
    at.put("x", 6.0);
    assertEquals(6 * 6 * Math.PI * 4 * 4.0, p.n(at));

    Expression ps = p.simplify();
    assertEquals("(16*π*(x^2))", ps.toString());
    assertEquals("16πx²", ps.prettyString());
  }

  @Test
  public void testProductOfTwoSums () {
    Expression x = Variable.make ("x");
    Expression expr = Expressions.mul (Expressions.add(Expressions.mul (2, x), 3), Expressions.add(x, 4));
    assertEquals("(((2*x)+3)*(x+4))", expr.toString());
    Expression s = expr.simplify();
    assertEquals("((2*(x^2))+(11*x)+12)", s.toString());
    assertEquals("2x² + 11x + 12", s.prettyString());
  }

  @Test
  public void testProductOfTwoQuotients () {
    Expression x = Variable.make ("x");
    Expression expr = Expressions.mul (x, Expressions.mul (Expressions.div (Expressions.mul(Expressions.term (3), Expressions.term(5)), Expressions.term(5)), Expressions.div (Expressions.term(4), Expressions.term(5))));
    assertEquals("((((3*5)/5)*(4/5))*x)", expr.toString());
    Expression s = expr.simplify();
    assertEquals("((12*x)/5)", s.toString());
  }

  @Test
  public void testSimplify () {
    Expression x = Variable.make("x");
    Expression expr = Product.make (Constant.ONE, x, x);
    assertEquals ("(1*x*x)", expr.toString());
    Expression s = expr.simplify();
    assertEquals ("(x^2)", s.toString());
    assertEquals ("x²", s.prettyString());
  }
}
