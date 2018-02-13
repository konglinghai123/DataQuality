package com.jollychic.holmes.controller;

import com.alibaba.fastjson.JSON;
import com.jollychic.holmes.common.utils.JSONUtils;
import com.jollychic.holmes.mapper.RuleMapper;
import com.jollychic.holmes.model.Rule;
import com.jollychic.holmes.model.UserEmail;
import com.jollychic.holmes.result.*;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.rule.RuleType;
import com.jollychic.holmes.rule.dailyRule.ComparisonToTheSameTime;
import com.jollychic.holmes.service.RuleService;
import com.jollychic.holmes.view.RuleView;
import com.jollychic.holmes.view.rule.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Api(tags = "RuleREST", description="REST API for rule operators")
@RequestMapping("rule")
public class RuleController {
    @Autowired
    private RuleService ruleService;
    @Autowired
    private RuleMapper ruleMapper;
    @ApiOperation(value = "get rule by name", notes = "", response = RuleViewResult.class)
    @RequestMapping(value = "/{ruleName}", method = RequestMethod.GET)
    @ResponseBody
    private RuleViewResult get(@PathVariable String ruleName) {
        try{
            RuleViewResult result = ruleService.getRuleByName(ruleName);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return RuleViewResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return RuleViewResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "get all rules", notes = "", response = RuleViewListResult.class)
    @RequestMapping(value = "/rules", method = RequestMethod.GET)
    @ResponseBody
    private RuleViewListResult getAll() {
        try{
            RuleViewListResult result = ruleService.getAllRule();
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return RuleViewListResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return RuleViewListResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "get rule by rule type", notes = "", response = RuleViewListResult.class)
    @RequestMapping(value = "/type/{ruleType}", method = RequestMethod.GET)
    @ResponseBody
    private RuleViewListResult getByRuleType(@PathVariable String ruleType) {
        try{
            RuleViewListResult result = ruleService.getRuleByRuleType(ruleType);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return RuleViewListResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return RuleViewListResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "delete rule by name", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/{ruleName}", method = RequestMethod.DELETE)
    @ResponseBody
    private BoolResult delete(@PathVariable String ruleName) {
        try{
            BoolResult result = ruleService.deleteRuleByName(ruleName);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "insert rule", notes = "", response = BoolResult.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    private BoolResult insert(@RequestBody String rule) {
        try{
            String ruleType = JSONUtils.getString(rule, "ruleType");
            if(ruleType==null) {
                return BoolResult.errorResult(ErrorCode.PARAMETER_ERROR, "rule type is null");
            }
            RuleView ruleView;
            if(RuleType.DATA_VOLUME.getType().equalsIgnoreCase(ruleType)) {
                ruleView = JSON.parseObject(rule, DataVolumeConfigView.class);
            } else if(RuleType.DATA_VOLUME_WITH_FIXED_WINDOW.getType().equalsIgnoreCase(ruleType)) {
                ruleView = JSON.parseObject(rule, DataVolumeWithFixedWindowConfigView.class);
            } else if(RuleType.KEY_INDICATOR.getType().equalsIgnoreCase(ruleType)) {
                ruleView = JSON.parseObject(rule, KeyIndicatorConfigView.class);
            } else if(RuleType.TABLE_VOLUME.getType().equalsIgnoreCase(ruleType)) {
                ruleView = JSON.parseObject(rule, TableVolumeConfigView.class);
            } else if(RuleType.KEY_INDICATOR_WITH_DIMENSION.getType().equalsIgnoreCase(ruleType)) {
                ruleView = JSON.parseObject(rule, KeyIndicatorWithDimensionConfigView.class);
            } else if(RuleType.COMPARSION_TO_THE_SAME_TIME.getType().equalsIgnoreCase(ruleType)) {
                ruleView = JSON.parseObject(rule, ComparisonToTheSameTimeView.class);
            } else {
                return BoolResult.errorResult(ErrorCode.PARAMETER_ERROR, "insert rule type is error");
            }
            BoolResult result = ruleService.insertRule(ruleView);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }
    @ApiOperation(value = "insert dataVolumeConfig rule", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/type/dataVolume",method = RequestMethod.POST)
    @ResponseBody
    private BoolResult insertDataVolume(@RequestBody DataVolumeConfigView rule) {
        try{
            rule.setRuleType("dataVolume");
            BoolResult result = ruleService.insertRule(rule);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "insert dataVolumeWithFixedWindowConfig rule", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/type/dataVolumeWithFixedWindow",method = RequestMethod.POST)
    @ResponseBody
    private BoolResult insertDataVolumeWithFixedWindow(@RequestBody DataVolumeWithFixedWindowConfigView rule) {
        try{
            rule.setRuleType("dataVolumeWithFixedWindow");
            BoolResult result = ruleService.insertRule(rule);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "insert keyIndicatorConfig rule", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/type/keyIndicator",method = RequestMethod.POST)
    @ResponseBody
    private BoolResult insertKeyIndicator(@RequestBody KeyIndicatorConfigView rule) {
        try{
            rule.setRuleType("keyIndicator");
            BoolResult result = ruleService.insertRule(rule);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }
    @ApiOperation(value = "insert keyIndicatorWithDimensionConfig rule", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/type/keyIndicatorWithDimension",method = RequestMethod.POST)
    @ResponseBody
    private BoolResult insertKeyIndicatorWithDimension(@RequestBody KeyIndicatorWithDimensionConfigView rule) {
        try{
            rule.setRuleType("keyIndicatorWithDimension");
            BoolResult result = ruleService.insertRule(rule);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "insert tableVolumeConfig rule", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/type/tableVolume",method = RequestMethod.POST)
    @ResponseBody
    private BoolResult insertTableVolume(@RequestBody TableVolumeConfigView rule) {
        try{
            rule.setRuleType("tableVolume");
            BoolResult result = ruleService.insertRule(rule);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "insert comparisonToTheSameTimeConfig rule", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/type/comparisonToTheSameTime",method = RequestMethod.POST)
    @ResponseBody
    private BoolResult insertComparisonToTheSameTime(@RequestBody ComparisonToTheSameTimeView rule) {
        try{
            rule.setRuleType("comparisonToTheSameTime");
            BoolResult result = ruleService.insertRule(rule);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "update rule by name", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/{ruleName}",method = RequestMethod.PUT)
    @ResponseBody
    private BoolResult update(@PathVariable String ruleName, @RequestBody String rule) {
        try{
            String ruleType = JSONUtils.getString(rule, "ruleType");
            if(ruleType==null) {
                Rule oldRule = ruleMapper.getByRuleName(ruleName);
                if(oldRule==null) {
                    throw new ServiceException(ErrorCode.FAIL, "rule doesn't exist");
                }
                ruleType = oldRule.getRuleType();
            }
            RuleView ruleView;
            if(RuleType.DATA_VOLUME.getType().equalsIgnoreCase(ruleType)) {
                ruleView = JSON.parseObject(rule, DataVolumeConfigView.class);
            } else if(RuleType.DATA_VOLUME_WITH_FIXED_WINDOW.getType().equalsIgnoreCase(ruleType)) {
                ruleView = JSON.parseObject(rule, DataVolumeWithFixedWindowConfigView.class);
            } else if(RuleType.KEY_INDICATOR.getType().equalsIgnoreCase(ruleType)) {
                ruleView = JSON.parseObject(rule, KeyIndicatorConfigView.class);
            } else if(RuleType.TABLE_VOLUME.getType().equalsIgnoreCase(ruleType)) {
                ruleView = JSON.parseObject(rule, TableVolumeConfigView.class);
            } else if(RuleType.KEY_INDICATOR_WITH_DIMENSION.getType().equalsIgnoreCase(ruleType)) {
                ruleView = JSON.parseObject(rule, KeyIndicatorWithDimensionConfigView.class);
            } else if(RuleType.COMPARSION_TO_THE_SAME_TIME.getType().equalsIgnoreCase(ruleType)) {
                ruleView = JSON.parseObject(rule, ComparisonToTheSameTimeView.class);
            } else {
                return BoolResult.errorResult(ErrorCode.PARAMETER_ERROR, "update rule type is error");
            }
            BoolResult result = ruleService.updateRule(ruleName, ruleView);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "get rule by tableName", notes = "", response = RuleViewListResult.class)
    @RequestMapping(value = "/{connName}/{tableName}", method = RequestMethod.GET)
    @ResponseBody
    private RuleViewListResult getByTableName(@PathVariable String connName, @PathVariable String tableName ) {
        try{
            RuleViewListResult result = ruleService.getRuleByTableName(connName,tableName);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return RuleViewListResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return RuleViewListResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "insert userEmail", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/userEmail",method = RequestMethod.POST)
    @ResponseBody
    private BoolResult insertUserEmail(@RequestBody String data) {
        try{
            BoolResult result = ruleService.insertUserEmail(data);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }
    @ApiOperation(value = "get userEmail by id", notes = "", response = UserEmailResult.class)
    @RequestMapping(value = "/userEmail/{userEmailId}",method = RequestMethod.GET)
    @ResponseBody
    private UserEmailResult getUserEmailById(@PathVariable String userEmailId) {
        try{
            UserEmailResult result = ruleService.getUserEmailById(userEmailId);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return UserEmailResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return UserEmailResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }
    @ApiOperation(value = "get userEmail by name", notes = "", response = UserEmailResult.class)
    @RequestMapping(value = "/userEmail/{userChineseName}/{userEnglishName}",method = RequestMethod.GET)
    @ResponseBody
    private UserEmailResult getUserEmailByName(@PathVariable String userChineseName,@PathVariable String userEnglishName) {
        try{
            UserEmailResult result = ruleService.getUserEmailByCName(userChineseName,userEnglishName);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return UserEmailResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return UserEmailResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "get all userEmail", notes = "", response = UserEmailListResult.class)
    @RequestMapping(value = "/userEmail/userEmails",method = RequestMethod.GET)
    @ResponseBody
    private UserEmailListResult getAllUserEmail() {
        try{
            UserEmailListResult result = ruleService.getAllUserEmail();
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return UserEmailListResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return UserEmailListResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "delete userEmail by id", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/userEmail/{userEmailId}",method = RequestMethod.DELETE)
    @ResponseBody
    private BoolResult deleteUserEmailById(@PathVariable String userEmailId) {
        try{
            BoolResult result = ruleService.deleteUserEmailById(userEmailId);
            return result;
        } catch (ServiceException se) {
            log.error(se.getMsg());
            return BoolResult.errorResult(se.getCode(), se.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage());
            return BoolResult.errorResult(ErrorCode.FAIL, e.getMessage());
        }
    }

    @ApiOperation(value = "get all userEmail", notes = "", response = BoolResult.class)
    @RequestMapping(value = "/userEmail/{userEmailId}",method = RequestMethod.PUT)
    @ResponseBody
    private BoolResult updateUserEmail(@PathVariable  String userEmailId,@RequestBody String data) {
        try{
            BoolResult result = ruleService.updateUserEmail(userEmailId,data);
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
