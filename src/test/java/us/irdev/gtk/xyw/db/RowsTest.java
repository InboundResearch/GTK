package us.irdev.gtk.xyw.db;

import us.irdev.gtk.xyw.Domain;
import us.irdev.gtk.xyw.Tuple;
import org.junit.jupiter.api.Test;

import java.util.List;

import static us.irdev.gtk.xyw.Helper.assertSimilar;
import static us.irdev.gtk.xyw.Tuple.PT;
import static us.irdev.gtk.xyw.Tuple.VEC;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RowsTest {
  @Test
  public void testFillConstant() {
    Domain domain = new Domain (-180, 175, -90, 90);
    Tuple size = domain.size();
    Tuple interval = PT (5, 5);
    Rows db = Rows.fromFill(domain, interval, xy -> 36.5);

    List<Row> rows = db.getRows();

    assertEquals((int)((Math.round(size.x / interval.x) + 1) * (Math.round(size.y / interval.y) + 1)), rows.size());

    // check that all the rows have the expected value and the x/y coords are multiples of the interval
    for (Row row: rows) {
      assertEquals(36.5, row.value);
      assertSimilar (row.xy.x, domain.min.x + (Math.round((row.xy.x - domain.min.x) / interval.x) * interval.x));
      assertSimilar (row.xy.y, domain.min.y + (Math.round((row.xy.y - domain.min.y) / interval.y) * interval.y));
    }
  }

  @Test
  public void testFillVariesWithY() {
    Domain domain = new Domain (-180, 175, -90, 90);
    Tuple size = domain.size();
    Tuple interval = VEC (5, 5);
    Rows db = Rows.fromFill(domain, interval, xy -> xy.y * 0.9);

    List<Row> rows = db.getRows();

    assertEquals((int)((Math.round(size.x / interval.x) + 1) * (Math.round(size.y / interval.y) + 1)), rows.size());

    // check that all the rows have the expected value and the x/y coords are multiples of the interval
    for (Row row: rows) {
      assertEquals(row.xy.y * 0.9, row.value);
      assertSimilar (row.xy.x, domain.min.x + (Math.round((row.xy.x - domain.min.x) / interval.x) * interval.x));
      assertSimilar (row.xy.y, domain.min.y + (Math.round((row.xy.y - domain.min.y) / interval.y) * interval.y));
    }
  }

  @Test
  public void testFillVariesWithXY() {
    Domain domain = new Domain (-180, 175, -90, 90);
    Tuple size = domain.size();
    Tuple interval = VEC (5, 5);
    Rows db = Rows.fromFill(domain, interval, xy -> (xy.y * 0.9) + (((1 + Math.cos(Math.toRadians(xy.x))) * 0.5) * xy.y * 0.05));

    List<Row> rows = db.getRows();

    assertEquals((int)((Math.round(size.x / interval.x) + 1) * (Math.round(size.y / interval.y) + 1)), rows.size());

    // check that all the rows have the expected value and the x/y coords are multiples of the interval
    for (Row row: rows) {
      assertEquals((row.xy.y * 0.9) + (((1 + Math.cos(Math.toRadians(row.xy.x))) * 0.5) * row.xy.y * 0.05), row.value);
      assertSimilar (row.xy.x, domain.min.x + (Math.round((row.xy.x - domain.min.x) / interval.x) * interval.x));
      assertSimilar (row.xy.y, domain.min.y + (Math.round((row.xy.y - domain.min.y) / interval.y) * interval.y));
    }
  }
}
