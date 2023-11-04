package us.irdev.gtk.xyw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static us.irdev.gtk.xyw.Helper.assertSimilar;
import static us.irdev.gtk.xyw.Tuple.PT;

public class PolyLineTest {
  @Test
  public void testMakeLines() {
    var a = PT (1, 1);
    var b = PT (1, -1);
    var c = PT (-1, -1);
    var d = PT (-1, 1);
    {
      List<Segment> segments = Arrays.asList(
              new Segment (a, b),
              new Segment (c, d),
              new Segment (d, a),
              new Segment (b, c)
      );

      // closed
      List<PolyLine> polyLines = PolyLine.polyLinesFromSegments (segments);
      assertEquals (polyLines.size (), 1);
      PolyLine polyLine = polyLines.get (0);
      assertTrue (polyLine.isClosed ());
      Tuple[] points = polyLine.getPoints ();
      assertEquals (points.length, 4);

      // XXX will the runtime always produce this exact ordering? the hash function in particular...
      Helper.assertSimilar (points[0], a);
      Helper.assertSimilar (points[1], b);
      Helper.assertSimilar (points[2], c);
      Helper.assertSimilar (points[3], d);
    }

    {
      List<Segment> segments = Arrays.asList(
              new Segment (a, b),
              new Segment (c, d),
              new Segment (b, c)
      );
      // open
      List<PolyLine> polyLines = PolyLine.polyLinesFromSegments (segments);
      assertEquals(polyLines.size(), 1);
      PolyLine polyLine = polyLines.get(0);
      assertFalse(polyLine.isClosed());
      Tuple[] points = polyLine.getPoints();
      assertEquals(points.length, 4);

      // XXX will the runtime always produce this exact ordering? the hash function in particular...
      Helper.assertSimilar (points[0], a);
      Helper.assertSimilar (points[1], b);
      Helper.assertSimilar (points[2], c);
      Helper.assertSimilar (points[3], d);
    }
  }

  @Test
  public void testToSegments() {
    var points = new ArrayList<Tuple> ();
    points.add (PT(-5, -5));
    points.add (PT(-4, 4));
    points.add (PT(-3, 3));

    var polyline = new  PolyLine (points, false);
    List<Segment> segments = polyline.toSegments ();
    assertEquals(2, segments.size());
    Helper.assertSimilar(segments.get(0).a, points.get(0));
    Helper.assertSimilar(segments.get(0).b, points.get(1));
    Helper.assertSimilar(segments.get(1).a, points.get(1));
    Helper.assertSimilar(segments.get(1).b, points.get(2));
  }
}
