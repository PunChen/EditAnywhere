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

    @Query("select id from (select entryName, max(id) id from entry group by entryName) T")
    List<Long> queryAllIds();

    @Query("select * from entry where id=:id limit 1")
    Entry queryById(Long id);

    @Query("select * from entry")
    List<Entry> queryAll();

    @Query("select * from entry where id in (:ids)")
    List<Entry> queryAllByIds(List<Long> ids);

    @Query("select * from entry where entryName like '%'||:entryName||'%'")
    List<Entry> queryByEntryName(String entryName);

    @Query("delete from entry where id=:id")
    void deleteById(Long id);

    @Query("update entry set entryContent=:entryContent, updateTime=createTime where id=:id")
    void updateEntryContentById(Long id, String entryContent);

}
