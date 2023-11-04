package us.irdev.gtk.symbolic_math.trig;

import us.irdev.gtk.symbolic_math.Constant;
import us.irdev.gtk.symbolic_math.Expression;
import us.irdev.gtk.symbolic_math.Function;
import us.irdev.gtk.symbolic_math.Product;

import java.util.Map;

public class Cos extends Function {
  private Cos(Expression angle) {
    super (angle);
  }

  public static Expression make (Expression angle) {
    return register (new Cos (angle));
  }

  @Override
  protected String computeString () {
    return "cos " + expr.toString ();
  }

  @Override
  protected String computePrettyString (boolean wrap) {
    return "cos " + expr.prettyString (true);
  }

  @Override
  protected double computeN (Map<String, Double> at) {
    return Math.cos (expr.n (at));
  }

  @Override
  public Expression d () {
    return Product.make (Constant.NEG_ONE, Sin.make(expr));
  }
}
