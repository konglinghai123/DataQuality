package com.jollychic.holmes.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by WIN7 on 2018/2/7.
 */
@Data
public class KafkaMaxOffsetManagement {
    private Integer kafkaMaxOffsetManagementId;      //自增主键
    private String ruleId;                  //规则表id
    private Long maxOffset;              //所有partition最大offset之和
    private Integer version;                //版本号
    private Date createdAt;
    private Date updatedAt;
}
