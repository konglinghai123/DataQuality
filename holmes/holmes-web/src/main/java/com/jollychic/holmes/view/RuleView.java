package com.jollychic.holmes.view;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.common.utils.JSONUtils;
import com.jollychic.holmes.mapper.SourceConnectionMapper;
import com.jollychic.holmes.mapper.SourceTableMapper;
import com.jollychic.holmes.model.Rule;
import com.jollychic.holmes.model.SourceConnection;
import com.jollychic.holmes.model.SourceTable;
import com.jollychic.holmes.rule.RuleConfig;
import com.jollychic.holmes.rule.RuleType;
import com.jollychic.holmes.rule.dailyRule.DataVolumeWithFixedWindow;
import com.jollychic.holmes.rule.ruleConfig.*;
import com.jollychic.holmes.view.rule.TableVolumeConfigView;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by WIN7 on 2018/1/16.
 */
@Data
public class RuleView<S extends SourceInfoView,R extends RuleConfig> {
    private String ruleName;
    private String ruleType;
    private S sourceInfo;
    private R ruleInfo;
    private String ruleDescription;
    private String alarmType;
    private AlarmUser alarmUser;

    //数据库表监控使用
    @Data
    public static class SourceConnectionInfoView implements SourceInfoView{
        private String sourceConnectionName;
    }

    //数据源为mysql/hive使用
    @Data
    public static class SourceTableInfoView implements SourceInfoView{
        private String sourceConnectionName;
        private String sourceTableName;
        private String partitionName;
        private String partitionValueFormat;
    }

    //数据源为kafka使用
    @Data
    public static class SourceTopicInfoView implements SourceInfoView{
        private String sourceConnectionName;
        private String sourceTableName;
        private List<Integer> partitionList;    //可以为null
    }

