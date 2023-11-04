package us.irdev.gtk.symbolic_math;

import us.irdev.gtk.functional.Interfaces;
import us.irdev.gtk.functional.ListFunc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public abstract class ComputedExpressionList extends ComputedExpression {
  private static final Logger log = LogManager.getLogger(ComputedExpressionList.class);

  public final List<Expression> expressions;

  protected static List<Expression> sorted (List<Expression> input, Comparator<Expression> comp) {
    List<Expression> output = new ArrayList<>(input);
    output.sort (comp);
    return output;
  }

  public ComputedExpressionList (Type type, List<Expression> expressions) {
    super (type);
    this.expressions = expressions;
  }

  // simplify all the expressions in the list
  protected static List<Expression> simplify (List<Expression> expressions) {
    boolean changed = false;
    List<Expression> simplified = new ArrayList<> (expressions.size ());
    for (Expression expr : expressions) {
      Expression simplifiedExpr = expr.simplify ();
      if (simplifiedExpr != expr) {
        changed = true;
        log.debug ("Replace " + expr.report() + " with " + simplifiedExpr.report());
      }
      simplified.add (simplifiedExpr);
    }
    return changed ? simplified : expressions;
  }

  // if any expressions are of a given type, hoist them into this list
  protected static List<Expression> hoist (List<Expression> expressions, Type hoistType) {
    boolean changed = false;
    List<Expression> hoisted = new ArrayList<> ();
    for (Expression expr : expressions) {
      if (expr.type == hoistType) {
        changed = true;
        log.debug ("Hoist " + expr.report());
        hoisted.addAll (((ComputedExpressionList) expr).expressions);
      } else {
        hoisted.add (expr);
      }
    }
    return changed ? hoisted : expressions;
  }

  protected static List<Expression> gatherConstants (List<Expression> expressions, Interfaces.ReduceNoIndexDouble<Expression> handler) {
    List<Expression>[] split = ListFunc.split (expressions, expr -> expr.type == Type.CONSTANT);
    List<Expression> constants = split[1];
    if (constants.size() > 1) {
      double constant = ListFunc.reduceDouble (constants, 1, ((Constant) constants.get(0)).value, handler);
      log.debug ("Replace constants [" + ListFunc.reduce (constants, 1, new StringBuilder().append(constants.get(0)), (c, sb) -> sb.append (", ").append (c)).toString() + "] with " + constant);
      split[0].add (Expressions.constant (constant));
      return split[0];
    }
    return expressions;
  }

  public Set<String> computeDependencies () {
    Set<String> dependencies = new HashSet<>();
    for (Expression expression: expressions) {
      dependencies.addAll (expression.getDependencies());
    }
    return dependencies;
  }

  public Expression trim () {
    boolean changed = false;
    List<Expression> trimmed = new ArrayList<> (expressions.size ());
    for (Expression expr : expressions) {
      Expression trimmedExpr = expr.trim ();
      if (trimmedExpr != expr) {
        changed = true;
        log.debug ("Replace " + expr.report() + " with " + trimmedExpr.report());
      }
      trimmed.add (trimmedExpr);
    }
    return changed ? computeTrim (trimmed) : this;
  }

  protected abstract Expression computeTrim(List<Expression> expressions);

}
