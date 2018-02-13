package com.jollychic.holmes.rule.dailyRule;

import com.alibaba.fastjson.*;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.common.utils.DateUtils;
import com.jollychic.holmes.model.Execution;
import com.jollychic.holmes.model.Rule;
import com.jollychic.holmes.model.SourceConnection;
import com.jollychic.holmes.model.SourceTable;
import com.jollychic.holmes.operator.Comparison;
import com.jollychic.holmes.rule.RuleRunnerDefault;
import com.jollychic.holmes.rule.ruleConfig.DataVolumeConfig;
import com.jollychic.holmes.source.ConnConfig.HiveConfig;
import com.jollychic.holmes.source.ConnConfig.MysqlConfig;
import com.jollychic.holmes.source.SourceConfig;
import com.jollychic.holmes.source.SourceFactory;
import com.jollychic.holmes.source.SourceReader;
import com.jollychic.holmes.source.SourceType;

import java.util.Map;

/**
 * Created by WIN7 on 2018/1/10.
 */
public class DataVolume extends RuleRunnerDefault {
    private DataVolumeConfig dataVolumeConfig;
    private Map<String, String> sourceInfo;
    private SourceTable sourceTable;
    private SourceConnection sourceConnection;
    private SourceConfig sourceConfig = new SourceConfig();
    private SourceReader sourceReader;

    @Override
    public void run() {
        sourceInfo = JSON.parseObject(rule.getSourceInfo(), new TypeReference<Map<String, String>>(){});
        sourceTable = sourceTableMapper.get(sourceInfo.get("sourceTableId"));
        sourceConnection = sourceConnectionMapper.get(sourceTable.getConnectionId());
        sourceConfig.setTableName(sourceTable.getTableName());
        String partitionValue = DateUtils.getOneUnitAgoTime(sourceInfo.get("partitionValueFormat"));
        sourceConfig.setPartitionName(sourceInfo.get("partitionName"));
        sourceConfig.setPartitionValue(partitionValue);
        if(SourceType.MYSQL.getType().equalsIgnoreCase(sourceConnection.getSourceType())) {
            sourceConfig.setConnConfig(JSON.parseObject(sourceConnection.getConnectionInfo(), MysqlConfig.class));
        } else if(SourceType.HIVE.getType().equalsIgnoreCase(sourceConnection.getSourceType())) {
            sourceConfig.setConnConfig(JSON.parseObject(sourceConnection.getConnectionInfo(), HiveConfig.class));
        } else {
            throw new ServiceException(ErrorCode.RULE_PARSE_ERROR, "source type must be mysql or hive");
        }
        // 初始化source reader
        sourceReader = new SourceFactory().getSourceReader(sourceConnection.getSourceType());
        sourceReader.init(sourceConfig);

        this.dataVolumeConfig = JSON.parseObject(rule.getRuleExpression(), DataVolumeConfig.class);
        long actualValue = this.sourceReader.getCount();
        boolean result = Comparison.compare(actualValue, dataVolumeConfig.getValue(), dataVolumeConfig.getOperator());
        if (result) {
            String sourceName = sourceConnection.getConnectionName()+"."+sourceTable.getTableName();
            this.outputAlarm(true, sourceName, "监控分区为："+sourceConfig.getPartitionName()+"="+sourceConfig.getPartitionValue()+
                    ", 数据量为：" + actualValue + ", 触发条件：" + dataVolumeConfig.getOperator() + dataVolumeConfig.getValue());
        } else {
            this.outputAlarm(false);
        }
    }

}
