package com.example.editanywhere.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.editanywhere.entity.model.Entry;

import java.util.List;

@Dao
public interface EntryDao {
    @Insert
    Long insertEntry(Entry entry);

    @Query("select * from entry where id=:id limit 1")
    Entry queryById(Long id);

    @Query("select * from entry where valid=:valid")
    List<Entry> queryByValid(Boolean valid);

    @Query("select * from entry")
    List<Entry> queryAll();

    @Query("update entry set valid=:valid where entryName=:entryName")
    int deleteByEntryName(String entryName, Boolean valid);

    @Query("update entry set entryContent=:entryContent where id=:id")
    int updateEntryContentById(Long id, String entryContent);

    @Query("select * from entry " +
            "where entryName=:entryName " +
            "and valid=:valid "+
            "and version=(select max(version) from entry where entryName=:entryName and valid=:valid)")
    Entry queryLatestByEntryName(String entryName, Boolean valid);
}
