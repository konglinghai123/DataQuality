package com.jollychic.holmes.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by WIN7 on 2018/1/5.
 */
@Data
public class Execution {
    private Integer executionId;    //自增主键
    private String executionName;   //任务名称，由规则名+"-"+随机串组成
    private String ruleId;      //规则id，外键
    private Integer status;     //任务状态，new(0),running(1),finished(2),stopped(3),error(4)
    private String errorInfo;   //status为error时的错误信息
    private Date createdAt;
    private Date updatedAt;
}
