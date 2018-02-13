package com.jollychic.holmes.service;


import com.google.common.collect.Lists;
import com.jollychic.holmes.common.enums.ExecutionStatus;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.common.utils.Tools;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.execution.ExecutionManagement;
import com.jollychic.holmes.mapper.AlarmMapper;
import com.jollychic.holmes.mapper.ExecutionMapper;
import com.jollychic.holmes.mapper.RuleMapper;
import com.jollychic.holmes.model.Alarm;
import com.jollychic.holmes.model.Execution;
import com.jollychic.holmes.model.Rule;
import com.jollychic.holmes.quartz.QuartzManager;
import com.jollychic.holmes.result.*;
import com.jollychic.holmes.view.AlarmView;
import com.jollychic.holmes.view.ExecutionView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class ExecutionService {
    @Autowired
    private ExecutionMapper executionMapper;
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private AlarmMapper alarmMapper;
    @Autowired
    public ExecutionManagement executionManagement;

    /**
     * @Description: 根据executionId查询某个excution
     * @param executionId
     *
     */
    public ExecutionViewResult getExecutionById(int executionId) {
        Execution execution = executionMapper.get(executionId);
        if (execution == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "execution doesn't exist");
        }
        ExecutionView executionView = ExecutionView.showExecutionView(execution, ruleMapper);
        return ExecutionViewResult.successResult(executionView);
    }

    /**
     * @Description: 根据ruleName查询多个excution
     * @param ruleName
     *
     */
    public ExecutionViewListResult getExecutionByruleName(String ruleName) {
        Rule rule = ruleMapper.getByRuleName(ruleName);
        if (rule == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "rule doesn't exist");
        }
        String ruleId = rule.getRuleId();
        List<Execution> executions = executionMapper.getByRuleId(ruleId);
        List<ExecutionView> executionViews = Lists.newArrayList();
        for (Execution execution : executions) {
            ExecutionView executionView = ExecutionView.showExecutionView(execution, ruleMapper);
            executionViews.add(executionView);
        }
        return ExecutionViewListResult.successResult(executionViews);
    }

    /**
     * @Description: 根据ruleName和status查询某个excution
     * @param ruleName
     * @param status
     */
    public ExecutionViewResult getExecutionByRuleNameAndStatus(String ruleName, Integer status) {
        Rule rule = ruleMapper.getByRuleName(ruleName);
        if (rule == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "rule doesn't exist");
        }
        String ruleId = rule.getRuleId();
        Execution execution = new Execution();
        execution.setRuleId(ruleId);
        execution.setStatus(status);
        Execution newExecution = executionMapper.getByRuleIdAndStatus(execution);
        if (newExecution == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "execution doesn't exist");
        }
        ExecutionView executionView = ExecutionView.showExecutionView(newExecution, ruleMapper);
        return ExecutionViewResult.successResult(executionView);
    }

    /**
     * @Description: 查询所有excution
     */
    public ExecutionViewListResult getAllExecution() {
        List<Execution> executions = executionMapper.getAll();
        List<ExecutionView> executionViews = Lists.newArrayList();
        for (Execution execution : executions) {
            ExecutionView executionView = ExecutionView.showExecutionView(execution, ruleMapper);
            executionViews.add(executionView);
        }
        return ExecutionViewListResult.successResult(executionViews);
    }

    /**
     * @Description: 根据ruleId删除某个excution
     * @param ruleId
     */
    public BoolResult deleteExecution(String ruleId) {
        executionMapper.delete(ruleId);
        return BoolResult.successResult(true);
    }

    /**
     * @Description:根据ruleName提交Execution

     * @param ruleName
     * @return
     */
    public AlarmListResult submitExecutionByRuleName(String ruleName) {
        Rule rule = ruleMapper.getByRuleName(ruleName);
        if (rule == null) {
            throw new ServiceException(ErrorCode.FAIL, "规则不存在");
        }
        //新增Execution
        Execution execution = new Execution();
        execution.setExecutionName(rule.getRuleName() + "-" + Tools.getRandomString(16));
        execution.setRuleId(rule.getRuleId());
        execution.setStatus(ExecutionStatus.NEW.getId());
        executionMapper.insert(execution);
        Integer executionId = execution.getExecutionId();
        execution = executionMapper.get(executionId);
        //提交Execution
        executionManagement.submit(execution);
        //提交完成后，生成alarm后，返回alarm结果，如果5分钟后无结果返回，报错
        long begin = System.currentTimeMillis();
        while(execution.getStatus() >= 2) {
            long next = System.currentTimeMillis();
            if(next-begin > 5*60*1000){
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "request timed out");
            }
            if (execution.getStatus() == 2) { break; }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(5*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Alarm> alarms = alarmMapper.getByExecutionId(executionId);
        List<AlarmView> alarmViews = Lists.newArrayList();
        for (Alarm alarm : alarms) {
            AlarmView alarmView = AlarmView.showAlarmView(alarm, ruleMapper);
            alarmViews.add(alarmView);
        }
        return AlarmListResult.successResult(alarmViews);

    }
    /**
     * 提交Execution
     * @param ruleId
     * @return
     */
    public Integer submitExecution(String ruleId) {
        Rule rule = ruleMapper.get(ruleId);
        if(rule==null) {
            throw new ServiceException(ErrorCode.FAIL, "规则不存在");
        }
        //新增Execution
        Execution execution = new Execution();
        execution.setExecutionName(rule.getRuleName()+"-"+Tools.getRandomString(16));
        execution.setRuleId(ruleId);
        execution.setStatus(ExecutionStatus.NEW.getId());
        executionMapper.insert(execution);
        execution = executionMapper.get(execution.getExecutionId());
        //提交Execution
        executionManagement.submit(execution);
        return execution.getExecutionId();
    }

    /**
     * 停止Execution
     * @param executionId
     * @return
     */
    public BoolResult stopExecution(Integer executionId) {
        Execution execution = executionMapper.get(executionId);
        if(execution==null) {
            throw new ServiceException(ErrorCode.FAIL, "任务不存在");
        }
        if(execution.getStatus()>=ExecutionStatus.FINISHED.getId()) {
            throw new ServiceException(ErrorCode.FAIL, "任务已停止或已完成");
        }
        executionManagement.cancel(execution);
        //修改状态为stopped
        execution.setStatus(ExecutionStatus.STOPPED.getId());
        execution.setUpdatedAt(new Date());
        executionMapper.updateStatus(execution);
        return BoolResult.successResult(true);
    }

    /**
     * 重新运行或恢复Execution
     * @param executionId
     * @return
     */
    public BoolResult resumeExecution(Integer executionId) {
        Execution execution = executionMapper.get(executionId);
        if(execution==null) {
            throw new ServiceException(ErrorCode.FAIL, "任务不存在");
        }
        if(execution.getStatus()<ExecutionStatus.FINISHED.getId()) {
            throw new ServiceException(ErrorCode.FAIL, "任务已经在执行中");
        }
        //修改状态为new
        execution.setStatus(ExecutionStatus.NEW.getId());
        execution.setUpdatedAt(new Date());
        executionMapper.updateStatus(execution);
        executionManagement.submit(execution);
        return BoolResult.successResult(true);
    }

}

