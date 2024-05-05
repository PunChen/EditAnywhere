package com.example.editanywhere.utils;

import java.util.Objects;

public final class StringUtils {
    public static boolean isEmpty(String str) {
        return Objects.isNull(str) || str.length() == 0;
    }

    public static boolean isAllBlank(String str) {
        return isEmpty(str) || isEmpty(str.trim());
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
