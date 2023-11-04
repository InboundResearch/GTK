package us.irdev.gtk.xyw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static us.irdev.gtk.xyw.Helper.assertNotSimilar;
import static us.irdev.gtk.xyw.Helper.assertSimilar;
import static us.irdev.gtk.xyw.Tuple.PT;
import static us.irdev.gtk.xyw.Tuple.VEC;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TupleTest {

  @Test
  public void testConstructor() {
    Tuple tuple = new Tuple (0, 0, 0);
    assertEquals(0, tuple.x);
    assertEquals(0, tuple.y);
    assertEquals(0, tuple.w);

    tuple = new Tuple (-1, -1, -1);
    assertEquals(-1, tuple.x);
    assertEquals(-1, tuple.y);
    assertEquals(-1, tuple.w);

    tuple = new Tuple (2, 2, 2);
    assertEquals(2, tuple.x);
    assertEquals(2, tuple.y);
    assertEquals(2, tuple.w);

    tuple = new Tuple (3.5,4.5, 5.5);
    assertEquals(3.5, tuple.x);
    assertEquals(4.5, tuple.y);
    assertEquals(5.5, tuple.w);
  }

  @Test
  public void testEquals() {
    Tuple a = Tuple.VEC (2.0, 3.5);
    Tuple b = Tuple.VEC (4.0, 7.0);
    Tuple c = Tuple.VEC (2.0, 3.5);
    assertNotSimilar(a, b);
    assertNotSimilar(b, c);
    assertSimilar(a, c);

    Tuple d = Tuple.PT (2.15, 3.6);
    assertNotSimilar (a, d);

    // XXX this needs a bit more thought to create a test that actually exercises similarity...
    Tuple e = a.add(b).scale(2.0).scale(3.0).scale(1.0 / 2.0).scale(1.0 / 3.0).subtract(b);
    assertSimilar(e, a);
  }

  @Test
  public void testMath() {
    Tuple a = Tuple.VEC (2.0, 3.5);
    Tuple b = Tuple.VEC (4.0, 7.0);
    assertSimilar(a.add (a), b);
    assertSimilar(b.subtract (a), a);
    assertSimilar(a.subtract (b).abs(), a);
  }

  @Test
  public void testToString() {
    Assertions.assertEquals(Tuple.VEC (1.2, 1).toString(), "(1.200000, 1.000000, 0.000000)");
    Assertions.assertEquals(Tuple.PT (-1.2, 1).toString(), "(-1.200000, 1.000000, 1.000000)");
  }

  // XXX test hquotient and hinverse for division by 0
}
