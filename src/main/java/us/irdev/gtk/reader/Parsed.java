package us.irdev.gtk.reader;

import java.util.Arrays;

// a base class reader for any parsed format (uses call stack as an implicit state machine). this
// class tracks the offset in a file and provides meaningful error messages
public class Parsed {
    protected final char[] input;

    protected int index;
    protected int inputLength;
    protected int lineNumber;
    protected int lastLineIndex;
    protected boolean error;

    protected final char[] whitespaceChars;

    protected static final String WHITESPACE_CHARS = " \u00a0\t";
    protected static final char NEW_LINE = '\n';

    public Parsed (String input, boolean newLineIsWhitespace) {
        assert (input != null);
        this.input = input.toCharArray ();
        inputLength = this.input.length;
        index = 0;
        lineNumber = 1;
        lastLineIndex = 0;
        this.whitespaceChars = sortString(newLineIsWhitespace ? WHITESPACE_CHARS + NEW_LINE : WHITESPACE_CHARS);
    }

    // ---------------------------------------------------------------------------------------------
    // char set operations
    // ---------------------------------------------------------------------------------------------
    protected static char[] sortString (String string) {
        var chars = string.toCharArray ();
        Arrays.sort (chars);
        return chars;
    }

    protected boolean notIn (char[] inChars, char c) {
        return Arrays.binarySearch(inChars, c) < 0;
    }

    protected boolean in (char[] inChars, char c) {
        return Arrays.binarySearch(inChars, c) >= 0;
    }

    // ---------------------------------------------------------------------------------------------
    // parsing helpers
    // ---------------------------------------------------------------------------------------------
    protected boolean check () {
        return (! error) && (index < inputLength);
    }

    protected boolean inspectForNewLine(char c) {
        if (c == NEW_LINE) {
            ++lineNumber;
            lastLineIndex = index;
            return true;
        }
        return false;
    }

    protected int consumeWhile (char[] inChars, boolean allowEscape) {
        var start = index;
        char c;
        while (check () && in (inChars, c = input[index])) {
            inspectForNewLine(c);

            // using the escape mechanism is like a free pass for the next character, but we don't
            // do any transformation on the substring, just return it as written after checking for
            // newlines
            if ((c == '\\') && allowEscape) {
                ++index;
                inspectForNewLine(input[index]);
            }

            // consume the character
            ++index;
        }
        return start;
    }

    protected void consumeWhitespace () {
        consumeWhile (whitespaceChars, false);
    }

    protected int consumeUntil (char[] stopChars, boolean allowEscape) {
        var start = index;
        char c;
        while (check () && notIn (stopChars, c = input[index])) {
            inspectForNewLine(c);

            // using the escape mechanism is like a free pass for the next character, but we don't
            // do any transformation on the substring, just return it as written after checking for
            // newlines
            if ((c == '\\') && allowEscape) {
                ++index;
                inspectForNewLine(input[index]);
            }

            // consume the character
            ++index;
        }
        return start;
    }

    protected boolean expect(char c) {
        consumeWhitespace();

        // the next character should be the one we expect
        if (check() && (input[index] == c)) {
            inspectForNewLine(c);
            ++index;
            return true;
        }
        return false;
    }

    // ---------------------------------------------------------------------------------------------
    // error reporting
    // ---------------------------------------------------------------------------------------------
    protected void onReadError (String errorMessage) {

        // log the messages, we only need to output the line if this is the first time the error is
        // being reported
        if (! error) {
            // say where the error is
            System.err.println ("Error while parsing input on line " + lineNumber + ", near: ");
            // find the end of the current line. note: line endings could only be '\n' because the
            // input reader consumed the actual line endings for us and replaced them with '\n'
            var lineEnd = index;
            while ((lineEnd < inputLength) && (input[lineEnd] != NEW_LINE)) {
                ++lineEnd;
            }
            System.err.println (Arrays.copyOfRange(input, lastLineIndex, lineEnd));

            // build the error message, by computing a carat line, and adding the error message to it
            var errorIndex = index - lastLineIndex;
            var caratChars = new char[errorIndex + 2];
            Arrays.fill (caratChars, ' ');
            caratChars[errorIndex] = '^';
            var carat = new String (caratChars) + errorMessage;

            System.err.println (carat);

            // set the error state
            error = true;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // composite reader functions
    // ---------------------------------------------------------------------------------------------
    protected String readString (char[] stopChars) {
        // " chars " | <chars>
        String result = null;
        if (expect('"')) {
            // digest the string, and be sure to eat the end quote
            var start = consumeUntil (stopChars, true);
            result = new String(Arrays.copyOfRange(input, start, index++));
        }
        return result;
    }

    protected String readBareValueUntil (char[] stopChars) {
        // " chars " | <chars>
        String result = null;
        var start = consumeUntil (stopChars, true);

        // captureInput the result if we actually consumed some characters
        if (index > start) {
            result = new String(Arrays.copyOfRange(input, start, index));
        }

        return result;
    }
}
