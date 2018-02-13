package com.jollychic.holmes.source;

import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.source.hive.HiveReader;
import com.jollychic.holmes.source.kafka.KafkaReader;
import com.jollychic.holmes.source.mysql.MysqlReader;

/**
 * Created by WIN7 on 2018/1/5.
 */
public enum SourceType {
    MYSQL("mysql", MysqlReader.class),
    HIVE("hive", HiveReader.class),
    KAFKA("kafka", KafkaReader.class);

    private String type;
    private Class<? extends SourceReader> readerClass;

    private SourceType(String type, Class<? extends SourceReader> readerClass) {
        this.type = type;
        this.readerClass = readerClass;
    }

    public String getType() {
        return type;
    }

    public SourceReader getSourceReader() {
        try {
            return readerClass.newInstance();
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.SOURCE_LOAD_ERROR, "can't new source reader class, "+e.getMessage());
        }
    }

}
