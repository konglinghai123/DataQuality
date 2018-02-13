package com.jollychic.holmes.result;

import com.jollychic.holmes.view.ScheduleView;
import lombok.Data;

@Data
public class ScheduleViewResult {
    private boolean success;
    private ScheduleView scheduleView;
    private int code;
    private String msg;

    public static ScheduleViewResult successResult(ScheduleView scheduleView) {
        ScheduleViewResult scheduleViewResult = new ScheduleViewResult();
        scheduleViewResult.setSuccess(true);
        scheduleViewResult.setScheduleView(scheduleView);
        return scheduleViewResult;
    }

    public static ScheduleViewResult errorResult(int errCode, String errMsg) {
        ScheduleViewResult scheduleViewResult = new ScheduleViewResult();
        scheduleViewResult.setSuccess(false);
        scheduleViewResult.setCode(errCode);
        scheduleViewResult.setMsg(errMsg);
        return scheduleViewResult;
    }
}
