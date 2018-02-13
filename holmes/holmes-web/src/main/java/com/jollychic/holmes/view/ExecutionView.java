package com.jollychic.holmes.view;

import com.jollychic.holmes.common.enums.ExecutionStatus;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.mapper.RuleMapper;
import com.jollychic.holmes.model.Execution;
import com.jollychic.holmes.model.Rule;

import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Data
public class ExecutionView {
    private Integer executionId;    //自增主键
    private String executionName;   //任务名称，由规则名+"-"+随机串组成
    private String ruleName;
    private String status;     //任务状态，new(0),running(1),finished(2),stopped(3),error(4)
    private String errorInfo;   //status为error时的错误信息
    private String createdAt;
    private String updatedAt;

    public Execution showExecutionModel(RuleMapper ruleMapper){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Execution execution = new Execution();
        execution.setExecutionId(this.getExecutionId());
        execution.setExecutionName(this.executionName);
        Rule rule = ruleMapper.getByRuleName(this.ruleName);
        if (rule == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "rule doesn't exist");
        }
        execution.setRuleId(rule.getRuleId());

        if(ExecutionStatus.NEW.getType().equals(this.status)) {
            execution.setStatus(ExecutionStatus.NEW.getId());
        } else if(ExecutionStatus.RUNNING.getType().equals(this.status)) {
            execution.setStatus(ExecutionStatus.RUNNING.getId());
        } else if(ExecutionStatus.FINISHED.getType().equals(this.status)) {
            execution.setStatus(ExecutionStatus.FINISHED.getId());
        } else if(ExecutionStatus.STOPPED.getType().equals(this.status)) {
            execution.setStatus(ExecutionStatus.STOPPED.getId());
        } else if(ExecutionStatus.ERROR.getType().equals(this.status)) {
            execution.setStatus(ExecutionStatus.ERROR.getId());
        }
        try {
            execution.setCreatedAt(formatter.parse(this.createdAt));
            execution.setUpdatedAt(formatter.parse(this.updatedAt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return execution;
    }

    public static ExecutionView showExecutionView(Execution execution,RuleMapper ruleMapper) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ExecutionView executionView = new ExecutionView();
        executionView.executionId = execution.getExecutionId();
        executionView.executionName = execution.getExecutionName();
        Rule rule= ruleMapper.get(execution.getRuleId());
        if (rule == null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "rule doesn't exist");
        }
        executionView.ruleName = rule.getRuleName();
        executionView.status = ExecutionStatus.getTypeById(execution.getStatus());
        executionView.createdAt = formatter.format(execution.getCreatedAt());
        executionView.updatedAt = formatter.format(execution.getUpdatedAt());
       return executionView;
    }
}
