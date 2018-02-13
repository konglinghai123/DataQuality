package com.jollychic.holmes.controller;

import com.alibaba.fastjson.JSON;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.common.utils.JSONUtils;
import com.jollychic.holmes.mapper.SourceConnectionMapper;
import com.jollychic.holmes.model.SourceConnection;
import com.jollychic.holmes.result.*;
import com.jollychic.holmes.service.SourceConnectionService;
import com.jollychic.holmes.service.SourceTableService;
import com.jollychic.holmes.source.SourceType;
import com.jollychic.holmes.view.SourceConnectionView;
import com.jollychic.holmes.view.SourceTableView;
import com.jollychic.holmes.view.sourceConnection.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by WIN7 on 2018/1/4.
 */

@Slf4j
@Api(tags = "SourceREST", description="REST API for source operators")
@RestController
@RequestMapping("source")
public class SourceController {

    @Autowired
    private SourceConnectionService sourceConnectionService;
    @Autowired
    private SourceTableService sourceTableService;
    @Autowired
    private SourceConnectionMapper sourceConnectionMapper;

    @ApiOperation(value = "get source connection by name", notes = "", response = SourceConnectionViewResult.class)
    @RequestMapping(value = "/conn/{connName}", method = RequestMethod.GET)
    @ResponseBody
    private SourceConnectionViewResult getConnByName(@PathVariable String connName) {
        try{
            SourceConnectionViewResult result = sourceConnectionService.getSourceConnectionByName(connName);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return SourceConnectionViewResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return SourceConnectionViewResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "get all source connections", notes = "", response = SourceConnectionViewListResult.class)
    @RequestMapping(value = "/conns", method = RequestMethod.GET)
    @ResponseBody
    private SourceConnectionViewListResult getAllConns() {
        try{
            SourceConnectionViewListResult result = sourceConnectionService.getAllSourceConnection();
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return SourceConnectionViewListResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return SourceConnectionViewListResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }
    @ApiOperation(value = "delete source connection by name", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/conn/{connName}", method = RequestMethod.DELETE)
    @ResponseBody
    private BoolResult deleteConn(@PathVariable String connName) {
        try{
            BoolResult result = sourceConnectionService.deleteSourceConnectionByName(connName);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "insert source connection", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/conn", method = RequestMethod.POST)
    @ResponseBody
    private BoolResult insertConn(@RequestBody String sourceConnection) {
        try{
            String sourceType = JSONUtils.getString(sourceConnection, "sourceType");
            SourceConnectionView sourceConnectionView;
            if(sourceType==null) {
                return BoolResult.errorResult(ErrorCode.PARAMETER_ERROR, "source type is null");
            }
            if(SourceType.MYSQL.getType().equalsIgnoreCase(sourceType)) {
                sourceConnectionView = JSON.parseObject(sourceConnection, MysqlConnectionView.class);
            } else if(SourceType.HIVE.getType().equalsIgnoreCase(sourceType)) {
                sourceConnectionView = JSON.parseObject(sourceConnection, HiveConnectionView.class);
            } else if(SourceType.KAFKA.getType().equalsIgnoreCase(sourceType)) {
                sourceConnectionView = JSON.parseObject(sourceConnection, KafkaConnectionView.class);
            } else {
                return BoolResult.errorResult(ErrorCode.PARAMETER_ERROR, "source type is error");
            }
            BoolResult result = sourceConnectionService.insertSourceConnection(sourceConnectionView);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "insert mysql source connection", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/conn/type/mysql", method = RequestMethod.POST)
    @ResponseBody
    private BoolResult insertMysqlConn(@RequestBody MysqlConnectionView sourceConnection) {
        try{
            sourceConnection.setSourceType("mysql");
            BoolResult result = sourceConnectionService.insertSourceConnection(sourceConnection);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "insert hive source connection", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/conn/type/hive", method = RequestMethod.POST)
    @ResponseBody
    private BoolResult insertHiveConn(@RequestBody HiveConnectionView sourceConnection) {
        try{
            sourceConnection.setSourceType("hive");
            BoolResult result = sourceConnectionService.insertSourceConnection(sourceConnection);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "insert kafka source connection", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/conn/type/kafka", method = RequestMethod.POST)
    @ResponseBody
    private BoolResult insertKafkaConn(@RequestBody KafkaConnectionView sourceConnection) {
        try{
            sourceConnection.setSourceType("kafka");
            BoolResult result = sourceConnectionService.insertSourceConnection(sourceConnection);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "update source connection by name", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/conn/{connName}", method = RequestMethod.PUT)
    @ResponseBody
    private BoolResult updateConn(@PathVariable  String connName, @RequestBody String sourceConnection) {
        try{
            String sourceType = JSONUtils.getString(sourceConnection, "sourceType");
            SourceConnectionView sourceConnectionView;
            if(sourceType==null) {
                SourceConnection oldSourceConnection = sourceConnectionMapper.getByConnName(connName);
                if(oldSourceConnection==null) {
                    throw new ServiceException(ErrorCode.FAIL, "conn doesn't exist");
                }
                sourceType = oldSourceConnection.getSourceType();
            }
            if(SourceType.MYSQL.getType().equalsIgnoreCase(sourceType)) {
                sourceConnectionView = JSON.parseObject(sourceConnection, MysqlConnectionView.class);
            } else if(SourceType.HIVE.getType().equalsIgnoreCase(sourceType)) {
                sourceConnectionView = JSON.parseObject(sourceConnection, HiveConnectionView.class);
            } else if(SourceType.KAFKA.getType().equalsIgnoreCase(sourceType)) {
                sourceConnectionView = JSON.parseObject(sourceConnection, KafkaConnectionView.class);
            } else {
                return BoolResult.errorResult(ErrorCode.PARAMETER_ERROR, "source type is error");
            }
            BoolResult result = sourceConnectionService.updateSourceConnection(connName, sourceConnectionView);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }


    @ApiOperation(value = "get source table schema by connName and tableName", notes = "", response = SourceTableSchemaResult.class)
    @RequestMapping(value = "/table/schema/conn/{connName}/table/{tableName}", method = RequestMethod.GET)
    @ResponseBody
    private SourceTableSchemaResult getTableSchema(@PathVariable String connName, @PathVariable String tableName) {
        try{
            SourceTableSchemaResult result = sourceTableService.getSourceTableSchema(connName, tableName);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return SourceTableSchemaResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return SourceTableSchemaResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "sync source table schema", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/table/schema/conn/{connName}/table/{tableName}", method = RequestMethod.PUT)
    @ResponseBody
    private BoolResult syncTableSchema(@PathVariable String connName, @PathVariable String tableName) {
        try{
            BoolResult result = sourceTableService.syncSourceTableSchema(connName, tableName);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "sync all source tables schema", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/tables/schema", method = RequestMethod.PUT)
    @ResponseBody
    private BoolResult syncAllTableSchemas() {
        try{
            BoolResult result = sourceTableService.syncAllSourceTablesSchema();
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "get source tables by connName", notes = "", response = SourceTableListResult.class)
    @RequestMapping(value = "/table/conn/{connName}", method = RequestMethod.GET)
    @ResponseBody
    private SourceTableListResult getByConn(@PathVariable String connName) {
        try{
            SourceTableListResult result = sourceTableService.getTablesByConnectionName(connName);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return SourceTableListResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return SourceTableListResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }
    @ApiOperation(value = "get all source tables", notes = "", response = SourceTableListResult.class)
    @RequestMapping(value = "/tables", method = RequestMethod.GET)
    @ResponseBody
    private SourceTableListResult getAllTables() {
        try{
            SourceTableListResult result = sourceTableService.getAllSourceTable();
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return SourceTableListResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return SourceTableListResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }
    @ApiOperation(value = "get source tables by ruleName", notes = "", response = SourceTableListResult.class)
    @RequestMapping(value = "/table/rule/{ruleName}", method = RequestMethod.GET)
    @ResponseBody
    private SourceTableListResult getTablesByRule(@PathVariable String ruleName) {
        try{
            SourceTableListResult result = sourceTableService.getTablesByRuleName(ruleName);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return SourceTableListResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return SourceTableListResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }
    @ApiOperation(value = "delete source table by connName and tableName", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/table/conn/{connName}/table/{tableName}", method = RequestMethod.DELETE)
    @ResponseBody
    private BoolResult deleteTable(@PathVariable String connName, @PathVariable String tableName) {
        try{
            BoolResult result = sourceTableService.deleteSourceTableByName(connName, tableName);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }


    @ApiOperation(value = "insert source table", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/table", method = RequestMethod.POST)
    @ResponseBody
    private BoolResult insertTable(@RequestBody SourceTableView sourceTable) {
        try{
            BoolResult result = sourceTableService.insertSourceTable(sourceTable);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

}
