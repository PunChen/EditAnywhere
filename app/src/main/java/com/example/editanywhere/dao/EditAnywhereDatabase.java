package com.example.editanywhere.dao;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.editanywhere.entity.model.Entry;
import com.example.editanywhere.entity.model.EntryBookKey;
import com.example.editanywhere.entity.model.Notebook;
import com.example.editanywhere.utils.DBConst;

@Database(entities = {Entry.class, Notebook.class, EntryBookKey.class}, version = 1, exportSchema = false)
public abstract class EditAnywhereDatabase extends RoomDatabase {
    private static final String TAG = "EditAnyWhere";
    private static final int MAX_MIGRATION = 20;
    private static final Migration[] migrations = new Migration[MAX_MIGRATION];
    private static volatile EditAnywhereDatabase sInstance;

    static {
        for (int i = 1; i <= MAX_MIGRATION; i++) {
            migrations[i - 1] = new Migration(i, i + 1) {
                @Override
                public void migrate(@NonNull SupportSQLiteDatabase database) {
                    String msg = String.format("migrate %s ===> %s", this.startVersion, this.endVersion);
                    Log.i(TAG, msg);
                    for (String tab : DBConst.TAB_SET) {
                        Log.w(TAG, "start dropping table: " + tab);
                        String sql = "DROP TABLE " + tab;
                        database.execSQL(sql);
                    }
                }
            };
        }
    }

    public static EditAnywhereDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (EditAnywhereDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                                    EditAnywhereDatabase.class, DBConst.DB_NAME)
                            .allowMainThreadQueries()
                            .addMigrations(migrations)
                            //.addCallback(CALLBACK)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return sInstance;
    }

    public abstract EntryDao getEntryDao();

    public abstract NotebookDao getNotebookDao();

    public abstract EntryBookKeyDao getEntryBookKeyDao();

}
