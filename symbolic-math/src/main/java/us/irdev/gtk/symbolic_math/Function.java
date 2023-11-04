package us.irdev.gtk.symbolic_math;

import java.util.Set;

public abstract class Function extends ComputedExpression {
  public final Expression expr;

  public Function(Expression expr) {
    super (Type.FUNCTION);
    this.expr = expr;
  }

  @Override
  public Set<String> computeDependencies () {
    return expr.getDependencies ();
  }

}
