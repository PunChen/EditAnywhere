package com.example.editanywhere.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_NUMBER = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_DATE_TIME = "MM-dd HH:mm";

    public static String dateFormat(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }

    public static String dateFormat(Long ts, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        Date date = new Date(ts);
        return sdf.format(date);
    }
}
