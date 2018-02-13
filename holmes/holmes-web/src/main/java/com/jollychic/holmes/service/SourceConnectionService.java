package com.jollychic.holmes.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jollychic.holmes.common.utils.AESUtils;
import com.jollychic.holmes.mapper.SourceTableMapper;
import com.jollychic.holmes.mapper.TableRuleTmpMapper;
import com.jollychic.holmes.model.SourceTable;
import com.jollychic.holmes.result.SourceConnectionViewListResult;
import com.jollychic.holmes.result.SourceConnectionViewResult;
import com.jollychic.holmes.source.ConnConfig.HiveConfig;
import com.jollychic.holmes.source.ConnConfig.KafkaConfig;
import com.jollychic.holmes.source.ConnConfig.MysqlConfig;
import com.jollychic.holmes.source.SourceType;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.common.utils.Tools;
import com.jollychic.holmes.mapper.SourceConnectionMapper;
import com.jollychic.holmes.model.SourceConnection;
import com.jollychic.holmes.result.BoolResult;
import com.jollychic.holmes.view.SourceConnectionView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by WIN7 on 2018/1/4.
 */
@Slf4j
@Service
public class SourceConnectionService {
    @Autowired
    private SourceConnectionMapper sourceConnectionMapper;
    @Autowired
    private SourceTableMapper sourceTableMapper;
    @Autowired
    private TableRuleTmpMapper tableRuleTmpMapper;
    private String tableId;

    public SourceConnectionViewResult getSourceConnectionById(String id){
        SourceConnection sourceConnection = sourceConnectionMapper.get(id);
        SourceConnectionView sourceConnectionView = SourceConnectionView.showSourceConnectionView(sourceConnection);
        return SourceConnectionViewResult.successResult(sourceConnectionView);
    }

