package com.jollychic.holmes.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.jollychic.holmes.common.enums.AlarmType;
import com.jollychic.holmes.common.web.Result;
import com.jollychic.holmes.mapper.*;
import com.jollychic.holmes.model.*;
import com.jollychic.holmes.operator.Aggregation;
import com.jollychic.holmes.operator.Comparison;
import com.jollychic.holmes.result.*;
import com.jollychic.holmes.rule.RuleType;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.common.utils.Tools;
import com.jollychic.holmes.rule.ruleConfig.*;
import com.jollychic.holmes.view.RuleView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class RuleService {
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private SourceConnectionMapper sourceConnectionMapper;
    @Autowired
    private SourceTableMapper sourceTableMapper;
    @Autowired
    private TableRuleTmpMapper tableRuleTmpMapper;
    @Autowired
    private ConnectionRuleTmpMapper connectionRuleTmpMapper;
    @Autowired
    private UserEmailMapper userEmailMapper;

    /**
     * @param ruleId
     * @Description: 根据ruleId查询某个rule
     */
    public RuleViewResult getRuleById(String ruleId) {
        Rule rule = ruleMapper.get(ruleId);
        RuleView ruleView = RuleView.showRuleView(rule, sourceConnectionMapper, sourceTableMapper);
        return RuleViewResult.successResult(ruleView);
    }

    /**
     * @param ruleName
     * @Description: 根据ruleName查询某个rule
     */
    public RuleViewResult getRuleByName(String ruleName) {
        Rule rule = ruleMapper.getByRuleName(ruleName);
        if (rule == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "rule doesn't exist");
        }
        RuleView ruleView = RuleView.showRuleView(rule, sourceConnectionMapper, sourceTableMapper);
        return RuleViewResult.successResult(ruleView);
    }

    /**
     * @Description: 查询所有rule
     */
    public RuleViewListResult getAllRule() {
        List<Rule> rules = ruleMapper.getAll();
        List<RuleView> ruleViews = Lists.newArrayList();
        for (Rule rule : rules) {
            RuleView ruleView = RuleView.showRuleView(rule, sourceConnectionMapper, sourceTableMapper);
            ruleViews.add(ruleView);
        }
        return RuleViewListResult.successResult(ruleViews);
    }

    /**
     * @param ruleType
     * @Description: 根据ruleType查询多个rule
     */
    public RuleViewListResult getRuleByRuleType(String ruleType) {
        if (!isIncludedInRuleType(ruleType)) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleType is error");
        }
        List<Rule> rules = ruleMapper.getByRuleType(ruleType);
        List<RuleView> ruleViews = Lists.newArrayList();
        for (Rule rule : rules) {
            RuleView ruleView = RuleView.showRuleView(rule, sourceConnectionMapper, sourceTableMapper);
            ruleViews.add(ruleView);
        }
        return RuleViewListResult.successResult(ruleViews);
    }

    /**
     * @param tableId
     * @Description: 查询符合某sourcetable的多个rule
     */
    public RuleViewListResult getRuleByTableId(String tableId) {
        List<Rule> rules = ruleMapper.getByTableId(tableId);
        List<RuleView> ruleViews = Lists.newArrayList();
        for (Rule rule : rules) {
            RuleView ruleView = RuleView.showRuleView(rule, sourceConnectionMapper, sourceTableMapper);
            ruleViews.add(ruleView);
        }
        return RuleViewListResult.successResult(ruleViews);
    }

    /**
     * @param connName
     * @param tableName
     * @Description: 根据tableName和connName确定某sourceTable，查询符合该sourceTable的多个rule
     */
    public RuleViewListResult getRuleByTableName(String connName, String tableName) {
        //查询到对应的table
        if (Tools.isEmptyString(connName)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "connName is null");
        }
        if (Tools.isEmptyString(tableName)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "tableName is null");
        }
        SourceConnection sourceConnection = sourceConnectionMapper.getByConnName(connName);
        if (sourceConnection == null) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "source connection doesn't exist");
        }
        SourceTable sourceTable = new SourceTable();
        sourceTable.setTableName(tableName);
        sourceTable.setConnectionId(sourceConnection.getConnectionId());
        sourceTable = sourceTableMapper.getByTableName(sourceTable);
        if (sourceTable == null) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "source table doesn't exist");
        }
        //查询到table对应的规则列表
        List<Rule> rules = ruleMapper.getByTableId(sourceTable.getTableId());
        List<RuleView> ruleViews = Lists.newArrayList();
        for (Rule rule : rules) {
            RuleView ruleView = RuleView.showRuleView(rule, sourceConnectionMapper, sourceTableMapper);
            ruleViews.add(ruleView);
        }
        return RuleViewListResult.successResult(ruleViews);
    }

    /**
     * @param ruleId
     * @Description: 根据ruleId删除中间表及规则表中的信息
     */
    public BoolResult deleteRuleById(String ruleId) {
        Rule oldRule = ruleMapper.get(ruleId);
        if (oldRule == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "rule doesn't exist");
        }
        if (RuleType.DATA_VOLUME.getType().equalsIgnoreCase(oldRule.getRuleType())
                || RuleType.KEY_INDICATOR.getType().equalsIgnoreCase(oldRule.getRuleType())
                || RuleType.DATA_VOLUME_WITH_FIXED_WINDOW.getType().equalsIgnoreCase(oldRule.getRuleType())
                || RuleType.KEY_INDICATOR_WITH_DIMENSION.getType().equalsIgnoreCase(oldRule.getRuleType())
                || RuleType.COMPARSION_TO_THE_SAME_TIME.getType().equalsIgnoreCase(oldRule.getRuleType())) {
            tableRuleTmpMapper.deleteByRuleId(ruleId);
        } else if (RuleType.TABLE_VOLUME.getType().equalsIgnoreCase(oldRule.getRuleType())) {
            connectionRuleTmpMapper.deleteByRuleId(ruleId);
        }
        ruleMapper.delete(ruleId);
        return BoolResult.successResult(true);
    }

    /**
     * @param ruleName
     * @Description: 根据ruleName删除中间表及规则表中的信息
     */
    public BoolResult deleteRuleByName(String ruleName) {
        Rule oldRule = ruleMapper.getByRuleName(ruleName);
        if (oldRule == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "rule doesn't exist");
        }
        if (RuleType.DATA_VOLUME.getType().equalsIgnoreCase(oldRule.getRuleType())
                || RuleType.KEY_INDICATOR.getType().equalsIgnoreCase(oldRule.getRuleType())
                || RuleType.DATA_VOLUME_WITH_FIXED_WINDOW.getType().equalsIgnoreCase(oldRule.getRuleType())
                || RuleType.KEY_INDICATOR_WITH_DIMENSION.getType().equalsIgnoreCase(oldRule.getRuleType())
                || RuleType.COMPARSION_TO_THE_SAME_TIME.getType().equalsIgnoreCase(oldRule.getRuleType())) {
            tableRuleTmpMapper.deleteByRuleName(ruleName);
        } else if (RuleType.TABLE_VOLUME.getType().equalsIgnoreCase(oldRule.getRuleType())) {
            connectionRuleTmpMapper.deleteByRuleName(ruleName);
        }
        ruleMapper.deleteByRuleName(ruleName);
        return BoolResult.successResult(true);
    }

    /**
     * @param ruleView
     * @Description: 添加规则及中间表
     */
    public BoolResult insertRule(RuleView ruleView) {
        if (ruleView == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "input is null");
        }
        String ruleName = ruleView.getRuleName();
        if (Tools.isEmptyString(ruleName)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "ruleName is null");
        }
        //判断是否存在
        Rule oldRule = ruleMapper.getByRuleName(ruleName);
        if (oldRule != null) {
            throw new ServiceException(ErrorCode.FAIL, "this ruleName already exists");
        }
        validateRuleView(ruleView);
        //将sourceConnectionName与sourceTableName转换成sourceTableId, 最后转换成rule存入数据库
        Rule rule = ruleView.showRuleModel(sourceConnectionMapper, sourceTableMapper);
        ruleMapper.insert(rule);
        //获取ruleId与tableId存入table_rule_tmp
        Rule newRule = ruleMapper.getByRuleName(rule.getRuleName());
        if (RuleType.DATA_VOLUME.getType().equalsIgnoreCase(newRule.getRuleType())
                || RuleType.KEY_INDICATOR.getType().equalsIgnoreCase(newRule.getRuleType())
                || RuleType.KEY_INDICATOR_WITH_DIMENSION.getType().equalsIgnoreCase(newRule.getRuleType())
                || RuleType.DATA_VOLUME_WITH_FIXED_WINDOW.getType().equalsIgnoreCase(newRule.getRuleType())
                || RuleType.COMPARSION_TO_THE_SAME_TIME.getType().equalsIgnoreCase(newRule.getRuleType())) {
            TableRuleTmp tableRuleTmp = new TableRuleTmp();
            tableRuleTmp.setRuleId(newRule.getRuleId());
            Map<String, String> ruleSourceInfoMap = JSON.parseObject(newRule.getSourceInfo(), new TypeReference<Map<String, String>>() {
            });
            tableRuleTmp.setTableId(ruleSourceInfoMap.get("sourceTableId"));
            tableRuleTmpMapper.insert(tableRuleTmp);
        } else if (RuleType.TABLE_VOLUME.getType().equalsIgnoreCase(newRule.getRuleType())) {
            ConnectionRuleTmp connectionRuleTmp = new ConnectionRuleTmp();
            connectionRuleTmp.setRuleId(newRule.getRuleId());
            Map<String, String> ruleSourceInfoMap = JSON.parseObject(newRule.getSourceInfo(), new TypeReference<Map<String, String>>() {
            });
            connectionRuleTmp.setConnectionId(ruleSourceInfoMap.get("sourceConnectionId"));
            connectionRuleTmpMapper.insert(connectionRuleTmp);
        }
        return BoolResult.successResult(true);
    }

    /**
     * @param ruleName
     * @param ruleView
     * @Description: 更改规则
     */
    public BoolResult updateRule(String ruleName, RuleView ruleView) {
        if (ruleView == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "input is null");
        }

        Rule oldRule = ruleMapper.getByRuleName(ruleName);
        if (oldRule == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "rule doesn't exist");
        }

        if (Tools.isEmptyString(ruleView.getRuleName())) {
            ruleView.setRuleName(oldRule.getRuleName());
        }

        if (Tools.isEmptyString(ruleView.getRuleType())) {
            ruleView.setRuleType(oldRule.getRuleType());
        }
        if (ruleView.getSourceInfo() == null) {
            if (RuleType.DATA_VOLUME.getType().equalsIgnoreCase(ruleView.getRuleType())
                    || RuleType.KEY_INDICATOR.getType().equalsIgnoreCase(ruleView.getRuleType())
                    || RuleType.KEY_INDICATOR_WITH_DIMENSION.getType().equalsIgnoreCase(ruleView.getRuleType())
                    || RuleType.DATA_VOLUME_WITH_FIXED_WINDOW.getType().equalsIgnoreCase(ruleView.getRuleType())
                    || RuleType.COMPARSION_TO_THE_SAME_TIME.getType().equalsIgnoreCase(ruleView.getRuleType())) {
                Map<String, String> sourceInfo = JSON.parseObject(oldRule.getSourceInfo(), new TypeReference<Map<String, String>>() {
                });
                String sourceTableId = sourceInfo.get("sourceTableId");
                SourceTable sourceTable = sourceTableMapper.get(sourceTableId);
                if (sourceTable == null) {
                    throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "source table doesn't exist");
                }
                SourceConnection sourceConnection = sourceConnectionMapper.get(sourceTable.getConnectionId());
                if (sourceConnection == null) {
                    throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "source connection doesn't exist");
                }
                sourceInfo.put("sourceTableId", null);
                sourceInfo.put("sourceConnectionName", sourceConnection.getConnectionName());
                sourceInfo.put("sourceTableName", sourceTable.getTableName());
                ruleView.setSourceInfo(JSON.parseObject(JSON.toJSONString(sourceInfo), RuleView.SourceTableInfoView.class));
            } else if (RuleType.TABLE_VOLUME.getType().equalsIgnoreCase(ruleView.getRuleType())) {
                Map<String, String> sourceInfo = JSON.parseObject(oldRule.getSourceInfo(), new TypeReference<Map<String, String>>() {
                });
                String sourceConnectionId = sourceInfo.get("sourceConnectionId");
                SourceConnection sourceConnection = sourceConnectionMapper.get(sourceConnectionId);
                if (sourceConnection == null) {
                    throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "source connection doesn't exist");
                }
                sourceInfo.put("sourceConnectionId", null);
                sourceInfo.put("sourceConnectionName", sourceConnection.getConnectionName());
                ruleView.setSourceInfo(JSON.parseObject(JSON.toJSONString(sourceInfo), RuleView.SourceConnectionInfoView.class));
            }
        }
        if (RuleType.DATA_VOLUME.getType().equalsIgnoreCase(ruleView.getRuleType())) {
            if (ruleView.getRuleInfo() == null) {
                ruleView.setRuleInfo(JSON.parseObject(oldRule.getRuleExpression(), DataVolumeConfig.class));
            }
        } else if (RuleType.KEY_INDICATOR.getType().equalsIgnoreCase(ruleView.getRuleType())) {
            if (ruleView.getRuleInfo() == null) {
                ruleView.setRuleInfo(JSON.parseObject(oldRule.getRuleExpression(), KeyIndicatorConfig.class));
            }
        } else if (RuleType.TABLE_VOLUME.getType().equalsIgnoreCase(ruleView.getRuleType())) {
            if (ruleView.getRuleInfo() == null) {
                ruleView.setRuleInfo(JSON.parseObject(oldRule.getRuleExpression(), TableVolumeConfig.class));
            }
        } else if (RuleType.KEY_INDICATOR_WITH_DIMENSION.getType().equalsIgnoreCase(ruleView.getRuleType())) {
            if (ruleView.getRuleInfo() == null) {
                ruleView.setRuleInfo(JSON.parseObject(oldRule.getRuleExpression(), KeyIndicatorWithDimensionConfig.class));
            }
        } else if (RuleType.DATA_VOLUME_WITH_FIXED_WINDOW.getType().equalsIgnoreCase(oldRule.getRuleType())) {
            if (ruleView.getRuleInfo() == null) {
                ruleView.setRuleInfo(JSON.parseObject(oldRule.getRuleExpression(), DataVolumeWithFixedWindowConfig.class));
            }
        } else if (RuleType.COMPARSION_TO_THE_SAME_TIME.getType().equalsIgnoreCase(oldRule.getRuleType())) {
            if (ruleView.getRuleInfo() == null) {
                ruleView.setRuleInfo(JSON.parseObject(oldRule.getRuleExpression(), ComparisonToTheSameTimeConfig.class));
            }
        }
        if (Tools.isEmptyString(ruleView.getRuleDescription())) {
            ruleView.setRuleDescription(oldRule.getRuleDescription());
        }
        if (Tools.isEmptyString(ruleView.getAlarmType())) {
            ruleView.setAlarmType(oldRule.getAlarmType());
        }
        if (ruleView.getAlarmUser() == null) {
            ruleView.setAlarmUser(JSON.parseObject(oldRule.getAlarmUser(), RuleView.AlarmUser.class));
        }
        validateRuleView(ruleView);
        Rule rule = ruleView.showRuleModel(sourceConnectionMapper, sourceTableMapper);
        rule.setRuleId(oldRule.getRuleId());
        //验证规则名是否重复
        if (!rule.getRuleName().equals(oldRule.getRuleName())) {
            Rule newRule = ruleMapper.getByRuleName(rule.getRuleName());
            if (newRule != null) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "this rule already exist");
            }
        }
        ruleMapper.update(rule);
        return BoolResult.successResult(true);
    }

    //判断ruleType是否存在
    private boolean isIncludedInRuleType(String ruleType) {
        boolean include = false;
        for (RuleType s : RuleType.values()) {
            if (s.getType().equalsIgnoreCase(ruleType)) {
                include = true;
                break;
            }
        }
        return include;
    }

    //判断AlarmType是否存在
    private boolean isIncludedInAlarmType(String alarmType) {
        boolean include = false;
        for (AlarmType s : AlarmType.values()) {
            if (s.getType().equalsIgnoreCase(alarmType)) {
                include = true;
                break;
            }
        }
        return include;
    }


    public BoolResult insertTableRuleTmp(String data) {
        //获取tableId
        Map<String, String> tableMap = JSON.parseObject(data, new TypeReference<Map<String, String>>() {
        });
        String tableName = tableMap.get("tableName");
        if (Tools.isEmptyString(tableMap.get("tableName"))) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "tableName is null");
        }
        String connName = tableMap.get("connName");
        if (Tools.isEmptyString(tableMap.get("connName"))) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "connName is null");
        }
        String ruleName = tableMap.get("ruleName");
        if (Tools.isEmptyString(tableMap.get("ruleName"))) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "ruleName is null");
        }
        SourceTable newsourceTable = new SourceTable();
        newsourceTable.setTableName(tableName);
        newsourceTable.setConnectionId(sourceConnectionMapper.getByConnName(connName).getConnectionId());
        String tableId = sourceTableMapper.getByTableName(newsourceTable).getTableId();
        //ruleId
        String ruleId = ruleMapper.getByRuleName(ruleName).getRuleId();
        if (Tools.isEmptyString(ruleId)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "ruleId is null");
        }
        //创建tableRuleTmpMap，添加Tmp信息
        TableRuleTmp tableRuleTmp = new TableRuleTmp();
        tableRuleTmp.setRuleId(ruleId);
        tableRuleTmp.setTableId(tableId);
        tableRuleTmpMapper.insert(tableRuleTmp);
        return BoolResult.successResult(true);
    }

    //根据ruleName和tableName更改某条tmp
    public BoolResult updateTableRuleTmp(String data) {

        Map<String, String> tableMap = JSON.parseObject(data, new TypeReference<Map<String, String>>() {
        });
        String ruleName = tableMap.get("ruleName");
        if (Tools.isEmptyString(tableMap.get("ruleName"))) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "ruleName is null");
        }
        String oldTableName = tableMap.get("oldTableName");
        if (Tools.isEmptyString(tableMap.get("oldTableName"))) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "oldTableName is null");
        }
        String oldConnName = tableMap.get("oldConnName");
        if (Tools.isEmptyString(tableMap.get("oldConnName"))) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "oldConnName is null");
        }
        String newTableName = tableMap.get("newTableName");
        if (Tools.isEmptyString(tableMap.get("newTableName"))) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "newTableName is null");
        }
        String newConnName = tableMap.get("newConnName");
        if (Tools.isEmptyString(tableMap.get("newConnName"))) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "newConnName is null");
        }

        String ruleId = ruleMapper.getByRuleName(ruleName).getRuleId();
        if (Tools.isEmptyString(ruleId)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "ruleId is null");
        }
        SourceTable oldSourceTable = new SourceTable();
        oldSourceTable.setTableName(oldTableName);
        oldSourceTable.setConnectionId(sourceConnectionMapper.getByConnName(oldConnName).getConnectionId());
        String oldTableId = sourceTableMapper.getByTableName(oldSourceTable).getTableId();
        if (Tools.isEmptyString(oldTableId)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "oldTableId is null");
        }
        TableRuleTmp tableRuleTmp = new TableRuleTmp();
        tableRuleTmp.setTableId(oldTableId);
        TableRuleTmp tableRuleTmp1 = tableRuleTmpMapper.getByRuleIdAndTableId(tableRuleTmp);
        if (tableRuleTmp1 == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "tmp is null");
        }
        Integer oldTmpId = tableRuleTmp1.getTmpId();
        tableRuleTmpMapper.delete(oldTmpId);

        SourceTable newSourceTable = new SourceTable();
        newSourceTable.setTableName(newTableName);
        newSourceTable.setConnectionId(sourceConnectionMapper.getByConnName(newConnName).getConnectionId());
        String newTableId = sourceTableMapper.getByTableName(newSourceTable).getTableId();
        if (Tools.isEmptyString(newTableId)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "newTableId is null");
        }
        TableRuleTmp newtableRuleTmp = new TableRuleTmp();
        newtableRuleTmp.setRuleId(ruleId);
        newtableRuleTmp.setTableId(newTableId);
        tableRuleTmpMapper.insert(newtableRuleTmp);
        return BoolResult.successResult(true);
    }

    //删除某一条tmp
    public BoolResult deleteTableRuleTmp(String data) {
        Map<String, String> tableMap = JSON.parseObject(data, new TypeReference<Map<String, String>>() {
        });
        String ruleName = tableMap.get("ruleName");
        if (Tools.isEmptyString(tableMap.get("ruleName"))) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "ruleName is null");
        }
        String tableName = tableMap.get("tableName");
        if (Tools.isEmptyString(tableMap.get("tableName"))) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "tableName is null");
        }
        String connName = tableMap.get("connName");
        if (Tools.isEmptyString(tableMap.get("connName"))) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "connName is null");
        }
        Rule rule = ruleMapper.getByRuleName(ruleName);
        if (rule == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "find rule is null");
        }
        String ruleId = rule.getRuleId();
        if (Tools.isEmptyString(ruleId)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "ruleId is null");
        }
        SourceTable SourceTable = new SourceTable();
        SourceTable.setTableName(tableName);
        SourceTable.setConnectionId(sourceConnectionMapper.getByConnName(connName).getConnectionId());
        SourceTable sourceTable = sourceTableMapper.getByTableName(SourceTable);
        if (sourceTable == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "find table is null");
        }
        String tableId = sourceTable.getTableId();
        if (Tools.isEmptyString(tableId)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "tableId is null");
        }
        TableRuleTmp tableRuleTmp = new TableRuleTmp();
        tableRuleTmp.setRuleId(ruleId);
        tableRuleTmp.setTableId(tableId);
        Integer tmpId = tableRuleTmpMapper.getTmpId(tableRuleTmp);
        if (tmpId == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "find tmp is null");
        }
        tableRuleTmpMapper.delete(tmpId);
        return BoolResult.successResult(true);
    }

    /**
     * @param ruleView
     * @Description: 对输入数据规格进行判断
     */
    private void validateRuleView(RuleView ruleView) {
        if (ruleView == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "input is null");
        }

        String ruleName = ruleView.getRuleName();
        if (Tools.isEmptyString(ruleName)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "ruleName is null");
        }

        String ruleType = ruleView.getRuleType();
        if (Tools.isEmptyString(ruleType)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "ruleType is null");
        }
        if (!isIncludedInRuleType(ruleType)) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleType is error");
        }

        //检查数据源配置
        if (RuleType.DATA_VOLUME.getType().equalsIgnoreCase(ruleType)
                || RuleType.KEY_INDICATOR.getType().equalsIgnoreCase(ruleType)
                || RuleType.KEY_INDICATOR_WITH_DIMENSION.getType().equalsIgnoreCase(ruleType)
                || RuleType.COMPARSION_TO_THE_SAME_TIME.getType().equalsIgnoreCase(ruleType)) {
            RuleView.SourceTableInfoView sourceInfoView = (RuleView.SourceTableInfoView) ruleView.getSourceInfo();
            if (sourceInfoView == null) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceInfo is null");
            }
            if (Tools.isEmptyString(sourceInfoView.getSourceConnectionName())) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceInfo.sourceConnectionName is null");
            }
            if (Tools.isEmptyString(sourceInfoView.getSourceTableName())) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceInfo.sourceTableName is null");
            }
            SourceConnection sourceConnection = sourceConnectionMapper.getByConnName(sourceInfoView.getSourceConnectionName());
            if (sourceConnection == null) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "source connection doesn't exist");
            }
            SourceTable sourceTable = new SourceTable();
            sourceTable.setConnectionId(sourceConnection.getConnectionId());
            sourceTable.setTableName(sourceInfoView.getSourceTableName());
            sourceTable = sourceTableMapper.getByTableName(sourceTable);
            if (sourceTable == null) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "source table doesn't exist");
            }
            if (Tools.isEmptyString(sourceInfoView.getPartitionName())) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceInfo.partitionName is null");
            }
            if (Tools.isEmptyString(sourceInfoView.getPartitionValueFormat())) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceInfo.partitionValueFormat is null");
            }

        } else if (RuleType.DATA_VOLUME_WITH_FIXED_WINDOW.getType().equalsIgnoreCase(ruleType)) {
            RuleView.SourceTopicInfoView sourceInfoView = (RuleView.SourceTopicInfoView) ruleView.getSourceInfo();
            if (sourceInfoView == null) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceInfo is null");
            }
            if (Tools.isEmptyString(sourceInfoView.getSourceConnectionName())) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceInfo.sourceConnectionName is null");
            }
            if (Tools.isEmptyString(sourceInfoView.getSourceTableName())) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceInfo.sourceTableName is null");
            }
            SourceConnection sourceConnection = sourceConnectionMapper.getByConnName(sourceInfoView.getSourceConnectionName());
            if (sourceConnection == null) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "source connection doesn't exist");
            }
            SourceTable sourceTable = new SourceTable();
            sourceTable.setConnectionId(sourceConnection.getConnectionId());
            sourceTable.setTableName(sourceInfoView.getSourceTableName());
            sourceTable = sourceTableMapper.getByTableName(sourceTable);
            if (sourceTable == null) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "source table doesn't exist");
            }

        } else if (RuleType.TABLE_VOLUME.getType().equalsIgnoreCase(ruleType)) {
            RuleView.SourceConnectionInfoView sourceConnectionInfoView = (RuleView.SourceConnectionInfoView) ruleView.getSourceInfo();
            if (sourceConnectionInfoView == null) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceInfo is null");
            }
            if (Tools.isEmptyString(sourceConnectionInfoView.getSourceConnectionName())) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceInfo.sourceConnectionName is null");
            }
            SourceConnection sourceConnection = sourceConnectionMapper.getByConnName(sourceConnectionInfoView.getSourceConnectionName());
            if (sourceConnection == null) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "source connection doesn't exist");
            }
        }

        //检查rule config配置
        if (RuleType.DATA_VOLUME.getType().equalsIgnoreCase(ruleType)) {
            DataVolumeConfig dataVolumeConfig = JSON.parseObject(JSON.toJSONString(ruleView.getRuleInfo()), DataVolumeConfig.class);
            if (dataVolumeConfig == null) {
                throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo is null");
            }
            if (dataVolumeConfig.getOperator() == null || (!Comparison.OPERATORS_1.contains(dataVolumeConfig.getOperator()))) {
                throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.operator is null or error");
            }
            if (dataVolumeConfig.getValue() == null) {
                throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.value is null");
            }

        } else if (RuleType.DATA_VOLUME_WITH_FIXED_WINDOW.getType().equalsIgnoreCase(ruleType)) {
            DataVolumeWithFixedWindowConfig dataVolumeWithFixedWindowConfig = JSON.parseObject(JSON.toJSONString(ruleView.getRuleInfo()), DataVolumeWithFixedWindowConfig.class);
            if (dataVolumeWithFixedWindowConfig == null) {
                throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo is null");
            }
            if (dataVolumeWithFixedWindowConfig.getOperator() == null || (!Comparison.OPERATORS_1.contains(dataVolumeWithFixedWindowConfig.getOperator()))) {
                throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.operator is null or error");
            }
            if (dataVolumeWithFixedWindowConfig.getValue() == null) {
                throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.value is null");
            }

        } else if (RuleType.KEY_INDICATOR.getType().equalsIgnoreCase(ruleType)) {
            KeyIndicatorConfig keyIndicatorConfig = JSON.parseObject(JSON.toJSONString(ruleView.getRuleInfo()), KeyIndicatorConfig.class);
            if (keyIndicatorConfig == null) {
                throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo is null");
            }
            if (keyIndicatorConfig.getColumns() == null) {
                throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.columns is null");
            }
            if (keyIndicatorConfig.getColumns().size() == 0) {
                throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.columns size is 0");
            }
            int i = 0;
            for (KeyIndicatorConfig.KeyIndicatorColumn column : keyIndicatorConfig.getColumns()) {
                i++;
                if (column.getColumnName() == null || column.getColumnName().trim().equals("")) {
                    throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.columns[" + i + "].columnName is null or error");
                }
                if (column.getOperator() == null ||
                        (!Comparison.OPERATORS_1.contains(column.getOperator()) &&
                                !Comparison.OPERATORS_2.contains(column.getOperator()) &&
                                !Comparison.OPERATORS_3.contains(column.getOperator()))
                        ) {
                    throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.columns[" + i + "].operator is null or not support");
                }
                if (Comparison.OPERATORS_1.contains(column.getOperator()) && column.getValue() == null) {
                    throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.columns[" + i + "].value is null");
                }
            }

        } else if (RuleType.TABLE_VOLUME.getType().equalsIgnoreCase(ruleType)) {
            //无需检查

        } else if (RuleType.KEY_INDICATOR_WITH_DIMENSION.getType().equalsIgnoreCase(ruleType)) {
            KeyIndicatorWithDimensionConfig keyIndicatorWithDimensionConfig = JSON.parseObject(JSON.toJSONString(ruleView.getRuleInfo()), KeyIndicatorWithDimensionConfig.class);
            if (keyIndicatorWithDimensionConfig == null) {
                throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo is null");
            }
            if (keyIndicatorWithDimensionConfig.getConditions() == null) {
                throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions is null");
            }
            if (keyIndicatorWithDimensionConfig.getConditions().size() == 0) {
                throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions size is 0");
            }
            int i = 0;
            int j = 0;
            int z = 0;
            for (KeyIndicatorWithDimensionConfig.KeyIndicatorWithDimensionCondition condition : keyIndicatorWithDimensionConfig.getConditions()) {
                i++;
                if (condition.getColumns() == null) {
                    throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].columns is null");
                }
                if (condition.getColumns().size() == 0) {
                    throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].columns size is 0");
                }
                if (condition.getDimensions() == null) {
                    throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].dimensions is null");
                }
                if (condition.getDimensions().size() == 0) {
                    throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].dimensions size is 0");
                }
                for (KeyIndicatorWithDimensionConfig.KeyIndicatorWithDimensionColumn column : condition.getColumns()) {
                    j++;
                    if (column.getColumnName() == null || column.getColumnName().trim().equals("")) {
                        throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].columns[" + j + "].columnName is null or error");
                    }
                    if (column.getOperator() == null || !Comparison.OPERATORS_1.contains(column.getOperator())) {
                        throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].columns[" + j + "].operator is null or error");
                    }
                    if (column.getValue() == null) {
                        throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].columns[" + j + "].value is null");
                    }
                    if (Aggregation.OPERATORS.contains(column.getAggregation()) && column.getAggregation() == null) {
                        throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].columns[" + j + "].aggregation is null");
                    }
                }
                j = 0;
                for (KeyIndicatorWithDimensionConfig.KeyIndicatorWithDimensionDimension dimension : condition.getDimensions()) {
                    z++;
                    if (dimension.getDimensionName() == null || dimension.getDimensionName().trim().equals("")) {
                        throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].dimension[" + z + "].dimensionName is null or error");
                    }
                    if (dimension.getDimensionValues() == null || dimension.getDimensionValues().size() == 0) {
                        throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].dimension[" + z + "].dimensionValues is null");
                    }
                }
                z = 0;
            }

        } else if (RuleType.COMPARSION_TO_THE_SAME_TIME.getType().equalsIgnoreCase(ruleType)) {
            ComparisonToTheSameTimeConfig comparisonToTheSameTimeConfig = JSON.parseObject(JSON.toJSONString(ruleView.getRuleInfo()), ComparisonToTheSameTimeConfig.class);
            if (comparisonToTheSameTimeConfig == null) {
                throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo is null");
            }
            if (comparisonToTheSameTimeConfig.getConditions() == null) {
                throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions is null");
            }
            if (comparisonToTheSameTimeConfig.getConditions().size() == 0) {
                throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions size is 0");
            }
            if (comparisonToTheSameTimeConfig.getDifferDay() == null) {
                throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.differDay is null");
            }
            int i = 0;
            int j = 0;
            int z = 0;
            for (ComparisonToTheSameTimeConfig.ComparisonToTheSameTimeCondition condition : comparisonToTheSameTimeConfig.getConditions()) {
                i++;
                if (condition.getDimensions() == null) {
                    throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].dimensions is null");
                }
                if (condition.getColumns() == null) {
                    throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].columns is null");
                }
                if (condition.getColumns().size() == 0) {
                    throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].columns size is 0");
                }

                for (ComparisonToTheSameTimeConfig.ComparisonToTheSameTimeColumn column : condition.getColumns()) {
                    j++;
                    if (column.getColumnName() == null || column.getColumnName().trim().equals("")) {
                        throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].columns[" + j + "].columnName is null or error");
                    }
                    if (column.getOperator() == null || !Comparison.OPERATORS_1.contains(column.getOperator())) {
                        throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].columns[" + j + "].operator is null or error");
                    }
                    if (column.getPercent() == null) {
                        throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].columns[" + j + "].value is null");
                    }
                    if (Aggregation.OPERATORS.contains(column.getAggregation()) && column.getAggregation() == null) {
                        throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].columns[" + j + "].aggregation is null");
                    }
                }
                j = 0;
                for (ComparisonToTheSameTimeConfig.ComparisonToTheSameTimeDimension dimension : condition.getDimensions()) {
                    z++;
                    if (dimension.getDimensionName() == null || dimension.getDimensionName().trim().equals("")) {
                        throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].dimension[" + z + "].dimensionName is null or error");
                    }
                    if (dimension.getDimensionValues() == null || dimension.getDimensionValues().size() == 0) {
                        throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleInfo.conditions[" + i + "].dimension[" + z + "].dimensionValues is null");
                    }
                }
                z = 0;
            }

        }

        String alarmType = ruleView.getAlarmType();
        if (Tools.isEmptyString(alarmType)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "alarmType is null");
        }
        if (!isIncludedInAlarmType(alarmType)) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "alarmType is error,the ruleType include both/wechat/email");
        }

        RuleView.AlarmUser alarmUser = ruleView.getAlarmUser();
        if (alarmUser == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "alarmUser is null");
        }
        if (alarmUser.getUser() == null || alarmUser.getUser().size() == 0) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "alarmUser.user is null");
        }
        if (alarmUser.getEmail() == null || alarmUser.getEmail().size() == 0) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "alarmUser.email is null");
        }
    }

    /**
     * @param userEmailId
     * @Description: 根据userEmailId查询某个userEmail
     */
    public UserEmailResult getUserEmailById(String userEmailId) {
        UserEmail userEmail = userEmailMapper.get(userEmailId);
        return UserEmailResult.successResult(userEmail);
    }

    /**
     * @param userChineseName
     * @param userEnglishName
     * @Description: 根据userChineseName, userEnglishName查询某个userEmail
     */
    public UserEmailResult getUserEmailByCName(String userChineseName, String userEnglishName) {
        UserEmail oldUserEmail = new UserEmail();
        oldUserEmail.setUserEnglishName(userEnglishName);
        oldUserEmail.setUserChineseName(userChineseName);
        UserEmail userEmail = userEmailMapper.getByName(oldUserEmail);
        return UserEmailResult.successResult(userEmail);
    }

    /**
     * @Description: 查询所有userEmail
     */
    public UserEmailListResult getAllUserEmail() {
        List<UserEmail> userEmails = userEmailMapper.getAll();
        return UserEmailListResult.successResult(userEmails);
    }

    /**
     * @param data
     * @Description: 添加userEmail
     */
    public BoolResult insertUserEmail(String data) {
        Map<String, String> userEmailMap = JSON.parseObject(data, new TypeReference<Map<String, String>>() {
        });
        String userEmailId = userEmailMap.get("userEmailId");
        if (Tools.isEmptyString(userEmailId)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "userEmailId is null");
        }
        String userChineseName = userEmailMap.get("userChineseName");
        if (Tools.isEmptyString(userChineseName)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "userChineseName is null");
        }
        String userEnglishName = userEmailMap.get("userEnglishName");
        if (Tools.isEmptyString(userEnglishName)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "userEnglishName is null");
        }
        String email = userEmailMap.get("email");
        if (Tools.isEmptyString(email)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "email is null");
        }
        UserEmail userEmail =new UserEmail();
        userEmail.setUserChineseName(userChineseName);
        userEmail.setUserEnglishName(userEnglishName);
        userEmail.setEmail(email);
        userEmail.setUserEmailId(userEmailId);
        userEmailMapper.insert(userEmail);
        return BoolResult.successResult(true);
    }
    /**
     * @param userEmailId
     * @Description: 根据userEmailId删除表中的信息
     */
    public BoolResult deleteUserEmailById(String userEmailId) {
        UserEmail oldUserEmail = userEmailMapper.get(userEmailId);
        if (oldUserEmail == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "userEmail doesn't exist");
        }
        userEmailMapper.delete(userEmailId);
        return BoolResult.successResult(true);
    }
    /**
     * @Description: 更新某个UserEmail
     * @param id
     * @param data
     */
    public BoolResult updateUserEmail(String id, String data) throws Exception {
        if(data==null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "input is null");
        }
        UserEmail oldUserEmail = userEmailMapper.get(id);
        if(oldUserEmail==null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "this userEmail is not exist");
        }
        Map<String, String> userEmailMap = JSON.parseObject(data, new TypeReference<Map<String, String>>() {
        });
        UserEmail userEmail = new UserEmail();
        userEmail.setUserEmailId(id);
        String userChineseName = userEmailMap.get("userChineseName");
        if (!Tools.isEmptyString(userChineseName)) {
            userEmail.setUserChineseName(userChineseName);
        }
        String userEnglishName = userEmailMap.get("userEnglishName");
        if (!Tools.isEmptyString(userEnglishName)) {
           userEmail.setUserEnglishName(userEnglishName);
        }
        String email = userEmailMap.get("email");
        if (!Tools.isEmptyString(email)) {
           userEmail.setEmail(email);
        }
        if (Tools.isEmptyString(userEnglishName)&&Tools.isEmptyString(email)&&Tools.isEmptyString(userChineseName)){
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "data is null");
        }
        userEmailMapper.update(userEmail);
        return BoolResult.successResult(true);
    }
}
