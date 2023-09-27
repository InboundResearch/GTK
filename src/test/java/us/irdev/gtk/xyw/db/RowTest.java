package us.irdev.gtk.xyw.db;

import org.junit.jupiter.api.Test;

import static us.irdev.gtk.xyw.Helper.assertSimilar;
import static us.irdev.gtk.xyw.Tuple.PT;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RowTest {
  @Test
  public void testRow() {
    Row row = new Row (1.5, 3.65, 36.5);
    assertSimilar(PT (1.5, 3.65), row.xy);
    assertSimilar(36.5, row.value);
    assertEquals("1.500000,3.650000,36.500000", row.toString());
  }
}
