package com.jollychic.holmes.source.ConnConfig;

import lombok.Data;

/**
 * Created by WIN7 on 2018/1/22.
 */
@Data
public class MysqlConfig implements ConnConfig {
    private String host;
    private Integer port;
    private String database;
    private String user;
    private String passwd;
}
