package us.irdev.gtk.symbolic_math;

import us.irdev.gtk.functional.ListFunc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static us.irdev.gtk.symbolic_math.Utility.wrap;

public class Sum extends ComputedExpressionList {
  private static final Logger log = LogManager.getLogger(Sum.class);

  private static int getOrder (Expression expr) {
    switch (expr.type) {
      case CONSTANT:
      case RATIONAL:
      case NAMED_CONSTANT:
        return 0;
      case PRODUCT:
      case SUM:
      case DIFFERENCE:
        // return the highest order of the child expressions
        return ListFunc.reduceInt(((ComputedExpressionList)expr).expressions, 0, (e, b) -> Math.max (getOrder(e), b));
      case QUOTIENT:
        // return the highest order of the dividend and divisor
        return Math.max (getOrder (((Quotient) expr).dividend), getOrder (((Quotient) expr).divisor));
      case VARIABLE:
        return 1;
      case POWER: {
        // return the constant exponent, or 1 if it's not constant
        Expression exponent = ((Power) expr).exponent;
        return (exponent.type.isConstant ()) ? (int) ((Constant) exponent).value : 1;
      }
      case FUNCTION:
        // return the highest order of the expression
        return getOrder (((Function)expr).expr);
    }
    return -1;
  }

  private Sum(List<Expression> addends) {
    super (Type.SUM, sorted (addends, (a, b) -> getOrder(b) - getOrder(a)));
  }

  public static Expression make (List<Expression> addends) {
    return register (new Sum (addends));
  }

  public static Expression make (Expression... addends) {
    return register (new Sum (Arrays.asList (addends)));
  }

  @Override
  public double computeN (Map<String, Double> at) {
    return ListFunc.reduce (expressions, 1, expressions.get(0).n(at), (a, b) -> b+ a.n(at));
  }

  @Override
  public Expression d () {
    return make (ListFunc.map(expressions, Expression::d));
  }

  private List<Expression> removeZeros (List<Expression> expressions) {
    List<Expression> retained = ListFunc.filter(expressions, expr -> (expr.type != Type.CONSTANT) || (((Constant) expr).value != 0));
    return retained.isEmpty () ? Collections.singletonList (Constant.ZERO) :
            (retained.size() == expressions.size()) ? expressions : retained;
  }

  private static void addToTerm (Map<String, List<Expression>> terms, String term, Expression expr) {
    terms.computeIfAbsent (term, k -> new ArrayList<> ()).add(expr);
  }

  @Override
  public Expression simplify () {
    log.debug ("Simplify " + report());
    // hide the sum addends...
    List<Expression> expressions = this.expressions;
    expressions = simplify (expressions);
    expressions = hoist (expressions, Type.SUM);
    expressions = gatherConstants (expressions, (expr, b) -> b + ((Constant) expr).value);

    // remove zero constants, and if there's nothing left, then the whole node is a zero
    expressions = removeZeros(expressions);

    // check if we only have one value left
    if (expressions.size () == 1) {
      return expressions.get (0);
    }

    // at this point, the entire expression should be terms, so we gather all terms of the same type
    List<Expression>[] splits = ListFunc.split (expressions, expr -> expr.getDependencies().isEmpty());
    if (splits[0].size() > 1) {
      Map<String, List<Expression>> terms = new HashMap<>();
      for (Expression expr : splits[0]) {
        if (expr.type == Type.PRODUCT) {
          Product product = (Product) expr;
          List<Expression>[] productSplits = ListFunc.split (product.expressions, e -> e.type.isConstant ());
          // there must be at least one non-constant
          Expression termProduct = (productSplits[0].size () == 1) ? productSplits[0].get(0) : Product.make (productSplits[0]);
          Expression termConstants = (productSplits[1].size() == 1) ? productSplits[1].get(0) : Product.make (productSplits[1]);
          String termStr = termProduct.toString();
          addToTerm (terms, termStr, termConstants);
        } else {
          addToTerm (terms, expr.toString(), Constant.ONE);
        }
      }

      for (String term : terms.keySet()) {
        List<Expression> termTerms = terms.get(term);
        Expression termExpression = (termTerms.size () == 1) ? termTerms.get (0) : Sum.make (termTerms).simplify();
        splits[1].add (Product.make (termExpression, registry.get (term)).simplify());
      }

      if (splits[1].size() != expressions.size()) {
        expressions = splits[1];
      }
      // don't rewrite expressions unless there will be a change
        /*
        expressions = splits[0];
        if (order != 1) {
          expressions.add (new Power (new Variable (dependency), constant (order)));
        } else {
          expressions.add (new Variable (dependency));
        }
        */
    }

    // check if we only have one value left
    if (expressions.size () == 1) {
      return expressions.get (0);
    }

    // if the expressions haven't changed, return this, otherwise... if the expressions list has only one
    // item we return that, and finally a new sum object based on the list
    return (expressions == this.expressions) ? this : make (expressions);
  }

  @Override
  public String computeString () {
    return wrap (ListFunc.reduce (expressions, 1, new StringBuilder ().append(expressions.get(0).toString()),
            (addend, sb) -> sb.append("+").append (addend.toString())), true);
  }
  @Override
  public String computePrettyString (boolean wrap) {
    return wrap (ListFunc.reduce (expressions, 1, new StringBuilder ().append(expressions.get(0).prettyString(true)),
            (addend, sb) -> sb.append(" + ").append (addend.prettyString(true))), wrap);
  }

  @Override
  protected Expression computeTrim (List<Expression> expressions) {
    return register (new Sum (expressions), true);
  }
}
