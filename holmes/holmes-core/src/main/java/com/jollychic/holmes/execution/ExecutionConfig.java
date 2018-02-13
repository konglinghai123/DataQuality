package com.jollychic.holmes.execution;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by WIN7 on 2018/1/9.
 */
@Data
public class ExecutionConfig {
    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer queueCapacity;
}
