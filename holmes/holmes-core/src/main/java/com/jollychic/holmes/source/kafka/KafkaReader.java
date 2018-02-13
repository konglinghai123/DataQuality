package com.jollychic.holmes.source.kafka;

import com.google.common.collect.Lists;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.source.ConnConfig.ConnConfig;
import com.jollychic.holmes.source.ConnConfig.KafkaConfig;
import com.jollychic.holmes.source.SourceConfig;
import com.jollychic.holmes.source.SourceReaderDefault;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.*;

/**
 * Created by WIN7 on 2018/1/15.
 */
public class KafkaReader extends SourceReaderDefault {
    private KafkaConsumer<String, String> kafkaConsumer;
    private List<TopicPartition> topicPartitions = Lists.newArrayList();

    @Override
    public void init(SourceConfig sourceConfig) {
        super.init(sourceConfig);
        KafkaConfig kafkaConfig = (KafkaConfig) sourceConfig.getConnConfig();
        Properties properties = new Properties();
        // 通过其中的一台broker来找到group的coordinator，并不需要列出所有的broker
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getServers());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getGroupId());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        try {
            kafkaConsumer = new KafkaConsumer<>(properties);
        } catch (KafkaException e) {
            throw new ServiceException(ErrorCode.SOURCE_LOAD_ERROR, "new kafka consumer error");
        }
        // 设置patition信息
        String topic = sourceConfig.getTableName();
        if(sourceConfig.getPartitionList()!=null && sourceConfig.getPartitionList().size()>0) {
            for(int partition : sourceConfig.getPartitionList()) {
                topicPartitions.add(new TopicPartition(topic, partition));
            }
        } else {
            List<PartitionInfo> partitionInfos;
            try {
                partitionInfos = kafkaConsumer.partitionsFor(topic);
            } catch (KafkaException e) {
                throw new ServiceException(ErrorCode.SOURCE_CONNECTION_ERROR, "kafka get topic's partitions error");
            }
            for (PartitionInfo partitionInfo : partitionInfos) {
                topicPartitions.add(new TopicPartition(topic, partitionInfo.partition()));
            }
        }
        try {
            kafkaConsumer.assign(topicPartitions);
        } catch (KafkaException e) {
            throw new ServiceException(ErrorCode.SOURCE_LOAD_ERROR, "kafka consumer assign partitions error");
        }
    }

    @Override
    public List<String> read() {
        List<String> result = Lists.newArrayList();
        ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(60*1000);
        if(consumerRecords.isEmpty()) {
            return result;
        }
        Iterator<ConsumerRecord<String, String>> iterator = consumerRecords.iterator();
        while(iterator.hasNext()) {
            ConsumerRecord<String, String> consumerRecord = iterator.next();
            result.add(consumerRecord.value());
        }
        return result;
    }

    @Override
    public List<String> read(String sql) {
        return null;
    }

    @Override
    public long getCount() {
        long sum = 0L;
        for(TopicPartition topicPartition : topicPartitions) {
            kafkaConsumer.seekToEnd(topicPartition);
            sum += kafkaConsumer.position(topicPartition);
        }
        return sum;
    }

    @Override
    public List<String> getTables() {
        return null;
    }

    @Override
    public void validateConnectionInfo(ConnConfig connConfig) {
        KafkaConfig kafkaConfig = (KafkaConfig) connConfig;
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getServers());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getGroupId());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        try {
            kafkaConsumer = new KafkaConsumer<>(properties);
        } catch (KafkaException e) {
            throw new ServiceException(ErrorCode.SOURCE_LOAD_ERROR, "new kafka consumer error");
        }
    }

    @Override
    public String getTableParam(String connectionInfo, String tableName) {
        return "";
    }

}
