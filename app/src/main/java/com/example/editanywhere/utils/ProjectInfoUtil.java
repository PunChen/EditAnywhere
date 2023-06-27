package com.example.editanywhere.utils;

import java.util.HashMap;
import java.util.Map;

public class ProjectInfoUtil {
    public static Map<String,String> relatedProjectUrl = new HashMap<>();
    static {
        relatedProjectUrl.put("Java后端","https://github.com/PunChen/AGI-Demo");
        relatedProjectUrl.put("MybatisGenerator","https://github.com/PunChen/MybatisGenerator");
        relatedProjectUrl.put("Vue前端","https://github.com/PunChen/AGI-Demo-Vue");
        relatedProjectUrl.put("Android前端","https://github.com/PunChen/EditAnywhere");
    }

    public static String getRelatedProjectInfo(){
        StringBuilder sb = new StringBuilder();
        sb.append("相关项目：\n");
        for(String key:relatedProjectUrl.keySet()){
            sb.append(key).append(": \n").append(relatedProjectUrl.get(key)).append("\n");
        }
        return  sb.toString();
    }
}
