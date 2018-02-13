package com.jollychic.holmes.rule.ruleConfig;

import com.jollychic.holmes.rule.RuleConfig;
import lombok.Data;

import java.util.List;

/**
 * Created by WIN7 on 2018/1/29.
 */
@Data
public class KeyIndicatorWithDimensionConfig implements RuleConfig {
    private List<KeyIndicatorWithDimensionCondition> conditions;

    @Data
    public static class KeyIndicatorWithDimensionCondition {
        private List<KeyIndicatorWithDimensionDimension> dimensions;
        private List<KeyIndicatorWithDimensionColumn> columns;
    }

    @Data
    public static class KeyIndicatorWithDimensionDimension {
        private String dimensionName;
        private List<String> dimensionValues;
    }

    @Data
    public static class KeyIndicatorWithDimensionColumn {
        private String columnName;
        private String aggregation;    //avg sum count min max
        private String operator;       // < <= = == >= > != <>
        private Integer value;
    }
}
