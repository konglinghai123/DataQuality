package com.jollychic.holmes.result;

import com.jollychic.holmes.view.ExecutionView;
import lombok.Data;

import java.util.List;

/**
 * Created by WIN7 on 2018/1/22.
 */
@Data
public class ExecutionViewListResult {
    private boolean success;
    private List<ExecutionView> executionViews;
    private int code;
    private String msg;

    public static ExecutionViewListResult successResult(List<ExecutionView> executionViews) {
        ExecutionViewListResult executionListResult = new ExecutionViewListResult();
        executionListResult.setSuccess(true);
        executionListResult.setExecutionViews(executionViews);
        return executionListResult;
    }

    public static ExecutionViewListResult errorResult(int errCode, String errMsg) {
        ExecutionViewListResult executionListResult = new ExecutionViewListResult();
        executionListResult.setSuccess(false);
        executionListResult.setCode(errCode);
        executionListResult.setMsg(errMsg);
        return executionListResult;
    }
}
