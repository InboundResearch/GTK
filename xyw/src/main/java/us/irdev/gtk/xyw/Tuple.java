package us.irdev.gtk.xyw;

/**
 * Homogenous 2D Tuple
 */
public class Tuple {
  public final double x, y, w;

  public static final Tuple ORIGIN = PT (0, 0);

  public Tuple(double x, double y, double w) {
    this.x = x; this.y = y; this.w = w;
  }

  public Tuple add(Tuple right) {
    return new Tuple (x + right.x, y + right.y, w + right.w);
  }

  public Tuple subtract(Tuple right) {
    return new Tuple (x - right.x, y - right.y, w - right.w);
  }

  public Tuple scale(double right) {
    return new Tuple (x * right, y * right, w * right);
  }

  // hadamard product, inverse, and division, aka pointwise - https://en.wikipedia.org/wiki/Hadamard_product_(matrices)
  public Tuple hproduct (Tuple right) {
    return new Tuple (x * right.x, y * right.y, w * right.w);
  }

  private double notTooSmall(double numerator, double x) {
    return (! Numerics.similar (x, 0)) ? (numerator / x) : 0;
  }

  public Tuple hinverse () {
    return new Tuple (notTooSmall(1.0, x), notTooSmall (1.0, y), notTooSmall (1.0, w));
  }

  public Tuple hquotient (Tuple right) {
    return new Tuple (notTooSmall(x, right.x), notTooSmall(y, right.y), notTooSmall(w, right.w));
  }

  public Tuple abs() {
    return new Tuple (Math.abs(x), Math.abs(y), Math.abs(w));
  }

  public Tuple floor() {
    return new Tuple (Math.floor(x), Math.floor(y), Math.floor(w));
  }

  public Tuple round() {
    return new Tuple (Math.round(x), Math.round(y), Math.round(w));
  }

  /**
   * @return the vector dot product, which is the cosine of the angle betwen the input vectors times
   * their lengths:
   *     |a| * |b| * cos(theta).
   * see https://en.wikipedia.org/wiki/Dot_product.
   * @param right the second vector input
   */
  public double dot(Tuple right) {
    return (x * right.x) + (y * right.y) + (w * right.w);
  }

  /**
   * @return the vector cross product, which is the vector perpendicular to the basis formed by the
   * two input vectors.
   * see https://en.wikipedia.org/wiki/Cross_product
   * @param right the second basis vector input
   */
  public Tuple cross (Tuple right) {
    return new Tuple ((y * right.w) - (w * right.y), (w * right.x) - (x * right.w), (x * right.y) - (y * right.x));
  }

  /**
   * @return the z-component of the vertical vector that would result from a 3D cross product, this
   * is the sine of the angle between the two input vectors times their lengths:
   *     |a| * |b| * sin(theta)
   * see https://en.wikipedia.org/wiki/Cross_product
   * @param right the second basis vector input
   */
  public double cross2 (Tuple right) {
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
    return (a != null) && (b != null) && Numerics.similar (a.x, b.x) && Numerics.similar (a.y, b.y) && Numerics.similar (a.w, b.w);
  }

  @Override
  public boolean equals (Object obj) {
    return (obj instanceof Tuple) && similar(this, (Tuple) obj);
  }

  @Override
  public String toString () {
    return String.format("(%01.06f, %01.06f, %01.06f)", x, y, w);
  }

  @Override
  public int hashCode () {
    return toString().hashCode();
  }

  public static Tuple PT(double x, double y) {
    return new Tuple (x, y, 1);
  }

  public static Tuple PT(Tuple xy) {
    // you probably don't mean to truncate an unprojected tuple
    assert(Numerics.similar (0, xy.w) || Numerics.similar (1, xy.w));
    return new Tuple (xy.x, xy.y, 1);
  }

  public Tuple projectToPoint () {
    return Numerics.similar(w, 0) ? null : PT (x / w, y / w);
  }

  public boolean isPt () {
    return Numerics.similar(w, 1);
  }

  public static Tuple VEC (double x, double y) {
    return new Tuple (x, y, 0);
  }

  public static Tuple VEC (Tuple xy) {
    // you probably don't mean to truncate an unprojected tuple
    assert(Numerics.similar (0, xy.w) || Numerics.similar (1, xy.w));
    return new Tuple (xy.x, xy.y, 0);
  }

  public boolean isVec() {
    return Numerics.similar(w, 0);
  }

  public Tuple perpendicular() {
    // make sure this is a vector (not a point or unprojected point)
    assert (isVec());
    return VEC (y, -x);
  }
}
