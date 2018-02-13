package com.jollychic.holmes.view;

import lombok.Data;

@Data
public class ScheduleInputView {
    private String ruleName;
    private String cron;

    //将SourceConnection转换成View
    public static ScheduleInputView showScheduleInputView(String ruleName,String corn) {
        ScheduleInputView scheduleInputView = new ScheduleInputView();
        scheduleInputView.ruleName = ruleName;
        scheduleInputView.cron = corn;
        return scheduleInputView;
    }

}
