package yanwittmann.utils;

import javafx.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {

    public static Date parseString(String stringDate, String format) throws ParseException {
        return new SimpleDateFormat(format, Locale.ENGLISH).parse(stringDate);
    }

    public static Date parseString(String stringDate, SimpleDateFormat format) throws ParseException {
        return format.parse(stringDate);
    }

    public static Date parseString(String stringDate) throws ParseException {

        // normalize input string
        stringDate = stringDate.replaceAll(" {2,}", " ").replace("/", ":")
                .replace(".", ":").replace(",", ":")
                .replaceAll(" ?/ ?", "/").replace("- ", "-")
                .replaceAll("(?<! )-", ":").trim();

        System.out.println("checking input string: " + stringDate);

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
                System.out.println(knownString.getKey() + " -- " + knownString.getValue() + " -- " + matcher.group() + " -- " + stringDate);
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

        System.out.println("Pattern used to decode date (" + parsableDate + "): " + parsableDateFormat);
        return new SimpleDateFormat(parsableDateFormat.toString(), Locale.ENGLISH).parse(parsableDate.toString());

    }

    private static Pair<SimpleDateFormat, String> extractPartFromString(String stringDate, List<Pair<Pattern, SimpleDateFormat>> patterns) {
        for (Pair<Pattern, SimpleDateFormat> pattern : patterns) {
            Matcher matcher = pattern.getKey().matcher(stringDate);
            if (matcher.find()) {
                System.out.println(pattern.getKey() + " -- " + pattern.getValue().toPattern() + " -- " + matcher.group());
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
            new Pair<>(Pattern.compile(NEW_DATE_PART_BEGINNING_PATTERN + "'\\d{2}"), "''yy")
    );

    public static String formatDate(Date date, String format) {
        return new SimpleDateFormat(format, Locale.ENGLISH).format(date);
    }

    public static String formatDate(Date date, SimpleDateFormat format) {
        return format.format(date);
    }
}
