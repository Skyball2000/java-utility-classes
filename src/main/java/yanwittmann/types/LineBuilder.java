package yanwittmann.types;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Works just as a StringBuilder, but every time you append to the LineBuilder, a new line is created.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 * @author Yan Wittmann<br>
 * @see StringBuilder StringBuilder
 */
public class LineBuilder {
    private final StringBuilder stringBuilder = new StringBuilder();
    private String linebreakSymbol = "\n";
    private final static String LINEBREAK = "LBEOL";

    public LineBuilder(String[] string) {
        append(string);
    }

    public LineBuilder(Object value) {
        appendAny(value);
    }

    public LineBuilder() {
    }

    public LineBuilder append(String string) {
        return appendAny(string);
    }

    public LineBuilder append(String[] string) {
        Arrays.stream(string).forEach(this::append);
        return this;
    }

    public LineBuilder append(int integer) {
        appendAny(integer);
        return this;
    }

    public LineBuilder appendAny(Object value) {
        if (stringBuilder.length() > 0) stringBuilder.append(LINEBREAK);
        stringBuilder.append(value.toString());
        return this;
    }

    public String toString() {
        return stringBuilder.toString().replace(LINEBREAK, linebreakSymbol);
    }

    public String[] toLines() {
        return stringBuilder.toString().split(LINEBREAK);
    }

    public int length() {
        return stringBuilder.length();
    }

    public int lines() {
        if (stringBuilder.length() == 0) return 0;
        return 1 + countOccurrences(stringBuilder.toString(), LINEBREAK);
    }

    public void setLinebreakSymbol(String linebreakSymbol) {
        this.linebreakSymbol = linebreakSymbol;
    }

    private int countOccurrences(String text, String find) {
        return (text.length() - text.replace(find, "").length()) / find.length();
    }

    public Stream<String> stream() {
        return Stream.of(toLines());
    }
}
