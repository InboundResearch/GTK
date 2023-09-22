package us.irdev.gtk.xy;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static us.irdev.gtk.xy.Helper.assertSimilar;
import static us.irdev.gtk.xy.PolyLine.polyLinesFromSegments;

public class PolyLineTest {
  @Test
  public void testMakeLines() {
    Tuple a = new Tuple (1, 1);
    Tuple b = new Tuple (1, -1);
    Tuple c = new Tuple (-1, -1);
    Tuple d = new Tuple (-1, 1);
    {
      List<Segment> segments = Arrays.asList(
              new Segment (a, b),
              new Segment (c, d),
              new Segment (d, a),
              new Segment (b, c)
      );

      // closed
      List<PolyLine> polyLines = polyLinesFromSegments (segments);
      assertEquals (polyLines.size (), 1);
      PolyLine polyLine = polyLines.get (0);
      assertTrue (polyLine.isClosed ());
      Tuple[] points = polyLine.getPoints ();
      assertEquals (points.length, 4);

      // XXX will the runtime always produce this exact ordering? the hash function in particular...
      assertSimilar (points[0], d);
      assertSimilar (points[1], c);
      assertSimilar (points[2], b);
      assertSimilar (points[3], a);
    }

    {
      List<Segment> segments = Arrays.asList(
              new Segment (a, b),
              new Segment (c, d),
              new Segment (b, c)
      );
      // open
      List<PolyLine> polyLines = polyLinesFromSegments (segments);
      assertEquals(polyLines.size(), 1);
      PolyLine polyLine = polyLines.get(0);
      assertFalse(polyLine.isClosed());
      Tuple[] points = polyLine.getPoints();
      assertEquals(points.length, 4);

      // XXX will the runtime always produce this exact ordering? the hash function in particular...
      assertSimilar (points[0], d);
      assertSimilar (points[1], c);
      assertSimilar (points[2], b);
      assertSimilar (points[3], a);
    }
  }
}
