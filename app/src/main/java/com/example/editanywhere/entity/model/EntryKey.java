package com.example.editanywhere.entity.model;

import java.util.Objects;

public class EntryKey {
    private String entryName;
    private Integer version;

    public EntryKey(String entryName, Integer version) {
        this.entryName = entryName;
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntryKey entryKey = (EntryKey) o;
        return entryName.equals(entryKey.entryName) && version.equals(entryKey.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryName, version);
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
