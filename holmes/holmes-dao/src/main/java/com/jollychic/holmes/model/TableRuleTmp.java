package com.jollychic.holmes.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by WIN7 on 2018/1/5.
 */
@Data
public class TableRuleTmp {
    private Integer tmpId;      //自增主键
    private String tableId;     //源表id，外键
    private String ruleId;     //规则表id，外键
    private String author;
    private Date createdAt;
    private Date updatedAt;
}
