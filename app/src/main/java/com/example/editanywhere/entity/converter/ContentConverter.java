package com.example.editanywhere.entity.converter;

import android.util.Log;

import androidx.room.TypeConverter;

import com.alibaba.fastjson2.JSON;
import com.example.editanywhere.entity.model.Content;

import java.util.ArrayList;
import java.util.List;

public class ContentConverter {

    private static final String TAG = "ContentConverter";

    @TypeConverter
    public static String convert(List<Content> list) {
        return JSON.toJSONString(list);
    }

    @TypeConverter
    public static List<Content> revert(String jsonStr) {
        try {
            return JSON.parseArray(jsonStr, Content.class);
        } catch (Exception e) {
            Log.e(TAG, "revert failed:" + e.getMessage());
            return new ArrayList<>();
        }

    }
}
