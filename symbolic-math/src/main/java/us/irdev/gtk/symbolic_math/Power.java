package us.irdev.gtk.symbolic_math;

import us.irdev.gtk.functional.ListFunc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static us.irdev.gtk.symbolic_math.Utility.wrap;

public class Power extends ComputedExpression {
  private static final Logger log = LogManager.getLogger(Power.class);

  public final Expression base;
  public final Expression exponent;

  private Power (Expression base, Expression exponent) {
    super (Type.POWER);
    this.base = base;
    this.exponent = exponent;
  }

  public static Expression make (Expression base, Expression exponent) {
    return register (new Power (base, exponent));
  }

  @Override
  public Expression d () {
    return Product.make (exponent, make (base, Difference.make (exponent, Constant.ONE)));
  }

  @Override
  public String computeString () {
    return wrap(new StringBuilder().append (base.toString()).append("^").append(exponent.toString ()), true);
  }

  @Override
  protected String computePrettyString (boolean wrap) {
    if ((exponent.type == Type.CONSTANT) && ((Constant) exponent).isInteger) {
      StringBuilder sb = new StringBuilder();
      //                   "+ -. 0123456789"
      char[] superscript = "⁺ ⁻∙ ⁰¹²³⁴⁵⁶⁷⁸⁹".toCharArray();
      char[] exp = exponent.prettyString(false).toCharArray();
      for (int i = 0; i < exp.length; ++i) {
        exp[i] = superscript[exp[i] - '+'];
      }
      sb.append (base.prettyString(true)).append(exp);
      return sb.toString();
    }
    return wrap(new StringBuilder().append (base.prettyString(true)).append("^").append(exponent.prettyString (true)), true);
  }

  @Override
  protected double computeN (Map<String, Double> at) {
    return Math.pow (base.n(at), exponent.n(at));
  }

  @Override
  public Expression simplify () {
    Expression simplifiedBase = base.simplify ();
    Expression simplifiedExponent = exponent.simplify ();
    if (simplifiedExponent.type == Type.CONSTANT) {
      Constant constant = (Constant) simplifiedExponent;
      if (constant.isInteger) {
        int exponentInt = (int) constant.value;

        // XXX might need to account for simpifiedBase being 0 (undefined)...
        if (exponentInt == 0) return Constant.ONE;
        if (exponentInt == 1) return simplifiedBase;

        // a particular form is expandable - the exponent is an integer constant and the base is a
        // sum or a product
        if ((simplifiedBase.type == Type.SUM) || (simplifiedBase.type == Type.PRODUCT)) {
          List<Expression> expanded = ListFunc.fill (exponentInt, i -> simplifiedBase);
          return Product.make (expanded).simplify();
        }
      }
    }
    if ((simplifiedExponent.type == Type.CONSTANT) && (((Constant) simplifiedExponent).value == 1.0)) {
      return simplifiedBase;
    }

    if ((simplifiedBase != base) || (simplifiedExponent != exponent)) {
      return new Power (simplifiedBase, simplifiedExponent);
    }

    // a power node should be expanded until it is of the canonical form: V ^ P. if base is a sum,
    // product, or quotient, it should be expanded to terms of the form C * Vn ^ P
    return this;
  }

  @Override
  public Set<String> computeDependencies () {
    Set<String> dependencies = new HashSet<> ();
    dependencies.addAll (base.getDependencies());
    dependencies.addAll (exponent.getDependencies());
    return dependencies;
  }
}
