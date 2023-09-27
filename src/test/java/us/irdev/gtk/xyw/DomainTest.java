package us.irdev.gtk.xyw;

import org.junit.jupiter.api.Test;

import static us.irdev.gtk.xyw.Helper.assertSimilar;
import static us.irdev.gtk.xyw.Tuple.PT;
import static us.irdev.gtk.xyw.Tuple.VEC;
import static org.junit.jupiter.api.Assertions.*;

public class DomainTest {

  @Test
  public void testConstructor() {
    Domain domain = new Domain ();
    assertEquals(domain.min, PT (Double.MAX_VALUE, Double.MAX_VALUE));
    assertEquals(domain.max, PT (-Double.MAX_VALUE, -Double.MAX_VALUE));
  }

  @Test
  public void testSimilar () {
    Domain domain = new Domain ().add(PT (1, 1));
    assertSimilar(PT (1, 1), domain.min);
    assertSimilar(PT (1, 1), domain.max);

    domain.add (PT (0, 0));
    assertSimilar(PT (0, 0), domain.min);
    assertSimilar(PT (1, 1), domain.max);

    Domain domain2 = new Domain ().add(PT (1.1, 1.1));
    domain2.add (PT (-0.1, -0.1));
    assertSimilar(new Domain (-0.1, 1.1, -0.1, 1.1), domain2);
  }

  @Test
  public void testAdd() {
    Domain domain = new Domain ().add(PT (1, 1));
    assertEquals(domain.min, PT (1, 1));
    assertEquals(domain.max, PT (1, 1));

    domain.add (PT (0, 0));
    assertEquals(domain.min, PT (0, 0));
    assertEquals(domain.max, PT (1, 1));

    domain.add (PT (-1, -1));
    assertEquals(domain.min, PT (-1, -1));
    assertEquals(domain.max, PT (1, 1));
  }

  @Test
  public void testBounds() {
    Domain domain = new Domain ()
            .add (PT (2.5, 3.5))
            .add (PT (0.5, 0.5));

    // points that should be considered inside the bound
    assertTrue(domain.contains (PT (1.0, 1.0)));
    assertTrue(domain.contains (PT (2.5, 1.0)));
    assertTrue(domain.contains (PT (1.0, 3.5)));
    assertTrue(domain.contains (PT (0.5, 0.5)));
    assertTrue(domain.contains (PT (2.5, 3.5)));

    // points that are outside the bound
    assertFalse(domain.contains (Tuple.ORIGIN));
    assertFalse(domain.contains (PT (1.0, 0.0)));
    assertFalse(domain.contains (PT (0.0, 1.0)));
    assertFalse(domain.contains (PT (5.0, 0.0)));
    assertFalse(domain.contains (PT (5.0, 1.0)));
    assertFalse(domain.contains (PT (0.0, 5.0)));
    assertFalse(domain.contains (PT (1.0, 5.0)));
    assertFalse(domain.contains (PT (5.0, 5.0)));

    assertSimilar (VEC (2.0, 3.0), domain.size());
    assertSimilar (VEC (2.0, 3.0).norm (), domain.span());
  }

  @Test
  public void testScale() {
    Domain domain = new Domain ()
            .add (Tuple.ORIGIN)
            .add (PT (1.0, 1.0));
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

  private void testDomainContainsSegment(Domain domain, Tuple a, Tuple b, boolean expect) {
    assertEquals(expect, domain.contains (new Segment (a, b)));
    assertEquals(expect, domain.contains (new Segment (b, a)));
  }

  @Test
  public void testContainsSegment () {
    Domain domain = new Domain (1, 3, 1, 3);

    // trivial accept and reject
    testDomainContainsSegment (domain, PT (0, 0), PT (0.5, 0.5), false);
    testDomainContainsSegment (domain, PT (1.5, 0), PT (1.5, 0.5), false);
    testDomainContainsSegment (domain, PT (3.5, 0), PT (3.5, 0.5), false);

    testDomainContainsSegment (domain, PT (0, 2), PT (0.5, 2.5), false);
    testDomainContainsSegment (domain, PT (1.5, 2), PT (1.5, 2.5), true);
    testDomainContainsSegment (domain, PT (3.5, 2), PT (3.5, 2.5), false);

    testDomainContainsSegment (domain, PT (0, 4), PT (0.5, 4.5), false);
    testDomainContainsSegment (domain, PT (1.5, 4), PT (1.5, 4.5), false);
    testDomainContainsSegment (domain, PT (3.5, 4), PT (3.5, 4.5), false);

    testDomainContainsSegment (domain, PT (0, 0), PT (1.5, 0.5), false);
    testDomainContainsSegment (domain, PT (2, 0), PT (4.5, 0.5), false);
    testDomainContainsSegment (domain, PT (0, 0), PT (4.5, 0.5), false);

    testDomainContainsSegment (domain, PT (0, 2), PT (4.5, 2.5), true);

    testDomainContainsSegment (domain, PT (0, 4), PT (1.5, 4.5), false);
    testDomainContainsSegment (domain, PT (2, 4), PT (4.5, 4.5), false);
    testDomainContainsSegment (domain, PT (0, 4), PT (4.5, 4.5), false);

    testDomainContainsSegment (domain, PT (0, 0), PT (0, 2.5), false);
    testDomainContainsSegment (domain, PT (0, 2.5), PT (0, 4.5), false);
    testDomainContainsSegment (domain, PT (0, 0), PT (0, 4.5), false);

    testDomainContainsSegment (domain, PT (2, 0), PT (2, 4.5), true);

    testDomainContainsSegment (domain, PT (4, 0), PT (4, 2.5), false);
    testDomainContainsSegment (domain, PT (4, 2.5), PT (4, 4.5), false);
    testDomainContainsSegment (domain, PT (4, 0), PT (4, 4.5), false);

    // trivial cases with one end point in
    testDomainContainsSegment (domain, PT (1.5, 1.5), PT (2.5, 2.5), true);

    testDomainContainsSegment (domain, PT (1.5, 1.5), PT (0.5, 0.5), true);
    testDomainContainsSegment (domain, PT (1.5, 1.5), PT (2.5, 0.5), true);
    testDomainContainsSegment (domain, PT (1.5, 1.5), PT (4.5, 0.5), true);
    testDomainContainsSegment (domain, PT (1.5, 1.5), PT (0.5, 2.5), true);
    testDomainContainsSegment (domain, PT (1.5, 1.5), PT (4.5, 2.5), true);
    testDomainContainsSegment (domain, PT (1.5, 1.5), PT (0.5, 4.5), true);
    testDomainContainsSegment (domain, PT (1.5, 1.5), PT (2.5, 4.5), true);
    testDomainContainsSegment (domain, PT (1.5, 1.5), PT (4.5, 4.5), true);

    // complex  cases, type 1
    testDomainContainsSegment (domain, PT (2, 0.5), PT (3.5, 2), true);
    testDomainContainsSegment (domain, PT (2, -0.5), PT (4.5, 2), false);

    testDomainContainsSegment (domain, PT (2, 0.5), PT (0.5, 2), true);
    testDomainContainsSegment (domain, PT (2, -0.5), PT (-0.5, 2), false);

    testDomainContainsSegment (domain, PT (0.5, 2.0), PT (2.0, 3.5), true);
    testDomainContainsSegment (domain, PT (-0.5, 2.0), PT (2.0, 4.5), false);

    testDomainContainsSegment (domain, PT (3.5, 2.0), PT (2.0, 3.5), true);
    testDomainContainsSegment (domain, PT (4.5, 2.0), PT (2.0, 4.5), false);

  }
}
