package us.irdev.gtk.symbolic_math;

public enum Type {
  CONSTANT,
  RATIONAL,
  NAMED_CONSTANT,
  PRODUCT,
  QUOTIENT,
  SUM,
  DIFFERENCE,
  VARIABLE,
  POWER,
  FUNCTION;

  boolean isConstant() {
    return (this == CONSTANT) || (this == NAMED_CONSTANT);
  }
}
