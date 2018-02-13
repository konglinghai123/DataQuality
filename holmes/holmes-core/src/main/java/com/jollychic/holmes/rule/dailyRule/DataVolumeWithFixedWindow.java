package com.jollychic.holmes.rule.dailyRule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.common.utils.DateUtils;
import com.jollychic.holmes.mapper.KafkaMaxOffsetManagementMapper;
import com.jollychic.holmes.model.*;
import com.jollychic.holmes.operator.Comparison;
import com.jollychic.holmes.rule.RuleRunnerDefault;
import com.jollychic.holmes.rule.ruleConfig.DataVolumeWithFixedWindowConfig;
import com.jollychic.holmes.source.ConnConfig.KafkaConfig;
import com.jollychic.holmes.source.SourceConfig;
import com.jollychic.holmes.source.SourceFactory;
import com.jollychic.holmes.source.SourceReader;
import com.jollychic.holmes.source.SourceType;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by WIN7 on 2018/2/1.
 */

//数据源为kafka，持续监控一段时间内的数据量
public class DataVolumeWithFixedWindow extends RuleRunnerDefault {
    private DataVolumeWithFixedWindowConfig dataVolumeWithFixedWindowConfig;
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
        if (sourceInfo.get("partitionList") != null) {
            List<Integer> partitionList = JSON.parseObject(sourceInfo.get("partitionList"), new TypeReference<List<Integer>>() {
            });
            sourceConfig.setPartitionList(partitionList);
        }
        if(SourceType.KAFKA.getType().equalsIgnoreCase(sourceConnection.getSourceType())) {
            sourceConfig.setConnConfig(JSON.parseObject(sourceConnection.getConnectionInfo(), KafkaConfig.class));
        } else {
            throw new ServiceException(ErrorCode.RULE_PARSE_ERROR, "source type must be kafka");
        }
        // 初始化source reader
        sourceReader = new SourceFactory().getSourceReader(sourceConnection.getSourceType());
        sourceReader.init(sourceConfig);

        this.dataVolumeWithFixedWindowConfig = JSON.parseObject(rule.getRuleExpression(), DataVolumeWithFixedWindowConfig.class);
        long currOffset = this.sourceReader.getCount();
        long actualValue;
        KafkaMaxOffsetManagement kafkaMaxOffsetManagement = kafkaMaxOffsetManagementMapper.getByRuleIdAndMaxVersion(rule.getRuleId());
        if(kafkaMaxOffsetManagement==null) {
            kafkaMaxOffsetManagement = new KafkaMaxOffsetManagement();
            kafkaMaxOffsetManagement.setRuleId(rule.getRuleId());
            kafkaMaxOffsetManagement.setMaxOffset(currOffset);
            kafkaMaxOffsetManagement.setVersion(1);
            kafkaMaxOffsetManagementMapper.insert(kafkaMaxOffsetManagement);
            return;
        }
        long lastOffset = kafkaMaxOffsetManagement.getMaxOffset();
        actualValue = currOffset - lastOffset;
        kafkaMaxOffsetManagement.setMaxOffset(currOffset);
        kafkaMaxOffsetManagement.setVersion(kafkaMaxOffsetManagement.getVersion()+1);
        kafkaMaxOffsetManagementMapper.insert(kafkaMaxOffsetManagement);
        boolean result = Comparison.compare(actualValue, dataVolumeWithFixedWindowConfig.getValue(), dataVolumeWithFixedWindowConfig.getOperator());
        if (result) {
            String sourceName = sourceConnection.getConnectionName() + "." + sourceTable.getTableName();
            this.outputAlarm(true, sourceName, "监控时间窗口为：" +
                    DateUtils.format(kafkaMaxOffsetManagement.getCreatedAt(), "yyyy-MM-dd HH:mm:ss") + " - " +
                    DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") + ", 数据量为：" + actualValue + ", 触发条件：" +
                    dataVolumeWithFixedWindowConfig.getOperator() + dataVolumeWithFixedWindowConfig.getValue());
        } else {
            this.outputAlarm(false);
        }
        kafkaMaxOffsetManagementMapper.deleteByDay(7);
    }

}
