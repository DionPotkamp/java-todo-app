package com.example.forms.utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateHelper {
    public static final String dateFormat = "EEE MMM dd HH:mm:ss z yyyy";
    public static final Locale locale = Locale.US;

    /**
     * Checks if a date string is valid
     * adapted from https://www.baeldung.com/java-string-valid-date
     */
    public static boolean isValid(String dateStr) {
        String dateRegex = "^\\d{1,2}/\\d{1,2}/\\d{4}$";
        String timeRegex = "^\\d{1,2}:\\d{2}$";
        String dateTimeRegex = "^\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{2}$";
        String dateFormat = "";

        if (dateStr.matches(dateRegex)) {
            dateFormat = "MM/dd/yyyy";
        } else if (dateStr.matches(timeRegex)) {
            dateFormat = "HH:mm";
        } else if (dateStr.matches(dateTimeRegex)) {
            dateFormat = "MM/dd/yyyy HH:mm";
        }

        DateFormat sdf = new SimpleDateFormat(dateFormat, locale);
        sdf.setLenient(false);

        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }
    /**
     * Converts a date string to a Calendar object
     * adapted from https://stackoverflow.com/a/11791544/10463118
     */
    public static Calendar calendarFromString(String dateStr) {
        Calendar cal = Calendar.getInstance();
        DateFormat simpleFormat = new SimpleDateFormat(dateFormat, locale);

        try {
            cal.setTime(simpleFormat.parse(dateStr));
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse date string: " + dateStr);
        }

        return cal;
    }

    public static String stringFromCalendar(Calendar cal) {
        return new SimpleDateFormat(dateFormat, locale).format(cal.getTime());
    }
}
