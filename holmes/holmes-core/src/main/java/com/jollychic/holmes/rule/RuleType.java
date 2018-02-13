package com.jollychic.holmes.rule;

import com.alibaba.fastjson.JSON;
import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import com.jollychic.holmes.rule.dailyRule.*;
import com.jollychic.holmes.rule.ruleConfig.*;

/**
 * Created by WIN7 on 2018/1/5.
 */
public enum RuleType {
    DATA_VOLUME("dataVolume", DataVolume.class, DataVolumeConfig.class),
    DATA_VOLUME_WITH_FIXED_WINDOW("dataVolumeWithFixedWindow", DataVolumeWithFixedWindow.class, DataVolumeWithFixedWindowConfig.class),
    KEY_INDICATOR("keyIndicator", KeyIndicator.class, KeyIndicatorConfig.class),
    TABLE_VOLUME("tableVolume", TableVolume.class, TableVolumeConfig.class),
    KEY_INDICATOR_WITH_DIMENSION("keyIndicatorWithDimension", KeyIndicatorWithDimension.class, KeyIndicatorWithDimensionConfig.class),
    COMPARSION_TO_THE_SAME_TIME("comparisonToTheSameTime", ComparisonToTheSameTime.class, ComparisonToTheSameTimeConfig.class);

    private String type;
    private Class<? extends RuleRunner> runnerClass;
    private Class<? extends RuleConfig> paramClass;

    private RuleType(String type, Class<? extends RuleRunner> runnerClass, Class<? extends RuleConfig> paramClass) {
        this.type = type;
        this.runnerClass = runnerClass;
        this.paramClass = paramClass;
    }

    public String getType() {
        return type;
    }

    public RuleRunner getRuleRunner() {
        try {
            return runnerClass.newInstance();
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.RULE_LOAD_ERROR, "can't new rule runner class, "+e.getMessage());
        }
    }

    public RuleConfig createRuleConfig(String ruleExpression) {
        if (ruleExpression == null || paramClass == null) {
            return null;
        }
        try {
            return JSON.parseObject(ruleExpression, paramClass);
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR, "ruleExpression json parse error");
        }
    }

}
