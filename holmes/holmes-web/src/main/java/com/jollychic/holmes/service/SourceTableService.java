package com.jollychic.holmes.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jollychic.holmes.mapper.RuleMapper;
import com.jollychic.holmes.model.Rule;
import com.jollychic.holmes.result.BoolResult;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.common.utils.Tools;
import com.jollychic.holmes.mapper.SourceConnectionMapper;
import com.jollychic.holmes.mapper.SourceTableMapper;
import com.jollychic.holmes.mapper.TableRuleTmpMapper;
import com.jollychic.holmes.model.SourceConnection;
import com.jollychic.holmes.model.SourceTable;
import com.jollychic.holmes.model.TableRuleTmp;
import com.jollychic.holmes.result.SourceTableListResult;
import com.jollychic.holmes.result.SourceTableSchemaResult;
import com.jollychic.holmes.source.SourceFactory;
import com.jollychic.holmes.view.SourceTableView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SourceTableService {
    @Autowired
    private SourceTableMapper sourceTableMapper;
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private TableRuleTmpMapper tableRuleTmpMapper;
    @Autowired
    private SourceConnectionMapper sourceConnectionMapper;

    /**
     * @Description: 根据tableName和connName确定某个SourceTable，获取该sourceTable的结构
     * @param connName
     * @param tableName
     */
    public SourceTableSchemaResult getSourceTableSchema(String connName, String tableName) {
        if (Tools.isEmptyString(connName)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "connName is null");
        }
        if (Tools.isEmptyString(tableName)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "tableName is null");
        }
        SourceConnection sourceConnection = sourceConnectionMapper.getByConnName(connName);
        if(sourceConnection==null) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "source connection doesn't exist");
        }
        SourceTable sourceTable = new SourceTable();
        sourceTable.setTableName(tableName);
        sourceTable.setConnectionId(sourceConnection.getConnectionId());
        sourceTable = sourceTableMapper.getByTableName(sourceTable);
        if(sourceTable==null) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "source table doesn't exist");
        }
        List<String> tableSchema = null;
        if(sourceTable.getTableSchema()!=null && !sourceTable.getTableSchema().trim().equals("")) {
            try {
                tableSchema = JSON.parseObject(sourceTable.getTableSchema(), new TypeReference<List<String>>() {
                });
            } catch (Exception e) {
                throw new ServiceException(ErrorCode.FAIL, "tableSchema json parse error, "+e.getMessage());
            }
        }
        return SourceTableSchemaResult.successResult(tableSchema);
    }
    /**
     * @Description: 同步该sourceTable的结构
     * @param connName
     * @param tableName
     */
    public BoolResult syncSourceTableSchema(String connName, String tableName) {
        if (Tools.isEmptyString(connName)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "connectionName is null");
        }
        if (Tools.isEmptyString(tableName)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "tableName is null");
        }
        //数据处理
        SourceConnection sourceConnection = sourceConnectionMapper.getByConnName(connName);
        if(sourceConnection==null) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "source connection doesn't exist");
        }
        String connectionId = sourceConnection.getConnectionId();
        String connectionInfo = sourceConnection.getConnectionInfo();
        SourceTable sourceTable = new SourceTable();
        sourceTable.setConnectionId(connectionId);
        sourceTable.setTableName(tableName);
        sourceTable = sourceTableMapper.getByTableName(sourceTable);
        if(sourceTable==null) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "source table doesn't exist");
        }
        //表结构自动导入生成
        String tableSchema = new SourceFactory().getSourceReader(sourceConnection.getSourceType()).getTableParam(connectionInfo,tableName);
        //update
        sourceTable.setTableSchema(tableSchema);
        sourceTable.setUpdatedAt(new Date());
        sourceTableMapper.update(sourceTable);
        return BoolResult.successResult(true);
    }
    /**
     * @Description: 同步所有sourceTable的结构
     */
    public BoolResult syncAllSourceTablesSchema() {
        List<SourceTable> sourceTables = sourceTableMapper.getAll();
        String error = "";
        for(SourceTable sourceTable : sourceTables) {
            String tableName = sourceTable.getTableName();
            String connectionId = sourceTable.getConnectionId();
            SourceConnection sourceConnection = sourceConnectionMapper.get(connectionId);
            String connectionInfo = sourceConnection.getConnectionInfo();
            //表结构自动导入生成
            String tableSchema;
            try {
                tableSchema = new SourceFactory().getSourceReader(sourceConnection.getSourceType()).getTableParam(connectionInfo,tableName);
            } catch (ServiceException se) {
                error += "error code: "+se.getCode()+" error msg: "+se.getMsg()+"; ";
                continue;
            }
            //update
            sourceTable.setTableSchema(tableSchema);
            sourceTable.setUpdatedAt(new Date());
            sourceTableMapper.update(sourceTable);
        }
        if(!error.trim().equals("")) {
            throw new ServiceException(ErrorCode.FAIL, error);
        }
        return BoolResult.successResult(true);
    }
    /**
     * @Description: 获取所有sourceTable

     */
    public SourceTableListResult getAllSourceTable() {
        List<SourceTable> sourceTables = sourceTableMapper.getAll();
        Map<String, List<SourceTableView>> sourceTableViews = getSourceTableViews(sourceTables);
        return SourceTableListResult.successResult(sourceTableViews);
    }
    /**
     * @Description: 获取存在某rule的所有sourceTable
     * @param ruleName
     */
    public SourceTableListResult getTablesByRuleName(String ruleName) {
        //验证rule是否存在
        Rule rule = ruleMapper.getByRuleName(ruleName);
        if(rule==null) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "this rule doesn't exist");
        }
        List<SourceTable> sourceTables = sourceTableMapper.getByRuleName(ruleName);
        Map<String, List<SourceTableView>> sourceTableViews = getSourceTableViews(sourceTables);
        return SourceTableListResult.successResult(sourceTableViews);
    }
    /**
     * @Description: 获取某connName的所有sourceTable
     * @param connName
     */
    public SourceTableListResult getTablesByConnectionName(String connName) {
        //验证conn是否存在
        SourceConnection sourceConnection = sourceConnectionMapper.getByConnName(connName);
        if(sourceConnection==null) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "this sourceConnection doesn't exist");
        }
        List<SourceTable> sourceTables = sourceTableMapper.getByConnName(connName);
        Map<String, List<SourceTableView>> sourceTableViews = getSourceTableViews(sourceTables);
        return SourceTableListResult.successResult(sourceTableViews);
    }

    /**
     * @Description: 将sourceTable转换成sourceTableViews
     * @param sourceTables
     */
    private Map<String, List<SourceTableView>> getSourceTableViews(List<SourceTable> sourceTables) {
        Map<String, List<SourceTableView>> sourceTableViews = Maps.newHashMap();
        if(sourceTables==null) {
            return sourceTableViews;
        }
        for(SourceTable sourceTable : sourceTables) {
            SourceTableView sourceTableView = new SourceTableView();
            SourceConnection sourceConnection = sourceConnectionMapper.get(sourceTable.getConnectionId());
            if(sourceConnection!=null) {
                sourceTableView.setConnectionName(sourceConnection.getConnectionName());
            }
            sourceTableView.setTableName(sourceTable.getTableName());
            if(sourceTableViews.get(sourceConnection.getConnectionName())==null) {
                List<SourceTableView> sourceTableViewList = Lists.newArrayList();
                sourceTableViews.put(sourceConnection.getConnectionName(), sourceTableViewList);
            }
            sourceTableViews.get(sourceConnection.getConnectionName()).add(sourceTableView);
        }
        return sourceTableViews;
    }

    //删除table表及中间表中信息
    public BoolResult deleteSourceTableById(String id) {
        tableRuleTmpMapper.deleteByTableId(id);
        sourceTableMapper.delete(id);
        return BoolResult.successResult(true);
    }

    /**
     * @Description: 删除table表及中间表中信息
     * @param connName
     * @param tableName
     */
    public BoolResult deleteSourceTableByName(String connName, String tableName) {
        if (Tools.isEmptyString(connName)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "connName is null");
        }
        if (Tools.isEmptyString(tableName)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "tableName is null");
        }
        SourceConnection sourceConnection = sourceConnectionMapper.getByConnName(connName);
        if(sourceConnection==null) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "source connection doesn't exist");
        }
        SourceTable sourceTable=new SourceTable();
        sourceTable.setTableName(tableName);
        sourceTable.setConnectionId(sourceConnection.getConnectionId());
        sourceTable= sourceTableMapper.getByTableName(sourceTable);
        if (sourceTable==null) {
            throw new ServiceException(ErrorCode.FAIL, "source table doesn't exist");
        }
        String tableId = sourceTable.getTableId();
        List<TableRuleTmp> tableRuleTmps = tableRuleTmpMapper.getByTableId(tableId);
        if (tableRuleTmps.size()!=0) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "this table still have rules,you can't delete it");
        }
        tableRuleTmpMapper.deleteByTableId(tableId);
        sourceTableMapper.delete(tableId);
        return BoolResult.successResult(true);
    }
    /**
     * @Description: 添加table表及中间表中信息
     * @param sourceTableView
     */
    public BoolResult insertSourceTable(SourceTableView sourceTableView) {
        if(sourceTableView==null) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "input is null");
        }
        String connName = sourceTableView.getConnectionName();
        String tableName = sourceTableView.getTableName();
        if (Tools.isEmptyString(connName)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "connectionName is null");
        }
        if (Tools.isEmptyString(tableName)) {
            throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "tableName is null");
        }
        //数据处理
        SourceConnection sourceConnection = sourceConnectionMapper.getByConnName(connName);
        if(sourceConnection==null) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "source connection doesn't exist");
        }
        String connectionId = sourceConnection.getConnectionId();
        String connectionInfo = sourceConnection.getConnectionInfo();
        //表结构自动导入生成
        String tableSchema = new SourceFactory().getSourceReader(sourceConnection.getSourceType()).getTableParam(connectionInfo,tableName);
        //存储
        SourceTable sourceTable = new SourceTable();
        sourceTable.setTableName(tableName);
        sourceTable.setConnectionId(connectionId);
        sourceTable.setTableSchema(tableSchema);
        //检验该表是否已存在
        SourceTable oldSourceTable = sourceTableMapper.getByTableName(sourceTable);
        if(oldSourceTable!=null) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "source table already exist");
        }
        sourceTableMapper.insert(sourceTable);
        return BoolResult.successResult(true);
    }

}
