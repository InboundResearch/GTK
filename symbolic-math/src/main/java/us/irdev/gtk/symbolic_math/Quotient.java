package us.irdev.gtk.symbolic_math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static us.irdev.gtk.symbolic_math.Utility.wrap;

public class Quotient extends ComputedExpression {
  private static final Logger log = LogManager.getLogger(Quotient.class);

  public final Expression dividend; // aka numerator
  public final Expression divisor;  // aka denominator

  /**
   * note: we don't support an expression of the form a / b / c / d ..., because that would be better
   * expressed as a / (b * c * d ...)
   */
  private Quotient(Expression dividend, Expression divisor) {
    super (Type.QUOTIENT);
    this.dividend = dividend;
    this.divisor = divisor;
  }

  public static Expression make (Expression dividend, Expression divisor) {
    return register (new Quotient (dividend, divisor));
  }

  @Override
  public Expression d () {
    // quotient rule:
    // (a / b)' = ((a' b) - (a b')) / b^2
    return make (
            Difference.make (Product.make (dividend.d(), divisor), Product.make (dividend, divisor.d())),
            Power.make (divisor, Constant.TWO)
    );
  }

  @Override
  public String computeString () {
    return Utility.wrap (new StringBuilder()
            .append (dividend.toString()).append ("/").append (divisor.toString ()), true);
  }

  @Override
  public String computePrettyString (boolean wrap) {
    return Utility.wrap (new StringBuilder()
            .append (dividend.prettyString(true)).append (" / ").append (divisor.prettyString (true)), wrap);
  }

  @Override
  public Expression simplify () {
    Expression dividend = this.dividend.simplify ();
    Expression divisor = this.divisor.simplify ();

    // if the divisor is a constant...
    if (divisor.type == Type.CONSTANT) {
      Constant divisorConstant = (Constant) divisor;
      double divisorValue = ((Constant) divisor).value;
      // division by 1
      if (divisorConstant.value == 1.0) {
        return dividend;
      }

      // collapse any new constants
      if (dividend.type == Type.CONSTANT) {
        Constant dividendConstant = (Constant) dividend;
        if (divisorConstant.isInteger && dividendConstant.isInteger) {
          int dividendInt = (int) dividendConstant.value;
          int divisorInt = (int) divisorConstant.value;
          int gcd = Utility.gcd (dividendInt, divisorInt);
          return make (Constant.make ((double) dividendInt / gcd), Constant.make ((double) divisorInt / gcd));
        } else {
          return Constant.make (((Constant) dividend).value / divisorValue);
        }
      }
    }

    // a quotient with a quotient in the dividend...
    if (dividend.type == Type.QUOTIENT) {
      Quotient q = (Quotient) dividend;
      return make (q.dividend, Product.make (q.divisor, divisor)).simplify ();
    }


    if ((dividend != this.dividend) || (divisor != this.divisor)) {
      return make (dividend, divisor);
    }

    // - cancel variables when they are above and below
    return this;
  }

  @Override
  public double computeN (Map<String, Double> at) {
    return dividend.n(at) / divisor.n(at);
  }

  @Override
  public Set<String> computeDependencies () {
    Set<String> dependencies = new HashSet<>();
    dependencies.addAll (dividend.getDependencies());
    dependencies.addAll (divisor.getDependencies());
    return dependencies;
  }

  @Override
  public Expression trim () {
    Expression dividend = this.dividend.trim ();
    Expression divisor = this.divisor.trim ();
    if ((dividend != this.dividend) || (divisor != this.divisor)) {
      return register (new Quotient (dividend, divisor), true);
    }
    return this;
  }
}
