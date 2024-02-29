package com.example.editanywhere.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class DateUtil {
    public static final String DEFAULT_DATE_FORMAT  = "yyyy-MM-dd HH:mm:ss";

    public static String dateFormat(Date date,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }

    public static String dateFormat(Long ts,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        Date date = new Date(ts);
        return sdf.format(date);
    }
}
