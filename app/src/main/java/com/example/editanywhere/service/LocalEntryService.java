package com.example.editanywhere.service;

import android.content.Context;
import android.util.Log;

import com.example.editanywhere.dao.EntryDao;
import com.example.editanywhere.dao.EntryDatabase;
import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.utils.EntryServiceBatchQueryCallback;
import com.example.editanywhere.utils.EntryServiceCallback;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LocalEntryService extends EntryService {

    private static final String TAG = "LocalEntryService";
    private final EntryDao entryDao;
    private final EntryDatabase entryDatabase;

    public LocalEntryService(Context context) {
        entryDatabase = EntryDatabase.getInstance(context);
        entryDao = entryDatabase.getEntryDao();
    }


    @Override
    public void addByEntryName(String entryName, EntryServiceCallback callback) {
        addByEntryNameAndContent(entryName, List.of(entryName), callback);
    }


    @Override
    public void deleteByEntryName(String entryName, EntryServiceCallback<Boolean> callback) {
        Entry latest = entryDao.queryLatestByEntryName(entryName);
        if (latest != null) {
            entryDao.deleteByEntryName(entryName);
            callback.onSuccess(true);
        } else {
            callback.onFailure("deleteByEntryName failed, entryName not exists: " + entryName);
        }
    }


    @Override
    public void queryAll(EntryServiceCallback<List<Entry>> callback) {
        List<Entry> entries = entryDao.queryAll();
        List<Entry> result = new ArrayList<>();
        entries.stream().collect(
                        Collectors.groupingBy(Entry::getEntryName,
                                Collectors.maxBy(Comparator.comparingInt(Entry::getVersion))))
                .forEach((k, v) -> v.ifPresent(result::add));
        callback.onSuccess(result);
    }

    @Override
    public void queryByEntryName(String entryName, EntryServiceCallback<Entry> callback) {
        Entry latest = entryDao.queryLatestByEntryName(entryName);
        if (latest != null) {
            callback.onSuccess(latest);
        } else {
            callback.onFailure("entry not exist: " + entryName);
        }

    }

    @Override
    public void queryLatestByMatchEntryContent(String entryContent, EntryServiceCallback callback) {

    }

    @Override
    public void queryHistoryByMatchEntryContent(String entryContent, EntryServiceCallback callback) {

    }

    @Override
    public void queryHistoryByEntryName(String entryName, EntryServiceCallback callback) {

    }


    @Override
    public void editEntryContentByEntryName(String entryName, List<String> entryContent, EntryServiceCallback<Entry> callback) {
        Entry latest = entryDao.queryLatestByEntryName(entryName);
        if (latest != null) {
            latest.setId(null);
            latest.setEntryContent(entryContent);
            latest.setVersion(latest.getVersion() + 1);
            Long id = entryDao.insertEntry(latest);
            Entry newEntry = entryDao.queryById(id);
            callback.onSuccess(newEntry);
        } else {
            callback.onFailure("editEntryContentByEntryName failed, entryName not exists: " + entryName);
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
        if (Objects.equals(id, EntryDao.INSERT_FAIL_RETURN_ID)) {
            callback.onFailure("addByEntryNameAndContent failed");
            return;
        }
        Entry entry = entryDao.queryById(id);
        callback.onSuccess(entry);
    }

    private Long addByEntryNameAndContent(String entryName, List<String> entryContent) {
        Entry latest = entryDao.queryLatestByEntryName(entryName);
        if (latest != null) {
            Log.e(TAG, "addByEntryNameAndContent: " + "entry: " + entryName + " exists");
            return null;
        }
        Entry entry = new Entry();
        entry.setEntryName(entryName);
        entry.setEntryContent(entryContent);
        entry.setCreateTime(new Date().getTime());
        entry.setUpdateTime(new Date().getTime());
        entry.setVersion(1);
        entry.setEntryNameOther("");
        return entryDao.insertEntry(entry);
    }


}
