package com.jollychic.holmes.result;

import com.jollychic.holmes.view.SourceTableView;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by WIN7 on 2018/1/22.
 */
@Data
public class SourceTableListResult {
    private boolean success;
    private Map<String, List<SourceTableView>> sourceTables;
    private int code;
    private String msg;

    public static SourceTableListResult successResult(Map<String, List<SourceTableView>> sourceTables) {
        SourceTableListResult sourceTableListResult = new SourceTableListResult();
        sourceTableListResult.setSuccess(true);
        sourceTableListResult.setSourceTables(sourceTables);
        return sourceTableListResult;
    }

    public static SourceTableListResult errorResult(int errCode, String errMsg) {
        SourceTableListResult sourceTableListResult = new SourceTableListResult();
        sourceTableListResult.setSuccess(false);
        sourceTableListResult.setCode(errCode);
        sourceTableListResult.setMsg(errMsg);
        return sourceTableListResult;
    }
}
