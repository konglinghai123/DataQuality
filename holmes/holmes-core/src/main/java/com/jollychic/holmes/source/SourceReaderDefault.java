package com.jollychic.holmes.source;

import java.util.List;
import java.util.Map;

/**
 * Created by WIN7 on 2018/1/15.
 */
public abstract class SourceReaderDefault implements SourceReader {
    protected SourceConfig sourceConfig;
    @Override
    public void init(SourceConfig sourceConfig) {
        this.sourceConfig = sourceConfig;
    }
}
