package com.jollychic.holmes.model;

import lombok.Data;

import java.util.Date;
/**
 * Created by WIN7 on 2018/1/26.
 */
@Data
public class TableVolumeStateManagement {
    private Integer tableVolumeStateManagementId;      //自增主键
    private String ruleId;                  //规则表id
    private String tableNames;              //所有源表名
    private Integer version;                //版本号
    private Date createdAt;
    private Date updatedAt;
}
