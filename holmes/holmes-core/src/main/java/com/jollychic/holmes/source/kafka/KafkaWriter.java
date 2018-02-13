package com.jollychic.holmes.source.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Random;

/**
 * Created by WIN7 on 2018/1/15.
 */
public class KafkaWriter {
    private KafkaProducer<String, String> producer;

    public KafkaWriter(String servers, String clientId) {
        Properties properties = new Properties();
        //broker 集群地址
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        //自定义客户端id
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        //key 序列号方式
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //value 序列号方式
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.producer = new KafkaProducer(properties);
    }

    public void write(String topic, String key, String msg) throws Exception {
        producer.send(new ProducerRecord(topic, key, msg));
    }

    public void write(String topic, String msg) throws Exception {
        Random random = new Random();
        String key = String.valueOf(random.nextInt(1000));
        //key value都为String
        producer.send(new ProducerRecord(topic, key, msg));
    }

    public static void main(String[] args) throws Exception {
        KafkaWriter kafkaWriter = new KafkaWriter("172.31.2.7:9292,172.31.2.8:9292", "holmes");
        for(int i=0; i<100; i++) {
            kafkaWriter.write("holmes_test", "111111111111111111");
        }
    }

}
