package yanwittmann.utils;

import javafx.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lots of date-related functions.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 *
 * @author Yan Wittmann
 */
public class DateUtils {

    /**
     * Will parse a given string into a <code>Date</code> by creating a <code>SimpleDateFormat</code> from the format.<br>
     * The <code>SimpleDateFormat</code> locale will be set to <code>Locale.ENGLISH</code>.
     *
     * @return The parsed <code>Date</code>
     * @throws ParseException If the string could not be parsed
     */
    public static Date parseString(String stringDate, String format) throws ParseException {
        return new SimpleDateFormat(format, Locale.ENGLISH).parse(stringDate);
    }

    /**
     * Will parse a given string into a <code>Date</code> using the <code>SimpleDateFormat</code>.<br>
     *
     * @return The parsed <code>Date</code>
     * @throws ParseException If the string could not be parsed
     */
    public static Date parseString(String stringDate, SimpleDateFormat format) throws ParseException {
        return format.parse(stringDate);
    }

    /**
     * This is a powerful function that will attempt to read a string into a <code>Date</code> by guessing its format.<br>
     * Although a lot of different formats are supported, not every string is currently detected correctly.
     *
     * @param stringDate The string to parse into a <code>Date</code>
     * @return The parsed date
     * @throws ParseException If the string could not be parsed
     */
    public static Date parseString(String stringDate) throws ParseException {

        // normalize input string
        stringDate = stringDate.replaceAll(" {2,}", " ").replace("/", ":")
                .replace(".", ":").replace(",", ":")
                .replaceAll(" ?/ ?", "/").replace("- ", "-")
                .replaceAll("(?<! )-", ":").trim();

        StringBuilder parsableDate = new StringBuilder();
        StringBuilder parsableDateFormat = new StringBuilder();

        // extract day from input string
        Pair<SimpleDateFormat, String> day = extractPartFromString(stringDate, DAY_PATTERNS);
        if (day != null) {
            stringDate = stringDate.replace(day.getValue(), "").trim();
            parsableDate.append(" ").append(day.getValue());
            parsableDateFormat.append(" ").append(day.getKey().toPattern());
        }

        // extract time from input string
        Pair<SimpleDateFormat, String> time = extractPartFromString(stringDate, TIME_PATTERNS);
        if (time != null) {
            stringDate = stringDate.replace(time.getValue(), "").trim();
            parsableDate.append(" ").append(time.getValue());
            parsableDateFormat.append(" ").append(time.getKey().toPattern());
        }

        // check for known strings
        for (Pair<Pattern, String> knownString : COMMON_DATE_PATTERNS) {
            Matcher matcher = knownString.getKey().matcher(stringDate);
            if (matcher.find()) {
                stringDate = stringDate.replace(matcher.group(), "").trim();
                parsableDate.append(" ").append(matcher.group());
                parsableDateFormat.append(" ").append(knownString.getValue());
            }
        }

        // check if there is any not yet found data
        if (!parsableDateFormat.toString().contains("M")) {
            Matcher matcher = DATE_MONTH_PATTERN.matcher(stringDate);
            if (matcher.find()) {
                stringDate = stringDate.replaceFirst(matcher.group(), "").trim();
                parsableDate.append(" ").append(matcher.group());
                parsableDateFormat.append(" ").append("M");
            }
        }
        if (!parsableDateFormat.toString().contains("d") && !parsableDateFormat.toString().contains("D")) {
            Matcher matcher = DATE_DAY_PATTERN.matcher(stringDate);
            if (matcher.find()) {
                stringDate = stringDate.replaceFirst(matcher.group(), "").trim();
                parsableDate.append(" ").append(matcher.group());
                parsableDateFormat.append(" ").append("d");
            }
        }
        if (!parsableDateFormat.toString().contains("y")) {
            Matcher matcher = DATE_YEAR_PATTERN.matcher(stringDate);
            if (matcher.find()) {
                stringDate = stringDate.replaceFirst(matcher.group(), "").trim();
                parsableDate.append(" ").append(matcher.group());
                parsableDateFormat.append(" ").append("yyyy");
            }
        }

        parsableDate = new StringBuilder(parsableDate.toString().trim());
        parsableDateFormat = new StringBuilder(parsableDateFormat.toString().trim());

        return new SimpleDateFormat(parsableDateFormat.toString(), Locale.ENGLISH).parse(parsableDate.toString());

    }

