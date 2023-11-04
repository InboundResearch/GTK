package us.irdev.gtk.symbolic_math.trig;

import us.irdev.gtk.symbolic_math.Expression;
import us.irdev.gtk.symbolic_math.Function;

import java.util.Map;

public class Sin extends Function {
  private Sin(Expression angle) {
    super (angle);
  }

  public static Expression make (Expression angle) {
    return register (new Sin (angle));
  }

  @Override
  protected String computeString () {
    return "sin " + expr;
  }

  @Override
  protected String computePrettyString (boolean wrap) {
    return "sin " + expr.prettyString (true);
  }

  @Override
  protected double computeN (Map<String, Double> at) {
    return Math.sin (expr.n (at));
  }

  @Override
  public Expression d () {
    return Cos.make (expr);
  }

}
