package com.jollychic.holmes.source;

import com.jollychic.holmes.source.ConnConfig.ConnConfig;
import lombok.Data;

import java.util.List;

/**
 * Created by WIN7 on 2018/1/15.
 */
@Data
public class SourceConfig {
    private ConnConfig connConfig;
    private String tableName;               //table or topic
    private String partitionName;           //mysql/hive使用
    private String partitionValue;          //例如'20180115'
    private List<Integer> partitionList;    //kafka使用，为null时读取所有partitions
}
