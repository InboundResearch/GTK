package us.irdev.gtk.xy;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static us.irdev.gtk.xy.Helper.assertSimilar;

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
    assertNull(clipToDomain(domain, new Segment(new Tuple (0, 0), new Tuple (0.5, 0.5))));
    assertNull(clipToDomain(domain, new Segment(new Tuple (1.5, 0), new Tuple (2.5, 0.5))));
    assertNull(clipToDomain(domain, new Segment(new Tuple (3.5, 0), new Tuple (4.5, 0.5))));

    assertNull(clipToDomain(domain, new Segment(new Tuple (0, 2), new Tuple (0.5, 2.5))));
    assertNull(clipToDomain(domain, new Segment(new Tuple (4, 0), new Tuple (4.5, 2.5))));

    assertNull(clipToDomain(domain, new Segment(new Tuple (0, 4), new Tuple (0.5, 4.5))));
    assertNull(clipToDomain(domain, new Segment(new Tuple (1.5, 4), new Tuple (2.5, 4.5))));
    assertNull(clipToDomain(domain, new Segment(new Tuple (3.5, 4), new Tuple (4.5, 4.5))));

    // all inside
    assertSimilar(new Segment (new Tuple (1.5, 1.5), new Tuple (2.5, 2.5)), clipToDomain(domain, new Segment (new Tuple (1.5, 1.5), new Tuple (2.5, 2.5))));

    // spanning one edge
    assertSimilar(new Segment (new Tuple (1, 2), new Tuple (1.5, 2.5)), clipToDomain(domain, new Segment (new Tuple (0.5, 1.5), new Tuple (1.5, 2.5))));
    assertSimilar(new Segment (new Tuple (2, 1), new Tuple (2.5, 1.5)), clipToDomain(domain, new Segment (new Tuple (1.5, 0.5), new Tuple (2.5, 1.5))));
    assertSimilar(new Segment (new Tuple (2.5, 1.5), new Tuple (3, 2)), clipToDomain(domain, new Segment (new Tuple (2.5, 1.5), new Tuple (3.5, 2.5))));
    assertSimilar(new Segment (new Tuple (1.5, 2.5), new Tuple (2, 3)), clipToDomain(domain, new Segment (new Tuple (1.5, 2.5), new Tuple (2.5, 3.5))));

    // spanning two edges
    assertSimilar(new Segment (new Tuple (1, 1.625), new Tuple (3, 2.125)), clipToDomain(domain, new Segment (new Tuple (0.5, 1.5), new Tuple (4.5, 2.5))));

  }
}
