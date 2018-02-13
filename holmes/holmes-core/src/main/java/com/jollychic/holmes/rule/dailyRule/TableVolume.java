package com.jollychic.holmes.rule.dailyRule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.mapper.TableVolumeStateManagementMapper;
import com.jollychic.holmes.model.Execution;
import com.jollychic.holmes.model.Rule;
import com.jollychic.holmes.model.SourceConnection;
import com.jollychic.holmes.model.TableVolumeStateManagement;
import com.jollychic.holmes.rule.RuleRunnerDefault;
import com.jollychic.holmes.rule.ruleConfig.TableVolumeConfig;
import com.jollychic.holmes.source.ConnConfig.HiveConfig;
import com.jollychic.holmes.source.ConnConfig.MysqlConfig;
import com.jollychic.holmes.source.SourceConfig;
import com.jollychic.holmes.source.SourceFactory;
import com.jollychic.holmes.source.SourceReader;
import com.jollychic.holmes.source.SourceType;

import java.util.List;
import java.util.Map;

/**
 * Created by WIN7 on 2018/1/26.
 */
public class TableVolume extends RuleRunnerDefault {
    private TableVolumeConfig tableVolumeConfig;
    private Map<String, String> sourceInfo;
    private SourceConnection sourceConnection;
    private SourceConfig sourceConfig = new SourceConfig();
    private SourceReader sourceReader;

    @Override
    public void run() {
        sourceInfo = JSON.parseObject(rule.getSourceInfo(), new TypeReference<Map<String, String>>(){});
        sourceConnection = sourceConnectionMapper.get(sourceInfo.get("sourceConnectionId"));
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

        this.tableVolumeConfig = JSON.parseObject(rule.getRuleExpression(), TableVolumeConfig.class);
        if(tableVolumeConfig.getMinReduceVolume()==null || tableVolumeConfig.getMinReduceVolume().compareTo(0)<=0) {
            tableVolumeConfig.setMinReduceVolume(1);
        }
        List<String> tables = sourceReader.getTables();
        TableVolumeStateManagement tableVolumeStateManagement = tableVolumeStateManagementMapper.getByRuleIdAndMaxVersion(rule.getRuleId());
        if(tableVolumeStateManagement==null) {
            tableVolumeStateManagement = new TableVolumeStateManagement();
            tableVolumeStateManagement.setRuleId(rule.getRuleId());
            tableVolumeStateManagement.setTableNames(JSON.toJSONString(tables));
            tableVolumeStateManagement.setVersion(1);
            tableVolumeStateManagementMapper.insert(tableVolumeStateManagement);
            return;
        }
        List<String> oldTables = JSON.parseObject(tableVolumeStateManagement.getTableNames(), new TypeReference<List<String>>(){});
        int reduceNum = 0;
        StringBuffer reduceTables = new StringBuffer("");
        for (String oldTable : oldTables) {
            if(!tables.contains(oldTable)) {
                reduceNum++;
                reduceTables.append("</br>");
                reduceTables.append(oldTable);
            }
        }
        if(reduceNum>=tableVolumeConfig.getMinReduceVolume()) {
            String sourceName = sourceConnection.getConnectionName();
            this.outputAlarm(true, sourceName, "监控的数据库为："+sourceConnection.getConnectionName()+
                    "， 删除的表数量为：" + reduceNum + "， 删除的表为: " + reduceTables);
        } else {
            this.outputAlarm(false);
        }
        if(reduceNum>0 || tables.size()!=oldTables.size()) {
            tableVolumeStateManagement.setTableNames(JSON.toJSONString(tables));
            tableVolumeStateManagement.setVersion(tableVolumeStateManagement.getVersion()+1);
            tableVolumeStateManagementMapper.insert(tableVolumeStateManagement);
        }
    }
}
