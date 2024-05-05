package com.example.editanywhere.entity.converter;

import android.util.Log;

import androidx.room.TypeConverter;

import com.example.editanywhere.utils.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {

    private static final String TAG = "DateConverter";

    @TypeConverter
    public static String convertDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DEFAULT_DATE_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }

    @TypeConverter
    public static Date revertDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DEFAULT_DATE_FORMAT, Locale.getDefault());
        Date date;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            Log.e(TAG, "revertDate: revertDate," + e.getMessage());
            date = new Date();
        }
        return date;
    }

}
