package com.example.editanywhere.entity.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.editanywhere.utils.DBConst;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

/**
 * 词条表
 * tab_entry
 * author: chainhow
 * date: 2023-06-15 22:53:00
 */
@Data
@Log4j2
@Entity(tableName = DBConst.ENTRY_DB_NAME, indices = {@Index(value = {"entryName", "version"}, unique = true)})
@TypeConverters({DateConverter.class, ListConverter.class})
public class Entry implements Serializable {
    /**
     * 自增主键
     */
    @PrimaryKey(autoGenerate = true)
    private Long id;

    /**
     * 词条名称
     */
    @ColumnInfo(name = "entryName", defaultValue = "")
    @NonNull
    private String entryName;

    /**
     * 其他词条名称
     */
    @ColumnInfo(name = "entryNameOther")
    private String entryNameOther;

    /**
     * 版本
     */
    @ColumnInfo(name = "version", defaultValue = "0")
    @NonNull
    private Integer version;

    /**
     * 创建时间
     */
    @ColumnInfo(name = "createTime")
    private Long createTime;

    /**
     * 更新时间
     */
    @ColumnInfo(name = "updateTime")
    private Long updateTime;


    /**
     * 词条内容列表
     */
    @ColumnInfo(name = "entryContent")
    private List<String> entryContent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public String getEntryNameOther() {
        return entryNameOther;
    }

    public void setEntryNameOther(String entryNameOther) {
        this.entryNameOther = entryNameOther;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }


    public List<String> getEntryContent() {
        return entryContent;
    }

    public void setEntryContent(List<String> entryContent) {
        this.entryContent = entryContent;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "id=" + id +
                ", entryName='" + entryName + '\'' +
                ", entryNameOther='" + entryNameOther + '\'' +
                ", version=" + version +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", entryContent='" + entryContent + '\'' +
                '}';
    }
}