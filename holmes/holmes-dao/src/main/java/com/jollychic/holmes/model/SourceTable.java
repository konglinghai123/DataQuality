package com.jollychic.holmes.model;

import lombok.Data;
import java.util.Date;

/**
 * Created by WIN7 on 2018/1/5.
 */
@Data
public class SourceTable {
    private String tableId;         //uuid主键
    private String tableName;       //与connectionId组成唯一索引
    private String connectionId;   //数据源连接表id，外键
    private String tableSchema;     //表结构，json字符串
    private String author;
    private Date createdAt;
    private Date updatedAt;
}
