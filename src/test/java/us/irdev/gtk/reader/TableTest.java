package us.irdev.gtk.reader;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static us.irdev.gtk.reader.Utility.slurpFile;

public class TableTest {
  @Test
  public void testFormatReaderDelimited () {
    String input = slurpFile ("data/sample.csv", StandardCharsets.UTF_8);
    assertNotEquals(null, input);
    Table tableReader = new Table (input, ',', '#');
    List<Map<String, String>> table = tableReader.readTable ();

    // make sure the test has the right number of lines (accounting for comments, etc.)
    assertEquals (8, table.size());

    // make sure a particular element is the correct string
    Map<String, String> row7 = table.get (6);
    String entry3 = row7.get ("mag lon (deg)");
    assertEquals ("16.967403411865234\\\"xxx\\\"", entry3);

    // make sure an empty comma row has no entries
    Map<String, String> row8 = table.get (7);
    assertEquals (0, row8.size());

  }
}
