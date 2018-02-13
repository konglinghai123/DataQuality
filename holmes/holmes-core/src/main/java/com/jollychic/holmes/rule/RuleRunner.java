package com.jollychic.holmes.rule;

import com.jollychic.holmes.mapper.*;
import com.jollychic.holmes.mapper.SourceConnectionMapper;
import com.jollychic.holmes.mapper.SourceTableMapper;
import com.jollychic.holmes.model.Execution;
import com.jollychic.holmes.model.Rule;
import com.alibaba.fastjson.*;

/**
 * Created by WIN7 on 2018/1/10.
 */
public interface RuleRunner {
    public void init(Rule rule, Execution execution);
    public void run();
}
