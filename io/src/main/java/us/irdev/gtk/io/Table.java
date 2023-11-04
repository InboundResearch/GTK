package us.irdev.gtk.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * a wrapper class to take a delimited file and convert it to an array of maps. the user may supply
 * an array of column names, or the reader will extract the column names from the first row of the
 * data.
 */
public class Table {
  private final Delimited delimited;
  private final String[] titlesSrc;

  /**
   * constructor
   * @param input text to be parsed by a delimited reader
   * @param delimiter the character used to delimit fields in the row format (e.g. ',' or '\t')
   * @param comment the character used to precede a comment line
   * @param titlesSrc optional list of column names to be used for building maps. if this parameter
   *                  is null, the Table class will try to use the first row of the delimited data
   *                  as a list of column names.
   */
  public Table (String input, char delimiter, char comment, String[] titlesSrc) {
    delimited = new Delimited (input, delimiter, comment);
    this.titlesSrc = titlesSrc;
  }

  /**
   * constructor - the Table class will try to use the first row of the delimited data as a list of
   * column names.
   * @param input text to be parsed by a delimited reader
   * @param delimiter the character used to delimit fields in the row format (e.g. ',' or '\t')
   * @param comment the character used to precede a comment line
   */
  public Table (String input, char delimiter, char comment) {
    this (input, delimiter, comment, null);
  }

  private void validateTitles(String[] titles, boolean validateTitles) throws IOException {
    if (validateTitles) {
      // sanity check the titles
      int validTitleCount = 0;
      for (String title : titles) {
        if (title.matches (".*[a-zA-Z].*")) {
          ++validTitleCount;
        }
      }

      // a preponderance of the column names should have word characters in them, don't you think?
      if ((validTitleCount / (double) titles.length) < 0.5) {
        // report the error message
        StringBuilder sb = new StringBuilder ()
                .append ("The titles appear to be invalid, at least half of them should include word chars: \n    ");
        String separator = "";
        for (String title : titles) {
          sb.append (separator).append (title);
          separator = ", ";
        }
        sb.append ("\nCheck the first row of the input file to be sure the column names are correct.");
        throw new IOException (sb.toString ());
      }
    }
  }

  /**
   * convert the input to a list of maps (a table of records)
   * @param validateTitles if true, and the user did not supply a list of column titles, the Table
   *                       will atempt to validate that the column names have a preponderance of
   *                       actual text in their name. the idea is to detect when a row of data is
   *                       being confused as the column titles. if the column titles are correct,
   *                       but consist of numeric names ("1", "2", etc.), then pass false to skip
   *                       validation.
   * @return the list of maps - empty rows were skipped, and empty fields were not added to the maps
   * so users should check for null when fetching values from the resulting map, rather than assume
   * all columns are present.
   */
  public List<Map<String, String>> readTable (boolean validateTitles) throws IOException {
    List<Map<String, String>> result = new ArrayList<> ();
    List<List<String>> array = delimited.readArray();
    if (array != null) {
      // gather the strings for the titles, depending on whether they were already supplied...
      final String[] titles;
      if (titlesSrc != null) {
        titles = titlesSrc;
      } else {
        // get the first data row as the titles and validate that...
        titles = array.get (0).toArray (new String[0]);
        validateTitles (titles, validateTitles);

        // trim the first row off the table since we used it for something.
        array = array.subList (1, array.size ());
      }

      // convert the array to a database format, looping over every record
      for (List<String> record : array) {
        // XXX what should we do if there aren't enough entries in the record to cover the titles?
        //assert(record.size() >= titles.length);

        // create the map for the record
        Map<String, String> map = new HashMap<> ();
        for (int i = 0, end = titles.length; i < end; ++i) {
          if (i < record.size ()) {
            // add the entry if it's not empty. this is primarily to save space when no value was
            // specified (like so: ,,,), rather than add a whole bunch of empty strings
            String entry = record.get (i);
            if (entry != null) {
              map.put (titles[i], entry);
            }
          }
        }

        // add the record if it's not empty, there's just no point in capturing an empty record
        if (!map.isEmpty ()) {
          result.add (map);
        }
      }
    }
    // return the mapped result
    return (result.isEmpty ()) ? null : result;
  }

  /**
   * helper function to convert the input to a list of maps (a table of records), with validation by
   * default. see above for more explanation.
   * @return the list of maps - empty rows were skipped, and empty fields were not added to the maps
   * so users should check for null when fetching values from the resulting map, rather than assume
   * all columns are present.
   */
  public List<Map<String, String>> readTable () throws IOException {
    return readTable(true);
  }

}
