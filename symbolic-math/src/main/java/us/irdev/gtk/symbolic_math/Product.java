package us.irdev.gtk.symbolic_math;

import us.irdev.gtk.functional.ListFunc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static us.irdev.gtk.symbolic_math.Utility.wrap;

public class Product extends ComputedExpressionList {
  private static final Logger log = LogManager.getLogger(Product.class);

  private static int compare (Expression a, Expression b) {
    int ordinal = a.type.ordinal() - b.type.ordinal ();
    if (ordinal == 0) {
      switch (a.type) {
        case CONSTANT: {
          // sort by value
          double delta = ((Constant) a).value - ((Constant) b).value;
          return (int)(delta / Math.abs(delta));
        }
        case NAMED_CONSTANT: {
          // sport by name
          return ((NamedConstant)a).name.compareTo (((NamedConstant)b).name);
        }
        case VARIABLE: {
          // sort by name
          return ((Variable)a).name.compareTo (((Variable)b).name);
        }
      }
    } return ordinal;
  }

  private Product(List<Expression> factors) {
    super (Type.PRODUCT, sorted (factors, Product::compare));
  }

  public static Expression make (List<Expression> factors) {
    return register (new Product (factors));
  }

  public static Expression make (Expression... factors) {
    return register (new Product (Arrays.asList (factors)));
  }

  @Override
  protected String computeString () {
    return wrap(ListFunc.reduce (expressions, 1, new StringBuilder ().append(expressions.get(0).toString()),
            (expression, sb) -> sb.append("×").append (expression.toString())), true);
  }

  @Override
  protected String computePrettyString (boolean wrap) {
    if (isTerm()) {
      Expression expr = expressions.get (0);
      String str = ((expr.type == Type.CONSTANT) && (((Constant) expr).value == -1)) ? "-" : expr.prettyString(true);
      return ListFunc.reduce (expressions, 1, new StringBuilder ().append (str),
              (expression, sb) -> sb.append (expression.prettyString (true))).toString ();
    }
    return wrap(ListFunc.reduce (expressions, 1, new StringBuilder ().append(expressions.get(0).prettyString(true)),
            (expression, sb) -> sb.append(" × ").append (expression.prettyString(true))), wrap);
  }

  @Override
  public double computeN (Map<String, Double> at) {
    return ListFunc.reduce(expressions, 1, expressions.get(0).n(at), (a, b) -> b * a.n(at));
  }

  @Override
  public Expression d () {
    // apply the power rule:
    // (f * g * h)' = (f' * g * h) + (f * g' * h) + (f * g * h')
    final List<Expression> derivatives = ListFunc.map(expressions, Expression::d);
    List<Expression> products = ListFunc.map(derivatives, (i, derivative) ->
            make (ListFunc.map(expressions, (j, expression) -> (i == j) ? derivative : expression))
    );
    return Sum.make(products);
  }

  private List<Expression> checkConstants (List<Expression> expressions) {
    // check if we have a 0 constant, in which case the whole product node is invalid and we should
    // just eliminate it... or a 1, which can just be removed
    List<Expression>[] splits = ListFunc.split(expressions, expr -> expr.type == Type.CONSTANT);
    if (splits[1].size() == 1) {
      Expression expression = splits[1].get(0);
        double value = ((Constant) expression).value;
        if (value == 0) {
          return splits[1];
        }
        if (value == 1) {
          return splits[0];
        }
    }
    return expressions;
  }

  @Override
  public Expression simplify () {
    log.debug ("Simplify " + report());
    // hide the product expressions...
    List<Expression> expressions = this.expressions;
    expressions = simplify (expressions);
    expressions = hoist (expressions, Type.PRODUCT);
    expressions = gatherConstants (expressions, (expr, b) -> b * ((Constant) expr).value);
    expressions = checkConstants (expressions);

    // check if we only have one factor left
    if (expressions.size () == 1) {
      return expressions.get (0);
    }

    // a factor that is a quotient can have the dividend replaced with a product node including all
    // the expressions of this node
    List<Expression> nonQuotients = new ArrayList<> ();
    Quotient quotient = null;
    for (Expression expression : expressions) {
      if ((quotient == null) && (expression.type == Type.QUOTIENT)) {
        quotient = (Quotient) expression;
      } else {
        nonQuotients.add (expression);
      }
    }
    if (quotient != null) {
      return Quotient.make (Product.make (Product.make (nonQuotients), quotient.dividend), quotient.divisor).simplify ();
    }

    // distribute sums that are multiplied through (x + 4)(X + 2) or 3 (x + 5)
    List<Expression> other = new ArrayList<> ();
    Sum sum = null;
    for (Expression expression : expressions) {
      if ((sum == null) && (expression.type == Type.SUM)) {
        sum = (Sum) expression;
      } else {
        other.add (expression);
      }
    }
    if (sum != null) {
      // replace this with a sum of products
      log.debug ("Distribute " + sum.report());
      Expression otherExpr = (other.size () > 1) ? Product.make (other) : other.get (0);
      List<Expression> addends = ListFunc.map (sum.expressions, addend -> Product.make (addend, otherExpr));
      return Sum.make (addends).simplify ();
    }

    // all products are ultimately reducible to a form: C * (Vn ^ P)*
    // C = a single constant
    // Vn ^ P = is a variable raised to a power, and there might be more than one of them
    Set<String> dependencies = getDependencies ();
    for (String dependency : dependencies) {
      List<Expression>[] splits = ListFunc.split (expressions, expr -> expr.getDependencies().contains (dependency));

      double order = 0;
      if (splits[1].size() > 1) {
        for (Expression expr : splits[1]) {
          switch (expr.type) {
            case VARIABLE: {
              order += 1.0;
              break;
            }
            case POWER: {
              Power power = (Power) expr;
              if (power.exponent.type == Type.CONSTANT) {
                order += ((Constant) (power.exponent)).value;
              } else {
                log.warn ("Non-constant exponent in PRODUCT gather-terms (POWER): " + expr);
              }
              break;
            }
            default:
              log.warn ("Unaccounted pattern in PRODUCT gather step: " + expr);
              break;
          }
        }

        // don't rewrite expressions unless there will be a change
        expressions = splits[0];
        if (order != 1) {
          expressions.add (Power.make (Variable.make (dependency), Constant.make (order)));
        } else {
          expressions.add (Variable.make (dependency));
        }
      }
    }
    // check if we only have one factor left
    if (expressions.size () == 1) {
      return expressions.get (0);
    }



    // factor out terms?


    // - gather all variables and power nodes into ... power nodes (aka terms)

    // if the expressions haven't changed, return this, otherwise... if the expressions list has only one
    // item we return that, and finally a new product object based on the list
    return (expressions == this.expressions) ? this : make (expressions);
  }

  /**
   * @return true if the node describes a canonical form "term", meaning a single constant, and one
   * variable or power factor (where the power factor is also a term) for each index
   */
  @Override
  public boolean isTerm () {
    List<Expression>[] splits = ListFunc.split(expressions, expr -> expr.type == Type.CONSTANT);
    return (splits[1].size () <= 1);
    /*
            (splits[0] != null) && (splits[0].size() == 1) &&
            (splits[0].get(0).type != Type.PRODUCT) &&
            (splits[0].get(0).type != Type.SUM);

            */
  }

  @Override
  protected Expression computeTrim (List<Expression> expressions) {
    return register (new Product (expressions), true);
  }
}
