package com.example.editanywhere.service;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson2.JSON;
import com.example.editanywhere.dao.EditAnywhereDatabase;
import com.example.editanywhere.dao.EntryBookKeyDao;
import com.example.editanywhere.dao.EntryDao;
import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.entity.model.EntryBookKey;
import com.example.editanywhere.utils.EntryServiceBatchQueryCallback;
import com.example.editanywhere.utils.EntryServiceCallback;
import com.example.editanywhere.utils.EntryUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class LocalEntryService extends EntryService {

    private static final String TAG = "LocalEntryService";
    private final EntryDao entryDao;
    private final EntryBookKeyDao entryBookKeyDao;
    private final EditAnywhereDatabase database;

    public LocalEntryService(Context context) {
        database = EditAnywhereDatabase.getInstance(context);
        entryDao = database.getEntryDao();
        entryBookKeyDao = database.getEntryBookKeyDao();
    }


    @Override
    public void addByEntryName(String entryName, EntryServiceCallback<Entry> callback) {
        addByEntryNameAndContent(entryName, List.of(entryName), callback);
    }

    @Override
    public void deleteByEntryId(Long id, EntryServiceCallback<Boolean> callback) {
        try {
            entryDao.deleteById(id);
            callback.onSuccess(true);
        }catch (Exception e) {
            String msg = String.format("deleteByEntryId fail, id:%s err:%s", id, e.getMessage());
            Log.e(TAG, msg);
            callback.onFailure(msg);
        }
    }

    @Override
    public void deleteByEntryIdSet(Set<Long> idSet, EntryServiceCallback<Boolean> callback) {
        try {
            entryDao.deleteByIdSet(idSet);
            callback.onSuccess(true);
        }catch (Exception e) {
            String msg = String.format("deleteByIdSet fail, id:%s err:%s", idSet, e.getMessage());
            Log.e(TAG, msg);
            callback.onFailure(msg);
        }
    }

    @Override
    public void editEntryContentByEntryId(Long id, List<String> entryContent, EntryServiceCallback<Entry> callback) {
        try {
            Entry entry = entryDao.queryById(id);
            entry.setEntryContent(entryContent);
            entry.setUpdateTime(new Date().getTime());
            entryDao.updateEntry(entry);
            entry = entryDao.queryById(id);
            callback.onSuccess(entry);
        }catch (Exception e) {
            String msg = String.format("editEntryContentByEntryId fail, id:%s err:%s", id, e.getMessage());
            Log.e(TAG, msg);
            callback.onFailure(msg);
        }
    }


    @Override
    public void queryAll(EntryServiceCallback<List<Entry>> callback) {
        try {
            List<Entry> entries = entryDao.queryAll();
            callback.onSuccess(entries);
        }catch (Exception e) {
            String msg = String.format("queryAll fail, err:%s", e.getMessage());
            Log.e(TAG, msg);
            callback.onFailure(msg);
        }
    }

    @Override
    public void queryByEntryId(Long id, EntryServiceCallback<Entry> callback) {
        try {
            Entry entry = entryDao.queryById(id);
            callback.onSuccess(entry);
        }catch (Exception e) {
            String msg = String.format("queryByEntryId fail, id:%s err:%s", id, e.getMessage());
            Log.e(TAG, msg);
            callback.onFailure(msg);
        }
    }

    @Override
    public void queryByEntryNameOrContent(String entryName, EntryServiceCallback<List<Entry>> callback) {
        try {
            List<Entry> entry = entryDao.queryByEntryNameOrContent(entryName);
            callback.onSuccess(entry);
        }catch (Exception e) {
            String msg = String.format("queryByEntryName fail, entryName:%s err:%s", entryName, e.getMessage());
            Log.e(TAG, msg);
            callback.onFailure(msg);
        }
    }

    @Override
    public void queryByEntryNameOrContentInNotebook(Long bookId, String text, EntryServiceCallback<List<Entry>> callback) {
        try {
            List<Entry> entries = entryDao.queryByEntryNameOrContentInBook(bookId, text);
            callback.onSuccess(entries);
        }catch (Exception e) {
            String msg = String.format("queryByEntryNameOrContentInNotebook fail, text:%s err:%s", text, e.getMessage());
            Log.e(TAG, msg);
            callback.onFailure(msg);
        }
    }

    @Override
    public void addByBatch(List<Entry> entryList, EntryServiceCallback<List<Long>> callback) {
        List<Long> idList = new ArrayList<>();
        for (Entry entry : entryList) {
            Long id = addByEntryNameAndContent(entry.getEntryName(), entry.getEntryContent());
            if (Objects.equals(id, EntryDao.INSERT_FAIL_RETURN_ID) || id == null) {
                Log.e(TAG, "addByBatch: fail for " + entry);
                continue;
            }
            idList.add(id);
        }
        if (idList.size() != entryList.size()) {
            String msg = String.format("expect add %s entries, actually added %s entries",
                    entryList.size(), idList.size());
            callback.onFailure(msg);
            return;
        }
        callback.onSuccess(idList);
    }

    @Override
    public void queryAllByBatch(int batchSize, EntryServiceBatchQueryCallback callback) {
        List<Long> idList = entryDao.queryAllIds();
        callback.onStart(idList.size());
        int curCount = 0;
        int totalCount = idList.size();
        for (int i = 0; i < totalCount; i += batchSize) {
            List<Long> subIdList = idList.subList(i, Math.min(i + batchSize, totalCount));
            List<Entry> entryList = entryDao.queryAllByIds(subIdList);
            curCount += entryList.size();
            callback.onProgress(curCount, totalCount, entryList);
        }
        boolean success = curCount == totalCount;
        String errMsg = success ? "" : "size not equal " + curCount + " != " + totalCount;
        callback.onFinish(success, errMsg);
    }

    @Override
    public void addByEntryNameAndContent(String entryName, List<String> entryContent, EntryServiceCallback<Entry> callback) {
        Long id = addByEntryNameAndContent(entryName, entryContent);
        if (id == null || Objects.equals(id, EntryDao.INSERT_FAIL_RETURN_ID)) {
            callback.onFailure("addByEntryNameAndContent failed");
            return;
        }
        Entry entry = entryDao.queryById(id);
        callback.onSuccess(entry);
    }

    @Override
    public void queryAllByNotebookId(Long bookId, EntryServiceCallback<List<Entry>> callback) {
        try {
            List<EntryBookKey> list = entryBookKeyDao.queryEntryIdsByBookId(bookId);
            List<Long> idList = list.stream().map(EntryBookKey::getEntryId).collect(Collectors.toList());
            List<Entry> entryList = entryDao.queryAllByIds(idList);
            callback.onSuccess(entryList);
        } catch (Exception e) {
            Log.e(TAG, "queryAllByNotebookId fail: " + e.getMessage());
            callback.onFailure("queryAllByNotebookId failed");
        }
    }

    private Long addByEntryNameAndContent(String entryName, List<String> entryContent) {
        try {
            Entry entry = new Entry();
            entry.setId(null);
            entry.setEntryName(entryName);
            entry.setEntryContent(entryContent);
            entry.setCreateTime(new Date().getTime());
            entry.setUpdateTime(new Date().getTime());
            entry.setEntryNameOther("");
            return entryDao.insertEntry(entry);
        } catch (Exception e) {
            Log.e(TAG, "addByEntryNameAndContent fail: " + e.getMessage());
            return null;
        }
    }
}
