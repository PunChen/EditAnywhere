package com.example.editanywhere.service;

import android.util.Log;

import com.alibaba.fastjson2.JSON;
import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.utils.ApiUti;
import com.example.editanywhere.utils.EntryServiceBatchQueryCallback;
import com.example.editanywhere.utils.EntryServiceCallback;
import com.example.editanywhere.utils.OKHttpUtil;
import com.example.editanywhere.utils.OkHttpCallBack;

import java.util.List;

public class RemoteEntryService extends EntryService {
    private static final String TAG = "RemoteEntryService";

    public RemoteEntryService() {

    }

    @Override
    public void addByEntryName(String entryName, EntryServiceCallback<Entry> callback) {
        OKHttpUtil.post(ApiUti.API_ENTRY_ADD,
                new ApiUti.Builder().add("entryName", entryName)
                        .add("entryContent", JSON.toJSON(new String[]{entryName})).build(),
                new OkHttpCallBack() {
                    @Override
                    public void onSuccess(String res) {
                        Entry retEntry = JSON.parseObject(res, Entry.class);
                        callback.onSuccess(retEntry);
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e(TAG, "onError: addEntry," + msg);
                    }
                });
    }

    @Override
    public void deleteByEntryName(String entryName, EntryServiceCallback callback) {
        Log.i(TAG, "deleteByEntryName: enter");
    }

    @Override
    public void editEntryContentByEntryName(String entryName, List<String> entryContent, EntryServiceCallback<Entry> callback) {
        OKHttpUtil.post(ApiUti.API_ENTRY_EDIT,
                new ApiUti.Builder().add("entryName", entryName).build(),
                new OkHttpCallBack() {
                    @Override
                    public void onSuccess(String res) {
                        Entry retEntry = JSON.parseObject(res, Entry.class);
                        callback.onSuccess(retEntry);
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e(TAG, "onError: deleteById," + msg);
                    }
                });
    }


    @Override
    public void queryAll(EntryServiceCallback<List<Entry>> callback) {
        OKHttpUtil.post(ApiUti.API_ENTRY_QUERY_ALL,
                new ApiUti.Builder().build(),
                new OkHttpCallBack() {
                    @Override
                    public void onSuccess(String res) {
                        List<Entry> entries = JSON.parseArray(res, Entry.class);
                        callback.onSuccess(entries);
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e(TAG, "onError: queryAll," + msg);
                    }
                });
    }

    @Override
    public void queryByEntryName(String entryName, EntryServiceCallback<Entry> callback) {
        OKHttpUtil.post(ApiUti.API_ENTRY_QUERY,
                new ApiUti.Builder().add("entryName", entryName).build(),
                new OkHttpCallBack() {
                    @Override
                    public void onSuccess(String res) {
                        Entry entry = JSON.parseObject(res, Entry.class);
                        callback.onSuccess(entry);
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e(TAG, "onError: queryByEntryName," + msg);
                    }
                });
    }

    @Override
    public void queryLatestByMatchEntryContent(String searchText, EntryServiceCallback<List<Entry>> callback) {

    }

    @Override
    public void queryHistoryByMatchEntryContent(String searchText, EntryServiceCallback<List<Entry>> callback) {

    }

    @Override
    public void queryHistoryByEntryName(String entryName, EntryServiceCallback<List<Entry>> callback) {

    }

    @Override
    public void addByBatch(List<Entry> entryList, EntryServiceCallback<List<Long>> callback) {

    }

    @Override
    public void queryAllByBatch(int batchSize, EntryServiceBatchQueryCallback callback) {

    }

    @Override
    public void addByEntryNameAndContent(String entryName, List<String> entryContent, EntryServiceCallback<Entry> callback) {

    }

    @Override
    public void queryAllByNotebookId(Long bookId, EntryServiceCallback<List<Entry>> callback) {

    }


}
