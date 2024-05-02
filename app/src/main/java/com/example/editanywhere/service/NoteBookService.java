package com.example.editanywhere.service;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.editanywhere.dao.EditAnywhereDatabase;
import com.example.editanywhere.dao.EntryBookKeyDao;
import com.example.editanywhere.dao.NotebookDao;
import com.example.editanywhere.entity.model.Notebook;
import com.example.editanywhere.utils.SPUtil;
import com.example.editanywhere.utils.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public final class NoteBookService {

    private static final String TAG = "NoteBookService";
    private final EditAnywhereDatabase database;
    private final NotebookDao notebookDao;
    private final EntryBookKeyDao entryBookKeyDao;

    private static volatile NoteBookService instance;

    public static NoteBookService getInstance(Activity activity) {
        if (instance == null) {
            synchronized (NoteBookService.class) {
                if (instance == null) {
                    instance = new NoteBookService(activity);
                }
            }
        }
        boolean local = SPUtil.getBoolean(activity.getApplication(), SPUtil.TAG_WORKING_MODE_LOCAL, true);
        return instance;
    }


    private NoteBookService(Context context) {
        database = EditAnywhereDatabase.getInstance(context);
        notebookDao = database.getNotebookDao();
        entryBookKeyDao = database.getEntryBookKeyDao();
    }

    public List<Notebook> getAllNotebooks() {
        try {
           return notebookDao.queryAll();
        } catch (Exception e) {
            Log.e(TAG,"getAllNotebooks error , msg: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Boolean addOneNotebook(String bookName, String description, Notebook result) {
        if (StringUtils.isAllBlank(bookName)) {
            Log.i(TAG, "invalid bookName: " + bookName);
            return false;
        }
        Notebook notebook = new Notebook();
        notebook.setNotebookName(bookName);
        notebook.setDescription(description);
        notebook.setCreateTime(new Date().getTime());
        notebook.setId(null);
        try {
            Long id = notebookDao.insert(notebook);
            boolean res = id != null;
            if (res) {
                Notebook temp = notebookDao.queryById(id);
                result.setId(temp.getId());
                result.setNotebookName(temp.getNotebookName());
                result.setDescription(temp.getDescription());
                result.setCreateTime(temp.getCreateTime());
            }
            return res;
        } catch (Exception e) {
            Log.e(TAG,"addOneNotebook error , msg: " + e.getMessage());
            return false;
        }
    }

    public Boolean deleteOneNotebook(Long id) {
        try {
            notebookDao.deleteById(id);
            Notebook notebook = notebookDao.queryById(id);
            return notebook == null;
        } catch (Exception e) {
            Log.e(TAG,"deleteOneNotebook error , msg: " + e.getMessage());
            return false;
        }
    }

    public Boolean updateOneNotebook(Notebook notebook) {
        try {
            Notebook temp = notebookDao.queryById(notebook.getId());
            if (temp == null) {
                return false;
            }
            notebookDao.updateNotebook(notebook);
            return true;
        } catch (Exception e) {
            Log.e(TAG,"updateOneNotebook error , msg: " + e.getMessage());
            return false;
        }
    }

}
