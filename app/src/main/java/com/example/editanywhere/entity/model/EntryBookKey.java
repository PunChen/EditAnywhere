package com.example.editanywhere.entity.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.editanywhere.entity.converter.DateConverter;
import com.example.editanywhere.utils.DBConst;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Data
@Log4j2
@Entity(tableName = DBConst.TAB_NAME_ENTRY_BOOK_KEY, indices = {@Index(value = {"entryId","bookId"}, unique = true)})
@TypeConverters({DateConverter.class})
public class EntryBookKey {

    /**
     * 自增主键
     */
    @PrimaryKey(autoGenerate = true)
    private Long id;

    /**
     * 词条ID
     */
    @ColumnInfo(name = "entryId")
    @NonNull
    private Long entryId;

    /**
     * 笔记本ID
     */
    @ColumnInfo(name = "bookId")
    @NonNull
    private Long bookId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}
