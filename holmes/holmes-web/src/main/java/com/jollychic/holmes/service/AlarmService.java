package com.jollychic.holmes.service;

import com.google.common.collect.Lists;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.mapper.AlarmMapper;
import com.jollychic.holmes.mapper.RuleMapper;
import com.jollychic.holmes.model.Alarm;
import com.jollychic.holmes.model.Rule;
import com.jollychic.holmes.result.AlarmListResult;
import com.jollychic.holmes.view.AlarmView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AlarmService {
    @Autowired
    private AlarmMapper alarmMapper;
    @Autowired
    private RuleMapper ruleMapper;

    /**
     * @Description: 根据executionId查询多个alarm
     * @param executionId
     *
     */
    public AlarmListResult getByExecutionId(Integer executionId) {
        List<Alarm> alarms = alarmMapper.getByExecutionId(executionId);
        List<AlarmView> alarmViews = Lists.newArrayList();
        for(Alarm alarm : alarms) {
            AlarmView alarmView = AlarmView.showAlarmView(alarm,ruleMapper);
            alarmViews.add(alarmView);
        }
        return AlarmListResult.successResult(alarmViews);
    }

    /**
     * @Description: 根据executionId和alarmStatus查询多个alarm
     * @param executionId
     * @param alarmStatus
     *
     */
    public AlarmListResult getByExecutionIdAndAlarm(Integer executionId, Boolean alarmStatus) {
        Alarm alarmInput = new Alarm();
        alarmInput.setExecutionId(executionId);
        alarmInput.setAlarm(alarmStatus);
        List<Alarm> alarms = alarmMapper.getByExecutionIdAndAlarm(alarmInput);
        List<AlarmView> alarmViews = Lists.newArrayList();
        for(Alarm alarm : alarms) {
            AlarmView alarmView = AlarmView.showAlarmView(alarm,ruleMapper);
            alarmViews.add(alarmView);
        }
        return AlarmListResult.successResult(alarmViews);
    }
    /**
     * @Description: 根据ruleName查询多个alarm
     * @param ruleName
     *
     */
    public AlarmListResult getByRuleName(String ruleName) {
        Rule rule = ruleMapper.getByRuleName(ruleName);
        if (rule==null){
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "rule is not exist");
        }
        List<Alarm> alarms = alarmMapper.getByRuleId(rule.getRuleId());
        List<AlarmView> alarmViews = Lists.newArrayList();
        for(Alarm alarm : alarms) {
            AlarmView alarmView = AlarmView.showAlarmView(alarm,ruleMapper);
            alarmViews.add(alarmView);
        }
        return AlarmListResult.successResult(alarmViews);
    }
    /**
     * @Description: 根据ruleName和alarmStatus查询多个alarm
     * @param ruleName
     * @param alarmStatus
     *
     */
    public AlarmListResult getByRuleNameAndAlarm(String ruleName, Boolean alarmStatus) {
        Rule rule = ruleMapper.getByRuleName(ruleName);
        if (rule==null){
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "rule is not exist");
        }
        Alarm alarmInput = new Alarm();
        alarmInput.setRuleId(rule.getRuleId());
        alarmInput.setAlarm(alarmStatus);
        List<Alarm> alarms = alarmMapper.getByRuleIdAndAlarm(alarmInput);
        List<AlarmView> alarmViews = Lists.newArrayList();
        for(Alarm alarm : alarms) {
            AlarmView alarmView = AlarmView.showAlarmView(alarm,ruleMapper);
            alarmViews.add(alarmView);
        }
        return AlarmListResult.successResult(alarmViews);
    }
    /**
     * @Description: 查询所有alarm
     */
    public AlarmListResult getAll() {
        List<Alarm> alarms = alarmMapper.getAll();
        List<AlarmView> alarmViews = Lists.newArrayList();
        for(Alarm alarm : alarms) {
            AlarmView alarmView = AlarmView.showAlarmView(alarm,ruleMapper);
            alarmViews.add(alarmView);
        }
        return AlarmListResult.successResult(alarmViews);
    }


}
