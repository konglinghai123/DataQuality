package com.jollychic.holmes.view;

import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.mapper.RuleMapper;
import com.jollychic.holmes.model.Alarm;
import com.jollychic.holmes.model.Rule;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Data
public class AlarmView {
    private Integer alarmId;     //自增主键
    private Integer executionId;//任务表id，外键
    private String ruleName;     //规则表id，外键
    private Boolean alarm;      //是否报警
    private String alarmInfo;  //报警信息
    private String createdAt;
    private String updatedAt;
    public Alarm showAlarmModel(RuleMapper ruleMapper){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Alarm alarm = new Alarm();
        alarm.setExecutionId(this.getExecutionId());
        alarm.setAlarmId(this.alarmId);
        Rule rule = ruleMapper.getByRuleName(this.ruleName);
        if (rule == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "rule doesn't exist");
        }
        alarm.setRuleId(rule.getRuleId());
        alarm.setAlarm(this.alarm);
        alarm.setAlarmInfo(this.alarmInfo);
        try {
            alarm.setCreatedAt(formatter.parse(this.createdAt));
            alarm.setUpdatedAt(formatter.parse(this.updatedAt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return alarm;
    }

    public static AlarmView showAlarmView(Alarm alarm,RuleMapper ruleMapper) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        AlarmView alarmView = new AlarmView();
        alarmView.executionId = alarm.getExecutionId();
        alarmView.alarmId = alarm.getAlarmId();
        Rule rule= ruleMapper.get(alarm.getRuleId());
        if (rule == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "rule doesn't exist");
        }
        alarmView.ruleName = rule.getRuleName();
        alarmView.alarm = alarm.getAlarm();
        alarmView.alarmInfo = alarm.getAlarmInfo();
        alarmView.createdAt = formatter.format(alarm.getCreatedAt());
        alarmView.updatedAt = formatter.format(alarm.getUpdatedAt());
        return alarmView;
    }
}
