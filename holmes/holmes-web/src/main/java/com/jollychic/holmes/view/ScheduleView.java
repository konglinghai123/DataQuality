package com.jollychic.holmes.view;

import com.google.common.collect.Lists;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.common.utils.DateUtils;
import com.jollychic.holmes.mapper.RuleMapper;
import com.jollychic.holmes.model.Rule;
import com.jollychic.holmes.quartz.QuartzManager;
import lombok.Data;
import org.quartz.CronTrigger;

import java.util.List;


@Data
public class ScheduleView {
    private String ruleName;
    private String cronExpression;
    private String startExecutetime;
    private String lastExecutetime;
    private String nextExecutetime;

    public static ScheduleView showScheduleView(String ruleName, RuleMapper ruleMapper) {
        Rule rule = ruleMapper.getByRuleName(ruleName);
        if (rule==null){
            throw new ServiceException(ErrorCode.FAIL, "this rule doesn't exist");
        }
        String ruleId =rule.getRuleId();
        CronTrigger trigger =(CronTrigger) QuartzManager.showTrigger(ruleId);
        if (trigger==null){
            return null;
        }
        ScheduleView scheduleView = new ScheduleView();
        scheduleView.setRuleName(ruleName);
        scheduleView.setCronExpression(trigger.getCronExpression());
        scheduleView.setStartExecutetime(DateUtils.format(trigger.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
        if(trigger.getPreviousFireTime()!=null) {
            scheduleView.setLastExecutetime(DateUtils.format(trigger.getPreviousFireTime(), "yyyy-MM-dd HH:mm:ss"));
        }
        scheduleView.setNextExecutetime(DateUtils.format(trigger.getNextFireTime(), "yyyy-MM-dd HH:mm:ss"));
        return scheduleView;
    }

    public static List<ScheduleView> showScheduleViews(RuleMapper ruleMapper) {
        List<Rule> rules = ruleMapper.getAll();
        List<ScheduleView> scheduleViews = Lists.newArrayList();
        if (rules==null){
            return scheduleViews;
        }
        for(Rule rule : rules) {
            String ruleId = rule.getRuleId();
            CronTrigger trigger = (CronTrigger) QuartzManager.showTrigger(ruleId);
            if (trigger == null) {
                continue;
            }
            ScheduleView scheduleView = new ScheduleView();
            scheduleView.setRuleName(rule.getRuleName());
            scheduleView.setCronExpression(trigger.getCronExpression());
            scheduleView.setStartExecutetime(DateUtils.format(trigger.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
            if(trigger.getPreviousFireTime()!=null) {
                scheduleView.setLastExecutetime(DateUtils.format(trigger.getPreviousFireTime(), "yyyy-MM-dd HH:mm:ss"));
            }
            scheduleView.setNextExecutetime(DateUtils.format(trigger.getNextFireTime(), "yyyy-MM-dd HH:mm:ss"));
            scheduleViews.add(scheduleView);
        }
        return scheduleViews;
    }
}
