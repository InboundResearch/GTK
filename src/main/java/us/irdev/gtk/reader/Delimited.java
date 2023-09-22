package us.irdev.gtk.reader;

import java.util.ArrayList;
import java.util.List;

// reads a delimited table format, like CSV, or tab delimited... the result is an array of arrays
// representing the data within columns. a separate process can take the first row of input as the
// column names or a given set of column names, and convert this to an array of map objects
// comment lines are allowed outside of an entry.
public class Delimited extends Parsed {
  private final char delimiter;
  private final char[] bareValueStopChars;
  private final char comment;

  private static final char[] COMMENT_STOP_CHARS = sortString ("\n");
  private static final char[] QUOTED_STRING_STOP_CHARS = sortString ("\"");

  protected Delimited (String input, char delimiter, char comment) {
    super (input, false);
    this.delimiter = delimiter;
    this.bareValueStopChars = sortString("\n" + delimiter);
    this.comment = comment;
  }

  public static Delimited formatReaderCSV (String input) {
    return new Delimited (input, ',', '#');
  }

  private String readEntry() {
    String entry = readString(QUOTED_STRING_STOP_CHARS);
    return (entry != null) ? entry : readBareValueUntil (bareValueStopChars);
  }

  private List<String> readLine() {
    // comment lines get eaten and discarded
    while (expect(comment)) {
      consumeUntil(COMMENT_STOP_CHARS, true);
      expect (NEW_LINE);
    }

    // marker is at the beginning of a line
    if (check ()) {
      List<String> line = new ArrayList<>();
      do {
        line.add (readEntry ());
      } while (expect(delimiter));
      expect (NEW_LINE);
      return line;
    }
    return null;
  }

  public List<List<String>> readArray () {
    List<List<String>> array = new ArrayList<>();
    List<String> line;
    while ((line = readLine()) != null) {
      array.add (line);
    }
    return array;
  }
}
