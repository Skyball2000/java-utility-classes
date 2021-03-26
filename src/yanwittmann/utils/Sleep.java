package yanwittmann.utils;

import java.util.concurrent.TimeUnit;

/**
 * Let the thread sleep for a given amount of time.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 */
public abstract class Sleep {
    public static void milliseconds(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (Exception ignored) {
        }
    }

    public static void seconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (Exception ignored) {
        }
    }

    public static void minutes(int minutes) {
        try {
            TimeUnit.MINUTES.sleep(minutes);
        } catch (Exception ignored) {
        }
    }

    public static void hours(int hours) {
        try {
            TimeUnit.HOURS.sleep(hours);
        } catch (Exception ignored) {
        }
    }
}

