package com.jollychic.holmes.common.web;

import lombok.Data;

/**
 * @DESCRIPTION: controller 返回值
 * Created by WIN7 on 2018/1/5.
 */

@Data
public class Result {
    private boolean success;
    private Object data;
    private int code;
    private String msg;

    public Result success(boolean success) {
        this.success = success;
        return this;
    }

    public Result data(Object data) {
        this.data = data;
        return this;
    }

    public Result errMsg(int errCode, String errMsg) {
        this.code = errCode;
        this.msg = errMsg;
        return this;
    }

    public static Result successResult() {
        return new Result().success(true);
    }

    public static Result successResult(Object data) {
        return successResult().data(data);
    }

    public static Result errorResult() {
        return new Result().success(false);
    }

    public static Result errorResult(int errCode, String errMsg) {
        return errorResult().errMsg(errCode, errMsg);
    }
}
