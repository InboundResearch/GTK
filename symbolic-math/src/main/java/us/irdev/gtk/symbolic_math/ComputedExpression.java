package us.irdev.gtk.symbolic_math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Set;

public abstract class ComputedExpression extends Expression {
  private static final Logger log = LogManager.getLogger(ComputedExpression.class);

  private Set<String> dependencies;

  // we cache the last numeric evaluation since there are conditions where an expression might be
  // repeated in some context (derivative of a product, for instance), and evaluated many times with
  // a high computational cost
  // TODO - add a static cache of signature and at with value...
  public String lastSignature;
  public double lastValue = 0;

  public ComputedExpression (Type type) {
    super (type);
    dependencies = null;
    lastSignature = null;
  }

  @Override
  public String prettyString (boolean wrap) {
    return computePrettyString(wrap);
  }

  protected abstract String computePrettyString (boolean wrap);

  private String signature (Map<String, Double> at) {
    StringBuilder sb = new StringBuilder ();
    Set<String> dependencies = getDependencies();
    // we rely on deterministic iteration
    for (String dependency : dependencies) {
      Double value = at.get(dependency);
      sb.append("(").append (dependency).append(":").append(value.toString ()).append(")");
    }
    return sb.toString ();
  }

  @Override
  public double n (Map<String, Double> at) {
    String signature = signature (at);
    if ((lastSignature == null) || (! lastSignature.equals (signature))) {
      lastSignature = signature;
      lastValue = computeN (at);
      log.debug ("N Refresh " + report () + ", @ " + signature + " = " + lastValue);
    } else {
      log.debug ("N Re-use " + report () + ", @ " + signature + " = " + lastValue);
    }
    return lastValue;
  }

  /**
   * The actual implementation of the 'n' method is hidden behind this 'compute' method to enable caching
   * the last result
   */
  protected abstract double computeN (Map<String, Double> at);

  @Override
  public Set<String> getDependencies () {
    return (dependencies != null) ? dependencies : (dependencies = computeDependencies());
  }

  /**
   * The actual implementation of the 'getDependencies' method is hidden behind this 'varyingGetDependencies' method to enable
   * the caching of the last result
   */
  public abstract Set<String> computeDependencies ();

}
