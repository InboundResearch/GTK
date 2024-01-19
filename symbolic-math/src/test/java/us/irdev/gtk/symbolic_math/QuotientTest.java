package us.irdev.gtk.symbolic_math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuotientTest {
  @Test
  public void testBasic() {
    Expression x = Variable.make ("x");
    Expression c = Constant.make (3);
    Expression q = Quotient.make(x, c);
    assertEquals("(x/3)", q.toString());
  }
}
