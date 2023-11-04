package us.irdev.gtk.symbolic_math.trig;

import us.irdev.gtk.symbolic_math.Expression;
import us.irdev.gtk.symbolic_math.Function;

import java.util.Map;

import static us.irdev.gtk.symbolic_math.Expressions.*;

public class ArcSin extends Function {
  private ArcSin(Expression expr) {
    super (expr);
  }

  public static Expression make (Expression expr) {
    return register (new ArcSin (expr));
  }

  @Override
  protected String computeString () {
    return "sin⁻¹ " + expr;
  }

  @Override
  protected String computePrettyString (boolean wrap) {
    return "sin⁻¹ " + expr.prettyString (true);
  }

  @Override
  protected double computeN (Map<String, Double> at) {
    return Math.acos (expr.n (at));
  }

  @Override
  public Expression d () {
    return neg(one_over (sqrt(one_minus(sq (expr)))));
  }

}
