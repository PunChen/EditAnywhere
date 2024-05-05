package com.example.editanywhere.entity.view;

public class NotebookView {
    private String notebookName;

    private Long id;

    private boolean isAll;

    private Boolean selected;

    public String getNotebookName() {
        return notebookName;
    }

    public void setNotebookName(String notebookName) {
        this.notebookName = notebookName;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isAll() {
        return isAll;
    }

    public void setAll(boolean all) {
        isAll = all;
    }

    @Override
    public String toString() {
        return "NotebookView{" +
                "notebookName='" + notebookName + '\'' +
                ", id=" + id +
                ", isAll=" + isAll +
                ", selected=" + selected +
                '}';
    }
}
