package us.irdev.gtk.symbolic_math;

public class Utility {
  public static String wrap (String s, boolean wrap) {
    return wrap ? "(" + s + ")" : s;
  }

  public static String wrap (StringBuilder sb, boolean wrap) {
    return wrap ? "(" + sb.append (")") : sb.toString ();
  }

  public static int gcd (int a, int b) {
    return (a == 0) ? b : gcd(b % a, a);
  }

  public static int lcd (int a, int b) {
    return (a * b) / gcd(a, b);
  }
}
