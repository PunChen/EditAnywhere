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
    public int insert(Entry entry);

    @Query("select * from entry where id=:id")
    public Entry findById(Integer id);

    @Query("select * from entry where valid=:valid")
    public List<Entry> findByValid(Boolean valid);

    @Delete
    public int deleteById(Entry entry);

    @Update
    public int updateContentById(Entry entry);

}
