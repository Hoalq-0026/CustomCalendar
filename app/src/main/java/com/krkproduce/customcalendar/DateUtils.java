package com.krkproduce.customcalendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static final String REVIEW_TIME_FORMAT = "yyyy年MM月dd日";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_PICKER_FORMAT = "MMM dd, yyyy";
    public static final long INVALID_TIME = -1;

    public static Date convertToDate(String source, String format) {
        if (source == null) {
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(format);

        try {
            Date date = formatter.parse(source);
            return date;
        } catch (ParseException e) {

        }
        return null;
    }

    public static String convertToString(Date source, String format) {
        if (source == null) {
            return null;
        }

        DateFormat df = new SimpleDateFormat(format);
        return df.format(source);
    }
}
