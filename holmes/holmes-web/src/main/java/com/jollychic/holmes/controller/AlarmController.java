package com.jollychic.holmes.controller;

import com.jollychic.holmes.common.web.Result;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.result.AlarmListResult;
import com.jollychic.holmes.service.AlarmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "AlarmREST", description="REST API for alarm operators")
@RestController
@RequestMapping("alarm")
public class AlarmController {
    @Autowired
    private AlarmService alarmService;

    @ApiOperation(value = "get alarm by executionId", notes = "", response = AlarmListResult.class)
    @RequestMapping(value = "/executionId/{executionId}", method = RequestMethod.GET)
    @ResponseBody
    private AlarmListResult getAlarmByExectionId(@PathVariable Integer executionId) {
        try{
            AlarmListResult result = alarmService.getByExecutionId(executionId);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return AlarmListResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return AlarmListResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "get alarm by executionId and alarm status", notes = "", response = AlarmListResult.class)
    @RequestMapping(value = "/executionId/{executionId}/alarm/{alarm}", method = RequestMethod.GET)
    @ResponseBody
    private AlarmListResult getAlarmByExectionIdAndAlarm(@PathVariable Integer executionId, @ApiParam(value = "alarm status") @PathVariable Boolean alarm) {
        try{
            AlarmListResult result = alarmService.getByExecutionIdAndAlarm(executionId, alarm);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return AlarmListResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return AlarmListResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "get alarm by ruleName", notes = "", response = AlarmListResult.class)
    @RequestMapping(value = "/ruleName/{ruleName}", method = RequestMethod.GET)
    @ResponseBody
    private AlarmListResult getAlarmByRuleName(@PathVariable String ruleName) {
        try{
            AlarmListResult result = alarmService.getByRuleName(ruleName);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return AlarmListResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return AlarmListResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "get alarm by ruleName and alarm status", notes = "", response = AlarmListResult.class)
    @RequestMapping(value = "/ruleName/{ruleName}/alarm/{alarm}", method = RequestMethod.GET)
    @ResponseBody
    private AlarmListResult getAlarmByRuleNameAndAlarm(@PathVariable String ruleName, @ApiParam(value = "alarm status") @PathVariable Boolean alarm) {
        try{
            AlarmListResult result = alarmService.getByRuleNameAndAlarm(ruleName, alarm);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return AlarmListResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return AlarmListResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

}