    private static Pair<SimpleDateFormat, String> extractPartFromString(String stringDate, List<Pair<Pattern, SimpleDateFormat>> patterns) {
        for (Pair<Pattern, SimpleDateFormat> pattern : patterns) {
            Matcher matcher = pattern.getKey().matcher(stringDate);
            if (matcher.find()) {
                //System.out.println(pattern.getKey() + " -- " + pattern.getValue().toPattern() + " -- " + matcher.group());
                return new Pair<>(pattern.getValue(), matcher.group());
            }
        }
        return null;
    }

    private final static Pattern NEW_DATE_PART_BEGINNING_PATTERN = Pattern.compile("(?<= |^|:|T)");
    private final static Pattern DATE_DAY_PATTERN = Pattern.compile("\\d{1,2}");
    private final static Pattern DATE_MONTH_PATTERN = Pattern.compile("\\d{1,2}");
    private final static Pattern DATE_YEAR_PATTERN = Pattern.compile("\\d{4}");
    private final static Pattern DATE_SHORT_YEAR_PATTERN = Pattern.compile("\\d{2}");
    private final static Pattern DATE_AD_BC_PATTERN = Pattern.compile("(?:AD|BC)");
    private final static Pattern DATE_MONTH_STRING_PATTERN = Pattern.compile("(January|February|March|April|June|July|August|September|October|November|December|Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sept|Sep|Oct|Nov|Dec)");
    private final static Pattern DATE_DAY_OF_WEEK_STRING_PATTERN = Pattern.compile("(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday|Sun|Mon|Tues|Wed|Thurs|Thur|Thu|Th|Fri|Sat)");

    private final static Pattern DATE_HOUR_PATTERN = Pattern.compile("\\d{1,2}");
    private final static Pattern DATE_MINUTE_PATTERN = Pattern.compile("\\d{1,2}");
    private final static Pattern DATE_SECOND_PATTERN = Pattern.compile("\\d{1,2}");
    private final static Pattern DATE_MILLISECONDS_PATTERN = Pattern.compile("\\d{3}");
    private final static Pattern DATE_TIMEZONE_PATTERN = Pattern.compile("(?:[-+]\\d{1,4}|[A-Z]{3,4}|[A-Z ]+)");
    private final static Pattern DATE_SPLITTER = Pattern.compile(":");

