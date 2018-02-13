package com.jollychic.holmes.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.common.utils.Tools;
import com.jollychic.holmes.common.web.Result;
import com.jollychic.holmes.mapper.ExecutionMapper;
import com.jollychic.holmes.mapper.RuleMapper;
import com.jollychic.holmes.model.Execution;
import com.jollychic.holmes.model.Rule;
import com.jollychic.holmes.quartz.QuartzJob;
import com.jollychic.holmes.quartz.QuartzManager;
import com.jollychic.holmes.result.BoolResult;
import com.jollychic.holmes.result.ScheduleViewListResult;
import com.jollychic.holmes.result.ScheduleViewResult;
import com.jollychic.holmes.view.ScheduleInputView;
import com.jollychic.holmes.view.ScheduleView;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;


/**
 * @Description: 定时任务管理类
 */
@Slf4j
@Service
@Component
public class ScheduleService {
    @Autowired
    private ExecutionService executionService;
    @Autowired
    private RuleMapper ruleMapper;

    /**
     * @Description: ruleName=jobName，获取某个Schedule信息
     * @param ruleName
     */
    public ScheduleViewResult getJob(String ruleName) {
        ScheduleView scheduleView = ScheduleView.showScheduleView(ruleName, ruleMapper);
        return ScheduleViewResult.successResult(scheduleView);
    }

    /**
     * @Description: 获取所有Schedule信息
     */
    public ScheduleViewListResult getAllJobs() {
        List<ScheduleView> scheduleViews = ScheduleView.showScheduleViews(ruleMapper);
        return ScheduleViewListResult.successResult(scheduleViews);
    }

    /**
     * @Description: 添加Schedule
     * @param scheduleInputView
     */
    public Result insertJob(ScheduleInputView scheduleInputView) {
        String ruleName = scheduleInputView.getRuleName();
        if (Tools.isEmptyString(ruleName)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "ruleName is null");
        }
        String corn =scheduleInputView.getCron();
        if (Tools.isEmptyString(corn)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "corn is null");
        }
       //解析，验证，添加JOB
        Rule rule = ruleMapper.getByRuleName(ruleName);
        if (rule==null){
            throw new ServiceException(ErrorCode.FAIL, "rule doesn't exist");
        }
        String ruleId =rule.getRuleId();
        //验证schedule是否存在
        String status = QuartzManager.showStatus(ruleId);
        if (!status.equals("NONE")){
            throw new ServiceException(ErrorCode.FAIL, "this rule already set schedule");
        }
        QuartzManager quartzManager = new QuartzManager();
        quartzManager.addJob(ruleId, QuartzJob.class, corn, executionService);
        Integer executionId = executionService.submitExecution(ruleId);
        return Result.successResult(executionId);
    }

    /**
     * @Description: ruleName=jobName，删除某个Schedule
     * @param ruleName
     */
    public BoolResult deleteJob(String ruleName) {
        Rule rule = ruleMapper.getByRuleName(ruleName);
        if (rule==null){
            throw new ServiceException(ErrorCode.FAIL, "this rule doesn't exist");
        }
        String ruleId =rule.getRuleId();
        //验证schedule是否存在
        String status = QuartzManager.showStatus(ruleId);
        if (status.equals("NONE")){
            throw new ServiceException(ErrorCode.FAIL, "this rule doesn't set schedule");
        }
        QuartzManager.removeJob(ruleId);
        return BoolResult.successResult(true);
    }
    public BoolResult deleteUselessJob() {
        //查到所有的job
        List<String> jobNames = QuartzManager.getAllJobNames();
        for (String ruleId : jobNames){
            Rule rule = ruleMapper.get(ruleId);
            if (rule==null){
                //删除job
                QuartzManager.removeJob(ruleId);
            }
        }
        return BoolResult.successResult(true);
    }
    /**
     * @Description: 更改某个Schedule
     * @param scheduleInputView
     */
    public BoolResult updateJob(ScheduleInputView scheduleInputView) {
        String ruleName = scheduleInputView.getRuleName();
        if (Tools.isEmptyString(ruleName)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "ruleName is null");
        }
        String corn =scheduleInputView.getCron();
        if (Tools.isEmptyString(corn)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "corn is null");
        }
        Rule rule = ruleMapper.getByRuleName(ruleName);
        if (rule==null){
            throw new ServiceException(ErrorCode.FAIL, "this rule doesn't exist");
        }
        String ruleId =rule.getRuleId();
        //验证schedule是否存在
        String status = QuartzManager.showStatus(ruleId);
        QuartzManager quartzManager = new QuartzManager();
        if (status.equals("NONE")){
            //如果不存在，添加
            quartzManager.addJob(ruleId, QuartzJob.class, corn, executionService);
        }else{
            //如果存在，删除再添加
            quartzManager.modifyJobTime(ruleId,corn,executionService);
        }
        return BoolResult.successResult(true);
    }

}