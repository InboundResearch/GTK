package us.irdev.gtk.reader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utility {
  /**
   * read a text file into a string, echoing functions in other languages with similar functionality
   * @param path - file system dependent path to the file
   * @param encoding - encoding, but... usually UTF8
   * @return a string with the contents of the file or null if an error occurred
   */
  public static String slurpFile (String path, Charset encoding) {
    try {
      byte[] encoded = Files.readAllBytes(Paths.get(path));
      return new String(encoded, encoding);
    }
    catch (IOException ignored) {
    }
    return null;
  }

  /**
   * read a text file in UTF8 encoding into a string, echoing functions in other languages with
   * similar functionality
   * @param path - file system dependent path to the file
   * @return a string with the contents of the file or null if an error occurred
   */
  public static String slurpFile (String path) {
    return slurpFile(path, StandardCharsets.UTF_8);
  }
}
