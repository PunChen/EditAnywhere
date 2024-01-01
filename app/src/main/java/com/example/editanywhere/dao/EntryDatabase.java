package com.example.editanywhere.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.editanywhere.entity.model.Entry;

@Database(entities = Entry.class, version = 1, exportSchema = false)
public abstract class EntryDatabase extends RoomDatabase {
    public abstract EntryDao getentryDao();
}
