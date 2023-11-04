package us.irdev.gtk.xyw;

import us.irdev.gtk.io.Utility;
import org.junit.jupiter.api.Test;
import us.irdev.gtk.svg.Axis;
import us.irdev.gtk.svg.Frame;
import us.irdev.gtk.svg.Traits;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static us.irdev.gtk.xyw.Tuple.PT;

public class SegmentsPairTest {
  private void drawSvg(String name, SegmentsPair segmentsPair, List<Tuple> points, List<SegmentsPair> pairs) {
    // add a svg file to show the result
    Domain domain = null;
    if (segmentsPair != null) {
      domain = segmentsPair.domain.scale (1.2);
    } else if( pairs != null) {
      domain = new Domain ();
      for (var pair: pairs) {
        domain = Domain.union (domain, pair.domain);
      }
    }
    var frame = new  Frame (domain)
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
    var svg = frame.emitSvg(name, 800);
    Utility.writeFile(Paths.get("output", name + ".svg").toString(), svg);
  }

  private SegmentsPair rawPathological () {
    final double stepSize = 0.005;
    Segments a = Segments.fromTupleFunction (1, 12, stepSize, x -> PT(1.0 + (Math.cos(x * 2 * Math.PI) * x), 0.5 + (Math.sin(x * 2 * Math.PI) * x)));
    Segments b = Segments.fromTupleFunction (1, 5, stepSize, x -> PT(-4.5 + (Math.cos(x * 2 * Math.PI) * x), -3.0 + (Math.sin(x * 2 * Math.PI) * x)));
    return new SegmentsPair(a, b);
  }

  @Test
  public void testPartition() {
    var raw = rawPathological();
    drawSvg("pathologicalCase-raw", raw, null, null);

    var reduced = raw.reduce();
    drawSvg("pathologicalCase-reduced", reduced, null, null);

    var boundedIntersections = reduced.partition ();
    assertNotNull(boundedIntersections);
    drawSvg("pathologicalCase-partitioned", reduced, null, boundedIntersections);

    var intersections = SegmentsPair.intersections (boundedIntersections);
    drawSvg("pathologicalCase-intersections", raw, intersections, null);
  }
}
