package com.example.editanywhere.entity.model;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
/**
 * 词条表
 * tab_entry
 * author: chainhow
 * date: 2023-06-15 22:53:00
 */
@Data
@Log4j2
public class Entry implements Serializable {
    /**
     * 自增主键
     */
    private Integer id;

    /**
     * 词条名称
     */
    private String entryName;

    /**
     * 其他词条名称
     */
    private String entryNameOther;

    /**
     * 版本
     */
    private Integer version;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否有效
     */
    private Boolean valid;

    /**
     * 词条内容列表
     */
    private String entryContent;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName == null ? null : entryName.trim();
    }

    public String getEntryNameOther() {
        return entryNameOther;
    }

    public void setEntryNameOther(String entryNameOther) {
        this.entryNameOther = entryNameOther == null ? null : entryNameOther.trim();
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getEntryContent() {
        return entryContent;
    }

    public void setEntryContent(String entryContent) {
        this.entryContent = entryContent == null ? null : entryContent.trim();
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
                ", valid=" + valid +
                ", entryContent='" + entryContent + '\'' +
                '}';
    }
}