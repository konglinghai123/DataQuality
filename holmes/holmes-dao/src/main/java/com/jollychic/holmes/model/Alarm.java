package com.jollychic.holmes.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by WIN7 on 2018/1/10.
 */
@Data
public class Alarm {
    private Integer alarmId;     //自增主键
    private Integer executionId;//任务表id，外键
    private String ruleId;     //规则表id，外键
    private Boolean alarm;      //是否报警
    private String alarmInfo;  //报警信息
    private Date createdAt;
    private Date updatedAt;
}
