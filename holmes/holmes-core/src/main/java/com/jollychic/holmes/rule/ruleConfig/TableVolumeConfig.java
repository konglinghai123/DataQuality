package com.jollychic.holmes.rule.ruleConfig;

import com.jollychic.holmes.rule.RuleConfig;
import lombok.Data;

/**
 * Created by WIN7 on 2018/1/26.
 */
@Data
public class TableVolumeConfig implements RuleConfig {
    private Integer minReduceVolume;
}
