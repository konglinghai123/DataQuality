package com.jollychic.holmes.result;

import lombok.Data;

import java.util.List;

/**
 * Created by WIN7 on 2018/1/22.
 */
@Data
public class SourceTableSchemaResult {
    private boolean success;
    private List<String> tableSchema;
    private int code;
    private String msg;

    public static SourceTableSchemaResult successResult(List<String> tableSchema) {
        SourceTableSchemaResult sourceTableSchemaResult = new SourceTableSchemaResult();
        sourceTableSchemaResult.setSuccess(true);
        sourceTableSchemaResult.setTableSchema(tableSchema);
        return sourceTableSchemaResult;
    }

    public static SourceTableSchemaResult errorResult(int errCode, String errMsg) {
        SourceTableSchemaResult sourceTableSchemaResult = new SourceTableSchemaResult();
        sourceTableSchemaResult.setSuccess(false);
        sourceTableSchemaResult.setCode(errCode);
        sourceTableSchemaResult.setMsg(errMsg);
        return sourceTableSchemaResult;
    }
}
