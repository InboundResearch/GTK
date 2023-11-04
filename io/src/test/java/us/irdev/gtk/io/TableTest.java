package us.irdev.gtk.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TableTest {
  @Test
  public void testFormatReaderDelimited () {
    assertDoesNotThrow(() -> {
      String input = Utility.slurpFile (Paths.get("data", "sample.csv").toString(), StandardCharsets.UTF_8);
      assertNotEquals(null, input);
      Table tableReader = new Table (input, ',', '#');
      List<Map<String, String>> table;
      table = tableReader.readTable ();

      // make sure the test has the right number of lines (accounting for comments, empty rows, etc.)
      assertEquals (7, table.size());

      // make sure every row has some values
      for (Map<String, String> row : table) {
        assertFalse(row.isEmpty ());
        for (String key: row.keySet()) {
          assertNotNull(row.get(key));
          assert(!row.get (key).isEmpty ());
        }
      }

      // make sure a particular element is the correct string
      Map<String, String> row7 = table.get (6);
      String entry3 = row7.get ("mag lon (deg)");
      assertEquals ("16.967403411865234\\\"xxx\\\"", entry3);
    });
  }

  @Test
  public void testBadFormat () {
    assertThrows(IOException.class, () -> {
      String input = Utility.slurpFile (Paths.get("data", "testWithIncorrectHeader.txt").toString(), StandardCharsets.UTF_8);
      assertNotEquals(null, input);
      Table tableReader = new Table (input, ',', '#');
      List<Map<String, String>> table = tableReader.readTable ();
    });
  }
}
