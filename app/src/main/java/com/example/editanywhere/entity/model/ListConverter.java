package com.example.editanywhere.entity.model;

import androidx.room.TypeConverter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListConverter {

    private static final String TAG = "ListConverter";

    @TypeConverter
    public static String convert(List<String> list) {
        return JSON.toJSONString(list);
    }

    @TypeConverter
    public static List<String> revert(String jsonStr) {
        JSONArray array = JSON.parseArray(jsonStr);
        List<String> res = new ArrayList<>();
        for (Object object : array) {
            res.add(Objects.toString(object));
        }
        return res;
    }

}
