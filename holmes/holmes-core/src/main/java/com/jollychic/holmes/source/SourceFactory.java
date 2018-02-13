package com.jollychic.holmes.source;


import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;

/**
 * Created by WIN7 on 2018/1/15.
 */
public class SourceFactory {
    public SourceReader getSourceReader(String sourceType) {
        for(SourceType currSourceType : SourceType.values()) {
            if(currSourceType.getType().equalsIgnoreCase(sourceType)) {
                SourceReader sourceReader = currSourceType.getSourceReader();
                return sourceReader;
            }
        }
        throw new ServiceException(ErrorCode.SOURCE_LOAD_ERROR, "数据源类型不存在");
    }
}
