package com.jollychic.holmes.controller;

import com.alibaba.fastjson.JSON;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.common.web.Result;
import com.jollychic.holmes.result.BoolResult;
import com.jollychic.holmes.result.ScheduleViewListResult;
import com.jollychic.holmes.result.ScheduleViewResult;
import com.jollychic.holmes.service.ExecutionService;
import com.jollychic.holmes.service.ScheduleService;
import com.jollychic.holmes.view.ScheduleInputView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Api(tags = "ScheduleREST", description="REST API for schedule operators")
@RequestMapping("schedule")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation(value = "get schedule by ruleName", notes = "", response = ScheduleViewResult.class)
    @RequestMapping(value = "/{ruleName}", method = RequestMethod.GET)
    @ResponseBody
    private ScheduleViewResult get(@PathVariable String ruleName) {
        try{
            ScheduleViewResult result = scheduleService.getJob(ruleName);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return ScheduleViewResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ScheduleViewResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "get all schedules", notes = "", response = ScheduleViewListResult.class)
    @RequestMapping(value = "/schedules", method = RequestMethod.GET)
    @ResponseBody
    private ScheduleViewListResult getAll() {
        try{
            ScheduleViewListResult result = scheduleService.getAllJobs();
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return ScheduleViewListResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ScheduleViewListResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "insert schedule", notes = "", response = Result.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    private String insert(@RequestBody ScheduleInputView scheduleInputView) {
        try{
            Result result = scheduleService.insertJob(scheduleInputView);
            return JSON.toJSONString(result);
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return JSON.toJSONString(Result.errorResult(se.getCode(), se.getMsg()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return JSON.toJSONString(Result.errorResult(ErrorCode.FAIL, e.getMessage()));
        }
    }


    @ApiOperation(value = "delete schedule by ruleName", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/{ruleName}", method = RequestMethod.DELETE)
    @ResponseBody
    private BoolResult delete(@PathVariable String ruleName) {
        try{
            BoolResult result = scheduleService.deleteJob(ruleName);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "delete useless job", notes = "", response = BoolResult.class)
    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseBody
    private BoolResult deleteUselessJob() {
        try{
            BoolResult result = scheduleService.deleteUselessJob();
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "update schedule corn by ruleName", notes = "", response = BoolResult.class)
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    private BoolResult update(@RequestBody ScheduleInputView scheduleInputView) {
        try{
            BoolResult result = scheduleService.updateJob(scheduleInputView);
            return result;
        }  catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }
}
