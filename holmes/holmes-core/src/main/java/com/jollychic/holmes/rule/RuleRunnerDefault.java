package com.jollychic.holmes.rule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.jollychic.holmes.alarm.EmailAlarm;
import com.jollychic.holmes.alarm.WechatAlarm;
import com.jollychic.holmes.common.context.SpringUtil;
import com.jollychic.holmes.common.enums.AlarmType;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.common.utils.DateUtils;
import com.jollychic.holmes.mapper.*;
import com.jollychic.holmes.model.*;
import com.jollychic.holmes.source.ConnConfig.HiveConfig;
import com.jollychic.holmes.source.ConnConfig.KafkaConfig;
import com.jollychic.holmes.source.ConnConfig.MysqlConfig;
import com.jollychic.holmes.source.SourceConfig;
import com.jollychic.holmes.source.SourceFactory;
import com.jollychic.holmes.source.SourceReader;
import com.jollychic.holmes.source.SourceType;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by WIN7 on 2018/1/10.
 */
public abstract class RuleRunnerDefault implements RuleRunner {
    protected SourceConnectionMapper sourceConnectionMapper;
    protected SourceTableMapper sourceTableMapper;
    protected ExecutionMapper executionMapper;
    protected AlarmMapper alarmMapper;
    protected TableVolumeStateManagementMapper tableVolumeStateManagementMapper;
    protected KafkaMaxOffsetManagementMapper kafkaMaxOffsetManagementMapper;

    protected Rule rule;
    protected Execution execution;
    protected List<String> errorInfoList = Lists.newArrayList();

    @Override
    public void init(Rule rule, Execution execution) {
        this.sourceConnectionMapper = SpringUtil.getBean(SourceConnectionMapper.class);
        this.sourceTableMapper = SpringUtil.getBean(SourceTableMapper.class);
        this.executionMapper = SpringUtil.getBean(ExecutionMapper.class);
        this.alarmMapper = SpringUtil.getBean(AlarmMapper.class);
        this.tableVolumeStateManagementMapper = SpringUtil.getBean(TableVolumeStateManagementMapper.class);
        this.kafkaMaxOffsetManagementMapper = SpringUtil.getBean(KafkaMaxOffsetManagementMapper.class);

        this.rule = rule;
        this.execution = execution;
    }

    protected void outputAlarm(boolean alarm, String sourceName, String alarmInfo) {
        Alarm alarmObject = new Alarm();
        alarmObject.setExecutionId(execution.getExecutionId());
        alarmObject.setRuleId(rule.getRuleId());
        alarmObject.setAlarm(alarm);
        alarmObject.setAlarmInfo(alarmInfo);
        this.alarmMapper.insert(alarmObject);
        if(alarm) {
            String ruleName = rule.getRuleName();
            Map<String, String> alarmUser = JSON.parseObject(rule.getAlarmUser(), new TypeReference<Map<String, String>>(){});
            String user = alarmUser.get("user");
            String email = alarmUser.get("email");
            String content = "触发规则："+ruleName+"</br>监控数据源："+sourceName+"</br>详细信息："+alarmInfo;
            String subject = ruleName+"报警";
            String title = "Holmes 报警";
            try {
                if (rule.getAlarmType() == null) {
                    WechatAlarm.sendMessage(user, title, content);
                    EmailAlarm.sendMessage(email, subject, content);
                } else if (rule.getAlarmType().equalsIgnoreCase(AlarmType.WECHAT.getType())) {
                    WechatAlarm.sendMessage(user, title, content);
                } else if (rule.getAlarmType().equalsIgnoreCase(AlarmType.EMAIL.getType())) {
                    EmailAlarm.sendMessage(email, subject, content);
                } else {
                    WechatAlarm.sendMessage(user, title, content);
                    EmailAlarm.sendMessage(email, subject, content);
                }
            } catch (ServiceException se) {
                execution.setErrorInfo(flushErrorInfo("["+new Date()+"] error code: "+se.getCode()+", error msg: "+se.getMsg()));
                execution.setUpdatedAt(new Date());
                executionMapper.updateErrorInfo(execution);
            }
        }
    }

    protected void outputAlarm(boolean alarm) {
        outputAlarm(alarm, "", "");
    }

    protected String flushErrorInfo(String errorInfo) {
        errorInfoList.add(errorInfo);
        if(errorInfoList.size()>10) {
            errorInfoList.remove(0);
        }
        StringBuffer newErrorInfo = new StringBuffer("");
        for(String one : errorInfoList) {
            newErrorInfo.append(one);
            newErrorInfo.append("</br>");
        }
        return String.valueOf(newErrorInfo);
    }

}
