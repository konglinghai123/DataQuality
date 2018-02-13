package com.jollychic.holmes.rule.ruleConfig;

import com.jollychic.holmes.rule.RuleConfig;
import lombok.Data;

import java.util.List;

/**
 * Created by WIN7 on 2018/2/7.
 */
@Data
public class ComparisonToTheSameTimeConfig implements RuleConfig {
    private List<ComparisonToTheSameTimeCondition> conditions;
    private Integer differDay;

    @Data
    public static class ComparisonToTheSameTimeCondition {
        private List<ComparisonToTheSameTimeDimension> dimensions;
        private List<ComparisonToTheSameTimeColumn> columns;
    }

    @Data
    public static class ComparisonToTheSameTimeDimension {
        private String dimensionName;
        private List<String> dimensionValues;
    }

    @Data
    public static class ComparisonToTheSameTimeColumn {
        private String columnName;
        private String aggregation;    //avg sum count min max
        private String operator;       // < <= = == >= > != <>
        private Double percent;        //百分比，例如0.5
    }
}
