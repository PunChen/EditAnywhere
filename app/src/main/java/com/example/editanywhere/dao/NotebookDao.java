package com.example.editanywhere.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.editanywhere.entity.model.Notebook;

import java.util.List;

@Dao
public interface NotebookDao {

    @Insert
    Long insert(Notebook notebook);

    @Query("select * from notebook where id=:id limit 1")
    Notebook queryById(Long id);

    @Query("select * from notebook")
    List<Notebook> queryAll();

    @Query("select * from notebook where id in (:ids)")
    List<Notebook> queryAllByIds(List<Long> ids);

    @Query("delete from notebook where id=:id")
    int deleteById(Long id);

    @Query("update notebook set notebookName=:notebookName where id=:id")
    int updateBookNameById(Long id, String notebookName);

    @Update
    void updateNotebook(Notebook notebook);


}
