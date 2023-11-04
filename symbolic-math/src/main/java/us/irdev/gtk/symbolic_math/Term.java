package us.irdev.gtk.symbolic_math;

import java.util.Map;
import java.util.Set;

public class Term extends Expression {
  // a constant expressed as a fraction
  public final double numerator;
  public final double denominator;
  public final String[] variable;
  public final double[] exponent;

  private final Set<String> dependencies;

  public Term () {
    super (Type.PRODUCT);
    numerator = denominator = 1.0;
    variable = null;
    exponent = null;
    dependencies = null;
  }

  @Override
  protected String computeString () {
    return null;
  }

  @Override
  public double n (Map<String, Double> at) {
    return 0;
  }

  @Override
  public Expression d () {
    return null;
  }
}
