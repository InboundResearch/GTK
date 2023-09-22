package us.irdev.gtk.xy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static us.irdev.gtk.xy.Helper.assertNotSimilar;
import static us.irdev.gtk.xy.Helper.assertSimilar;

public class TupleTest {

  @Test
  public void testConstructor() {
    Tuple tuple = new Tuple ();
    assertEquals(0, tuple.x);
    assertEquals(0, tuple.y);

    tuple = new Tuple (-1);
    assertEquals(-1, tuple.x);
    assertEquals(-1, tuple.y);

    tuple = new Tuple (2);
    assertEquals(2, tuple.x);
    assertEquals(2, tuple.y);

    tuple = new Tuple (3.5,4.5);
    assertEquals(3.5, tuple.x);
    assertEquals(4.5, tuple.y);
  }

  @Test
  public void testEquals() {
    Tuple a = new Tuple (2.0, 3.5);
    Tuple b = new Tuple (4.0, 7.0);
    Tuple c = new Tuple (2.0, 3.5);
    assertNotSimilar(a, b);
    assertNotSimilar(b, c);
    assertSimilar(a, c);

    Tuple d = new Tuple (2.15, 3.6);
    assertNotSimilar (a, d);

    // XXX this needs a bit more thought to create a test that actually exercises similarity...
    Tuple e = a.add(b).scale(2.0).scale(3.0).scale(1.0 / 2.0).scale(1.0 / 3.0).subtract(b);
    assertSimilar(e, a);
  }

  @Test
  public void testMath() {
    Tuple a = new Tuple (2.0, 3.5);
    Tuple b = new Tuple (4.0, 7.0);
    assertSimilar(a.add (a), b);
    assertSimilar(b.subtract (a), a);
    assertSimilar(a.subtract (b).abs(), a);
  }

  @Test
  public void testToString() {
    assertEquals(new Tuple(1.2, 1).toString(), "(1.200000, 1.000000)");
    assertEquals(new Tuple(-1.2, 1).toString(), "(-1.200000, 1.000000)");
  }

  // XXX test hquotient and hinverse for division by 0
}
