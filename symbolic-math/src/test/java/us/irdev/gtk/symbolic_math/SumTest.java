package us.irdev.gtk.symbolic_math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SumTest {
  @Test
  public void testBasic() {
    Expression x = Variable.make ("x");
    Expression c = Constant.make (3);
    Expression s = Sum.make(x, c);
    assertEquals("(x+3)", s.toString());
  }

}
