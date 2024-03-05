package com.example.editanywhere.utils;

import android.util.Log;

import com.alibaba.fastjson2.JSON;
import com.example.editanywhere.entity.model.Entry;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntryUtil {
    private static final Map<String, Integer> FIELD_ORDER = new HashMap<>();
    private static final String TAG = "EntryUtil";

    public static final int EXPORT_BATCH_SIZE = 10;

    static {
        Field[] fields = Entry.class.getDeclaredFields();
        int ind = 0;
        for (Field field : fields) {
            FIELD_ORDER.put(field.getName(), ind++);
        }
    }

    public static Entry fromStringArray(String[] str) {
        try {
            Entry entry = new Entry();
            Field[] fields = Entry.class.getDeclaredFields();
            for (Field field : fields) {
                Integer ind = FIELD_ORDER.get(field.getName());
                if (ind == null) {
                    continue;
                }
                field.setAccessible(true);
                Object val = JSON.parseObject(str[ind], field.getType());
                field.set(entry, val);
            }
            return entry;
        } catch (Exception e) {
            Log.e(TAG, "fromStringArray: " + e.getMessage());
        }
        return null;
    }

    private static Object parseObject(Field field, String str) {
        Class<?> type = field.getType();
        try {
            if (type == Integer.class) {
                return Integer.parseInt(str);
            } else if (type == Long.class) {
                return Long.parseLong(str);
            } else if (type == Boolean.class) {
                return Boolean.parseBoolean(str);
            } else if (type == String.class) {
                return str;
            } else {
                Log.e(TAG, "parseObject: unknown type " + type + " " + str);
            }
        } catch (Exception e) {
            Log.e(TAG, "parseObject: " + e.getMessage());
        }
        return null;
    }

    public static List<Entry> fromStringArrayList(List<String[]> list, boolean hasTitle) {
        int start = hasTitle ? 1 : 0;
        List<Entry> entryList = new ArrayList<>();
        for (int i = start; i < list.size(); i++) {
            String[] str = list.get(i);
            Entry entry = fromStringArray(str);
            entryList.add(entry);
        }
        return entryList;
    }

    public static String[] toStringArray(Entry entry) {
        try {
            Field[] fields = Entry.class.getDeclaredFields();
            String[] arr = new String[fields.length];
            for (Field field : fields) {
                Integer ind = FIELD_ORDER.get(field.getName());
                if (ind == null) {
                    continue;
                }
                field.setAccessible(true);
                arr[ind] = JSON.toJSONString(field.get(entry));
            }
            return arr;
        } catch (Exception e) {
            Log.e(TAG, "toStringArray: " + e.getMessage());
            return null;
        }
    }

    public static List<String[]> toStringArrayList(List<Entry> entryList, boolean hasTitle) {
        Field[] fields = Entry.class.getDeclaredFields();
        List<String[]> strings = new ArrayList<>();
        if (hasTitle) {
            String[] titles = new String[fields.length];
            for (Field field : fields) {
                Integer ind = FIELD_ORDER.get(field.getName());
                if (ind == null) {
                    continue;
                }
                titles[ind] = field.getName();
            }
            strings.add(titles);
        }
        for (Entry entry : entryList) {
            String[] array = toStringArray(entry);
            if (array != null) {
                strings.add(array);
            }
        }
        return strings;
    }


}