    @Data
    public static class AlarmUser {
        private List<String> user;
        private List<String> email;
    }
    public Rule showRuleModel(SourceConnectionMapper sourceConnectionMapper, SourceTableMapper sourceTableMapper) {
        Rule rule = new Rule();
        rule.setRuleName(this.getRuleName());
        rule.setRuleType(this.getRuleType());
        if (RuleType.DATA_VOLUME.getType().equalsIgnoreCase(rule.getRuleType())
                ||RuleType.KEY_INDICATOR.getType().equalsIgnoreCase(rule.getRuleType())
                ||RuleType.KEY_INDICATOR_WITH_DIMENSION.getType().equalsIgnoreCase(rule.getRuleType())
                ||RuleType.COMPARSION_TO_THE_SAME_TIME.getType().equalsIgnoreCase(rule.getRuleType())) {
            SourceTableInfoView sourceTableInfoView = (SourceTableInfoView)this.getSourceInfo();
            Map<String, String> sourceTableInfo = JSON.parseObject(JSON.toJSONString(sourceTableInfoView), new TypeReference<Map<String, String>>(){});
            sourceTableInfo.put("sourceTableId", showSourceTableId(sourceTableInfoView.getSourceConnectionName(),
                    sourceTableInfoView.getSourceTableName(), sourceConnectionMapper, sourceTableMapper));
            sourceTableInfo.put("sourceConnectionName",null);
            sourceTableInfo.put("sourceTableName",null);
            rule.setSourceInfo(JSON.toJSONString(sourceTableInfo));
        } else if (RuleType.DATA_VOLUME_WITH_FIXED_WINDOW.getType().equalsIgnoreCase(rule.getRuleType())) {
            SourceTopicInfoView sourceTopicInfoView = (SourceTopicInfoView)this.getSourceInfo();
            Map<String, String> sourceTopicInfo = JSON.parseObject(JSON.toJSONString(sourceTopicInfoView), new TypeReference<Map<String, String>>(){});
            sourceTopicInfo.put("sourceTableId", showSourceTableId(sourceTopicInfoView.getSourceConnectionName(),
                    sourceTopicInfoView.getSourceTableName(), sourceConnectionMapper, sourceTableMapper));
            sourceTopicInfo.put("sourceConnectionName",null);
            sourceTopicInfo.put("sourceTableName",null);
            rule.setSourceInfo(JSON.toJSONString(sourceTopicInfo));
        } else if (RuleType.TABLE_VOLUME.getType().equalsIgnoreCase(rule.getRuleType())) {
            SourceConnectionInfoView sourceConnectionInfoView = (SourceConnectionInfoView)this.getSourceInfo();
            SourceConnection sourceConnection = sourceConnectionMapper.getByConnName(sourceConnectionInfoView.getSourceConnectionName());
            if (sourceConnection==null){
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "source connection doesn't exist");
            }
            String sourceConnectionId = sourceConnection.getConnectionId();
            Map<String, String> sourceConnectionInfo = JSON.parseObject(JSON.toJSONString(sourceConnectionInfoView), new TypeReference<Map<String, String>>(){});
            sourceConnectionInfo.put("sourceConnectionId",sourceConnectionId);
            sourceConnectionInfo.put("sourceConnectionName",null);
            rule.setSourceInfo(JSON.toJSONString(sourceConnectionInfo));
        }
        rule.setRuleExpression(JSON.toJSONString(this.getRuleInfo()));
        rule.setRuleDescription(this.getRuleDescription());
        rule.setAlarmType(this.getAlarmType());
        rule.setAlarmUser(JSON.toJSONString(this.getAlarmUser()));
        return rule;
    }

    public String showSourceTableId(String connectionName, String tableName, SourceConnectionMapper sourceConnectionMapper, SourceTableMapper sourceTableMapper) {
        SourceTable sourceTable = new SourceTable();
        SourceConnection sourceConnection = sourceConnectionMapper.getByConnName(connectionName);
        if (sourceConnection==null){
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "source connection doesn't exist");
        }
        sourceTable.setConnectionId(sourceConnection.getConnectionId());
        sourceTable.setTableName(tableName);
        sourceTable = sourceTableMapper.getByTableName(sourceTable);
        if (sourceTable==null){
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "source table doesn't exist");
        }
        return sourceTable.getTableId();
    }

    public static RuleView showRuleView(Rule rule, SourceConnectionMapper sourceConnectionMapper, SourceTableMapper sourceTableMapper) {
        RuleView ruleView = new RuleView();
        ruleView.ruleName = rule.getRuleName();
        ruleView.ruleType = rule.getRuleType();
        //sourceInfo判断
        if (RuleType.DATA_VOLUME.getType().equalsIgnoreCase(rule.getRuleType())
                ||RuleType.KEY_INDICATOR.getType().equalsIgnoreCase(rule.getRuleType())
                ||RuleType.KEY_INDICATOR_WITH_DIMENSION.getType().equalsIgnoreCase(rule.getRuleType())
                ||RuleType.COMPARSION_TO_THE_SAME_TIME.getType().equalsIgnoreCase(rule.getRuleType())) {
            String sourceTableId = JSONUtils.getString(rule.getSourceInfo(), "sourceTableId");
            ruleView.sourceInfo = JSON.parseObject(rule.getSourceInfo(), SourceTableInfoView.class);
            SourceTable sourceTable = sourceTableMapper.get(sourceTableId);
            if (sourceTable==null){
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "source table doesn't exist");
            }
            SourceTableInfoView sourceTableInfoView = (SourceTableInfoView)ruleView.sourceInfo;
            sourceTableInfoView.setSourceTableName(sourceTable.getTableName());
            SourceConnection sourceConnection = sourceConnectionMapper.get(sourceTable.getConnectionId());
            if (sourceConnection==null){
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "source connection doesn't exist");
            }
            sourceTableInfoView.setSourceConnectionName(sourceConnection.getConnectionName());
            ruleView.setSourceInfo(sourceTableInfoView);
        } else if (RuleType.DATA_VOLUME_WITH_FIXED_WINDOW.getType().equalsIgnoreCase(rule.getRuleType())) {
            String sourceTableId = JSONUtils.getString(rule.getSourceInfo(), "sourceTableId");
            ruleView.sourceInfo = JSON.parseObject(rule.getSourceInfo(), SourceTopicInfoView.class);
            SourceTable sourceTable = sourceTableMapper.get(sourceTableId);
            if (sourceTable==null){
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "source table doesn't exist");
            }
            SourceTopicInfoView sourceTopicInfoView = (SourceTopicInfoView)ruleView.sourceInfo;
            sourceTopicInfoView.setSourceTableName(sourceTable.getTableName());
            SourceConnection sourceConnection = sourceConnectionMapper.get(sourceTable.getConnectionId());
            if (sourceConnection==null){
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "source connection doesn't exist");
            }
            sourceTopicInfoView.setSourceConnectionName(sourceConnection.getConnectionName());
            ruleView.setSourceInfo(sourceTopicInfoView);
        } else if(RuleType.TABLE_VOLUME.getType().equalsIgnoreCase(rule.getRuleType())) {
            String connectionId = JSONUtils.getString(rule.getSourceInfo(), "sourceConnectionId");
            ruleView.sourceInfo = JSON.parseObject(rule.getSourceInfo(), SourceConnectionInfoView.class);
            SourceConnection sourceConnection = sourceConnectionMapper.get(connectionId);
            if (sourceConnection==null){
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "source table doesn't exist");
            }
            SourceConnectionInfoView sourceConnectionInfoView = (SourceConnectionInfoView)ruleView.sourceInfo;
            sourceConnectionInfoView.setSourceConnectionName(sourceConnection.getConnectionName());
        }
        //ruleInfo判断
        if(RuleType.DATA_VOLUME.getType().equalsIgnoreCase(rule.getRuleType())) {
            ruleView.setRuleInfo(JSON.parseObject(rule.getRuleExpression(), DataVolumeConfig.class));
        } else if (RuleType.DATA_VOLUME_WITH_FIXED_WINDOW.getType().equalsIgnoreCase(rule.getRuleType())) {
            ruleView.setRuleInfo(JSON.parseObject(rule.getRuleExpression(), DataVolumeWithFixedWindowConfig.class));
        } else if(RuleType.KEY_INDICATOR.getType().equalsIgnoreCase(rule.getRuleType())) {
            ruleView.setRuleInfo(JSON.parseObject(rule.getRuleExpression(), KeyIndicatorConfig.class));
        } else if(RuleType.TABLE_VOLUME.getType().equalsIgnoreCase(rule.getRuleType())) {
            ruleView.setRuleInfo(JSON.parseObject(rule.getRuleExpression(), TableVolumeConfig.class));
        } else if(RuleType.KEY_INDICATOR_WITH_DIMENSION.getType().equalsIgnoreCase(rule.getRuleType())) {
            ruleView.setRuleInfo(JSON.parseObject(rule.getRuleExpression(), KeyIndicatorWithDimensionConfig.class));
        } else if(RuleType.COMPARSION_TO_THE_SAME_TIME.getType().equalsIgnoreCase(rule.getRuleType())) {
            ruleView.setRuleInfo(JSON.parseObject(rule.getRuleExpression(), ComparisonToTheSameTimeConfig.class));
        }
        ruleView.ruleDescription = rule.getRuleDescription();
        ruleView.alarmType = rule.getAlarmType();
        ruleView.alarmUser = JSON.parseObject(rule.getAlarmUser(), AlarmUser.class);
        return ruleView;
    }

}
