package com.example.editanywhere.service;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson2.JSON;
import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.utils.ApiUti;
import com.example.editanywhere.utils.EntryServiceCallback;
import com.example.editanywhere.utils.OKHttpUtil;
import com.example.editanywhere.utils.OkHttpCallBack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RemoteEntryService extends EntryService {
    private static final String TAG = "RemoteEntryService";

    public RemoteEntryService() {

    }

    @Override
    public void addByEntryName(String entryName, EntryServiceCallback callback) {
        OKHttpUtil.post(ApiUti.API_ENTRY_ADD,
                new ApiUti.Builder().add("entryName", entryName)
                        .add("entryContent", JSON.toJSON(new String[]{entryName})).build(),
                new OkHttpCallBack() {
                    @Override
                    public void onSuccess(String res) {
                        Entry retEntry = JSON.parseObject(res,Entry.class);
                        callback.onAddByEntryName(retEntry);
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
    public void editEntryContentByEntryName(String entryName, List<String> entryContent, EntryServiceCallback callback) {
        OKHttpUtil.post(ApiUti.API_ENTRY_EDIT,
                new ApiUti.Builder().add("entryName", entryName).build(),
                new OkHttpCallBack() {
                    @Override
                    public void onSuccess(String res) {
                        Entry retEntry = JSON.parseObject(res,Entry.class);
                        callback.onEditEntryContentByEntryName(retEntry);
                    }
                    @Override
                    public void onError(String msg) {
                        Log.e(TAG, "onError: deleteById," + msg);
                    }
                });
    }


    @Override
    public void queryAll(EntryServiceCallback callback) {
        OKHttpUtil.post(ApiUti.API_ENTRY_QUERY_ALL,
                new ApiUti.Builder().build(),
                new OkHttpCallBack() {
                    @Override
                    public void onSuccess(String res) {
                        List<Entry> entries = JSON.parseArray(res,Entry.class);
                        callback.onQueryAll(entries);
                    }
                    @Override
                    public void onError(String msg) {
                        Log.e(TAG, "onError: queryAll," + msg);
                    }
                });
    }

    @Override
    public void queryByEntryName(String entryName, EntryServiceCallback callback) {
        OKHttpUtil.post(ApiUti.API_ENTRY_QUERY,
                new ApiUti.Builder().add("entryName", entryName).build(),
                new OkHttpCallBack() {
                    @Override
                    public void onSuccess(String res) {
                        Entry entry = JSON.parseObject(res,Entry.class);
                        callback.onAddByEntryName(entry);
                    }
                    @Override
                    public void onError(String msg) {
                        Log.e(TAG, "onError: queryByEntryName," + msg);
                    }
                });
    }

    @Override
    public void queryLatestByMatchEntryContent(String searchText, EntryServiceCallback callback) {
        Log.i(TAG, "queryLatestByMatchEntryContent: enter");
    }

    @Override
    public void queryHistoryByMatchEntryContent(String searchText, EntryServiceCallback callback) {
        Log.i(TAG, "queryHistoryByMatchEntryContent: enter");
    }

    @Override
    public void queryHistoryByEntryName(String entryName, EntryServiceCallback callback) {
        Log.i(TAG, "queryHistoryByEntryName: enter");
    }



}
