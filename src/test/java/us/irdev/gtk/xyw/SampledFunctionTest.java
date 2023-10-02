package us.irdev.gtk.xyw;

import us.irdev.gtk.reader.Table;
import us.irdev.gtk.reader.Utility;
import us.irdev.gtk.svg.Axis;
import us.irdev.gtk.svg.Frame;
import us.irdev.gtk.svg.Grid;
import us.irdev.gtk.svg.Traits;
import us.irdev.gtk.xyw.bb.BoundaryBehaviorAccordion;
import us.irdev.gtk.xyw.bb.BoundaryBehaviorClamp;
import us.irdev.gtk.xyw.bb.BoundaryBehaviorValue;
import us.irdev.gtk.xyw.bb.BoundaryBehaviorWrap;
import us.irdev.gtk.xyw.db.Row;
import us.irdev.gtk.xyw.db.Rows;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static us.irdev.gtk.xyw.Helper.assertSimilar;
import static us.irdev.gtk.xyw.Tuple.PT;
import static us.irdev.gtk.xyw.Tuple.VEC;
import static org.junit.jupiter.api.Assertions.*;

public class SampledFunctionTest {
  private static Rows fromArray(double[] array) {
    List<Row> rows = new ArrayList<>();
    for (int i = 0; i < array.length; i += 3) {
      rows.add (new Row (PT (array[i], array[i + 1]), array[i + 2]));
    }
    return new Rows (rows);
  }

  @Test
  public void testConstructor() {
  }

  private List<Segment> getRefinedIso(SampledFunction function, double iso) {
    List<Segment> segments = function.iso(iso);
    segments = function.refineSegments(segments, iso, 1.0);
    return segments;
  }

  private void drawSvg(String name, SampledFunction function) {
    // add a svg file to show the result
    Frame frame = new Frame (function.getDomain())
            .begin (new Traits (0.1, "#bbb", "none"))
            .element(new Grid (18, 18))
            .begin (new Traits (0.25, "#444", "none"))
            .element(new Axis ())
            .begin (new Traits (0.25, "#070", "none"))
            .poly (getRefinedIso(function, 0))
            .begin (new Traits (0.25, "#007", "none"));
    for (int i = 1; i < 18; ++i) {
      frame.poly (getRefinedIso (function, i * 5.0));
    }
    frame
            .begin (new Traits (0.25, "#700", "none"));
    for (int i = 1; i < 18; ++i) {
      frame.poly (getRefinedIso (function, i * -5.0));
    }
    frame.end();
    String svg = frame.emitSvg(name, 800);
    Utility.writeFile(Paths.get("output", name + ".svg").toString(), svg);
  }

  @Test
  public void testRealFile() {
    String input = Utility.slurpFile ("data/sample.txt");
    assertNotEquals(null, input);
    Table tableReader = new Table (input, ',', '#');
    List<Map<String, String>> table = tableReader.readTable ();
    Rows rows = Rows.fromTable(table, "geo lon (deg)", "geo lat (deg)", "mag lat (deg)");
    rows.addRow(90, 90);

    SampledFunction function = SampledFunction.fromDatabase (rows, new BoundaryBehaviorWrap (), new BoundaryBehaviorAccordion ());
    List<Segment> segments = function.iso (55);
    List<PolyLine> polyLines = PolyLine.polyLinesFromSegments (segments);

    assertEquals(1, polyLines.size());
    PolyLine polyLine = polyLines.get(0);
    Tuple[] points = polyLine.getPoints();
    assertTrue(points.length > (360 / 5));

    drawSvg("testRealFile", function);
  }

