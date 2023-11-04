package us.irdev.gtk.xyw;

import us.irdev.gtk.io.Utility;
import us.irdev.gtk.svg.Axis;
import us.irdev.gtk.svg.Frame;
import us.irdev.gtk.svg.Traits;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static us.irdev.gtk.xyw.Tuple.PT;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SegmentsPairTest {
  private void drawSvg(String name, SegmentsPair segmentsPair, List<Tuple> points, List<SegmentsPair> pairs) {
    // add a svg file to show the result
    Domain domain = null;
    if (segmentsPair != null) {
      domain = segmentsPair.domain.scale (1.2);
    } else if( pairs != null) {
      domain = new Domain ();
      for (SegmentsPair pair: pairs) {
        domain = Domain.union (domain, pair.domain);
      }
    }
    Frame frame = new Frame (domain)
            .begin (new Traits (0.1, "#888", "none"))
            .element(new Axis ());
    if (segmentsPair != null) {
      frame
              .begin (new Traits (0.25, "#88f", "none"))
              .poly (segmentsPair.a)
              .begin (new Traits (0.25, "#f88", "none"))
              .poly (segmentsPair.b);
    }
    if (points != null) {
      frame
              .begin (new Traits (0.125, "#000", "red"))
              .points (domain.span() * 0.01, points.toArray(new Tuple[0]));
    }
    if (pairs != null) {
      for (SegmentsPair pair: pairs) {
        frame.begin (new Traits (0.1, "#484", "none"))
                .box (pair.domain)
                .begin (new Traits (0.25, "#007", "none"))
                .poly (pair.a)
                .begin (new Traits (0.25, "#700", "none"))
                .poly (pair.b);
      }
    }
    String svg = frame.emitSvg(name, 800);
    Utility.writeFile(Paths.get("output", name + ".svg").toString(), svg);
  }

  private SegmentsPair rawPathological () {
    final double stepSize = 0.005;
    Segments a = Segments.fromTupleFunction (1, 12, stepSize, x -> Tuple.PT(1.0 + (Math.cos(x * 2 * Math.PI) * x), 0.5 + (Math.sin(x * 2 * Math.PI) * x)));
    Segments b = Segments.fromTupleFunction (1, 5, stepSize, x -> Tuple.PT(-4.5 + (Math.cos(x * 2 * Math.PI) * x), -3.0 + (Math.sin(x * 2 * Math.PI) * x)));
    return new SegmentsPair(a, b);
  }

  @Test
  public void testPartition() {
    SegmentsPair raw = rawPathological();
    drawSvg("pathologicalCase-raw", raw, null, null);

    SegmentsPair reduced = raw.reduce();
    drawSvg("pathologicalCase-reduced", reduced, null, null);

    List<SegmentsPair> boundedIntersections = reduced.partition ();
    assertNotNull(boundedIntersections);
    drawSvg("pathologicalCase-partitioned", reduced, null, boundedIntersections);

    List<Tuple> intersections = SegmentsPair.intersections (boundedIntersections);
    drawSvg("pathologicalCase-intersections", raw, intersections, null);
  }
}
