package com.jollychic.holmes.source.kafka;

import com.jollychic.holmes.source.ConnConfig.KafkaConfig;
import com.jollychic.holmes.source.SourceConfig;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by WIN7 on 2018/2/7.
 */
public class KafkaReaderTest {
    @Test
    public void getCount() throws Exception {
        String topic = "holmes_test";
        String servers = "172.31.2.7:9292,172.31.2.8:9292";
        KafkaReader kafkaReader = new KafkaReader();
        SourceConfig sourceConfig = new SourceConfig();
        KafkaConfig kafkaConfig = new KafkaConfig();
        kafkaConfig.setServers(servers);
        kafkaConfig.setGroupId("holmes");
        sourceConfig.setConnConfig(kafkaConfig);
        sourceConfig.setTableName(topic);
        kafkaReader.init(sourceConfig);
        System.out.println(kafkaReader.getCount());
    }

}