    private final static List<Pair<Pattern, SimpleDateFormat>> TIME_PATTERNS = Arrays.asList(
            new Pair<>(Pattern.compile("" + NEW_DATE_PART_BEGINNING_PATTERN + DATE_HOUR_PATTERN + DATE_SPLITTER + DATE_MINUTE_PATTERN + DATE_SPLITTER + DATE_SECOND_PATTERN + DATE_SPLITTER + DATE_MILLISECONDS_PATTERN + " " + DATE_TIMEZONE_PATTERN), new SimpleDateFormat("HH:mm:ss:SSS zzz", Locale.ENGLISH)),
            new Pair<>(Pattern.compile("" + NEW_DATE_PART_BEGINNING_PATTERN + DATE_HOUR_PATTERN + DATE_SPLITTER + DATE_MINUTE_PATTERN + DATE_SPLITTER + DATE_SECOND_PATTERN + " " + DATE_TIMEZONE_PATTERN), new SimpleDateFormat("HH:mm:ss zzz", Locale.ENGLISH)),
            new Pair<>(Pattern.compile("" + NEW_DATE_PART_BEGINNING_PATTERN + DATE_HOUR_PATTERN + DATE_SPLITTER + DATE_MINUTE_PATTERN + DATE_SPLITTER + DATE_SECOND_PATTERN), new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH)),
            new Pair<>(Pattern.compile("(?<= |^|T)" + DATE_HOUR_PATTERN + DATE_SPLITTER + DATE_MINUTE_PATTERN), new SimpleDateFormat("hh:mm", Locale.ENGLISH))
    );

    private final static List<Pair<Pattern, SimpleDateFormat>> DAY_PATTERNS = Arrays.asList(
            new Pair<>(Pattern.compile("" + NEW_DATE_PART_BEGINNING_PATTERN + DATE_MONTH_STRING_PATTERN + DATE_SPLITTER + DATE_DAY_PATTERN + DATE_SPLITTER + DATE_YEAR_PATTERN), new SimpleDateFormat("MMM:dd:yyyy", Locale.ENGLISH)),
            new Pair<>(Pattern.compile("" + NEW_DATE_PART_BEGINNING_PATTERN + DATE_DAY_PATTERN + DATE_SPLITTER + DATE_MONTH_STRING_PATTERN + DATE_SPLITTER + DATE_YEAR_PATTERN), new SimpleDateFormat("dd:MMM:yyyy", Locale.ENGLISH)),
            new Pair<>(Pattern.compile("" + NEW_DATE_PART_BEGINNING_PATTERN + DATE_DAY_PATTERN + DATE_SPLITTER + DATE_MONTH_PATTERN + DATE_SPLITTER + DATE_YEAR_PATTERN), new SimpleDateFormat("dd:MM:yyyy", Locale.ENGLISH)),
            new Pair<>(Pattern.compile("" + NEW_DATE_PART_BEGINNING_PATTERN + DATE_YEAR_PATTERN + DATE_SPLITTER + DATE_MONTH_PATTERN + DATE_SPLITTER + DATE_DAY_PATTERN), new SimpleDateFormat("yyyy:MM:dd", Locale.ENGLISH))
    );

    private final static List<Pair<Pattern, String>> COMMON_DATE_PATTERNS = Arrays.asList(
            new Pair<>(Pattern.compile("" + NEW_DATE_PART_BEGINNING_PATTERN + DATE_AD_BC_PATTERN), "G"),
            new Pair<>(Pattern.compile("" + NEW_DATE_PART_BEGINNING_PATTERN + DATE_MONTH_STRING_PATTERN), "MMM"),
            new Pair<>(Pattern.compile("" + NEW_DATE_PART_BEGINNING_PATTERN + DATE_DAY_OF_WEEK_STRING_PATTERN), "EEE"),
            new Pair<>(Pattern.compile(NEW_DATE_PART_BEGINNING_PATTERN + "'" + DATE_SHORT_YEAR_PATTERN), "''yy")
    );

    /**
     * Will format a given date by creating a <code>SimpleDateFormat</code> from the format.<br>
     * The <code>SimpleDateFormat</code> locale will be set to <code>Locale.ENGLISH</code>.
     *
     * @return The formatted <code>Date</code>.
     */
    public static String formatDate(Date date, String format) {
        return new SimpleDateFormat(format, Locale.ENGLISH).format(date);
    }

    /**
     * Will format a given date using the <code>SimpleDateFormat</code>.<br>
     *
     * @return The formatted <code>Date</code>.
     */
    public static String formatDate(Date date, SimpleDateFormat format) {
        return format.format(date);
    }

    /**
     * Converts a <code>Date</code> into a <code>Calendar</code>.
     */
    public static Calendar toCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Converts a <code>Date</code> into a <code>Calendar</code> using a <code>TimeZone</code>.
     */
    public static Calendar toCalendar(Date date, TimeZone timeZone) {
        Calendar calendar = toCalendar(date);
        calendar.setTimeZone(timeZone);
        return calendar;
    }

    /**
     * Adds a certain amount of years to the <code>Date</code>.
     */
    public static Date addYears(Date date, int years) {
        Calendar calendar = toCalendar(date);
        calendar.add(Calendar.YEAR, years);
        return calendar.getTime();
    }

    /**
     * Adds a certain amount of months to the <code>Date</code>.
     */
    public static Date addMonths(Date date, int months) {
        Calendar calendar = toCalendar(date);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    /**
     * Adds a certain amount of days to the <code>Date</code>.
     */
    public static Date addDays(Date date, int days) {
        Calendar calendar = toCalendar(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }


    /**
     * Adds a certain amount of hours to the <code>Date</code>.
     */
    public static Date addHours(Date date, int hours) {
        Calendar calendar = toCalendar(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    /**
     * Adds a certain amount of minutes to the <code>Date</code>.
     */
    public static Date addMinutes(Date date, int minutes) {
        Calendar calendar = toCalendar(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    /**
     * Adds a certain amount of seconds to the <code>Date</code>.
     */
    public static Date addSeconds(Date date, int seconds) {
        Calendar calendar = toCalendar(date);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    /**
     * Adds a certain amount of milliseconds to the <code>Date</code>.
     */
    public static Date addMillis(Date date, int millis) {
        Calendar calendar = toCalendar(date);
        calendar.add(Calendar.MILLISECOND, millis);
        return calendar.getTime();
    }

    public static long elapsedMillis(Date before, Date after) {
        return elapsedMillis(toCalendar(before), toCalendar(after));
    }

    public static long elapsedMillis(Calendar before, Calendar after) {
        return (after.getTimeInMillis() - before.getTimeInMillis());
    }
}
