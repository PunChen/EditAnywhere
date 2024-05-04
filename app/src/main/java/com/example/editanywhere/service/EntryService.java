package com.example.editanywhere.service;

import android.app.Activity;

import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.utils.EntryServiceBatchQueryCallback;
import com.example.editanywhere.utils.EntryServiceCallback;
import com.example.editanywhere.utils.SPUtil;

import java.util.List;
import java.util.Set;

public abstract class EntryService {

    private static LocalEntryService localEntryService = null;
    private static RemoteEntryService remoteEntryService = null;
    private static volatile Object instance;

    EntryService() {
    }

    public static EntryService getInstance(Activity activity) {
        if (instance == null) {
            synchronized (EntryService.class) {
                if (instance == null) {
                    if (localEntryService == null) {
                        localEntryService = new LocalEntryService(activity);
                    }
                    if (remoteEntryService == null) {
                        remoteEntryService = new RemoteEntryService();
                    }
                    instance = new Object();
                }
            }
        }
        boolean local = SPUtil.getBoolean(activity.getApplication(), SPUtil.TAG_WORKING_MODE_LOCAL, true);
        return local ? localEntryService : remoteEntryService;
    }

    public abstract void addByEntryName(String entryName, EntryServiceCallback<Entry> callback);

    public abstract void deleteByEntryId(Long id, EntryServiceCallback<Boolean> callback);
    public abstract void deleteByEntryIdSet(Set<Long> idSet, EntryServiceCallback<Boolean> callback);

    public abstract void editEntryContentByEntryId(Long id, List<String> entryContent, EntryServiceCallback<Entry> callback);

    public abstract void queryAll(EntryServiceCallback<List<Entry>> callback);

    public abstract void queryByEntryId(Long id, EntryServiceCallback<Entry> callback);

    public abstract void queryByEntryNameOrContent(String text, EntryServiceCallback<List<Entry>> callback);
    public abstract void queryByEntryNameOrContentInNotebook(Long bookId, String text, EntryServiceCallback<List<Entry>> callback);

    public abstract void addByBatch(List<Entry> entryList, EntryServiceCallback<List<Long>> callback);

    public abstract void queryAllByBatch(int batchSize, EntryServiceBatchQueryCallback callback);

    public abstract void addByEntryNameAndContent(String entryName, List<String> entryContent, EntryServiceCallback<Entry> callback);

    // 根据笔记本id查询所属的所有词条
    public abstract void queryAllByNotebookId(Long bookId, EntryServiceCallback<List<Entry>> callback);

}
