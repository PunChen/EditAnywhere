package com.example.editanywhere.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {
    public final static String TAG_SERVER_ADDRESS = "TAG_SERVER_ADDRESS";
    public final static String TAG_WORKING_MODE = "TAG_WORKING_MODE";
    public final static String TAG_SP_DEFAULT_NAME="default";

    public static String getString(Application application, String key, String def){
        SharedPreferences sp = application.getSharedPreferences(TAG_SP_DEFAULT_NAME,Context.MODE_PRIVATE);
        return sp.getString(key,def);
    }

    public static void putString(Application application, String key, String val){
        SharedPreferences.Editor editor = application.getSharedPreferences(TAG_SP_DEFAULT_NAME,Context.MODE_PRIVATE).edit();
        editor.putString(key,val);
        editor.apply();
    }

    public static Boolean getBoolean(Application application, String key, Boolean def){
        SharedPreferences sp = application.getSharedPreferences(TAG_SP_DEFAULT_NAME,Context.MODE_PRIVATE);
        return sp.getBoolean(key,def);
    }

    public static void putBoolean(Application application, String key, Boolean val){
        SharedPreferences.Editor editor = application.getSharedPreferences(TAG_SP_DEFAULT_NAME,Context.MODE_PRIVATE).edit();
        editor.putBoolean(key,val);
        editor.apply();
    }


    public static int getInt(Application application, String key, int def){
        SharedPreferences sp = application.getSharedPreferences(TAG_SP_DEFAULT_NAME,Context.MODE_PRIVATE);
        return sp.getInt(key,def);
    }

    public static void putInt(Application application, String key, int val){
        SharedPreferences.Editor editor = application.getSharedPreferences(TAG_SP_DEFAULT_NAME,Context.MODE_PRIVATE).edit();
        editor.putInt(key,val);
        editor.apply();
    }
}
