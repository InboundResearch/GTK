package us.irdev.gtk.xyw;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Assertions {
  public static void assertSimilar (Tuple expect, Tuple actual) {
    assertTrue(Tuple.similar (expect, actual));
  }

  public static void assertSimilar (double expect, double actual) {
    assertTrue(Numerics.similar (expect, actual));
  }

  public static void assertSimilar (double expect, double actual, double enough) {
    assertTrue(Numerics.similar (expect, actual, enough));
  }

  public static void assertSimilar (Segment expect, Segment actual) {
    assertTrue(Segment.similar (expect, actual));
  }

  public static void assertSimilar (Domain expect, Domain actual) {
    assertTrue(Domain.similar (expect, actual));
  }

  public static void assertNotSimilar (Tuple expect, Tuple actual) {
    assertFalse(Tuple.similar (expect, actual));
  }

  public static void assertNotSimilar (double expect, double actual) {
    assertFalse(Numerics.similar (expect, actual));
  }

  public static void assertNotSimilar (double expect, double actual, double enough) {
    assertFalse(Numerics.similar (expect, actual, enough));
  }

  public static void assertNotSimilar (Segment expect, Segment actual) {
    assertFalse(Segment.similar (expect, actual));
  }

  public static void assertNotSimilar (Domain expect, Domain actual) {
    assertFalse(Domain.similar (expect, actual));
  }

}
