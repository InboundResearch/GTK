package us.irdev.gtk.symbolic_math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NamedConstant extends Constant {
  private static final Logger log = LogManager.getLogger(NamedConstant.class);
  public final String name;
  public final String plain;

  static {
    // useful links:
    //   https://en.wikipedia.org/wiki/Mathematical_Alphanumeric_Symbols
    //   https://stackoverflow.com/questions/17908593/how-to-find-the-unicode-of-the-subscript-alphabet
    make ("π", "pi", Math.PI);
    make ("\uD835\uDC52", "euler", Math.E);
    make ("\uD835\uDE29", "planck", 6.62607015e-34);
    make ("\uD835\uDC50", "light", 299792458.0);
    make ("\uD835\uDC58", "boltzmann", 1.380649e-23);
  }

  private NamedConstant (String name, String plain, double value) {
    super (value, Type.NAMED_CONSTANT);
    this.name = name;
    this.plain = plain;
  }

  public static Expression make (String name, String plain, double value) {
    return register(new NamedConstant (name, plain, value));
  }

  @Override
  protected String computeString () {
    return name;
  }

  public static NamedConstant get(String name) {
    // try to fetch the expression and return it if successful
    Expression expr = registry.get(name);
    if ((expr != null) && (expr.type == Type.NAMED_CONSTANT)) {
      return (NamedConstant) expr;
    }

    // try searching the plain text of naed constants
    if (expr == null) {
      for (String key : registry.keySet ()) {
        expr = registry.get (key);
        if ((expr.type == Type.NAMED_CONSTANT) && ((NamedConstant) expr).plain.equals (name)) {
          return (NamedConstant) expr;
        }
      }
    }

    log.error("Unknown " + NamedConstant.class.getSimpleName() + ": " + name);
    return null;
  }
}
