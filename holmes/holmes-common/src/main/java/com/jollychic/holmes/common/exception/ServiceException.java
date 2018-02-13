package com.jollychic.holmes.common.exception;

/**
 * Created by WIN7 on 2018/1/5.
 */
public class ServiceException extends RuntimeException {
    /**
     * 错误码
     */
    private int code;
    /**
     * 错误信息
     */
    private String msg;

    public ServiceException(int code, String msg) {
        super("错误码：" + code + "；错误信息：" + msg);
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
