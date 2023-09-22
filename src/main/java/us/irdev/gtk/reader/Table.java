package us.irdev.gtk.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
  private final Delimited formatReader;
  private final String[] titlesSrc;

  public Table (String input, char delimiter, char comment, String[] titlesSrc) {
    formatReader = new Delimited (input, delimiter, comment);
    this.titlesSrc = titlesSrc;
  }

  public Table (String input, char delimiter, char comment) {
    this (input, delimiter, comment, null);
  }

  public List<Map<String, String>> readTable () {
    List<List<String>> array = formatReader.readArray();

    // gather the strings for the titles, if we don't already have them, the first data row in the
    // read file is it...
    final String[] titles;
    if (titlesSrc != null) {
      titles = titlesSrc;
    } else {
      titles = array.get(0).toArray(new String[0]);
      array = array.subList(1, array.size());
    }

    // convert the array to a database format, only if the field has value
    List<Map<String, String>> result = new ArrayList<> ();
    for (List<String> record: array) {
      Map<String, String> map = new HashMap<> ();
      for (int i = 0, end = titles.length; i < end; ++i) {
        if (i < record.size ()) {
          String entry = record.get (i);
          if (entry != null) {
            map.put (titles[i], entry);
          }
        }
      }
      result.add(map);
    }

    // return the mapped result
    return result;
  }
}
