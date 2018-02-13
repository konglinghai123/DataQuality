package com.jollychic.holmes.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by WIN7 on 2018/1/5.
 */
@Data
public class Rule {
    private String ruleId;         //uuid主键
    private String ruleName;       //唯一索引
    private String ruleType;        //目前支持data_volume、key_indicator
    private String sourceInfo;     //数据源表信息，json字符串
    private String ruleExpression;  //规则表达式，json字符串
    private String ruleDescription; //规则文字描述
    private String alarmType;       //微信、邮件、微信和邮件
    private String alarmUser;       //多个user邮箱，逗号隔开
    private String author;
    private Date createdAt;
    private Date updatedAt;
}
