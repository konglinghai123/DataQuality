package com.jollychic.holmes.source.mysql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.common.utils.AESUtils;
import com.jollychic.holmes.common.utils.Tools;
import com.jollychic.holmes.rule.ruleConfig.KeyIndicatorWithDimensionConfig;
import com.jollychic.holmes.source.ConnConfig.ConnConfig;
import com.jollychic.holmes.source.ConnConfig.MysqlConfig;
import com.jollychic.holmes.source.SourceConfig;
import com.jollychic.holmes.source.SourceReaderDefault;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by WIN7 on 2018/1/15.
 */
@Slf4j
public class MysqlReader extends SourceReaderDefault {
    public static Map<String, MysqlConnection> conns = Maps.newHashMap();
    private MysqlConnection mysqlConnection = null;

    @Override
    public void init(SourceConfig sourceConfig) {
        super.init(sourceConfig);
        MysqlConfig mysqlConfig = (MysqlConfig) sourceConfig.getConnConfig();
        String url = "jdbc:mysql://"+mysqlConfig.getHost()+":"+mysqlConfig.getPort()+"/"+mysqlConfig.getDatabase();
        String user = mysqlConfig.getUser();
        String passwd = AESUtils.AES_CBC_Decrypt(mysqlConfig.getPasswd(), AESUtils.DEFAULT_PASSWORD);
        synchronized (this) {
            if (conns.get(url+user+passwd) != null) {
                mysqlConnection = conns.get(url+user+passwd);
            } else {
                try {
                    mysqlConnection = new MysqlConnection(url, user, passwd);
                } catch (SQLException e) {
                    throw new ServiceException(ErrorCode.SOURCE_CONNECTION_ERROR, "mysql connection error");
                }
                conns.put(url+user+passwd, mysqlConnection);
            }
        }
    }

    @Override
    public List<String> read() {
        List<String> result = Lists.newArrayList();
        try {
            ResultSet resultSet = mysqlConnection.executeQuery("SELECT * FROM "+sourceConfig.getTableName()+" WHERE "+
                    sourceConfig.getPartitionName()+"='"+sourceConfig.getPartitionValue()+"';");
            ResultSetMetaData md = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, Object> row = Maps.newHashMap();
                for(int i=1; i<=md.getColumnCount(); i++) {
                    row.put(md.getColumnName(i), resultSet.getObject(i));
                }
                result.add(JSON.toJSONString(row));
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new ServiceException(ErrorCode.SOURCE_READ_ERROR, e.getMessage());
        }
        return result;
    }

    @Override
    public List<String> read(String sql) {
        List<String> result = Lists.newArrayList();
        try {
            ResultSet resultSet = mysqlConnection.executeQuery(sql);
            ResultSetMetaData md = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, Object> row = Maps.newHashMap();
                for(int i=1; i<=md.getColumnCount(); i++) {
                    row.put(md.getColumnName(i), resultSet.getObject(i));
                }
                result.add(JSON.toJSONString(row));
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new ServiceException(ErrorCode.SOURCE_READ_ERROR, e.getMessage());
        }
        return result;
    }

    @Override
    public long getCount() {
        long result = 0;
        try {
            ResultSet resultSet = mysqlConnection.executeQuery("SELECT COUNT(*) FROM "+sourceConfig.getTableName()+" WHERE "+
                    sourceConfig.getPartitionName()+"='"+sourceConfig.getPartitionValue()+"';");
            if(resultSet.next()) {
                result = resultSet.getLong(1);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new ServiceException(ErrorCode.SOURCE_READ_ERROR, e.getMessage());
        }
        return result;
    }

    @Override
    public List<String> getTables() {
        List<String> result = Lists.newArrayList();
        try {
            ResultSet resultSet = mysqlConnection.executeQuery("SHOW tables;");
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new ServiceException(ErrorCode.SOURCE_READ_ERROR, e.getMessage());
        }
        return result;
    }

    @Override
    public void validateConnectionInfo(ConnConfig connConfig) {
        MysqlConfig mysqlConfig = (MysqlConfig) connConfig;
        String url = "jdbc:mysql://"+mysqlConfig.getHost()+":"+mysqlConfig.getPort()+"/"+mysqlConfig.getDatabase();
        String user = mysqlConfig.getUser();
        String passwd = AESUtils.AES_CBC_Decrypt(mysqlConfig.getPasswd(), AESUtils.DEFAULT_PASSWORD);
        MysqlConnection mysqlConnection;
        try {
            mysqlConnection = new MysqlConnection(url, user, passwd);
        } catch (SQLException e) {
            throw new ServiceException(ErrorCode.SOURCE_CONNECTION_ERROR, "mysql connection error");
        }
        if(!mysqlConnection.validate()) {
            throw new ServiceException(ErrorCode.SOURCE_CONNECTION_ERROR, "mysql connection error");
        }
    }

    @Override
    public String getTableParam(String connectionInfo, String tableName) {
        SourceConfig sourceConfig = new SourceConfig();
        sourceConfig.setConnConfig(JSON.parseObject(connectionInfo, MysqlConfig.class));
        sourceConfig.setTableName(tableName);
        init(sourceConfig);
        List<String> columns = Lists.newArrayList();
        try {
            ResultSet resultSet = mysqlConnection.executeQuery("show columns from "+tableName);
            while (resultSet.next()) {
                columns.add(resultSet.getString(1));
                columns.add(resultSet.getString(2).split("\\(")[0]);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new ServiceException(ErrorCode.SOURCE_READ_ERROR, e.getMessage());
        }
        return JSON.toJSONString(columns);
    }


}
