package yanwittmann.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtilsTest {

    private final static SimpleDateFormat CHECK_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z", Locale.ENGLISH);

    @Test
    public void detectDateFormatTest() throws ParseException {
        checkStringFormatter("         13:23:59  ", "1970-01-01T13:23:59 CET", true);
        checkStringFormatter("05/07/2000 13-23-59-912 -   0400", "2000-05-07T19:23:59 CEST", true);
        checkStringFormatter("05/07-2000 13-23-59", "2000-05-07T13:23:59 CEST", true);
        checkStringFormatter("2001.07.04 AD at 12:08:56 PDT", "2001-07-04T21:08:56 CEST", true);
        checkStringFormatter("2015-11-19 13:19:24.000", "2015-11-19T13:19:24 CET", true);
        checkStringFormatter("2015-11-19T13:19:24 CET", "2015-11-19T13:19:24 CET", true);
        checkStringFormatter("Wed, July 4, '01", "2001-07-04T00:00:00 CEST", true);
        checkStringFormatter("Wed,3.4, '02", "2002-03-04T00:00:00 CET", true);
        checkStringFormatter("19/Nov/2015:13:19:24 CET", "2015-11-19T13:19:24 CET", true);
        checkStringFormatter("Nov/19/2015:13:19:24 CET", "2015-11-19T13:19:24 CET", true);
        checkStringFormatter("11/19/2015 13:23:59:912 EST", "2015-11-19T19:23:59 CET", true);
        checkStringFormatter("Thu Nov 19 13:21:11 2015", "2015-11-19T13:21:11 CET", true);
        checkStringFormatter("13:21:11 Thu Nov 19 2015", "2015-11-19T13:21:11 CET", true);

        checkStringFormatter("2021-06-24", "2021-06-24T00:00:00 CEST", true);
        checkStringFormatter("18.06.2021", "2021-06-18T00:00:00 CEST", false);
        checkStringFormatter("1/2/2000 01:23", "2000-02-01T01:23:00 CET", false);
        checkStringFormatter("1 2 2000 01 23", "2000-02-01T01:23:00 CET", false);
    }

    public void checkStringFormatter(String timestamp, String expected, boolean monthFirst) throws ParseException {
        System.out.println("\n" + timestamp + " ==> " + expected);
        Assertions.assertEquals(expected, CHECK_FORMAT.format(DateUtils.parseString(timestamp, monthFirst)));
    }

    @Test
    public void modifyDateTest() throws ParseException {
        Date date = DateUtils.parseString("1/2/2000 01:23");
        date = DateUtils.addDays(date, 40);
        Assertions.assertEquals("Sun Mar 12 01:23:00 CET 2000", date.toString());
        date = DateUtils.addMonths(date, 1);
        Assertions.assertEquals("Wed Apr 12 01:23:00 CEST 2000", date.toString());
        date = DateUtils.addYears(date, 4);
        Assertions.assertEquals("Mon Apr 12 01:23:00 CEST 2004", date.toString());
        date = DateUtils.addHours(date, 6);
        Assertions.assertEquals("Mon Apr 12 07:23:00 CEST 2004", date.toString());
        date = DateUtils.addMinutes(date, 110);
        Assertions.assertEquals("Mon Apr 12 09:13:00 CEST 2004", date.toString());
        date = DateUtils.addSeconds(date, 50);
        Assertions.assertEquals("Mon Apr 12 09:13:50 CEST 2004", date.toString());
        date = DateUtils.addMillis(date, 2500);
        Assertions.assertEquals("Mon Apr 12 09:13:52 CEST 2004", date.toString());
    }

    @Test
    public void getDateDataTest() throws ParseException {
        Date date = DateUtils.parseString("04:23 1/3/2000");

        Assertions.assertEquals(2000, DateUtils.getYear(date));
        Assertions.assertEquals(2, DateUtils.getMonth(date));
        Assertions.assertEquals(1, DateUtils.getDayOfMonth(date));
        Assertions.assertEquals(4, DateUtils.getDayOfWeek(date));
        Assertions.assertEquals(61, DateUtils.getDayOfYear(date));
        Assertions.assertEquals(4, DateUtils.getHour(date));
        Assertions.assertEquals(23, DateUtils.getMinute(date));
        Assertions.assertEquals(0, DateUtils.getMilliseconds(date));
    }

    @Test
    public void timeDiffTest() throws ParseException {
        Assertions.assertEquals(518, DateUtils.elapsedMillis(DateUtils.parseString("1/2/2000 01:23"), DateUtils.parseString("Wed, July 4, '01")) / (1000 * 60 * 60 * 24));
        Assertions.assertEquals(883, DateUtils.elapsedMillis(DateUtils.parseString("1/2/2000 01:23"), DateUtils.parseString("Wed, July 4, '02")) / (1000 * 60 * 60 * 24));
    }

}
