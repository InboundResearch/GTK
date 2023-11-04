package us.irdev.gtk.io;

import java.util.ArrayList;
import java.util.List;

/**
 * a reader for a delimited table format, like CSV, or tab delimited... the result is an
 * array-of-arrays representing the data within columns. a separate process can take the first row
 * of input as the column names or use a given set of column names, and convert this to an array of
 * map objects. comment lines are allowed outside an entry.
 */
public class Delimited extends Parsed {
  private final char delimiter;
  private final char[] bareValueStopChars;
  private final char comment;

  private static final char[] COMMENT_STOP_CHARS = sortString ("\n");
  private static final char[] QUOTED_STRING_STOP_CHARS = sortString ("\"");

  /**
   * constructor
   * @param input the input text to be parsed and extracted from a delimited format
   * @param delimiter the character used to delimit fields in the row format (e.g. ',' or '\t')
   * @param comment the character used to precede a comment line
   */
  public Delimited (String input, char delimiter, char comment) {
    super (input, false);
    this.delimiter = delimiter;
    this.bareValueStopChars = sortString("\n" + delimiter);
    this.comment = comment;
  }

  /**
   * a simple helper for getting a Comma-Separated Values (CSV) parser
   * @param input the input to be parsed and extracted from a delimited format
   * @return a new Delimited parser object
   */
  public static Delimited csv (String input) {
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

  /**
   * read the entire contents of the input, breaking it into rows of strings according to the
   * supplied delimiters
   * @return a list of lists of string values
   */
  public List<List<String>> readArray () {
    List<List<String>> array = new ArrayList<>();
    List<String> line;
    while ((line = readLine()) != null) {
      array.add (line);
    }
    return array.isEmpty () ? null : array;
  }

  /**
   * helper function to do it all in one go
   * @param input the input text to be parsed and extracted from Comma-Separated Values (CSV) format
   * @return a list of lists of string values
   */
  public static List<List<String>> readCsv (String input) {
    return csv (input).readArray();
  }
}
