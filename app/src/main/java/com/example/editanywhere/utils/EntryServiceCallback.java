package com.example.editanywhere.utils;

public interface EntryServiceCallback<T> {
    void onSuccess(T result);

    void onFailure(String errMsg);
}
