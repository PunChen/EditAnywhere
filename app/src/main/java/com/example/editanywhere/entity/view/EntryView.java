package com.example.editanywhere.entity.view;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class EntryView implements Serializable {
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 词条名称
     */
    private String entryName;

    /**
     * 其他词条名称
     */
    private String entryNameOther;


    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */

    private Long updateTime;

    /**
     * 词条内容列表
     */
    private List<String> entryContent;

    private Boolean checked;
    private Boolean showCheckbox;

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

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }


    public Boolean getShowCheckbox() {
        return showCheckbox;
    }

    public void setShowCheckbox(Boolean showCheckbox) {
        this.showCheckbox = showCheckbox;
    }

    @Override
    public String toString() {
        return "EntryView{" +
                "id=" + id +
                ", entryName='" + entryName + '\'' +
                ", entryNameOther='" + entryNameOther + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", entryContent=" + entryContent +
                ", checked=" + checked +
                ", showCheckbox=" + showCheckbox +
                '}';
    }
}