  @Test
  public void testFromDatabase() {
    double[] array = new double[]{
            -1.0, -1.0, 0.0,   0.0, -1.0, 0.0,   1.0, -1.0, 0.0,
            -1.0,  0.0, 1.0,   0.0,  0.0, 1.0,   1.0,  0.0, 1.0,
            -1.0,  1.0, 2.0,   0.0,  1.0, 2.0,   1.0,  1.0, 2.0
    };
    Rows rows = fromArray(array);
    SampledFunction function = SampledFunction.fromDatabase (rows, new BoundaryBehaviorWrap ());
    assertSimilar (function.getDomain(), new Domain(PT (-1, -1), PT (2, 2)));

    // test the actual sample points
    assertEquals(0.0, function.fxy(-1.0, -1.0));
    assertEquals(0.0, function.fxy( 0.0, -1.0));
    assertEquals(0.0, function.fxy( 1.0, -1.0));

    assertEquals(1.0, function.fxy(-1.0,  0.0));
    assertEquals(1.0, function.fxy( 0.0,  0.0));
    assertEquals(1.0, function.fxy( 1.0,  0.0));

    assertEquals(2.0, function.fxy(-1.0,  1.0));
    assertEquals(2.0, function.fxy( 0.0,  1.0));
    assertEquals(2.0, function.fxy(1.0,  1.0));

    // test the wrapped sample points
    assertEquals(0.0, function.fxy( -2.0, -1.0));
    assertEquals(0.0, function.fxy(  2.0, -1.0));

    assertEquals(1.0, function.fxy( -2.0,  0.0));
    assertEquals(1.0, function.fxy(  2.0,  0.0));

    assertEquals(2.0, function.fxy( -2.0,  1.0));
    assertEquals(2.0, function.fxy(  2.0,  1.0));

    assertEquals(2.0, function.fxy( -1.0, -2.0));
    assertEquals(0.0, function.fxy( -1.0,  2.0));

    assertEquals(2.0, function.fxy(  0.0, -2.0));
    assertEquals(0.0, function.fxy(  0.0,  2.0));

    assertEquals(2.0, function.fxy(  1.0, -2.0));
    assertEquals(0.0, function.fxy(  1.0,  2.0));

    // test some midpoints
    assertEquals(0.0, function.fxy(-0.5, -1.0));
    assertEquals(0.0, function.fxy( 0.5, -1.0));
    assertEquals(0.0, function.fxy( 1.5, -1.0));

    assertEquals(0.5, function.fxy(0.0, -0.5));
    assertEquals(1.5, function.fxy(0.0,  0.5));
    assertEquals(1.0, function.fxy(0.0,  1.5));

    // test some quarter points (to verify lerp)
    assertEquals(0.0, function.fxy(-0.25, -1.0));
    assertEquals(0.0, function.fxy( 0.75, -1.0));
    assertEquals(0.0, function.fxy( 1.75, -1.0));
    assertEquals(0.0, function.fxy( 2.75, -1.0));

    assertEquals(0.75, function.fxy(0.0, -0.25));
    assertEquals(1.75, function.fxy(0.0,  0.75));
    assertEquals(0.5, function.fxy( 0.0,  1.75));
    assertEquals(0.75, function.fxy(0.0,  2.75));

    assertEquals(0.25, function.fxy(0.0, -0.75));
    assertEquals(1.25, function.fxy(0.0,  0.25));
    assertEquals(1.5, function.fxy( 0.0,  1.25));
    assertEquals(0.25, function.fxy(0.0,  2.25));
  }

  private void verifyList(Segment[] expectArray, List<Segment> verifyList) {
    assertEquals(expectArray.length, verifyList.size());
    int verifiedCount = 0;
    for (Segment expect: expectArray) {
      for (Segment verify : verifyList) {
        verifiedCount += Segment.similar(verify, expect) ? 1 : 0;
      }
    }
    assertEquals(verifiedCount, expectArray.length);
  }

