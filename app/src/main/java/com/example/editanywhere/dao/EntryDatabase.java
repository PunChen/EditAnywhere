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
import com.example.editanywhere.utils.DBConst;

@Database(entities = Entry.class, version = 4, exportSchema = false)
public abstract class EntryDatabase extends RoomDatabase {
    private static final String TAG = "EntryDatabase";
    private static final int MAX_MIGRATION = 20;
    private static final Migration[] migrations = new Migration[MAX_MIGRATION];
    private static volatile EntryDatabase sInstance;

    static {
        for (int i = 1; i <= MAX_MIGRATION; i++) {
            migrations[i - 1] = new Migration(i, i + 1) {
                @Override
                public void migrate(@NonNull SupportSQLiteDatabase database) {
                    String msg = String.format("migrate %s ===> %s", this.startVersion, this.endVersion);
                    Log.i(TAG, msg);
                    String sql = "DROP TABLE " + DBConst.ENTRY_DB_NAME;
                    database.execSQL(sql);
                }
            };
        }
    }

    public static EntryDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (EntryDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                                    EntryDatabase.class, DBConst.ENTRY_DB_NAME)
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
}
