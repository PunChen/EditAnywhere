package com.example.editanywhere.service;

import android.app.Activity;

import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.utils.EntryServiceBatchQueryCallback;
import com.example.editanywhere.utils.EntryServiceCallback;
import com.example.editanywhere.utils.SPUtil;

import java.util.List;

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

    /*
        词条+版本为主键
        1.添加词条
            1）首先查询最大版本的有效词条
            2）不存在，新增一个
            3）存在，返回添加失败：词条已存在
        2.删除词条
            1）首先查询最大版本的有效词条
            2）存在，设置所有版本为无效
            3）不存在，返回删除失败：词条不存在
        3.修改词条
            1）首先查询最大版本的有效词条
            2）存在，校验修改内容，添加一个新版本的有效词条
            3）不存在，返回修改失败：词条不存在
        4.查询所有词条
            1）查询所有有效词条
            2）通过词条名称+版本分组
            3）取每个分组版本最大的一条
            4）汇总成列表返回
        5.根据词条名称查询单个词条
            1）查询最大版本的有效词条
        6.根据词条内容查询最新版本词条匹配内容
        7.根据词条内容查询所有有效匹配词条
        8.查询词条所有历史版本
     */
    public abstract void addByEntryName(String entryName, EntryServiceCallback<Entry> callback);

    public abstract void deleteByEntryName(String entryName, EntryServiceCallback<Boolean> callback);

    public abstract void editEntryContentByEntryName(String entryName, List<String> entryContent, EntryServiceCallback<Entry> callback);

    public abstract void queryAll(EntryServiceCallback<List<Entry>> callback);

    public abstract void queryByEntryName(String entryName, EntryServiceCallback<Entry> callback);

    public abstract void queryLatestByMatchEntryContent(String searchText, EntryServiceCallback<List<Entry>> callback);

    public abstract void queryHistoryByMatchEntryContent(String searchText, EntryServiceCallback<List<Entry>> callback);

    public abstract void queryHistoryByEntryName(String entryName, EntryServiceCallback<List<Entry>> callback);

    public abstract void addByBatch(List<Entry> entryList, EntryServiceCallback<List<Long>> callback);

    public abstract void queryAllByBatch(int batchSize, EntryServiceBatchQueryCallback callback);

    public abstract void addByEntryNameAndContent(String entryName, List<String> entryContent, EntryServiceCallback<Entry> callback);


}
