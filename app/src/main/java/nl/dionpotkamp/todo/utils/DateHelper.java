package nl.dionpotkamp.todo.utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class DateHelper {
    public static final String dateFormat = "EEE MMM dd HH:mm:ss z yyyy";
    public static final Locale locale = Locale.US;

    /**
     * Converts a date string to a Calendar object.
     * Adapted from https://stackoverflow.com/a/11791544/10463118.
     */
    public static Calendar calendarFromString(String dateStr) {
        Calendar cal = Calendar.getInstance();
        DateFormat simpleFormat = new SimpleDateFormat(dateFormat, locale);

        try {
            cal.setTime(Objects.requireNonNull(simpleFormat.parse(dateStr)));
        } catch (ParseException e) {
            System.err.println("Could not parse date string: " + dateStr);
        }

        return cal;
    }

    /**
     * Converts a Calender object to a string.
     *
     * @param cal the Calendar object to convert
     * @return the string representation of the Calendar object
     */
    public static String stringFromCalendar(Calendar cal) {
        return new SimpleDateFormat(dateFormat, locale).format(cal.getTime());
    }
}
