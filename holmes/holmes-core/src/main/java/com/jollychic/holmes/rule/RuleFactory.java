package com.jollychic.holmes.rule;

import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;

/**
 * Created by WIN7 on 2018/1/10.
 */
public class RuleFactory {
    public RuleRunner getRuleRunner(String ruleType) {
        for(RuleType currRuleType : RuleType.values()) {
            if(currRuleType.getType().equalsIgnoreCase(ruleType)) {
                RuleRunner ruleRunner = currRuleType.getRuleRunner();
                return ruleRunner;
            }
        }
        throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "ruleType is error");
    }

    public RuleConfig getRuleConfig(String ruleType, String ruleExpression) {
        for(RuleType currRuleType : RuleType.values()) {
            if(currRuleType.getType().equalsIgnoreCase(ruleType)) {
                RuleConfig ruleConfig = currRuleType.createRuleConfig(ruleExpression);
                return ruleConfig;
            }
        }
        throw new ServiceException(ErrorCode.PARAMETER_MISSING_ERROR, "ruleType is error");
    }
}
