package com.example.editanywhere.entity.view;

import java.util.List;

import lombok.Data;

@Data
public class SimpleRequest {
    //添加节点
    private String nodeName;
    private String nodeKey;

    //建立节点连接
    private Boolean makeConnection;
    private String nodeKeyFrom;
    private String nodeKeyTo;

    private String entryName;
    private List<String> entryContent;


}
