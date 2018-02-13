package com.jollychic.holmes.view;


import com.alibaba.fastjson.JSON;
import com.jollychic.holmes.common.utils.AESUtils;
import com.jollychic.holmes.model.SourceConnection;
import com.jollychic.holmes.source.ConnConfig.ConnConfig;
import com.jollychic.holmes.source.ConnConfig.HiveConfig;
import com.jollychic.holmes.source.ConnConfig.KafkaConfig;
import com.jollychic.holmes.source.ConnConfig.MysqlConfig;
import com.jollychic.holmes.source.SourceFactory;
import com.jollychic.holmes.source.SourceType;
import lombok.Data;

import java.util.Date;

/**
 * Created by WIN7 on 2018/1/16.
 */
@Data
public class SourceConnectionView<S extends ConnConfig> {
    private String connectionName;
    private String sourceType;      //数据源类型，mysql/hive/kafka
    private S sourceConfig;

    //将View转换成SourceConnection
    public SourceConnection showSourceConnectionModel() {
        SourceConnection sourceConnection = new SourceConnection();
        sourceConnection.setConnectionName(this.getConnectionName());
        sourceConnection.setSourceType(this.getSourceType());
        if(SourceType.MYSQL.getType().equalsIgnoreCase(sourceConnection.getSourceType())) {
            MysqlConfig mysqlConfig = (MysqlConfig) sourceConfig;
            mysqlConfig.setPasswd(AESUtils.AES_CBC_Encrypt(mysqlConfig.getPasswd(), AESUtils.DEFAULT_PASSWORD));
            validateConnectionInfo(mysqlConfig);
            sourceConnection.setConnectionInfo(JSON.toJSONString(mysqlConfig));
        } else if(SourceType.HIVE.getType().equalsIgnoreCase(sourceConnection.getSourceType())) {
            HiveConfig hiveConfig = (HiveConfig) sourceConfig;
            hiveConfig.setPasswd(AESUtils.AES_CBC_Encrypt(hiveConfig.getPasswd(), AESUtils.DEFAULT_PASSWORD));
            validateConnectionInfo(hiveConfig);
            sourceConnection.setConnectionInfo(JSON.toJSONString(hiveConfig));
        } else if(SourceType.KAFKA.getType().equalsIgnoreCase(sourceConnection.getSourceType())) {
            KafkaConfig kafkaConfig = (KafkaConfig) sourceConfig;
            validateConnectionInfo(kafkaConfig);
            sourceConnection.setConnectionInfo(JSON.toJSONString(kafkaConfig));
        }
        sourceConnection.setUpdatedAt(new Date());
        return sourceConnection;
    }

    //将SourceConnection转换成View
    public static SourceConnectionView showSourceConnectionView(SourceConnection sourceConnection) {
        SourceConnectionView sourceConnectionView = new SourceConnectionView();
        sourceConnectionView.connectionName = sourceConnection.getConnectionName();
        sourceConnectionView.sourceType = sourceConnection.getSourceType();
        if(SourceType.MYSQL.getType().equalsIgnoreCase(sourceConnection.getSourceType())) {
            MysqlConfig mysqlConfig = JSON.parseObject(sourceConnection.getConnectionInfo(), MysqlConfig.class);
            mysqlConfig.setPasswd("******");
            sourceConnectionView.sourceConfig = mysqlConfig;
        } else if(SourceType.HIVE.getType().equalsIgnoreCase(sourceConnection.getSourceType())) {
            HiveConfig hiveConfig = JSON.parseObject(sourceConnection.getConnectionInfo(), HiveConfig.class);
            hiveConfig.setPasswd("******");
            sourceConnectionView.sourceConfig = hiveConfig;
        } else if(SourceType.KAFKA.getType().equalsIgnoreCase(sourceConnection.getSourceType())) {
            sourceConnectionView.sourceConfig = JSON.parseObject(sourceConnection.getConnectionInfo(), KafkaConfig.class);
        }
        return sourceConnectionView;
    }

    //验证规格，使用填充密码解密时，输入长度必须是16的倍数
    public void validateConnectionInfo(ConnConfig connConfig) {
        new SourceFactory().getSourceReader(this.getSourceType()).validateConnectionInfo(connConfig);
    }
}
