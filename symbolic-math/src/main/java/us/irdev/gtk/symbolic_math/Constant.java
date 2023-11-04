package us.irdev.gtk.symbolic_math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class Constant extends Expression {
  private static final Logger log = LogManager.getLogger(Constant.class);

  public final double value;
  public boolean isInteger;
  public static final Constant ZERO = (Constant) make (0.0);
  public static final Constant ONE_HALF = (Constant) make (0.5);
  public static final Constant ONE = (Constant) make (1.0);
  public static final Constant TWO = (Constant) make (2.0);
  public static final Constant NEG_ONE = (Constant) make (-1.0);

  protected Constant (double value, Type type) {
    super (type);
    this.value = value;
    isInteger = (value == (int) value);
  }

  private Constant (double value) {
    this (value, Type.CONSTANT);
  }

  public static Expression make (double value) {
    return register (new Constant (value));
  }

  @Override
  public double n (Map<String, Double> at) {
    return value;
  }

  @Override
  public Expression d () {
    return Constant.ZERO;
  }

  @Override
  protected String computeString () {
    if (isInteger) {
      return Integer.toString ((int) value);
    }
    return Double.toString (value);
  }
}