  @Test
  public void testIso() {
    double[] array = new double[]{
            -1.0, -1.0, 0.0,   0.0, -1.0, 0.0,   1.0, -1.0, 0.0,
            -1.0,  0.0, 0.0,   0.0,  0.0, 1.0,   1.0,  0.0, 0.0,
            -1.0,  1.0, 0.0,   0.0,  1.0, 0.0,   1.0,  1.0, 0.0
    };
    Rows rows = fromArray(array);
    SampledFunction function = SampledFunction.fromDatabase (rows, new BoundaryBehaviorValue ());
    assertSimilar (function.getDomain(), new Domain(PT (-1, -1), PT (1, 1)));

    List<Segment> segments = function.iso(0.5);
    verifyList(new Segment[]{
            new Segment (0, 0.5, 0.5, 0),
            new Segment (0, -0.5, 0.5, 0),
            new Segment (0, 0.5, -0.5, 0),
            new Segment (0, -0.5, -0.5, 0),
    }, segments);

    // look for my line segments
    segments = function.iso(0.75);
    verifyList(new Segment[] {
            new Segment (0, 0.25, 0.25, 0),
            new Segment (0, -0.25, 0.25, 0),
            new Segment (0, 0.25, -0.25, 0),
            new Segment (0, -0.25, -0.25, 0),
    }, segments);
  }

  @Test
  public void testRefinedIsoWrap() {
    double[] array = new double[]{
            -1.0, -1.0, 0.0,   0.0, -1.0, 0.0,
            -1.0,  0.0, 0.0,   0.0,  0.0, 1.0,
    };
    Rows rows = fromArray(array);
    SampledFunction function = SampledFunction.fromDatabase (rows, new BoundaryBehaviorWrap ());
    assertSimilar (function.getDomain(), new Domain(PT (-1, -1), PT (1, 1)));

    double targetValue = 0.5;
    List<Segment> segments = function.iso(targetValue);
    verifyList(new Segment[]{
            new Segment (0, 0.5, 0.5, 0),
            new Segment (0, -0.5, 0.5, 0),
            new Segment (0, 0.5, -0.5, 0),
            new Segment (0, -0.5, -0.5, 0),
    }, segments);

    // try to refine the  iso
    segments = function.refineSegments(segments, targetValue, 0.25);
    assertEquals(16, segments.size());

    // collect the segments into a single polyline
    List<PolyLine> polyLines = PolyLine.polyLinesFromSegments (segments);
    assertEquals(1, polyLines.size());

    PolyLine polyLine = polyLines.get(0);

  }

  @Test
  public void testBilinear2x3() {
    // copied from BilinearInterpolator class tests at Lil's request
    /*
     *       Y
     *       ^
     *       |           1          2          3
     *    2 ---         @----------@----------@
     *       |          |          |          |
     *       |          |1         |3         |5
     *    1 ---         @----------@----------@
     *       |
     *       |
     *       o----------|----------|----------|-----> X
     *                 10         20         30
     */
    double[] array = new double[]{
            10, 1, 1,    20, 1, 3,    30, 1, 5,
            10, 2, 1,    20, 2, 2,    30, 2, 3
    };
    Rows rows = fromArray(array);
    SampledFunction function = SampledFunction.fromDatabase (rows, new BoundaryBehaviorClamp ());
    assertEquals(1.0,  function.fxy(-100, -100)); //edge case
    assertEquals(1.0,  function.fxy(10, 1));
    assertEquals(1.0,  function.fxy(10, 1.5));
    assertEquals(1.0,  function.fxy(10, 2));
    assertEquals(2.0,  function.fxy(15, 1));
    assertEquals(1.5,  function.fxy(15, 2));
    assertEquals(3.0,  function.fxy(20, 1));
    assertEquals(2.5,  function.fxy(20, 1.5));
    assertEquals(2.0,  function.fxy(20, 2));
    assertEquals(4.0,  function.fxy(25, 0));    //edge case
    assertEquals(4.0,  function.fxy(25, 1));
    assertEquals(3.25, function.fxy(25, 1.5));
    assertEquals(2.5,  function.fxy(25, 2));
    assertEquals(5.0,  function.fxy(30, 1));
    assertEquals(4.0,  function.fxy(30, 1.5));
    assertEquals(3.0,  function.fxy(30, 2));
    assertEquals(5.0,  function.fxy(30, 0));    //edge case
    assertEquals(3.0,  function.fxy(30, 3));    //edge case
    assertEquals(4.0,  function.fxy(40, 1.5));  //edge case
  }

