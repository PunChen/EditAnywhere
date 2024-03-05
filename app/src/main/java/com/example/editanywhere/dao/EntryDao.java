package com.example.editanywhere.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.editanywhere.entity.model.Entry;

import java.util.List;

@Dao
public interface EntryDao {
    Long INSERT_FAIL_RETURN_ID = -1L;

    @Insert
    Long insertEntry(Entry entry);

    @Insert
    List<Long> insertBatch(List<Entry> entryList);

    @Query("select id from (select entryName, max(id) id from entry where valid=:valid group by entryName) T")
    List<Long> queryAllIds(Boolean valid);

    @Query("select * from entry where id=:id limit 1")
    Entry queryById(Long id);

    @Query("select * from entry where valid=:valid")
    List<Entry> queryByValid(Boolean valid);

    @Query("select * from entry")
    List<Entry> queryAll();

    @Query("select * from entry where id in (:ids)")
    List<Entry> queryAllByIds(List<Long> ids);

    @Query("update entry set valid=:valid where entryName=:entryName")
    int deleteByEntryName(String entryName, Boolean valid);

    @Query("update entry set entryContent=:entryContent where id=:id")
    int updateEntryContentById(Long id, String entryContent);

    @Query("select * from entry " +
            "where entryName=:entryName " +
            "and valid=:valid " +
            "and version=(select max(version) from entry where entryName=:entryName and valid=:valid)")
    Entry queryLatestByEntryName(String entryName, Boolean valid);
}
