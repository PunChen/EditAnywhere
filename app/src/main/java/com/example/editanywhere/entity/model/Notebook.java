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
@Entity(tableName = DBConst.TAB_NAME_NOTEBOOK, indices = {@Index(value = {"notebookName"}, unique = true)})
@TypeConverters({DateConverter.class})
public class Notebook {

    /**
     * 自增主键
     */
    @PrimaryKey(autoGenerate = true)
    private Long id;

    /**
     * 笔记本名称
     */
    @ColumnInfo(name = "notebookName", defaultValue = "")
    @NonNull
    private String notebookName;

    /**
     * 描述
     */
    @ColumnInfo(name = "description", defaultValue = "")
    private String description;

    /**
     * 创建时间
     */
    @ColumnInfo(name = "createTime")
    private Long createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNotebookName() {
        return notebookName;
    }

    public void setNotebookName(String notebookName) {
        this.notebookName = notebookName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }


    @Override
    public String toString() {
        return "Notebook{" +
                "id=" + id +
                ", notebookName='" + notebookName + '\'' +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
