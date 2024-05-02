package com.example.editanywhere.utils;

import java.util.HashSet;
import java.util.Set;

public class DBConst {
    public static final String DB_NAME = "edit_anywhere";
    public static final String TAB_NAME_ENTRY = "entry";
    public static final String TAB_NAME_NOTEBOOK = "notebook";
    public static final String TAB_NAME_ENTRY_BOOK_KEY = "entry_book_key";
    public static final Set<String> TAB_SET = new HashSet<>();

    // 添加数据表务必同步添加
    static {
        TAB_SET.add(TAB_NAME_ENTRY);
        TAB_SET.add(TAB_NAME_NOTEBOOK);
        TAB_SET.add(TAB_NAME_ENTRY_BOOK_KEY);
    }
}
