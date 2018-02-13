package com.jollychic.holmes.result;

import com.jollychic.holmes.view.SourceConnectionView;
import lombok.Data;

/**
 * Created by WIN7 on 2018/1/22.
 */
@Data
public class SourceConnectionViewResult {
    private boolean success;
    private SourceConnectionView sourceConnection;
    private int code;
    private String msg;

    public static SourceConnectionViewResult successResult(SourceConnectionView sourceConnectionView) {
        SourceConnectionViewResult sourceConnectionViewResult = new SourceConnectionViewResult();
        sourceConnectionViewResult.setSuccess(true);
        sourceConnectionViewResult.setSourceConnection(sourceConnectionView);
        return sourceConnectionViewResult;
    }

    public static SourceConnectionViewResult errorResult(int errCode, String errMsg) {
        SourceConnectionViewResult sourceConnectionViewResult = new SourceConnectionViewResult();
        sourceConnectionViewResult.setSuccess(false);
        sourceConnectionViewResult.setCode(errCode);
        sourceConnectionViewResult.setMsg(errMsg);
        return sourceConnectionViewResult;
    }
}
