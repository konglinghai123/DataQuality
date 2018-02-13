package com.jollychic.holmes.common.enums;

/**
 * Created by WIN7 on 2018/1/23.
 */
public enum ExecutionStatus {
    NEW(0, "new"),
    RUNNING(1, "running"),
    FINISHED(2, "finished"),
    STOPPED(3, "stopped"),
    ERROR(4, "error");

    private Integer id;
    private String type;

    private ExecutionStatus(Integer id, String type) {
        this.id = id;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public static String getTypeById(Integer id) {
        for(ExecutionStatus executionStatus : ExecutionStatus.values()) {
            if(executionStatus.getId().equals(id)) {
                return executionStatus.getType();
            }
        }
        return null;
    }
}
