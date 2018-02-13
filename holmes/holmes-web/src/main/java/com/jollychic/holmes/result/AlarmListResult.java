package com.jollychic.holmes.result;

import com.jollychic.holmes.view.AlarmView;
import lombok.Data;

import java.util.List;
@Data
public class AlarmListResult {
    private boolean success;
    private List<AlarmView> alarms;
    private int code;
    private String msg;

    public static AlarmListResult successResult(List<AlarmView> alarmViews) {
        AlarmListResult alarmListResult = new AlarmListResult();
        alarmListResult.setSuccess(true);
        alarmListResult.setAlarms(alarmViews);
        return alarmListResult;
    }

    public static AlarmListResult errorResult(int errCode, String errMsg) {
        AlarmListResult alarmListResult = new AlarmListResult();
        alarmListResult.setSuccess(false);
        alarmListResult.setCode(errCode);
        alarmListResult.setMsg(errMsg);
        return alarmListResult;
    }
}
