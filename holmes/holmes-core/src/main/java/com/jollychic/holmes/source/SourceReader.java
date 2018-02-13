package com.jollychic.holmes.source;

import com.jollychic.holmes.rule.ruleConfig.KeyIndicatorWithDimensionConfig;
import com.jollychic.holmes.source.ConnConfig.ConnConfig;

import java.util.List;
import java.util.Map;

/**
 * Created by WIN7 on 2018/1/10.
 */
public interface SourceReader {
    public void init(SourceConfig sourceConfig);
    public List<String> read();
    public List<String> read(String sql);
    public long getCount();
    public List<String> getTables();
    public void validateConnectionInfo(ConnConfig connConfig);
    public String getTableParam(String connectionInfo, String tableName);
}
