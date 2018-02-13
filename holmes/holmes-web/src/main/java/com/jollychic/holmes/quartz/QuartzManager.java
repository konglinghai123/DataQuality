package com.jollychic.holmes.quartz;

import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.common.utils.DateUtils;
import com.jollychic.holmes.service.ExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @Description: 定时任务管理类
 */
@Slf4j
@Component
public class QuartzManager {
    private static String JOB_GROUP_NAME = "EXTJWEB_JOBGROUP_NAME";
    private static String TRIGGER_GROUP_NAME = "EXTJWEB_TRIGGERGROUP_NAME";
    private  static Scheduler sched;
    public QuartzManager(){
        try {
            sched = StdSchedulerFactory.getDefaultScheduler();
            sched.start();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    /**
     * @Description: 根据ruleId查询一个任务(使用默认的任务组名，触发器名，触发器组名)
     * @param ruleId
     *
     */
    public static JobDetail getJob(String ruleId) {
        JobKey jobKey =new JobKey(ruleId,JOB_GROUP_NAME);
        JobDetail jobDetail = null;
        try {
            jobDetail = sched.getJobDetail(jobKey);
        } catch (SchedulerException e) {
            throw new ServiceException(ErrorCode.FAIL, "getJobDetail is error, "+e.getMessage());
        }
        return jobDetail;

    }
    /**
     * @Description: 查询所有任务名(使用默认的任务组名，触发器名，触发器组名)
     *
     */
    public static List<String> getAllJobNames(){
        List<String> jobNames = new ArrayList<>();
        try {
            for (String groupName : sched.getJobGroupNames()) {
                for (JobKey jobKey : sched.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    String jobName = jobKey.getName();
                    jobNames.add(jobName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobNames;
    }
    /**
     * @Description: 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
     *
     * @param ruleId     任务名
     * @param cls        任务
     * @param corn       时间设置，参考quartz说明文档
     * @Title: QuartzManager.java
     */
    @SuppressWarnings("unchecked")
    public void addJob( String ruleId, Class cls, String corn,ExecutionService executionService) {
        try {
            String jobName = ruleId;
            JobDetail jobDetail = JobBuilder.newJob(cls)
                    .withIdentity(jobName, JOB_GROUP_NAME)
                    .withDescription("job")
                    .build();// 任务名，任务组，任务执行类
            // 触发器
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .startNow()//一旦加入scheduler，立即生效
                    .withIdentity(jobName, TRIGGER_GROUP_NAME) // 触发器名,触发器组
                    .withSchedule( CronScheduleBuilder.cronSchedule(corn))//使用CornTrigger
                    .build();
            jobDetail.getJobDataMap().put("ruleId",ruleId);
            sched.scheduleJob(jobDetail, trigger);
            // 启动
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.FAIL, "getJobDetail is error, "+e.getMessage());
        }
    }

    /**
     * @Description: 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名)
     *
     * @param ruleId
     * @param corn
     */
    @SuppressWarnings("unchecked")
    public void modifyJobTime(String ruleId, String corn,ExecutionService executionService ) {
        try {
            TriggerKey triggerKey = new TriggerKey(ruleId,TRIGGER_GROUP_NAME);
            CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }
            String oldCorn = trigger.getCronExpression();
            if (!oldCorn.equalsIgnoreCase(corn)) {
                JobKey jobKey =new JobKey(ruleId,JOB_GROUP_NAME);
                JobDetail jobDetail = sched.getJobDetail(jobKey);
                Class objJobClass = jobDetail.getJobClass();
                removeJob(ruleId);
                addJob(ruleId, objJobClass, corn, executionService);
            }
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "modifyJobTime is error, "+e.getMessage());
        }
    }


    /**
     * @Description: 移除一个任务(使用默认的任务组名，触发器名，触发器组名)
     *
     * @param ruleId
     *
     */
    public static void removeJob(String ruleId) {
        try {
            log.info("start RemoveJob");
            sched.pauseTrigger(new TriggerKey(ruleId, TRIGGER_GROUP_NAME));// 停止触发器
            sched.unscheduleJob(new TriggerKey(ruleId, TRIGGER_GROUP_NAME));// 移除触发器
            sched.deleteJob(new JobKey(ruleId, JOB_GROUP_NAME));// 删除任务
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.FAIL, "removeJob is error, "+e.getMessage());
        }
    }

    /**
     * @Description:启动所有定时任务
     */
    public static void startJobs() {
        try {
            Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
            sched.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description:关闭所有定时任务
     */
    public static void shutdownJobs() {
        try {
            Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
            if (!sched.isShutdown()) {
                sched.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatTime(Date date){
        //0/1 * * * * ?
        String time = DateUtils.format( date, "s m h d * ?");
        return time;
    }

    public static String showStatus(String ruleId){
        try {
            Trigger.TriggerState state = sched.getTriggerState(new TriggerKey(ruleId, TRIGGER_GROUP_NAME));
            return state.toString();
        } catch (SchedulerException e) {
            throw new ServiceException(ErrorCode.FAIL, "getTriggerState is error, "+e.getMessage());
        }
    }
    public static Trigger showTrigger(String ruleId){
        try {
            Trigger trigger = sched.getTrigger(new TriggerKey(ruleId, TRIGGER_GROUP_NAME));
            return trigger;
        } catch (SchedulerException e) {
            throw new ServiceException(ErrorCode.FAIL, "getTrigger is error, "+e.getMessage());
        }
    }





    /**
     * @Description: 添加一个定时任务
     *
     * @param jobName
     *      任务名
     * @param jobGroupName
     *      任务组名
     * @param triggerName
     *      触发器名
     * @param triggerGroupName
     *      触发器组名
     * @param jobClass
     *      任务
     * @param corn
     *      时间设置，corn类型
     *
     */
    @SuppressWarnings("unchecked")
    public static void addJob(String jobName, String jobGroupName,
                              String triggerName, String triggerGroupName, Class jobClass,
                              String corn,String ruleId) {
        try {
            Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
            JobDetail jobDetail =JobBuilder.newJob(jobClass)
                    .withIdentity(jobName, jobGroupName)
                    .withDescription("job")
                    .build();// 任务名，任务组，任务执行类
            jobDetail.getJobDataMap().put("ruleId",ruleId);
            // 触发器
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .startNow()//一旦加入scheduler，立即生效
                    .withIdentity(triggerName, triggerGroupName)
                    .withSchedule(CronScheduleBuilder.cronSchedule(corn))//使用CornTrigger
                    .build();
            sched.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * @Description: 修改一个任务的触发时间
     *
     */
    public static void modifyJobTime(String jobName, String jobGroupName, String triggerName, String triggerGroupName, String cron) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(cron)) {
                // 触发器
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
                // 触发器名,触发器组
                triggerBuilder.withIdentity(triggerName, triggerGroupName);
                triggerBuilder.startNow();
                // 触发器时间设定
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
                // 创建Trigger对象
                trigger = (CronTrigger) triggerBuilder.build();
                // 方式一 ：修改一个任务的触发时间
                scheduler.rescheduleJob(triggerKey, trigger);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 功能: 移除一个任务
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     */
    public static void removeJob(String jobName, String jobGroupName,String triggerName, String triggerGroupName) {
        try {
            Scheduler scheduler =  StdSchedulerFactory.getDefaultScheduler();

            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName,triggerGroupName);
            // 停止触发器
            scheduler.pauseTrigger(triggerKey);
            // 移除触发器
            scheduler.unscheduleJob(triggerKey);
            // 删除任务
            scheduler.deleteJob(JobKey.jobKey(jobName,jobGroupName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}