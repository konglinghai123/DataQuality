package com.jollychic.holmes.source.ConnConfig;

import lombok.Data;

/**
 * Created by WIN7 on 2018/1/22.
 */
@Data
public class KafkaConfig implements ConnConfig {
    private String servers;
    private String groupId;
}
