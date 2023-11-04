package us.irdev.gtk.xyw.db;

import org.junit.jupiter.api.Test;
import us.irdev.gtk.xyw.Domain;
import us.irdev.gtk.xyw.Tuple;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static us.irdev.gtk.xyw.Helper.*;
import static us.irdev.gtk.xyw.Tuple.PT;
import static us.irdev.gtk.xyw.Tuple.VEC;

public class RowsTest {
  @Test
  public void testFillConstant() {
    var domain = new  Domain (-180, 175, -90, 90);
    var size = domain.size();
    var interval = PT(5, 5);
    var db = Rows.fromFxy (domain, interval, xy -> 36.5);

    assertEquals((int)((Math.round(size.x / interval.x) + 1) * (Math.round(size.y / interval.y) + 1)), db.rows.size());

    // check that all the rows have the expected value and the x/y coords are multiples of the interval
    for (var row: db.rows) {
      assertEquals(36.5, row.value);
      assertSimilar (row.xy.x, domain.min.x + (Math.round((row.xy.x - domain.min.x) / interval.x) * interval.x));
      assertSimilar (row.xy.y, domain.min.y + (Math.round((row.xy.y - domain.min.y) / interval.y) * interval.y));
    }
  }

  @Test
  public void testFillVariesWithY() {
    var domain = new  Domain (-180, 175, -90, 90);
    var size = domain.size();
    var interval = Tuple.VEC (5, 5);
    var db = Rows.fromFxy (domain, interval, xy -> xy.y * 0.9);

    assertEquals((int)((Math.round(size.x / interval.x) + 1) * (Math.round(size.y / interval.y) + 1)), db.rows.size());

    // check that all the rows have the expected value and the x/y coords are multiples of the interval
    for (var row: db.rows) {
      assertEquals(row.xy.y * 0.9, row.value);
      assertSimilar (row.xy.x, domain.min.x + (Math.round((row.xy.x - domain.min.x) / interval.x) * interval.x));
      assertSimilar (row.xy.y, domain.min.y + (Math.round((row.xy.y - domain.min.y) / interval.y) * interval.y));
    }
  }

  @Test
  public void testFillVariesWithXY() {
    var domain = new  Domain (-180, 175, -90, 90);
    var size = domain.size();
    var interval = Tuple.VEC (5, 5);
    var db = Rows.fromFxy (domain, interval, xy -> (xy.y * 0.9) + (((1 + Math.cos(Math.toRadians(xy.x))) * 0.5) * xy.y * 0.05));

    assertEquals((int)((Math.round(size.x / interval.x) + 1) * (Math.round(size.y / interval.y) + 1)), db.rows.size());

    // check that all the rows have the expected value and the x/y coords are multiples of the interval
    for (var row: db.rows) {
      assertEquals((row.xy.y * 0.9) + (((1 + Math.cos(Math.toRadians(row.xy.x))) * 0.5) * row.xy.y * 0.05), row.value);
      assertSimilar (row.xy.x, domain.min.x + (Math.round((row.xy.x - domain.min.x) / interval.x) * interval.x));
      assertSimilar (row.xy.y, domain.min.y + (Math.round((row.xy.y - domain.min.y) / interval.y) * interval.y));
    }
  }
}
