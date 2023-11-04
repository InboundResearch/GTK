package us.irdev.gtk.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * helper function for reading and writing text files without all the boilerplate.
 */
public class Utility {
  /**
   * read a text file into a string, echoing functions in other languages with similar functionality
   * @param path file system dependent path to the file
   * @param encoding encoding, but... usually UTF8
   * @return a string with the contents of the file
   * @throws IOException does not intercept any exceptions thrown by the underlying calls
   */
  public static String slurpFileThrows (String path, Charset encoding) throws IOException {
      return new String(Files.readAllBytes(Paths.get(path)), encoding);
  }

  /**
   * read a text file into a string, echoing functions in other languages with similar functionality
   * @param path file system dependent path to the file
   * @param encoding encoding, but... usually UTF8
   * @return a string with the contents of the file or null if an error occurred
   */
  public static String slurpFile (String path, Charset encoding) {
    try {
      return slurpFileThrows(path, encoding);
    }
    catch (IOException ignored) {
    }
    return null;
  }

  /**
   * read a text file in UTF8 encoding into a string, echoing functions in other languages with
   * similar functionality
   * @param path file system dependent path to the file
   * @return a string with the contents of the file or null if an error occurred
   */
  public static String slurpFile (String path) {
    return slurpFile(path, StandardCharsets.UTF_8);
  }

  /**
   * write a text file from a string
   * @return true if the file was successfully written
   * @param path the filesystem-dependent path to save the file to
   * @param text the content top save to a file
   * @param encoding the encoding to use when converting the text to bytes
   */
  public static boolean writeFile (String path, String text, Charset encoding) {
    try {
      var targetPath = Paths.get (path);
      Files.writeString (targetPath, text, encoding, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
      return true;
    }
    catch (IOException ignored) {
    }
    return false;
  }

  /**
   * write a text file from a string
   * @return true if the file was successfully written
   * @param path the filesystem-dependent path to save the file to
   * @param text the content top save to a file
   */
  public static boolean writeFile (String path, String text) {
    return writeFile (path, text, StandardCharsets.UTF_8);
  }
}
