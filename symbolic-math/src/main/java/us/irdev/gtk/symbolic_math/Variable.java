package us.irdev.gtk.symbolic_math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class Variable extends ComputedExpression {
  private static final Logger log = LogManager.getLogger(Variable.class);

  public final String name;

  private Variable(String name) {
    super (Type.VARIABLE);
    this.name = name;
  }

  public static Expression make (String name) {
    return register (new Variable (name));
  }

  @Override
  protected double computeN (Map<String, Double> at) {
    return at.get(name);
  }

  @Override
  public Expression d () {
    return Constant.ONE;
  }

  @Override
  public Set<String> computeDependencies () {
    return Collections.singleton (name);
  }

  @Override
  public String computeString () {
    return name;
  }
  @Override
  protected String computePrettyString (boolean wrap) {
    return name;
  }

}