  @Test
  public void testBilinear2x2() {
    // copied from BilinearInterpolator class tests at Lil's request
    /*
     *       Y
     *       ^
     *       |           2          4
     *    2 ---         @----------@
     *       |          |          |
     *       |          |1         |3
     *    1 ---         @----------@
     *       |
     *       |
     *       o----------|----------|-----> X
     *                 20         30
     */
    double[] array = new double[]{
            20, 1, 1,    30, 1, 3,
            20, 2, 2,    30, 2, 4
    };
    Rows rows = fromArray(array);
    SampledFunction function = SampledFunction.fromDatabase (rows, new BoundaryBehaviorClamp ());
    assertEquals(1.0, function.fxy(0,  0)); // edge case
    assertEquals(1.0, function.fxy(20, 1));
    assertEquals(1.5, function.fxy(20, 1.5));
    assertEquals(2.5, function.fxy(25, 1.5));
    assertEquals(3.5, function.fxy(30, 1.5));
  }

  @Test
  public void testHyperbolicParaboloid() {
    double[] array = new double[]{
            0, 0, 0,    1, 0, 0,
            0, 1, 0,    1, 1, 1
    };
    Rows rows = fromArray(array);
    SampledFunction function = SampledFunction.fromDatabase (rows, new BoundaryBehaviorValue ());

    // verify the iso-line we get for the target value of 0.5 is a straight line from the midpoint
    // of two edges.
    double targetValue = 0.5;
    List<Segment> segments = function.iso (targetValue);
    assertEquals(1, segments.size());

    PolyLine iso = PolyLine.polyLinesFromSegments (segments).get(0);
    assertEquals(2, iso.getPoints().length);
    assertSimilar(PT (0.5, 1), iso.getPoints()[0]);
    assertSimilar(PT (1, 0.5), iso.getPoints()[1]);

    // verify that a point on that straight line is not a correct approximation of the bilinear
    // interpolation (ruled surface)
    double midpoint = 0.75;
    assertFalse(Numerics.similar (targetValue, function.fxy(midpoint, midpoint)));

    // verify the correct value is on the hyperbolic paraboloid
    // https://en.wikipedia.org/wiki/Paraboloid#Hyperbolic_paraboloid
    double coord = Math.sqrt(2.0) / 2.0;
    assertTrue(Numerics.similar (targetValue, function.fxy(coord, coord)));

    // now try to refine the original midpoint to see if we get the correct value
    Tuple refined = function.refineSampleLocation(midpoint, midpoint, targetValue);
    assertSimilar(refined, PT (coord, coord));
  }


  /**
   * a helper function adapter to create a sine wave function we can use to compute crossings.
   */
  private static class TupleFunctionAtAdapter implements SampledFunction.TupleFunctionAt {
    private double scale;
    private double count;

    public TupleFunctionAtAdapter(double count, double scale) {
      this.count = count;
      this.scale = scale;
    }

    public TupleFunctionAtAdapter(double count) {
      this(count, 1);
    }

    public TupleFunctionAtAdapter() {
      this (1, 1);
    }

    @Override
    public Tuple at (double x) {
      return PT (x, (Math.sin(x * Math.PI * 2.0 * count) + 1.0) * 0.5 * scale);
    }
  }

  @Test
  public void testFindCrossing() {
    double[] array = new double[]{
            0, 0, 0,    1, 0, 0,
            0, 1, 1,    1, 1, 1
    };
    Rows rows = fromArray(array);
    SampledFunction function = SampledFunction.fromDatabase (rows, new BoundaryBehaviorValue ());
    SampledFunction.Crossing crossing = function.findCrossing(new TupleFunctionAtAdapter(), 0.31, 0.71, 0.5);
    assertSimilar (0.5, crossing.x);
    assertSimilar (PT (0.5, 0.5), crossing.loc);
    assertSimilar (0.5, crossing.value);
  }

