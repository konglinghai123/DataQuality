package com.jollychic.holmes.rule.ruleConfig;

import com.jollychic.holmes.rule.RuleConfig;
import lombok.Data;

import java.util.List;

/**
 * Created by WIN7 on 2018/1/12.
 */
@Data
public class KeyIndicatorConfig implements RuleConfig {
    private List<KeyIndicatorColumn> columns;   //指标判断

    @Data
    public static class KeyIndicatorColumn {
        private String columnName;
        private String operator;       // < <= = == >= > != <> is null is not null
        private Object value;
    }
}
