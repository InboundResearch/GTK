package us.irdev.gtk.io;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParsedTest {
  @Test
  void testSortString() {
    char[] sorted = Parsed.sortString("cba");
    assertEquals (3, sorted.length);
    assertEquals ('a', sorted[0]);
    assertEquals ('b', sorted[1]);
    assertEquals ('c', sorted[2]);
  }
}
