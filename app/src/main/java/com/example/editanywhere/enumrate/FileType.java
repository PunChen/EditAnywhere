package com.example.editanywhere.enumrate;

public enum FileType {
    CSV(".csv"),
    JSON(".json");
    public final String type;
    FileType(String type) {
        this.type = type;
    }
}
