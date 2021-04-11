package yanwittmann.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.UnaryOperator;

/**
 * A simple logging class with five severity levels and the option to record and dump the log.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 * @author Yan Wittmann
 */
public abstract class Log {

    private final static DumpedLog log = new DumpedLog();
    private static boolean record = false;

    private final static String HEADER_INFO = "[INFO] ";
    private final static String HEADER_ERROR = "[ERROR] ";
    private final static String HEADER_WARNING = "[WARNING] ";
    private final static String HEADER_DEBUG = "[DEBUG] ";
    private final static String HEADER_FATAL = "[FATAL] ";

    public static void info(Object object) {
        System.out.println(prepare(object, HEADER_INFO));
    }

    public static void debug(Object object) {
        System.out.println(prepare(object, HEADER_DEBUG));
    }

    public static void error(Object object) {
        System.out.println(prepare(object, HEADER_ERROR));
    }

    public static void warning(Object object) {
        System.out.println(prepare(object, HEADER_WARNING));
    }

    public static void fatal(Object object) {
        System.out.println(prepare(object, HEADER_FATAL));
    }

    public static void none(Object object) {
        System.out.println(prepare(object, ""));
    }

    public static void info(Object object, Object... inserts) {
        System.out.println(prepare(object, HEADER_INFO, inserts));
    }

    public static void debug(Object object, Object... inserts) {
        System.out.println(prepare(object, HEADER_DEBUG, inserts));
    }

    public static void error(Object object, Object... inserts) {
        System.out.println(prepare(object, HEADER_ERROR, inserts));
    }

    public static void warning(Object object, Object... inserts) {
        System.out.println(prepare(object, HEADER_WARNING, inserts));
    }

    public static void fatal(Object object, Object... inserts) {
        System.out.println(prepare(object, HEADER_FATAL, inserts));
    }

    public static void none(Object object, Object... inserts) {
        System.out.println(prepare(object, "", inserts));
    }

    private static String prepare(Object object, String header) {
        String message = header + object.toString();
        if (record) log.add(message);
        return message;
    }

    private static String prepare(Object object, String header, Object... inserts) {
        String message = object.toString();
        for (Object insert : inserts) message = message.replaceFirst("\\{}", insert + "");
        message = header + message;
        if (record) log.add(message);
        return message;
    }

    public static void record() {
        record = true;
    }

    public static void stopRecording() {
        record = false;
    }

    /**
     * Requires record() to be called first.
     *
     * @return The dumped log that can be accessed just like a List.
     */
    public static DumpedLog dump() {
        return log;
    }

    private static class DumpedLog extends ArrayList<String> {
        @Override
        public String remove(int index) {
            return "";
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public void clear() {
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public void replaceAll(UnaryOperator<String> operator) {
        }
    }
}
