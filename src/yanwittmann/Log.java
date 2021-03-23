package yanwittmann;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * Logs strings and objects. You can dump or print the log. You can add and remove indentations<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 */
public abstract class Log {
    private static final ArrayList<String> log = new ArrayList<>();
    private static boolean activated = false;
    private static boolean print = true;
    private static int indent = 0;

    public static void add(String text) {
        if (activated) {
            StringBuilder textBuilder = new StringBuilder(text);
            for (int i = 0; i < indent; i++) textBuilder.insert(0, " ");
            text = textBuilder.toString();
            if(print) System.out.println(text);
            log.add(text);
        }
    }

    public static void add(Object object) {
        StringBuilder text = new StringBuilder("" + object);
        if (activated) {
            for (int i = 0; i < indent; i++) text.insert(0, " ");
            if(print) System.out.println(text);
            log.add(text.toString());
        }
    }

    public static void debug(String text) {
        text = "D: " + text;
        if(print) System.out.println(text);
        log.add(text);
    }

    public static void debug(Object object) {
        String text = "D: " + object.toString();
        if(print) System.out.println(text);
        log.add(text);
    }

    public static void dump(String filename) throws IOException {
        if (filename.equals(""))
            filename = String.valueOf(new Timestamp(new Date().getTime())).replace(":", "-") + ".txt";
        else filename = filename + (filename.contains(".txt") ? "" : ".txt");
        add("Dumping log to: " + filename);
        String[] logLines = new String[log.size()];
        for (int i = 0; i < logLines.length; i++) logLines[i] = log.get(i);
        FileUtils.writeFile(new File(filename), logLines);
    }

    public static void activate() {
        activated = true;
    }

    public static void deactivate() {
        activated = false;
    }

    public static void setActive(boolean active) {
        activated = active;
    }

    public static boolean isActive() {
        return activated;
    }

    public static void setPrint(boolean active) {
        print = active;
    }

    public static boolean isPrinting() {
        return print;
    }

    public static void addIndent() {
        indent++;
    }

    public static void removeIndent() {
        if (indent > 0) indent--;
    }

    public static void resetIndent() {
        indent = 0;
    }
}
