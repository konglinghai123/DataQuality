package com.jollychic.holmes.rule.dailyRule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.common.utils.DateUtils;
import com.jollychic.holmes.model.Execution;
import com.jollychic.holmes.model.Rule;
import com.jollychic.holmes.model.SourceConnection;
import com.jollychic.holmes.model.SourceTable;
import com.jollychic.holmes.operator.Comparison;
import com.jollychic.holmes.rule.RuleRunnerDefault;
import com.jollychic.holmes.rule.ruleConfig.ComparisonToTheSameTimeConfig;
import com.jollychic.holmes.source.ConnConfig.HiveConfig;
import com.jollychic.holmes.source.ConnConfig.MysqlConfig;
import com.jollychic.holmes.source.SourceConfig;
import com.jollychic.holmes.source.SourceFactory;
import com.jollychic.holmes.source.SourceReader;
import com.jollychic.holmes.source.SourceType;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by WIN7 on 2018/2/7.
 */
@Slf4j
public class ComparisonToTheSameTime extends RuleRunnerDefault {
    private ComparisonToTheSameTimeConfig comparisonToTheSameTimeConfig;
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

        this.comparisonToTheSameTimeConfig = JSON.parseObject(rule.getRuleExpression(), ComparisonToTheSameTimeConfig.class);
        String partitionDifferDayValue = DateUtils.getOneUnitAndDidderDayAgoTime(sourceInfo.get("partitionValueFormat"),
                comparisonToTheSameTimeConfig.getDifferDay());
        List<String> results = Lists.newArrayList();
        for(ComparisonToTheSameTimeConfig.ComparisonToTheSameTimeCondition condition : comparisonToTheSameTimeConfig.getConditions()) {
            String dimensionString = "";
            if(condition.getDimensions()!=null && condition.getDimensions().size()>0) {
                dimensionString += "维度为：";
                for(ComparisonToTheSameTimeConfig.ComparisonToTheSameTimeDimension dimension : condition.getDimensions()) {
                    dimensionString += dimension.getDimensionName() + "=" +dimension.getDimensionValues()+" ";
                }
                dimensionString += "， ";
            }

            String sql = this.getSql(condition, partitionValue);
            log.info("sql: "+sql);
            List<String> currValuesList = this.sourceReader.read(sql);
            sql = this.getSql(condition, partitionDifferDayValue);
            log.info("sql: "+sql);
            List<String> differDayValuesList = this.sourceReader.read(sql);
            if(currValuesList.size()>0 && differDayValuesList.size()>0) {
                String currValues = currValuesList.get(0);
                String differDayValues = differDayValuesList.get(0);
                Map<String, Object> currValuesMap = JSON.parseObject(currValues, new TypeReference<Map<String, Object>>() {
                });
                Map<String, Object> differDayValuesMap = JSON.parseObject(differDayValues, new TypeReference<Map<String, Object>>() {
                });
                for (ComparisonToTheSameTimeConfig.ComparisonToTheSameTimeColumn column : condition.getColumns()) {
                    Object currValue = currValuesMap.get(column.getColumnName().trim());
                    Object differDayValue = differDayValuesMap.get(column.getColumnName().trim());
                    System.out.println(currValuesMap);
                    System.out.println(new BigDecimal(String.valueOf(currValue)) + "  "+differDayValue);
                    Double actualValue = Comparison.divide(currValue, differDayValue);
                    if(actualValue==null) {
                        continue;
                    }
                    if (Comparison.compare(actualValue, column.getPercent(), column.getOperator())) {
                        results.add("监控分区为："+sourceConfig.getPartitionName()+"="+sourceConfig.getPartitionValue()+", "+dimensionString+
                                column.getAggregation()+"(" + column.getColumnName() + ")同比为：" + actualValue +
                                ", 触发条件：" + column.getOperator() + column.getPercent());
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

    private String getSql(ComparisonToTheSameTimeConfig.ComparisonToTheSameTimeCondition condition, String partitionValue) {
        StringBuffer sql = new StringBuffer("select ").append(sourceConfig.getPartitionName());
        for(ComparisonToTheSameTimeConfig.ComparisonToTheSameTimeColumn column : condition.getColumns()) {
            sql.append(",").append(column.getAggregation()).append("(").append(column.getColumnName()).append(") ").append(column.getColumnName());
        }
        sql.append(" from ").append(sourceConfig.getTableName());
        sql.append(" where ").append(sourceConfig.getPartitionName()).append("='").append(partitionValue).append("'");
        if(condition.getDimensions()!=null) {
            for (ComparisonToTheSameTimeConfig.ComparisonToTheSameTimeDimension dimension : condition.getDimensions()) {
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
        return new String(sql);
    }
}
