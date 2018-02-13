package com.jollychic.holmes.result;

import com.jollychic.holmes.view.ExecutionView;
import lombok.Data;

/**
 * Created by WIN7 on 2018/1/22.
 */
@Data
public class ExecutionViewResult {
    private boolean success;
    private ExecutionView executionView;
    private int code;
    private String msg;

    public static ExecutionViewResult successResult(ExecutionView executionView) {
        ExecutionViewResult executionResult = new ExecutionViewResult();
        executionResult.setSuccess(true);
        executionResult.setExecutionView(executionView);
        return executionResult;
    }

    public static ExecutionViewResult errorResult(int errCode, String errMsg) {
        ExecutionViewResult executionResult = new ExecutionViewResult();
        executionResult.setSuccess(false);
        executionResult.setCode(errCode);
        executionResult.setMsg(errMsg);
        return executionResult;
    }
}
