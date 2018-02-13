package com.jollychic.holmes.model;

import lombok.Data;
import java.util.Date;

/**
 * Created by WIN7 on 2018/1/4.
 */

@Data
public class SourceConnection {
    private String connectionId;    //uuid主键
    private String connectionName;  //唯一索引
    private String sourceType;      //数据源类型，mysql/hive/kafka
    private String connectionInfo;  //连接信息，json字符串
    private String author;
    private Date createdAt;
    private Date updatedAt;
}