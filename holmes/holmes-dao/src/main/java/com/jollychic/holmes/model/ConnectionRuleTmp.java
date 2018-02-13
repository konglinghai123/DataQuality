package com.jollychic.holmes.model;

import lombok.Data;

import java.util.Date;
/**
 * Created by WIN7 on 2018/1/26.
 */
@Data
public class ConnectionRuleTmp {
    private Integer connectionRuleTmpId;    //自增主键
    private String ruleId;                  //规则表id
    private String connectionId;            //连接表Id
    private String author;
    private Date createdAt;
    private Date updatedAt;
}
