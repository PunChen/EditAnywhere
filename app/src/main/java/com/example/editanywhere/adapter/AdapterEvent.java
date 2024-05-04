package com.example.editanywhere.adapter;

public class AdapterEvent<T> {
    private Long arg1;
    private Integer arg2;
    private T obj;

    public AdapterEvent() {}
    public AdapterEvent(AdapterEventType type) {this.type = type;}

    private AdapterEventType type;

    public Long getArg1() {
        return arg1;
    }

    public void setArg1(Long arg1) {
        this.arg1 = arg1;
    }

    public Integer getArg2() {
        return arg2;
    }

    public void setArg2(Integer arg2) {
        this.arg2 = arg2;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public AdapterEventType getType() {
        return type;
    }

    public void setType(AdapterEventType type) {
        this.type = type;
    }
}
