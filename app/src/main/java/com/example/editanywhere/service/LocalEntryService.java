package com.example.editanywhere.service;

import android.content.Context;

import com.example.editanywhere.dao.EntryDao;
import com.example.editanywhere.dao.EntryDatabase;
import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.entity.model.EntryKey;
import com.example.editanywhere.utils.EntryServiceCallback;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalEntryService extends EntryService {

    private EntryDatabase entryDatabase;
    private final EntryDao entryDao;

    public LocalEntryService(Context context) {
        entryDatabase = EntryDatabase.getInstance(context);
        entryDao = entryDatabase.getEntryDao();
    }


    @Override
    public void addByEntryName(String entryName, EntryServiceCallback callback) {
        Entry latest = entryDao.queryLatestByEntryName(entryName, true);
        if (latest != null) {
            callback.onFinish("entry: " + entryName + " exists");
            return;
        }
        Entry entry = new Entry();
        entry.setEntryName(entryName);
        entry.setEntryContent(List.of(entryName));
        entry.setCreateTime(new Date().getTime());
        entry.setUpdateTime(new Date().getTime());
        entry.setValid(true);
        entry.setVersion(1);
        entry.setEntryNameOther("");
        long id = entryDao.insertEntry(entry);
        Entry addedEntry = entryDao.queryById(id);
        callback.onAddByEntryName(addedEntry);
    }


    @Override
    public void deleteByEntryName(String entryName, EntryServiceCallback callback) {
        Entry latest = entryDao.queryLatestByEntryName(entryName, true);
        if (latest != null) {
            entryDao.deleteByEntryName(entryName, false);
            callback.onDeleteByEntryName(null);
        } else {
            callback.onFinish("deleteByEntryName failed, entryName not exists: "+ entryName);
        }
    }

    @Override
    public void editEntryContentByEntryName(String entryName, List<String> entryContent, EntryServiceCallback callback) {
        Entry latest = entryDao.queryLatestByEntryName(entryName, true);
        if (latest != null) {
            latest.setId(null);
            latest.setEntryContent(entryContent);
            latest.setVersion(latest.getVersion() + 1);
            Long id = entryDao.insertEntry(latest);
            Entry newEntry = entryDao.queryById(id);
            callback.onEditEntryContentByEntryName(newEntry);
        } else {
            callback.onFinish("editEntryContentByEntryName failed, entryName not exists: "+ entryName);
        }
    }

    @Override
    public void queryAll(EntryServiceCallback callback) {
        List<Entry> entries = entryDao.queryByValid(true);
        List<Entry> result = new ArrayList<>();
        entries.stream().collect(
                Collectors.groupingBy(Entry::getEntryName,
                        Collectors.maxBy(Comparator.comparingInt(Entry::getVersion))))
                .forEach((k,v) -> v.ifPresent(result::add));
        callback.onQueryAll(result);
    }

    @Override
    public void queryByEntryName(String entryName, EntryServiceCallback callback) {
        Entry latest = entryDao.queryLatestByEntryName(entryName, true);
        if (latest != null) {
            callback.onQueryByEntryName(latest);
        } else {
            callback.onFinish("entry not exist: "+ entryName);
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




}
