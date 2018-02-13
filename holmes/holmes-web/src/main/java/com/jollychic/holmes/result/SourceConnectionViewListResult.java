package com.jollychic.holmes.result;

import com.jollychic.holmes.view.SourceConnectionView;
import lombok.Data;

import java.util.List;

/**
 * Created by WIN7 on 2018/1/22.
 */
@Data
public class SourceConnectionViewListResult {
    private boolean success;
    private List<SourceConnectionView> sourceConnections;
    private int code;
    private String msg;

    public static SourceConnectionViewListResult successResult(List<SourceConnectionView> sourceConnectionViewList) {
        SourceConnectionViewListResult sourceConnectionViewListResult = new SourceConnectionViewListResult();
        sourceConnectionViewListResult.setSuccess(true);
        sourceConnectionViewListResult.setSourceConnections(sourceConnectionViewList);
        return sourceConnectionViewListResult;
    }

    public static SourceConnectionViewListResult errorResult(int errCode, String errMsg) {
        SourceConnectionViewListResult sourceConnectionViewListResult = new SourceConnectionViewListResult();
        sourceConnectionViewListResult.setSuccess(false);
        sourceConnectionViewListResult.setCode(errCode);
        sourceConnectionViewListResult.setMsg(errMsg);
        return sourceConnectionViewListResult;
    }
}
