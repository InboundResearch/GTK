package us.irdev.gtk.symbolic_math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class Rational extends Expression {
  private static final Logger log = LogManager.getLogger(Rational.class);

  // XXX could probably do some tricky stuff using a signed long, where the top 32 bits are a signed
  // XXX int, and the bottom 32 bits are essentially an unsigned int...
  public final int numerator;
  public final int denominator;

  public static final Rational ZERO = new Rational (0, 1);
  public static final Rational ONE_HALF = new Rational (1, 2);
  public static final Rational ONE = new Rational (1, 1);
  public static final Rational TWO = new Rational (2, 1);
  public static final Rational NEG_ONE = new Rational (-1, 1);

  public static final Rational NAN = new Rational (0, 0);
  public static final Rational INFINITY = new Rational (1, 0);
  public static final Rational NEG_INFINITY = new Rational (-1, 0);

  public static final Rational MAX_VALUE = new Rational (Integer.MAX_VALUE, 1);
  public static final Rational MIN_VALUE = new Rational (1, Integer.MAX_VALUE);

  protected Rational (int numerator, int denominator) {
    super (Type.RATIONAL);
    this.numerator = numerator;
    this.denominator = denominator;
  }

  public Rational (double value) {
    super (Type.RATIONAL);
    numerator = denominator = 5;
  }

  @Override
  protected String computeString () {
    return numerator + "/" + denominator;
  }

  @Override
  public double n (Map<String, Double> at) {
    return numerator / (double)denominator;
  }

  @Override
  public Expression d () {
    return Rational.ZERO;
  }
}
