package us.irdev.gtk.symbolic_math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Expression {
  private static final Logger log = LogManager.getLogger(Expression.class);

  public final int id;
  public final Type type;

  private static int next_id = 0;
  private static final Set<String> EMPTY_SET = Collections.emptySet ();

  public Expression (Type type) {
    this.type = type;
    id = next_id++;
  }

  // there are two types of string version of expressions - the definitive one, and the pretty one
  private String string;

  @Override
  public String toString() {
    return (string != null) ? string : (string = computeString ());
  }

  protected abstract String computeString ();

  // pretty string can decide to handle spaces and parentheses differently than the definitive
  // string representation of the expression
  public String prettyString (boolean wrap) {
    return toString();
  }

  public String prettyString () {
    return prettyString (false);
  }

  // expression registry
  protected static final Map<String, Expression> registry = new HashMap<>();

  protected static Expression register (Expression expression, boolean force) {
    // in the event of a collision with an already registered expression, keep the expression with
    // the lowest id (which is probably the one that is already registered).
    String signature = expression.toString ();
    Expression registeredExpression = registry.get(signature);
    if (registeredExpression == null) {
      log.debug("Register (NEW) " + expression.report());
      registry.put (signature, expression);
      return expression;
    }
    if ((expression.id < registeredExpression.id) || force) {
      log.debug("Register (REPLACE, force = " + (force ? "true" : "false") + ") " + registeredExpression.report() + " with " + expression.report());
      registry.put (signature, expression);
      return expression;
    }
    log.debug("Register (RE-USE) " + registeredExpression.report());
    return registeredExpression;
  }

  protected static Expression register (Expression expression) {
    return register (expression, false);
  }

  public String report() {
    return type + "_" + id + " = " + string;
  }

  public Set<String> getDependencies () {
    return EMPTY_SET;
  }

  public abstract double n (Map<String, Double> at);
  public abstract Expression d ();

  public Expression simplify () {
    // what circumstance might lead to a null result from simplify? nothing...
    // any zero constant in a product node replaces the whole node with 0
    // any zero constant in a sum or difference is removed (by the sum or difference - not the constant itself)
    return this;
  }

  /**
   * return only the registered version of the expression, but sub-classes should trim their child
   * expressions, too
   */
  public Expression trim () {
    return register (this);
  }

  /**
   * @return true if the node describes a canonical form "term", meaning a single constant, and one
   * variable or power factor (where the power factor is also a term)
   */
  public boolean isTerm () {
    return true;
  }
}
