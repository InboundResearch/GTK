package us.irdev.gtk.io;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DelimitedTest {
  @Test
  public void testDelimited () {
    String input = Utility.slurpFile (Paths.get("data", "sample.csv").toString(), StandardCharsets.UTF_8);
    assertNotNull(input);
    Delimited delimitedReader = new Delimited (input, ',', '#');
    List<List<String>> array = delimitedReader.readArray ();
    assertNotNull (array);

    // make sure the test has the right number of lines (accounting for comments, etc.)
    assertEquals (9, array.size());

    // make sure a particular element is the correct string
    List<String> row7 = array.get (7);
    String entry3 = row7.get (3);
    assertEquals ("16.967403411865234\\\"xxx\\\"", entry3);

    // make sure an empty comma row has 4 entries
    List<String> row8 = array.get (8);
    assertEquals (4, row8.size());
  }

  @Test
  public void testWithCRLF () {
    String input = Utility.slurpFile (Paths.get("data", "testWithCRLF.csv").toString(), StandardCharsets.UTF_8);
    assertNotNull(input);
    Delimited delimitedReader = new Delimited (input, ',', '#');
    List<List<String>> array = delimitedReader.readArray ();
    assertNotNull (array);

    // make sure no element in any row has a '\r' in it.
    for (List<String> entry: array) {
      for (String value: entry) {
        assertFalse(value.contains ("\r"));
      }
    }
  }
}
