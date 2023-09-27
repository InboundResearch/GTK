package us.irdev.gtk.reader;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class DelimitedTest {
  @Test
  public void testDelimited () {
    String input = Utility.slurpFile ("data/sample.csv", StandardCharsets.UTF_8);
    assertNotEquals(null, input);
    Delimited delimitedReader = new Delimited (input, ',', '#');
    List<List<String>> array = delimitedReader.readArray ();

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
}
