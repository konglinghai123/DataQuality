package com.jollychic.holmes.result;

import com.jollychic.holmes.view.ScheduleView;
import lombok.Data;

import java.util.List;

/**
 * Created by WIN7 on 2018/1/25.
 */
@Data
public class ScheduleViewListResult {
    private boolean success;
    private List<ScheduleView> scheduleViews;
    private int code;
    private String msg;

    public static ScheduleViewListResult successResult(List<ScheduleView> scheduleViews) {
        ScheduleViewListResult scheduleViewListResult = new ScheduleViewListResult();
        scheduleViewListResult.setSuccess(true);
        scheduleViewListResult.setScheduleViews(scheduleViews);
        return scheduleViewListResult;
    }

    public static ScheduleViewListResult errorResult(int errCode, String errMsg) {
        ScheduleViewListResult executionListResult = new ScheduleViewListResult();
        executionListResult.setSuccess(false);
        executionListResult.setCode(errCode);
        executionListResult.setMsg(errMsg);
        return executionListResult;
    }
}
