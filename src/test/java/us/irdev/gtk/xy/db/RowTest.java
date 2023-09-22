package us.irdev.gtk.xy.db;

import org.junit.jupiter.api.Test;
import us.irdev.gtk.xy.Tuple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static us.irdev.gtk.xy.Helper.assertSimilar;

public class RowTest {
  @Test
  public void testRow() {
    Row row = new Row (1.5, 3.65, 36.5);
    assertSimilar(new Tuple (1.5, 3.65), row.xy);
    assertSimilar(36.5, row.value);
    assertEquals("1.500000,3.650000,36.500000", row.toString());
  }
}
