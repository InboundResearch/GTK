package us.irdev.gtk.xyw;

import us.irdev.gtk.reader.Utility;
import us.irdev.gtk.svg.Axis;
import us.irdev.gtk.svg.Frame;
import us.irdev.gtk.svg.Traits;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static us.irdev.gtk.xyw.Helper.assertSimilar;
import static us.irdev.gtk.xyw.SegmentsTest.makeSegments;
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

  @Test
  public void testReduce() {
    // generate a bunch of points in a sinusoidal line
    Segments a = makeSegments (-10, 10, 0.1, x -> PT(x, x * 0.1));
    Segments b = makeSegments (-10, 10, 0.1, x -> PT(x * 0.1, x + 2));

    // precompute a line intersection for comparison
    Tuple testPoint = Line.intersect (
            Line.fromTwoPoints (a.segments.get(0).a, a.segments.get(10).b),
            Line.fromTwoPoints (b.segments.get(0).a, b.segments.get(10).b)
    );

    // do the reduction and verify the found segments contain our intersection point
    SegmentsPair sp = SegmentsPair.reduce(a, b);
    assertNotNull(sp);
    Tuple intersectionPoint = Segment.intersect(sp.a.segments.get(0), sp.b.segments.get(0));
    assertNotNull(intersectionPoint);
    assertSimilar(testPoint, intersectionPoint);
  }

  private SegmentsPair rawPathological () {
    final double stepSize = 0.005;
    Segments a = makeSegments (1, 12, stepSize, x -> PT(1.0 + (Math.cos(x * 2 * Math.PI) * x), 0.5 + (Math.sin(x * 2 * Math.PI) * x)));
    Segments b = makeSegments (1, 5, stepSize, x -> PT(-4.5 + (Math.cos(x * 2 * Math.PI) * x), -3.0 + (Math.sin(x * 2 * Math.PI) * x)));
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

  @Test
  public void testIntersectionPathological() {
    SegmentsPair raw = rawPathological();
    List<Tuple> result = raw.reduce().intersections ();
    //drawSvg("testIntersectionPathological", raw.a, raw.b, result, null);
  }
}
