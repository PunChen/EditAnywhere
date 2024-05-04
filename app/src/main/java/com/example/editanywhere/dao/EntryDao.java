package com.example.editanywhere.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.editanywhere.entity.model.Entry;

import java.util.List;
import java.util.Set;

@Dao
public interface EntryDao {
    Long INSERT_FAIL_RETURN_ID = -1L;

    @Insert
    Long insertEntry(Entry entry);

    @Update
    void updateEntry(Entry entry);

    @Insert
    List<Long> insertBatch(List<Entry> entryList);

    @Query("select id from (select entryName, max(id) id from entry group by entryName order by createTime desc) T ")
    List<Long> queryAllIds();

    @Query("select * from entry where id=:id limit 1")
    Entry queryById(Long id);

    @Query("select * from entry order by createTime desc")
    List<Entry> queryAll();

    @Query("select * from entry where id in (:ids) order by createTime desc")
    List<Entry> queryAllByIds(List<Long> ids);

    @Query("select * from entry where entryName like '%'||:entryName||'%' or entryContent like '%'||:entryName||'%' order by createTime desc")
    List<Entry> queryByEntryNameOrContent(String entryName);

    @Query("select * from entry where id in (select entryId from entry_book_key where bookId = :bookId)" +
            "and (entryName like '%'||:entryName||'%' or entryContent like '%'||:entryName||'%') order by createTime desc")
    List<Entry> queryByEntryNameOrContentInBook(Long bookId, String entryName);

    @Query("delete from entry where id=:id")
    void deleteById(Long id);

    @Query("delete from entry where id in(:idSet)")
    void deleteByIdSet(Set<Long> idSet);


}
