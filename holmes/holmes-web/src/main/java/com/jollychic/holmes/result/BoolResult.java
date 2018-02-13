package com.jollychic.holmes.result;

import lombok.Data;

/**
 * @DESCRIPTION: controller 返回值
 * Created by WIN7 on 2018/1/5.
 */

@Data
public class BoolResult {
    private boolean success;
    private boolean result;
    private int code;
    private String msg;

    public static BoolResult successResult(boolean result) {
        BoolResult boolResult = new BoolResult();
        boolResult.setSuccess(true);
        boolResult.setResult(result);
        return boolResult;
    }

    public static BoolResult errorResult(int errCode, String errMsg) {
        BoolResult boolResult = new BoolResult();
        boolResult.setSuccess(false);
        boolResult.setCode(errCode);
        boolResult.setMsg(errMsg);
        return boolResult;
    }
}
