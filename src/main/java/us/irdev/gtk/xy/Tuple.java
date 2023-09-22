package us.irdev.gtk.xy;

public class Tuple {
  public double x, y;

  public static final Tuple ORIGIN = new Tuple (0, 0);

  public Tuple() {
    this.x = this.y = 0;
  }

  public Tuple(double defaultValue) {
    this.x = this.y = defaultValue;
  }

  public Tuple(double x, double y) {
    this.x = x; this.y = y;
  }

  public Tuple add(Tuple right) {
    return new Tuple (x + right.x, y + right.y);
  }

  public Tuple subtract(Tuple right) {
    return new Tuple (x - right.x, y - right.y);
  }

  public Tuple scale(double right) {
    return new Tuple (x * right, y * right);
  }

  // hadamard product, inverse, and division, aka pointwise - https://en.wikipedia.org/wiki/Hadamard_product_(matrices)
  public Tuple hproduct (Tuple right) {
    return new Tuple (x * right.x, y * right.y);
  }

  private double notTooSmall(double numerator, double x) {
    return (! Numerics.similar (x, 0)) ? (numerator / x) : 0;
  }

  public Tuple hinverse () {
    return new Tuple (notTooSmall(1.0, x), notTooSmall (1.0, y));
  }

  public Tuple hquotient (Tuple right) {
    return new Tuple (notTooSmall(x, right.x), notTooSmall(y, right.y));
  }

  public Tuple perpendicular() {
    return new Tuple (y, -x);
  }

  public Tuple abs() {
    return new Tuple (Math.abs(x), Math.abs(y));
  }

  public Tuple floor() {
    return new Tuple (Math.floor(x), Math.floor(y));
  }

  public Tuple round() {
    return new Tuple (Math.round(x), Math.round(y));
  }

  // return the vector dot product, which is the cosine of the angle betwen the input vectors times
  // their lengths: |a| * |b| * cos(theta)
  // (https://en.wikipedia.org/wiki/Dot_product)
  public double dot(Tuple right) {
    return (x * right.x) + (y * right.y);
  }

  // return the z-component of the vertical vector that would result from a 3D cross product, this is
  // the sine of the angle between the two input vectors times their lengths: |a| * |b| * sin(theta)
  // https://en.wikipedia.org/wiki/Cross_product
  public double cross(Tuple right) {
    // a1b2 - a2b1
    return (x * right.y) - (y * right.x);
  }

  public double normSq() {
    return this.dot(this);
  }

  public double norm () {
    return Math.sqrt(normSq());
  }

  public Tuple normalized() {
    double norm = norm ();
    return (norm > 0.0) ? scale(1.0 / norm) : this;
  }

  public static boolean similar(Tuple a, Tuple b) {
    return (a != null) && (b != null) && Numerics.similar (a.x, b.x) && Numerics.similar (a.y, b.y);
  }

  @Override
  public boolean equals (Object obj) {
    return (obj instanceof Tuple) && similar(this, (Tuple) obj);
  }

  @Override
  public String toString () {
    return String.format("(%01.06f, %01.06f)", x, y);
  }

  @Override
  public int hashCode () {
    return toString().hashCode();
  }
}
