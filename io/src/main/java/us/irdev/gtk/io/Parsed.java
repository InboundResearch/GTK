package us.irdev.gtk.io;

import java.util.Arrays;

/**
 * a base class reader for any parsed format (it uses the call stack as an implicit state machine).
 * this class tracks the offset in a file and provides meaningful error messages. this class is not
 * meant to be exposed directly to the public, as other types of parsers are meant to be built on
 * top of it.
 */
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

    /**
     * constructor
     * @param input the text to be parsed
     * @param newLineIsWhitespace indicating whether newline characters are to be treated as general
     *                           whitespace. formats that treat newlines as a row delimiter, for
     *                            instance, will want code that skips whitespace to leave newlines
     *                            intact.
     */
    public Parsed (String input, boolean newLineIsWhitespace) {
        assert (input != null);

        // we don't believe there is a non-legacy case where a windows-formatted text needs to
        // preserve the carriage return ('\r') characters, and this vestigial artifact of old
        // computer days significantly complicates line parsing - we wholesale replace CRLF pairs
        // with LF to keep it simple, and convert that to a char array for faster indexing.
        this.input = input.replace("\r\n", "\n").toCharArray();
        inputLength = this.input.length;

        // initialize the operating parameters
        index = 0;
        lineNumber = 1;
        lastLineIndex = 0;
        this.whitespaceChars = sortString(newLineIsWhitespace ? WHITESPACE_CHARS + NEW_LINE : WHITESPACE_CHARS);
    }

    // ---------------------------------------------------------------------------------------------
    // char set operations
    // ---------------------------------------------------------------------------------------------
    protected static char[] sortString (String string) {
        char[] chars = string.toCharArray ();
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
        int start = index;
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
        int start = index;
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
            int lineEnd = index;
            while ((lineEnd < inputLength) && (input[lineEnd] != NEW_LINE)) {
                ++lineEnd;
            }
            System.err.println (Arrays.copyOfRange(input, lastLineIndex, lineEnd));

            // build the error message, by computing a carat line, and adding the error message to it
            int errorIndex = index - lastLineIndex;
            char[] caratChars = new char[errorIndex + 2];
            Arrays.fill (caratChars, ' ');
            caratChars[errorIndex] = '^';
            String carat = new String (caratChars) + errorMessage;

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
            int start = consumeUntil (stopChars, true);
            result = new String(Arrays.copyOfRange(input, start, index++));
        }
        return result;
    }

    protected String readBareValueUntil (char[] stopChars) {
        // " chars " | <chars>
        String result = null;
        int start = consumeUntil (stopChars, true);

        // captureInput the result if we actually consumed some characters
        if (index > start) {
            result = new String(Arrays.copyOfRange(input, start, index));
        }

        return result;
    }
}
