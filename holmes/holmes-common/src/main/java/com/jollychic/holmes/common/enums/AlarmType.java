package com.jollychic.holmes.common.enums;

/**
 * Created by WIN7 on 2018/1/5.
 */
public enum AlarmType {
    WECHAT("wechat"),
    EMAIL("email"),
    BOTH("both");

    private String type;

    private AlarmType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
