package us.irdev.gtk.symbolic_math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DifferenceTest {
  @Test
  public void testBasic() {
    Expression x = Variable.make ("x");
    Expression c = Constant.make (3);
    Expression d = Difference.make(x, c);
    assertEquals("(x-3)", d.toString());
  }

}
