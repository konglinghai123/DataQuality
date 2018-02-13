package com.jollychic.holmes.execution;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jollychic.holmes.alarm.EmailAlarm;
import com.jollychic.holmes.alarm.WechatAlarm;
import com.jollychic.holmes.common.enums.AlarmType;
import com.jollychic.holmes.common.enums.ExecutionStatus;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.mapper.*;
import com.jollychic.holmes.model.Execution;
import com.jollychic.holmes.model.Rule;
import com.jollychic.holmes.rule.RuleFactory;
import com.jollychic.holmes.rule.RuleRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.FutureTask;

/**
 * Created by WIN7 on 2018/1/8.
 */
@Slf4j
@Component
public class ExecutionManagement {
    @Autowired
    protected SourceConnectionMapper sourceConnectionMapper;
    @Autowired
    protected SourceTableMapper sourceTableMapper;
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private ExecutionMapper executionMapper;

    private ExecutionConfig executionConfig;
    private static ThreadPoolTaskExecutor taskExecutor;
    private static ConcurrentMap<Integer, FutureTask> futureTaskMap= new ConcurrentHashMap<>();

    //初始化 taskExecutor
    @Autowired
    public ExecutionManagement(ExecutionConfig executionConfig) {
        if (taskExecutor==null){
            this.executionConfig = executionConfig;
            taskExecutor = new ThreadPoolTaskExecutor();
            taskExecutor.setCorePoolSize(executionConfig.getCorePoolSize());
            taskExecutor.setMaxPoolSize(executionConfig.getMaxPoolSize());
            taskExecutor.setQueueCapacity(executionConfig.getQueueCapacity());
            taskExecutor.initialize();
        }
    }

    public boolean submit(Execution execution) {
        ExecutionThread executionThread = new ExecutionThread(execution);
        FutureTask futureTask = new FutureTask<Void>(executionThread, null);
        futureTaskMap.put(executionThread.getExecutionId(), futureTask);
        taskExecutor.submit(futureTask);
        return true;
    }

    public boolean cancel(Execution execution) {
        FutureTask futureTask = futureTaskMap.get(execution.getExecutionId());
        boolean result = futureTask.cancel(true);
        futureTaskMap.remove(execution.getExecutionId());
        return result;
    }

    public ExecutionConfig getExecutionConfig() {
        return this.executionConfig;
    }

    private class ExecutionThread implements Runnable {
        private Execution execution;
        private Rule rule;

        @Override
        public void run() {
            try {
                //设置为running状态
                execution.setStatus(ExecutionStatus.RUNNING.getId());
                execution.setUpdatedAt(new Date());
                executionMapper.updateStatus(execution);

                String ruleId = execution.getRuleId();
                rule = ruleMapper.get(ruleId);
                RuleRunner ruleRunner = new RuleFactory().getRuleRunner(rule.getRuleType());
                ruleRunner.init(rule, execution);
                ruleRunner.run();

                //正常结束，设置为finished状态
                execution.setStatus(ExecutionStatus.FINISHED.getId());
                execution.setUpdatedAt(new Date());
                executionMapper.updateStatus(execution);
            } catch (ServiceException se) {
                execution.setStatus(ExecutionStatus.ERROR.getId());
                execution.setErrorInfo(execution.getErrorInfo()+"</br> error code: "+se.getCode()+", error msg: "+se.getMsg());
                execution.setUpdatedAt(new Date());
                executionMapper.updateStatus(execution);
                try {
                    //出现异常
                    this.outputErrorInfo(rule, execution.getErrorInfo());
                } catch (Exception e1) {

                }
            } catch (Exception e) {
                log.error("run execution error, "+e.getMessage());
                execution.setStatus(ExecutionStatus.ERROR.getId());
                execution.setErrorInfo(execution.getErrorInfo()+"</br> "+e.getMessage());
                execution.setUpdatedAt(new Date());
                executionMapper.updateStatus(execution);
                try {
                    //出现异常
                    this.outputErrorInfo(rule, execution.getErrorInfo());
                } catch (Exception e1) {

                }
            } finally {
                futureTaskMap.remove(execution.getExecutionId());
            }
        }

        public ExecutionThread(Execution execution) {
            this.execution = execution;
        }

        public Integer getExecutionId() {
            return execution.getExecutionId();
        }

        //对于出错的alarm返回信息
        private void outputErrorInfo(Rule rule, String errorInfo) {
            String ruleName = rule.getRuleName();
            Map<String, String> alarmUser = JSON.parseObject(rule.getAlarmUser(), new TypeReference<Map<String, String>>(){});
//            String user = alarmUser.get("user");
//            String email = alarmUser.get("email");
            String user = "[\"1199|1228\"]";
            String email = "[\"evan@jollycorp.com,zizi@jollycorp.com\"]";
            String content = "执行规则："+ruleName+"</br>异常信息："+errorInfo;
            String subject = ruleName+"执行异常";
            String title = "Holmes 执行异常";
            if(rule.getAlarmType()==null){
                WechatAlarm.sendMessage(user, title, content);
                EmailAlarm.sendMessage(email, subject, content);
            }else if(rule.getAlarmType().equalsIgnoreCase(AlarmType.WECHAT.getType())) {
                WechatAlarm.sendMessage(user, title, content);
            } else if(rule.getAlarmType().equalsIgnoreCase(AlarmType.EMAIL.getType())) {
                EmailAlarm.sendMessage(email, subject, content);
            } else {
                WechatAlarm.sendMessage(user, title, content);
                EmailAlarm.sendMessage(email, subject, content);
            }
        }

    }
}
