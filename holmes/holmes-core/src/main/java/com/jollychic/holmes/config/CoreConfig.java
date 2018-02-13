package com.jollychic.holmes.config;

import com.jollychic.holmes.execution.ExecutionConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Created by WIN7 on 2018/1/10.
 */
@Configuration
public class CoreConfig {
    @Bean(name = "executionConfig")
    public ExecutionConfig executionConfig(Environment environment) {
        ExecutionConfig executionConfig = new ExecutionConfig();
        executionConfig.setCorePoolSize(Integer.valueOf(environment.getProperty("core.pool.size")));
        executionConfig.setMaxPoolSize(Integer.valueOf(environment.getProperty("max.pool.size")));
        executionConfig.setQueueCapacity(Integer.valueOf(environment.getProperty("queue.capacity")));
        return executionConfig;
    }
}
