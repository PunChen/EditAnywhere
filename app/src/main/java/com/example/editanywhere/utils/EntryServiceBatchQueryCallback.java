package com.example.editanywhere.utils;

import com.example.editanywhere.entity.model.Entry;

import java.util.List;

public interface EntryServiceBatchQueryCallback {
    void onStart(int totalCount);

    void onProgress(int curCount, int totalCount, List<Entry> entryList);

    void onFinish(boolean success, String errMsg);

}
