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

    domain = domain.add (PT (0, 0));
    assertSimilar(PT (0, 0), domain.min);
    assertSimilar(PT (1, 1), domain.max);

    Domain domain2 = new Domain ().add(PT (1.1, 1.1));
    domain2 = domain2.add (PT (-0.1, -0.1));
    Helper.assertSimilar(new Domain (-0.1, 1.1, -0.1, 1.1), domain2);
  }

  @Test
  public void testAdd() {
    Domain domain = new Domain ().add(PT (1, 1));
    assertEquals(domain.min, PT (1, 1));
    assertEquals(domain.max, PT (1, 1));

    domain = domain.add (PT (0, 0));
    assertEquals(domain.min, PT (0, 0));
    assertEquals(domain.max, PT (1, 1));

    domain = domain.add (PT (-1, -1));
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
           db = new Domain (0.5, 1.5, 0.5, 1.5),
           dc = new Domain (5, 6, 5, 6);
    assertSimilar (new Domain (0.5, 1, 0.5, 1), Domain.intersection(da, db));
    assertSimilar (da, Domain.intersection(da, da));
    assertSimilar (new Domain (0, 1.5, 0, 1.5), Domain.union(da, db));
    assertSimilar (da, Domain.union(da, da));

    // test when the bounds are disjoint
    assertFalse(Domain.intersection(da, dc).valid());
    assertSimilar (new Domain (0, 6, 0, 6), Domain.union(da, dc));

    // test union with an empty domain returns the original domain
    assertSimilar(db, Domain.union(db, new Domain()));
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

    // complex  cases, type 1 - two points outside only one edge
    testDomainContainsSegment (domain, PT (2, 0.5), PT (3.5, 2), true);
    testDomainContainsSegment (domain, PT (2, -0.5), PT (4.5, 2), false);

    testDomainContainsSegment (domain, PT (2, 0.5), PT (0.5, 2), true);
    testDomainContainsSegment (domain, PT (2, -0.5), PT (-0.5, 2), false);

    testDomainContainsSegment (domain, PT (0.5, 2.0), PT (2.0, 3.5), true);
    testDomainContainsSegment (domain, PT (-0.5, 2.0), PT (2.0, 4.5), false);

    testDomainContainsSegment (domain, PT (3.5, 2.0), PT (2.0, 3.5), true);
    testDomainContainsSegment (domain, PT (4.5, 2.0), PT (2.0, 4.5), false);

    // complex  cases, type 2 - one point outside an edge, the other outside two edges (corner)
    // above
    testDomainContainsSegment (domain, PT (1.1, 3.1), PT (3.1, 0), true);
    testDomainContainsSegment (domain, PT (2.9, 3.1), PT (4, 0), true);
    testDomainContainsSegment (domain, PT (2.9, 5), PT (5, 0), false);

    testDomainContainsSegment (domain, PT (1.1, 5), PT (-1, 0), false);
    testDomainContainsSegment (domain, PT (1.1, 3.1), PT (0, 0), true);
    testDomainContainsSegment (domain, PT (2.9, 3.1), PT (0.9, 0), true);

    // below
    testDomainContainsSegment (domain, PT (1.1, 0.9), PT (3.1, 4), true);
    testDomainContainsSegment (domain, PT (2.9, 0.9), PT (4, 4), true);
    testDomainContainsSegment (domain, PT (2.9, -1), PT (5, 4), false);

    testDomainContainsSegment (domain, PT (1.1, -1), PT (-1, 4), false);
    testDomainContainsSegment (domain, PT (1.1, 0.9), PT (0, 4), true);
    testDomainContainsSegment (domain, PT (2.9, 0.9), PT (0.9, 4), true);

    // left
    testDomainContainsSegment (domain, PT (0.9, 1.1), PT (4, 3.1), true);
    testDomainContainsSegment (domain, PT (0.9, 2.9), PT (4, 4), true);
    testDomainContainsSegment (domain, PT (-1., 2.9), PT (4, 5), false);

    testDomainContainsSegment (domain, PT (-1, 1.1), PT (4, -1), false);
    testDomainContainsSegment (domain, PT (0.9, 1.1), PT (4, 0), true);
    testDomainContainsSegment (domain, PT (0.9, 2.9), PT (4, 0.9), true);

    // right
    testDomainContainsSegment (domain, PT (3.1, 1.1), PT (0, 3.1), true);
    testDomainContainsSegment (domain, PT (3.1, 2.9), PT (0, 4), true);
    testDomainContainsSegment (domain, PT (5, 2.9), PT (0, 5), false);

    testDomainContainsSegment (domain, PT (5, 1.1), PT (0, -1), false);
    testDomainContainsSegment (domain, PT (3.1, 1.1), PT (0, 0), true);
    testDomainContainsSegment (domain, PT (3.1, 2.9), PT (0, 0.9), true);


    // complex  cases, type 3 - both points outside on opposite corners
    testDomainContainsSegment (domain, PT (0.5, 0.5), PT (3.5, 3.5), true);
    testDomainContainsSegment (domain, PT (3.5, 0.5), PT (0.5, 3.5), true);

    // positive slope
    testDomainContainsSegment (domain, PT (0, 0.9), PT (3.1, 4), true);
    testDomainContainsSegment (domain, PT (0.9, 0), PT (4, 3.1), true);

    testDomainContainsSegment (domain, PT (0, 0.9), PT (4, 3.1), true);
    testDomainContainsSegment (domain, PT (0.9, 0), PT (3.1, 4), true);

    testDomainContainsSegment (domain, PT (-2, 0.9), PT (3.1, 6), false);
    testDomainContainsSegment (domain, PT (0.9, -2), PT (6, 3.1), false);

    // negative slope
    testDomainContainsSegment (domain, PT (3.1, 0), PT (0, 3.1), true);
    testDomainContainsSegment (domain, PT (4, 0.9), PT (0.9, 4), true);

    testDomainContainsSegment (domain, PT (3.1, 0), PT (0.9, 4), true);
    testDomainContainsSegment (domain, PT (4, 0.9), PT (0, 3.1), true);

    testDomainContainsSegment (domain, PT (-2, 3.1), PT (3.1, -2), false);
    testDomainContainsSegment (domain, PT (0.9, 6), PT (6, 0.9), false);

  }
}
