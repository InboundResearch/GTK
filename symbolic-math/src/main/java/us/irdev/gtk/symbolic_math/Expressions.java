package us.irdev.gtk.symbolic_math;

import us.irdev.gtk.symbolic_math.trig.Cos;
import us.irdev.gtk.symbolic_math.trig.Sin;

public class Expressions {
  public static Expression constant (double constant) {
    return Constant.make (constant);
  }

  public static Expression neg (Expression expr) {
    return mul(term (-1), expr);
  }

  public static Expression neg (String variable) {
    return neg(term(variable));
  }

  public static Expression cos (Expression angle) {
    return Cos.make (angle);
  }

  public static Expression cos (String variable) {
    return cos (term (variable));
  }

  public static Expression sin (Expression angle) {
    return Sin.make (angle);
  }

  public static Expression sin (String variable) {
    return sin (term (variable));
  }

  public static Expression tan (Expression angle) {
    return div(sin(angle), cos(angle));
  }

  public static Expression tan (String variable) {
    return tan (term(variable));
  }

  public static Expression mul (Expression... factors) {
    return Product.make(factors);
  }

  public static Expression mul (double constant, Expression... factors) {
    return mul (term(constant), mul (factors));
  }

  public static Expression mul (double constant, Expression expr) {
    return mul (term(constant), expr);
  }

  public static Expression div (Expression dividend, Expression divisor) {
    return Quotient.make(dividend, divisor);
  }

  public static Expression add (Expression... addends) {
    return Sum.make(addends);
  }

  public static Expression add (double constant, Expression... addends) {
    return add (add (addends), term (constant));
  }

  public static Expression add (Expression expr, double constant) {
    return add (expr, term (constant));
  }

  public static Expression sub (Expression... subtrahends) {
    return Difference.make(subtrahends);
  }

  public static Expression pow (Expression base, Expression exponent) {
    return Power.make(base, exponent);
  }

  public static Expression pow (String variable, Expression exponent) {
    return pow (term (variable), exponent);
  }

  public static Expression pow (Expression base, int exponent) {
    return pow (base, term (exponent));
  }

  public static Expression pow (String variable, int exponent) {
    return pow (term (variable), term (exponent));
  }

  public static Expression root (Expression radicand, int index) {
    return pow (radicand, term (1.0 / index));
  }

  public static Expression sqrt (Expression radicand) {
    return pow (radicand, Constant.ONE_HALF);
  }

  public static Expression sqrt (String variable) {
    return sqrt (term(variable));
  }

  public static Expression sq (Expression radicand) {
    return pow (radicand, Constant.TWO);
  }

  public static Expression sq (String variable) {
    return sq (term(variable));
  }

  public static Expression one_over (Expression reciprocal) {
    return div (Constant.ONE, reciprocal);
  }

  public static Expression one_over (String variable) {
    return div (Constant.ONE, term(variable));
  }

  public static Expression one_minus (Expression expr) {
    return sub (Constant.ONE, expr);
  }

  public static Expression one_minus (String variable) {
    return sub (Constant.ONE, term(variable));
  }

  public static Expression term (double constant) {
    return constant (constant);
  }

  public static Expression term (String variable) {
    return Variable.make(variable);
  }

  public static Expression term (String variable, int order) {
    return pow (term (variable), term (order));
  }

  public static Expression term (double constant, String variable) {
    return mul (term (constant), term (variable));
  }

  public static Expression term (double constant, String variable, int order) {
    return mul (term (constant), term (variable, order));
  }

  public static Expression pi () {
    return NamedConstant.get ("π");
  }

  public static Expression e () {
    return NamedConstant.get ("ℇ");
  }
}
