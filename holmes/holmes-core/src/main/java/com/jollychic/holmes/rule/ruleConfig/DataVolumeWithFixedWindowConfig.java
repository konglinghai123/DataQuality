package com.jollychic.holmes.rule.ruleConfig;

import com.jollychic.holmes.rule.RuleConfig;
import lombok.Data;

/**
 * Created by WIN7 on 2018/2/1.
 */
@Data
public class DataVolumeWithFixedWindowConfig implements RuleConfig {
    private String operator;                //< <= = >= > !=
    private Integer value;                  //比较的值
}
