package us.irdev.gtk.symbolic_math;

import us.irdev.gtk.io.Parsed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Reader extends Parsed {
  private static final Logger log = LogManager.getLogger(Reader.class);

  private Reader (String input) {
    super (input, true);
  }

  private Double tryParseDouble(String stringValue) {
    try {
      return Double.parseDouble (stringValue);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private static final char[] operatorChars = sortString("+-*/^");
  private static final char[] expressionStopChars = sortString(")+-*/^ ");

  private static interface OperatorBuilder {
    Expression make (List<Expression> exprs);
  }

  private static final Map<String, OperatorBuilder> operatorFactory;

  static {
    operatorFactory = new HashMap<> ();
    operatorFactory.put("+", Sum::make);
    operatorFactory.put("-", Difference::make);
    operatorFactory.put("*", Product::make);
    operatorFactory.put("/", exprs -> Quotient.make(exprs.get(0), exprs.get(1)));
    operatorFactory.put("^", exprs -> Power.make(exprs.get(0), exprs.get(1)));
  }

  public Expression readExpression() {
    // eat up white space
    consumeWhitespace();

    // look for either a parenthesis or a bare expression
    if (expect('(')) {
      // read the first expression
      Expression left = readExpression();
      if (left != null) {
        // set up to capture the first operator. we support multiple operands, but the operator must
        // be the same for every one.
        String operator = null;

        // save the first operand into a list
        List<Expression> operands = new ArrayList<>();
        operands.add (left);

        // loop, reading an operator and operand pair until we reach the end of the of list
        do {
          // read the operator
          int start = consumeWhile(operatorChars, false);
          String nextOperator = new String (Arrays.copyOfRange (input, start, index));

          // capture the operator
          if ((operator == null) || (nextOperator.equals(operator))) {
            operator = nextOperator;
            Expression right = readExpression ();
            if (right != null) {
              operands.add(right);
            } else {
              return null;
            }
          } else {
            // this is an error condition
            onReadError("Invalid operator: '" + nextOperator + "'");
            return null;
          }
        } while (!expect(')'));

        // convert the read list of operands into an expression
        return operatorFactory.get(operator).make(operands);
      } else {
        return null;
      }
    } else {
      // a number might start with a negative sign, but that's not meant to be interpreted as a stop
      // char, so we read it first...
      boolean negative = expect ('-');

      String stringValue = readBareValueUntil(expressionStopChars);
      if (stringValue != null) {

        // try to parse a number
        Double doubleValue = tryParseDouble (stringValue);
        if (doubleValue != null) {
          return Constant.make (negative ? -doubleValue : doubleValue);
        }

        // try to read it as a named constant, then default to a variable
        Expression namedConstant = NamedConstant.get (stringValue);
        return (namedConstant != null) ? namedConstant : Variable.make (stringValue);
      } else {
        onReadError ("expected an expression");
      }
    }
    return null;
  }

  public static Expression fromString (String string) {
    return new Reader (string).readExpression();
  }
}
