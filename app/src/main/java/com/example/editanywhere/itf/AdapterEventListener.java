package com.example.editanywhere.itf;

import com.example.editanywhere.entity.view.AdapterEvent;

public interface AdapterEventListener {
    void onEvent(AdapterEvent<?> event);
}
