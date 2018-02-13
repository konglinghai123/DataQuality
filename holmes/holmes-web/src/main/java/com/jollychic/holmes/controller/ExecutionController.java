package com.jollychic.holmes.controller;

import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.result.*;
import com.jollychic.holmes.service.AlarmService;
import com.jollychic.holmes.service.ExecutionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "ExecutionREST", description="REST API for execution operators")
@RestController
@RequestMapping("execution")
public class ExecutionController {
    @Autowired
    private ExecutionService executionService;
    @Autowired
    private AlarmService alarmService;

    @ApiOperation(value = "get execution by executionId", notes = "", response = ExecutionViewResult.class)
    @RequestMapping(value = "/{executionId}", method = RequestMethod.GET)
    @ResponseBody
    private ExecutionViewResult get(@PathVariable int executionId) {
        try{
            ExecutionViewResult result = executionService.getExecutionById(executionId);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return ExecutionViewResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ExecutionViewResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }
    @ApiOperation(value = "get execution by ruleName", notes = "", response = ExecutionViewListResult.class)
    @RequestMapping(value = "/rule/{ruleName}", method = RequestMethod.GET)
    @ResponseBody
    private ExecutionViewListResult getExceptionByRuleNameAndStatus(@PathVariable String ruleName) {
        try{
            ExecutionViewListResult result = executionService.getExecutionByruleName(ruleName);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return ExecutionViewListResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ExecutionViewListResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "get execution by ruleName and status", notes = "", response = ExecutionViewResult.class)
    @RequestMapping(value = "/rule/{ruleName}/status/{status}", method = RequestMethod.GET)
    @ResponseBody
    private ExecutionViewResult getExceptionByRuleNameAndStatus(@PathVariable String ruleName,
                 @ApiParam(value = "new(0), running(1), finished(2), stopped(3), error(4)") @PathVariable Integer status) {
        try{
            ExecutionViewResult result = executionService.getExecutionByRuleNameAndStatus(ruleName,status);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return ExecutionViewResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ExecutionViewResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }
    @ApiOperation(value = "sumbit execution by ruleName", notes = "", response = AlarmListResult.class)
    @RequestMapping(value = "/submit/{ruleName}", method = RequestMethod.GET)
    @ResponseBody
    private AlarmListResult submitExecution(@PathVariable String ruleName) {
        try{
            AlarmListResult result =executionService.submitExecutionByRuleName(ruleName);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return AlarmListResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return AlarmListResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }
    @ApiOperation(value = "stop execution by executionId", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/stop/{executionId}", method = RequestMethod.GET)
    @ResponseBody
    private BoolResult stopExecution(@PathVariable Integer executionId) {
        try{
            BoolResult result = executionService.stopExecution(executionId);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }
    @ApiOperation(value = "resume execution by executionId", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/resume/{executionId}", method = RequestMethod.GET)
    @ResponseBody
    private BoolResult resumeExecution(@PathVariable Integer executionId) {
        try{
            BoolResult result = executionService.resumeExecution(executionId);
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