    /**
     * @Description: 根据connName，获取某个SourceConnection
     * @param connName
     */
    public SourceConnectionViewResult getSourceConnectionByName(String connName){
        SourceConnection sourceConnection = sourceConnectionMapper.getByConnName(connName);
        if(sourceConnection==null){
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "this connection is not exist");
        }
        SourceConnectionView sourceConnectionView = SourceConnectionView.showSourceConnectionView(sourceConnection);
        return SourceConnectionViewResult.successResult(sourceConnectionView);
    }

    /**
     * @Description: 获取所有SourceConnection
     */
    public SourceConnectionViewListResult getAllSourceConnection() {
        List<SourceConnection> sourceConnections = sourceConnectionMapper.getAll();
        List<SourceConnectionView> sourceConnectionViews = Lists.newArrayList();
        for(SourceConnection sourceConnection : sourceConnections) {
            SourceConnectionView connectionView = SourceConnectionView.showSourceConnectionView(sourceConnection);
            sourceConnectionViews.add(connectionView);
        }
        return SourceConnectionViewListResult.successResult(sourceConnectionViews);
    }

    public BoolResult deleteSourceConnectionById(String id) {
        sourceConnectionMapper.delete(id);
        return BoolResult.successResult(true);
    }

    /**
     * @Description: 根据connName，删除某个SourceConnection
     * @param connName
     */
    public BoolResult deleteSourceConnectionByName(String connName) {
        SourceConnection sourceConnection=sourceConnectionMapper.getByConnName(connName);
        if(sourceConnection==null){
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "this connection is not exist");
        }
        List<SourceTable> sourceTables = sourceTableMapper.getByConnName(connName);
        if(sourceTables.size()!=0){
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "this connection still exist table,you can't delete it");
        }
        sourceConnectionMapper.deleteByConnName(connName);
        return BoolResult.successResult(true);
    }

    /**
     * @Description: 添加某个SourceConnection
     * @param sourceConnectionView
     */
    public BoolResult insertSourceConnection(SourceConnectionView sourceConnectionView) throws Exception {
        if(sourceConnectionView==null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "input is null");
        }
        //验证参数
        validateSourceConnectionView(sourceConnectionView);
        //验证connectionName是否已存在
        SourceConnection oldSourceConnection = sourceConnectionMapper.getByConnName(sourceConnectionView.getConnectionName());
        if(oldSourceConnection!=null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "this connectionName already exist");
        }
        SourceConnection sourceConnection = sourceConnectionView.showSourceConnectionModel();
        sourceConnectionMapper.insert(sourceConnection);
        return BoolResult.successResult(true);
    }

    /**
     * @Description: 更新某个SourceConnection
     * @param connName
     * @param sourceConnectionView
     */
    public BoolResult updateSourceConnection(String connName, SourceConnectionView sourceConnectionView) throws Exception {
        if(sourceConnectionView==null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "input is null");
        }
        SourceConnection oldSourceConnection = sourceConnectionMapper.getByConnName(connName);
        if(oldSourceConnection==null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "this sourceConnection is not exist");
        }
        // 将old值加入到view
        if (Tools.isEmptyString(sourceConnectionView.getConnectionName())) {
            sourceConnectionView.setConnectionName(oldSourceConnection.getConnectionName());
        }
        if (Tools.isEmptyString(sourceConnectionView.getSourceType())) {
            sourceConnectionView.setSourceType(oldSourceConnection.getSourceType());
        }
        String sourceType = sourceConnectionView.getSourceType();
        if(SourceType.MYSQL.getType().equalsIgnoreCase(sourceType)) {
            if (sourceConnectionView.getSourceConfig()==null) {
                MysqlConfig mysqlConfig = JSON.parseObject(oldSourceConnection.getConnectionInfo(), MysqlConfig.class);
                String passwd = AESUtils.AES_CBC_Decrypt(mysqlConfig.getPasswd(), AESUtils.DEFAULT_PASSWORD);
                mysqlConfig.setPasswd(passwd);
                sourceConnectionView.setSourceConfig(mysqlConfig);
                log.info(mysqlConfig.toString());
            }
        } else if(SourceType.HIVE.getType().equalsIgnoreCase(sourceType)) {
            if (sourceConnectionView.getSourceConfig()==null) {
                HiveConfig hiveConfig = JSON.parseObject(oldSourceConnection.getConnectionInfo(), HiveConfig.class);
                String passwd = AESUtils.AES_CBC_Decrypt(hiveConfig.getPasswd(), AESUtils.DEFAULT_PASSWORD);
                hiveConfig.setPasswd(passwd);
                sourceConnectionView.setSourceConfig(hiveConfig);
            }
        } else if(SourceType.KAFKA.getType().equalsIgnoreCase(sourceType)) {
            if (sourceConnectionView.getSourceConfig()==null) {
                sourceConnectionView.setSourceConfig(JSON.parseObject(oldSourceConnection.getConnectionInfo(), KafkaConfig.class));
            }
        }
        //验证参数
        validateSourceConnectionView(sourceConnectionView);
        SourceConnection sourceConnection = sourceConnectionView.showSourceConnectionModel();
        sourceConnection.setConnectionId(oldSourceConnection.getConnectionId());
        //验证连接名是否重复
        if(!sourceConnection.getConnectionName().equals(oldSourceConnection.getConnectionName())) {
            SourceConnection newSourceConnection = sourceConnectionMapper.getByConnName(sourceConnection.getConnectionName());
            if (newSourceConnection != null) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "this connectionName already exist");
            }
        }
        sourceConnectionMapper.update(sourceConnection);
        return BoolResult.successResult(true);
    }
    /**
     * @Description: 对输入数据规格进行判断
     * @param sourceConnectionView
     */
    private void validateSourceConnectionView(SourceConnectionView sourceConnectionView) {
        String connectionName = sourceConnectionView.getConnectionName();
        if(Tools.isEmptyString(connectionName)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "connectionName is null");
        }
        String sourceType = sourceConnectionView.getSourceType();
        if(Tools.isEmptyString(sourceType)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceType is null");
        }
        if(!isIncludedInSourceType(sourceType)) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "sourceType is error, type must be mysql/hive/kafka.");
        }
        if(SourceType.MYSQL.getType().equalsIgnoreCase(sourceType)) {
            MysqlConfig mysqlConfig = (MysqlConfig) sourceConnectionView.getSourceConfig();
            if(mysqlConfig==null) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceConfig is null");
            }
            if(Tools.isEmptyString(mysqlConfig.getHost())) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceConfig.host is null");
            }
            if(mysqlConfig.getPort()==null) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceConfig.port is null");
            }
            if(Tools.isEmptyString(mysqlConfig.getUser())) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceConfig.user is null");
            }
            if(Tools.isEmptyString(mysqlConfig.getPasswd())) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceConfig.passwd is null");
            }
            if(Tools.isEmptyString(mysqlConfig.getDatabase())) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceConfig.database is null");
            }
        } else if(SourceType.HIVE.getType().equalsIgnoreCase(sourceType)) {
            HiveConfig hiveConfig = (HiveConfig) sourceConnectionView.getSourceConfig();
            if(hiveConfig==null) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceConfig is null");
            }
            if(Tools.isEmptyString(hiveConfig.getHost())) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceConfig.host is null");
            }
            if(hiveConfig.getPort()==null) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceConfig.port is null");
            }
            if(Tools.isEmptyString(hiveConfig.getUser())) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceConfig.user is null");
            }
            if(Tools.isEmptyString(hiveConfig.getPasswd())) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceConfig.passwd is null");
            }
            if(Tools.isEmptyString(hiveConfig.getDatabase())) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceConfig.database is null");
            }
        } else if(SourceType.KAFKA.getType().equalsIgnoreCase(sourceType)) {
            KafkaConfig kafkaConfig = (KafkaConfig) sourceConnectionView.getSourceConfig();
            if(kafkaConfig==null) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceConfig is null");
            }
            if(Tools.isEmptyString(kafkaConfig.getServers())) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceConfig.servers is null");
            }
            if(Tools.isEmptyString(kafkaConfig.getGroupId())) {
                throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "sourceConfig.groupId is null");
            }
        }
    }
    /**
     * @Description: 判断sourceType是否存在
     * @param sourceType
     */
    private boolean isIncludedInSourceType(String sourceType) {
        boolean include = false;
        for (SourceType s: SourceType.values()){
            if(s.getType().equalsIgnoreCase(sourceType)){
                include = true;
                break;
            }
        }
        return include;
    }

}
