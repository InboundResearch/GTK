package us.irdev.gtk.reader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static us.irdev.gtk.reader.Parsed.sortString;

public class ParsedTest {
  @Test
  void testSortString() {
    char[] sorted = sortString("cba");
    assertEquals (3, sorted.length);
    assertEquals ('a', sorted[0]);
    assertEquals ('b', sorted[1]);
    assertEquals ('c', sorted[2]);
  }
}
