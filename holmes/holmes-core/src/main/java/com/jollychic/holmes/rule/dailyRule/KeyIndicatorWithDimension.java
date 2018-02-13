package com.jollychic.holmes.rule.dailyRule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.common.utils.DateUtils;
import com.jollychic.holmes.model.Execution;
import com.jollychic.holmes.model.Rule;
import com.jollychic.holmes.model.SourceConnection;
import com.jollychic.holmes.model.SourceTable;
import com.jollychic.holmes.operator.Comparison;
import com.jollychic.holmes.rule.RuleRunnerDefault;
import com.jollychic.holmes.rule.ruleConfig.KeyIndicatorWithDimensionConfig;
import com.jollychic.holmes.source.ConnConfig.HiveConfig;
import com.jollychic.holmes.source.ConnConfig.MysqlConfig;
import com.jollychic.holmes.source.SourceConfig;
import com.jollychic.holmes.source.SourceFactory;
import com.jollychic.holmes.source.SourceReader;
import com.jollychic.holmes.source.SourceType;

import java.util.List;
import java.util.Map;

/**
 * Created by WIN7 on 2018/1/30.
 */
public class KeyIndicatorWithDimension extends RuleRunnerDefault {
    private KeyIndicatorWithDimensionConfig keyIndicatorWithDimensionConfig;
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

        this.keyIndicatorWithDimensionConfig = JSON.parseObject(rule.getRuleExpression(), KeyIndicatorWithDimensionConfig.class);
        List<String> results = Lists.newArrayList();
        for(KeyIndicatorWithDimensionConfig.KeyIndicatorWithDimensionCondition condition : keyIndicatorWithDimensionConfig.getConditions()) {
            String dimensionString = "";
            if(condition.getDimensions()!=null && condition.getDimensions().size()>0) {
                dimensionString += "维度为：";
                for(KeyIndicatorWithDimensionConfig.KeyIndicatorWithDimensionDimension dimension : condition.getDimensions()) {
                    dimensionString += dimension.getDimensionName() + "=" +dimension.getDimensionValues()+" ";
                }
                dimensionString += "， ";
            }
            StringBuffer sql = new StringBuffer("select ").append(sourceConfig.getPartitionName());
            for(KeyIndicatorWithDimensionConfig.KeyIndicatorWithDimensionColumn column : condition.getColumns()) {
                sql.append(",").append(column.getAggregation()).append("(").append(column.getColumnName()).append(") ").append(column.getColumnName());
            }
            sql.append(" from ").append(sourceConfig.getTableName());
            sql.append(" where ").append(sourceConfig.getPartitionName()).append("='").append(sourceConfig.getPartitionValue()).append("'");
            if(condition.getDimensions()!=null) {
                for (KeyIndicatorWithDimensionConfig.KeyIndicatorWithDimensionDimension dimension : condition.getDimensions()) {
                    sql.append(" and ").append(dimension.getDimensionName()).append(" in (");
                    int i = 0;
                    for (String dimensionValue : dimension.getDimensionValues()) {
                        sql.append("'").append(dimensionValue).append("'");
                        if (i < dimension.getDimensionValues().size() - 1) {
                            sql.append(",");
                        }
                        i++;
                    }
                    sql.append(")");
                }
            }
            sql.append(" group by "+sourceConfig.getPartitionName());
            sql.append(";");

            System.out.println("sql: "+sql);

            List<String> actualValuesList = this.sourceReader.read(new String(sql));
            if(actualValuesList.size()>0) {
                String actualValues = actualValuesList.get(0);
                Map<String, Object> actualValueMap = JSON.parseObject(actualValues, new TypeReference<Map<String, Object>>() {
                });
                for (KeyIndicatorWithDimensionConfig.KeyIndicatorWithDimensionColumn column : condition.getColumns()) {
                    Object actualValue = actualValueMap.get(column.getColumnName().trim());
                    if (Comparison.compare(actualValue, column.getValue(), column.getOperator())) {
                        results.add("监控分区为："+sourceConfig.getPartitionName()+"="+sourceConfig.getPartitionValue()+", "+dimensionString+
                                column.getAggregation()+"(" + column.getColumnName() + ")的值为：" + actualValue +
                                ", 触发条件：" + column.getOperator() + column.getValue());
                    }
                }
            }
        }
        if(results.size()>0) {
            StringBuffer alarmInfo = new StringBuffer("");
            for(String result : results) {
                alarmInfo.append(result);
                alarmInfo.append("</br>");
            }
            String sourceName = sourceConnection.getConnectionName()+"."+sourceTable.getTableName();
            this.outputAlarm(true, sourceName, String.valueOf(alarmInfo));
        } else {
            this.outputAlarm(false);
        }
    }
}
