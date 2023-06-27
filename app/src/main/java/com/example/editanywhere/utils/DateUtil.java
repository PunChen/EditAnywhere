package com.example.editanywhere.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateUtil {
    public static final String DEFAULT_DATE_FORMAT  = "yyyy-MM-dd HH:mm:ss";

    public static String dateFormat(Date date,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }
    private static List<Integer> list = new ArrayList<>();
    private static int ind = 0;
    static {
        for (int i=0;i<1000;i++){
            list.add(i);
        }
    }
    private static int getNext(){
        return  list.get(ind++);
    }
    public static void main(String[] args) {
        int target = 200;
        int bottom = 0;
        int up = 1000;
        while(true){
            int v = getNext();
            if(v < bottom || v > up) continue;
            if(target > v){
                bottom = v;
            }
            if(target < v){
                up = v;
            }
            if (target == v) {
                System.out.println("found");
                break;
            }
            if(bottom == up){
                System.out.println("not found");
                break;
            }
        }
    }
}
