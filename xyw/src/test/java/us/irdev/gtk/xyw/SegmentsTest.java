package us.irdev.gtk.xyw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static us.irdev.gtk.xyw.Helper.assertSimilar;
import static us.irdev.gtk.xyw.Tuple.PT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SegmentsTest {

  private Segment clipToDomain(Domain domain, Segment segment) {
    List<Segment> segments = new ArrayList<>(1);
    segments.add(segment);
    segments = Segments.clipToDomain (segments, domain);
    return (segments != null) ? segments.get(0) : null;
  }

  @Test
  public void testClip() {
    Domain domain = new Domain (1, 3, 1, 3);

    // all outside
    assertNull(clipToDomain(domain, new Segment(PT (0, 0), PT (0.5, 0.5))));
    assertNull(clipToDomain(domain, new Segment(PT (1.5, 0), PT (2.5, 0.5))));
    assertNull(clipToDomain(domain, new Segment(PT (3.5, 0), PT (4.5, 0.5))));

    assertNull(clipToDomain(domain, new Segment(PT (0, 2), PT (0.5, 2.5))));
    assertNull(clipToDomain(domain, new Segment(PT (4, 0), PT (4.5, 2.5))));

    assertNull(clipToDomain(domain, new Segment(PT (0, 4), PT (0.5, 4.5))));
    assertNull(clipToDomain(domain, new Segment(PT (1.5, 4),PT (2.5, 4.5))));
    assertNull(clipToDomain(domain, new Segment(PT (3.5, 4),PT (4.5, 4.5))));

    // all inside
    Helper.assertSimilar(new Segment (PT (1.5, 1.5),PT (2.5, 2.5)), clipToDomain(domain, new Segment (PT (1.5, 1.5),PT (2.5, 2.5))));

    // spanning one edge
    Helper.assertSimilar(new Segment (PT (1, 2),PT (1.5, 2.5)), clipToDomain(domain, new Segment (PT (0.5, 1.5),PT (1.5, 2.5))));
    Helper.assertSimilar(new Segment (PT (2, 1),PT (2.5, 1.5)), clipToDomain(domain, new Segment (PT (1.5, 0.5),PT (2.5, 1.5))));
    Helper.assertSimilar(new Segment (PT (2.5, 1.5),PT (3, 2)), clipToDomain(domain, new Segment (PT (2.5, 1.5),PT (3.5, 2.5))));
    Helper.assertSimilar(new Segment (PT (1.5, 2.5),PT (2, 3)), clipToDomain(domain, new Segment (PT (1.5, 2.5),PT (2.5, 3.5))));

    // spanning two edges
    Helper.assertSimilar(new Segment (PT (1, 1.625),PT (3, 2.125)), clipToDomain(domain, new Segment (PT (0.5, 1.5),PT (4.5, 2.5))));
  }

  @Test
  public void testBounds() {
    // generate a bunch of points in a sinusoidal line
    Segments segments = Segments.fromTupleFunction (-10, 10, 0.1, x -> PT(x, Math.sin (x * Math.PI) * 2));
    assertEquals(200, segments.segments.size());
    assertSimilar(new Domain (-10, 10, -2, 2), segments.domain);
  }

}
