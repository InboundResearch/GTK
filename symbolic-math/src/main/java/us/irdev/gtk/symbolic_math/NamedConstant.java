package us.irdev.gtk.symbolic_math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NamedConstant extends Constant {
  private static final Logger log = LogManager.getLogger(NamedConstant.class);
  public final String name;
  static {
    make ("π", Math.PI);
    make ("ℇ", Math.E);
  }

  private NamedConstant (String name, double value) {
    super (value, Type.NAMED_CONSTANT);
    this.name = name;
  }

  public static Expression make (String name, double value) {
    return register(new NamedConstant (name, value));
  }

  @Override
  protected String computeString () {
    return name;
  }

  public static NamedConstant get(String name) {
    Expression expr = registry.get(name);
    if (expr == null) {
      log.error("Unknown " + NamedConstant.class.getSimpleName() + ": " + name);
      return null;
    }

    // only allow named constants to be returned from here
    return (expr.type == Type.NAMED_CONSTANT) ? (NamedConstant) expr : null;
  }
}
