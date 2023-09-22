package us.irdev.gtk.xy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static us.irdev.gtk.xy.Helper.assertSimilar;

public class DomainTest {

  @Test
  public void testConstructor() {
    Domain domain = new Domain ();
    assertEquals(domain.min, new Tuple (Double.MAX_VALUE));
    assertEquals(domain.max, new Tuple (-Double.MAX_VALUE));
  }

  @Test
  public void testSimilar () {
    Domain domain = new Domain ().add(new Tuple (1));
    assertSimilar(new Tuple (1), domain.min);
    assertSimilar(new Tuple (1), domain.max);

    domain.add (new Tuple (0));
    assertSimilar(new Tuple (0), domain.min);
    assertSimilar(new Tuple (1), domain.max);

    Domain domain2 = new Domain ().add(new Tuple (1.1));
    domain2.add (new Tuple (-0.1));
    assertSimilar(new Domain (-0.1, 1.1, -0.1, 1.1), domain2);
  }

  @Test
  public void testAdd() {
    Domain domain = new Domain ().add(new Tuple (1));
    assertEquals(domain.min, new Tuple (1));
    assertEquals(domain.max, new Tuple (1));

    domain.add (new Tuple (0));
    assertEquals(domain.min, new Tuple (0));
    assertEquals(domain.max, new Tuple (1));

    domain.add (new Tuple (-1));
    assertEquals(domain.min, new Tuple (-1));
    assertEquals(domain.max, new Tuple (1));
  }

  @Test
  public void testBounds() {
    Domain domain = new Domain ()
            .add (new Tuple (2.5, 3.5))
            .add (new Tuple (0.5, 0.5));

    // points that should be considered inside the bound
    assertTrue(domain.contains (new Tuple(1.0, 1.0)));
    assertTrue(domain.contains (new Tuple(2.5, 1.0)));
    assertTrue(domain.contains (new Tuple(1.0, 3.5)));
    assertTrue(domain.contains (new Tuple(0.5, 0.5)));
    assertTrue(domain.contains (new Tuple(2.5, 3.5)));

    // points that are outside the bound
    assertFalse(domain.contains (Tuple.ORIGIN));
    assertFalse(domain.contains (new Tuple(1.0, 0.0)));
    assertFalse(domain.contains (new Tuple(0.0, 1.0)));
    assertFalse(domain.contains (new Tuple(5.0, 0.0)));
    assertFalse(domain.contains (new Tuple(5.0, 1.0)));
    assertFalse(domain.contains (new Tuple(0.0, 5.0)));
    assertFalse(domain.contains (new Tuple(1.0, 5.0)));
    assertFalse(domain.contains (new Tuple(5.0, 5.0)));

    assertSimilar (new Tuple (2.0, 3.0), domain.size());
    assertSimilar (new Tuple (2.0, 3.0).norm (), domain.span());
  }

  @Test
  public void testScale() {
    Domain domain = new Domain ()
            .add (new Tuple ())
            .add (new Tuple (1.0));
    assertSimilar (new Domain (-1, 2, -1, 2), domain.scale (3));
    assertSimilar (new Domain (0.25, 0.75, 0.25, 0.75), domain.scale (0.5));
  }

  @Test
  public void testSetOps() {
    Domain da = new Domain (0, 1, 0, 1),
           db = new Domain (0.5, 1.5, 0.5, 1.5);
    assertSimilar (new Domain (0.5, 1, 0.5, 1), Domain.intersection(da, db));
    assertSimilar (new Domain (0, 1.5, 0, 1.5), Domain.union(da, db));
  }

}
