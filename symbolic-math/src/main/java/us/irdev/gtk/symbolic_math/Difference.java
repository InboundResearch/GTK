package us.irdev.gtk.symbolic_math;

import us.irdev.gtk.functional.ListFunc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static us.irdev.gtk.symbolic_math.Utility.wrap;

public class Difference extends ComputedExpressionList {
  private static final Logger log = LogManager.getLogger(Difference.class);

  private Difference(List<Expression> subtrahends) {
    super (Type.DIFFERENCE, subtrahends);
  }

  public static Expression make (List<Expression> subtrahends) {
    return register (new Difference (subtrahends));
  }

  public static Expression make (Expression... subtrahends) {
    return register (new Difference (Arrays.asList (subtrahends)));
  }

  @Override
  public double computeN (Map<String, Double> at) {
    return ListFunc.reduce (expressions, 1, expressions.get(0).n(at), (a, b) -> b - a.n(at));
  }

  @Override
  public Expression d () {
    return Difference.make (ListFunc.map(expressions, Expression::d));
  }

  @Override
  public Expression simplify () {
    // rewrite the difference as a sum of negative products
    List<Expression> addends = new ArrayList<>(expressions.size());
    addends.add (expressions.get (0));
    for (int i = 1; i < expressions.size (); ++i) {
      addends.add (Product.make (Constant.NEG_ONE, expressions.get (i)));
    }
    return Sum.make(addends).simplify();
  }

  @Override
  protected String computeString () {
    return wrap (ListFunc.reduce (expressions, 1, new StringBuilder ().append(expressions.get(0).toString()),
            (subtrahend, sb) -> sb.append("-").append (subtrahend.toString())), true);
  }

  @Override
  protected String computePrettyString (boolean wrap) {
    return wrap (ListFunc.reduce (expressions, 1, new StringBuilder ().append(expressions.get(0).prettyString(wrap)),
            (subtrahend, sb) -> sb.append(" - ").append (subtrahend.prettyString(wrap))), wrap);
  }


  @Override
  protected Expression computeTrim (List<Expression> expressions) {
    return register (new Difference (expressions), true);
  }
}