  @Test
  public void testFindCrossings() {
    double[] array = new double[]{
            0, 0, 0,    1, 0, 0,
            0, 1, 1,    1, 1, 1
    };
    Rows rows = fromArray(array);
    SampledFunction function = SampledFunction.fromDatabase (rows, new BoundaryBehaviorValue ());
    List<SampledFunction.Crossing> crossings = function.findCrossings(new TupleFunctionAtAdapter(1.25), 0.31, 0.91, 0.5, 2);
    assertEquals(2, crossings.size());
    assertSimilar (0.4, crossings.get(0).x);
    assertSimilar (0.8, crossings.get(1).x);

    assertSimilar (0.5, crossings.get(0).value);
    assertSimilar (0.5, crossings.get(1).value);

    assertSimilar (PT (0.4, 0.5), crossings.get(0).loc);
    assertSimilar (PT (0.8, 0.5), crossings.get(1).loc);

    assertTrue (crossings.get (0).above);
    assertFalse (crossings.get (1).above);
  }

  @Test
  public void testWithValue() {
    Domain domain = new Domain (-180, 175, -90, 90);
    Tuple interval = VEC (5, 5);
    // simple perfectly horizontal isolines, scaled down just a bit
    Rows db = Rows.fromFill(domain, interval, xy -> xy.y);
    SampledFunction function = SampledFunction.fromDatabase (db, new BoundaryBehaviorWrap (), new BoundaryBehaviorAccordion ());
    List<Segment> segments = function.iso (0.5);
    for (Segment segment: segments) {
      // verify the found y coordinate is actally a little above the iso value, since we scaled it
      assertSimilar(0.5, segment.a.y);
      assertSimilar(segment.a.y, segment.b.y);
    }

    drawSvg("testWithValue", function);
    Utility.writeFile(Paths.get("output", "testWithValue.csv").toString(), db.toString ("geo lon (deg)", "geo lat (deg)", "mag lat (deg)"));
  }

  @Test
  public void testWithNoGradient() {
    Domain domain = new Domain (-180, 175, -90, 90);
    Tuple interval = VEC (5, 5);
    // simple perfectly horizontal isolines, scaled down just a bit
    Rows db = Rows.fromFill(domain, interval, xy -> xy.y * 0.95);
    SampledFunction function = SampledFunction.fromDatabase (db, new BoundaryBehaviorWrap (), new BoundaryBehaviorAccordion ());
    List<Segment> segments = function.iso (0.5);
    for (Segment segment: segments) {
      // verify the found y coordinate is actally a little above the iso value, since we scaled it
      assertSimilar(0.5 / 0.95, segment.a.y);
      assertSimilar(segment.a.y, segment.b.y);
    }

    drawSvg("testWithNoGradient", function);
    Utility.writeFile(Paths.get("output", "testWithNoGradient.csv").toString(), db.toString ("geo lon (deg)", "geo lat (deg)", "mag lat (deg)"));
  }

  @Test
  public void testWithSinusoidal() {
    Domain domain = new Domain (-180, 175, -90, 90);
    Tuple interval = VEC (5, 5);
    Rows db = Rows.fromFill(domain, interval, xy -> (xy.y * 0.95) + (3 * Math.cos(1 + Math.toRadians(xy.x) * 2)));
    SampledFunction function = SampledFunction.fromDatabase (db, new BoundaryBehaviorWrap (), new BoundaryBehaviorAccordion ());
    List<Segment> segments = function.iso (0.5);

    drawSvg("testWithSinusoidal", function);
    Utility.writeFile(Paths.get("output", "testWithSinusoidal.csv").toString(), db.toString ("geo lon (deg)", "geo lat (deg)", "mag lat (deg)"));
  }

}
