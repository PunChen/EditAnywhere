package com.example.editanywhere.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.editanywhere.entity.model.EntryBookKey;
import com.example.editanywhere.entity.model.Notebook;

import java.util.List;
import java.util.Set;

@Dao
public interface EntryBookKeyDao {

    @Insert
    Long insert(EntryBookKey entryBookKey);

    @Query("select * from entry_book_key where id=:id limit 1")
    EntryBookKey queryById(Long id);

    @Query("select * from entry_book_key")
    List<EntryBookKey> queryAll();

    @Query("select * from entry_book_key where bookId =:bookId and  entryId =:entryId limit 1")
    EntryBookKey queryByEntryIdAndBookId(Long entryId, Long bookId);

    @Query("select * from entry_book_key where bookId =:bookId")
    List<EntryBookKey> queryEntryIdsByBookId(Long bookId);

    @Query("select * from entry_book_key where entryId =:entryId")
    List<EntryBookKey> queryBookIdsByEntryId(Long entryId);

    @Query("delete from entry_book_key where bookId=:bookId")
    void deleteByBookId(Long bookId);

    @Query("delete from entry_book_key where entryId in (:entryIdSet)")
    void deleteByEntryIdSet(Set<Long> entryIdSet);

    @Query("delete from entry_book_key where entryId=:entryId")
    void deleteByEntryId(Long entryId);

}
