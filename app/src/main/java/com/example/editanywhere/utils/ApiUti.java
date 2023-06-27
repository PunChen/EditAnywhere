package com.example.editanywhere.utils;

import android.net.ipsec.ike.IkeIpv4AddrIdentification;
import android.os.Build;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ApiUti {
    public static final String API_ENTRY_QUERY = "/wiki/entry/query";
    public static final String API_ENTRY_QUERY_ALL = "/wiki/entry/queryAll";
    public static final String API_ENTRY_EDIT = "/wiki/entry/edit";
    public static final String API_ENTRY_DELETE = "/wiki/entry/delete";
    public static final String API_ENTRY_ADD = "/wiki/entry/add";

    public static class Builder{
        public Map<String,Object> body = null;
        public Map<String,Object> build(){
            return body;
        }
        public Builder(){
            body = new HashMap<>();
        }
        public Builder add(String key,Object val){
            if(body != null){
                body.put(key,val);
            }
            return this;
        }

        public Builder remove(String key,Object val){
            if(body != null){
                body.remove(key);
            }
            return this;
        }
    }



}
