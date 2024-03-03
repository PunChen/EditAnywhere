package com.example.editanywhere.utils;

import android.util.Log;

import com.example.editanywhere.entity.model.Entry;

import java.util.List;

public interface EntryServiceCallback {
    String TAG = "EntryServiceCallback";

    default void onAddByEntryName(Entry result) {
        Log.i(TAG, "onAddByEntryName: " + result);
    }

    default void onDeleteByEntryName(Entry result) {
        Log.i(TAG, "onDeleteByEntryName: " + result);
    }

    default void onEditEntryContentByEntryName(Entry result) {
        Log.i(TAG, "onEditEntryContentByEntryName: " + result);
    }

    default void onQueryAll(List<Entry> result) {
        Log.i(TAG, "onQueryAll:" + result);
    }

    default void onQueryByEntryName(Entry result) {
        Log.i(TAG, "onQueryByEntryName: " + result);
    }

    default void onQueryLatestByMatchEntryContent(List<Entry> result) {
        Log.i(TAG, "onQueryLatestByMatchEntryContent:" + result);
    }

    default void onQueryHistoryByMatchEntryContent(List<Entry> result) {
        Log.i(TAG, "onQueryHistoryByMatchEntryContent:" + result);
    }

    default void onQueryHistoryByEntryName(List<Entry> result) {
        Log.i(TAG, "onQueryHistoryByEntryName:" + result);
    }

    void onFinish(String errMsg);
}
