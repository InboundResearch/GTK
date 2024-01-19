package us.irdev.gtk.symbolic_math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReaderTest {
  @Test
  public void testBasics() {
    Expression expr = Reader.fromString("a");
    assertEquals(Type.VARIABLE, expr.type);

    expr = Reader.fromString("π");
    assertEquals(Type.NAMED_CONSTANT, expr.type);
    assertEquals(NamedConstant.get("π"), expr);

    expr = Reader.fromString("-3.14");
    assertEquals(Type.CONSTANT, expr.type);
    assertEquals(-3.14, ((Constant)expr).value);

    expr = Reader.fromString("(x*3)");
    assertEquals(Type.PRODUCT, expr.type);
    assertEquals("(3*x)", expr.toString());

    expr = Reader.fromString("(λ/ϕ)");
    assertEquals(Type.QUOTIENT, expr.type);
    assertEquals("(λ/ϕ)", expr.toString());

    expr = Reader.fromString("(β+π)");
    assertEquals(Type.SUM, expr.type);
    assertEquals("(β+π)", expr.toString());

    expr = Reader.fromString("(γι-energy)");
    assertEquals(Type.DIFFERENCE, expr.type);
    assertEquals("(γι-energy)", expr.toString());

    expr = Reader.fromString("(energy^2)");
    assertEquals(Type.POWER, expr.type);
    assertEquals("(energy^2)", expr.toString());
  }

  @Test
  public void testComplex() {
    // blackbody radiation
    Expression expr = Reader.fromString("(((2*planck*(v^3))/(light^2))*(1/((euler^((planck*v)/(boltzmann*T)))-1)))");
    assertEquals("(((2*\uD835\uDE29*(v^3))/(\uD835\uDC50^2))*(1/((\uD835\uDC52^((\uD835\uDE29*v)/(\uD835\uDC58*T)))-1)))", expr.toString());
    assertEquals("(2\uD835\uDE29v³ / \uD835\uDC50²)(1 / ((\uD835\uDC52^(\uD835\uDE29v / \uD835\uDC58T)) - 1))", expr.prettyString());
  }

  @Test
  public void testErrorCase() {
    Expression expr = Reader.fromString ("(2*)");
    assertNull(expr);
  }
}
