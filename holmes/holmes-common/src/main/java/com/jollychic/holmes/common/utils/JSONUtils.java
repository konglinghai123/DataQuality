package com.jollychic.holmes.common.utils;

import com.jollychic.holmes.common.exception.ErrorCode;
import com.jollychic.holmes.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by WIN7 on 2018/1/5.
 */
@Slf4j
public class JSONUtils {

    public static String getString(String json, String key) {
        JsonValidator jv = new JsonValidator();
        if (jv.validate(json)) {
            JSONUtils jsonUtils1 = new JSONUtils();
            Object obj = jsonUtils1.getObjectByJson(json, key);
            if(obj==null) {
                return null;
            } else {
                return String.valueOf(obj);
            }
        }
        log.error("json[ "+json+" ] is illegal");
        throw new ServiceException(ErrorCode.PARAMETER_ERROR, "json is illegal");
    }

    private static ObjectMapper mapper = new ObjectMapper();
    private int i = 0;

    /**
     * 复杂嵌套Json获取Object数据
     */
    public Object getObjectByJson(String jsonStr, String argsPath) {
        if (argsPath == null || argsPath.equals("")) {
            return null;
        }

        Object obj = null;
        try {
            Map maps = mapper.readValue(jsonStr, Map.class);
            //多层获取
            if (argsPath.indexOf(".") >= 0) {
                //类型自适应
                obj = getObject(maps, argsPath);
            } else {
                //第一层获取
                obj = maps.get(argsPath);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return obj;
    }

    //递归获取object
    private Object getObject(Object m, String key) {
        if (m == null) {
            return null;
        }
        Object o = null; //用于返回的对象

        Map mp = null;
        List ls = null;
        try {
            //{}对象层级解析
            if (m instanceof Map || m instanceof LinkedHashMap) {
                mp = (LinkedHashMap) m;
                if (i < key.split("\\.").length) {
                    String subKey = key.split("\\.")[i];
                    o = mp.get(subKey);
                    i++;
                    //递归最后一次
                    if (i == key.split("\\.").length) {
                        return o;
                    }
                    if (o instanceof LinkedHashMap) {
                        o = getObject((LinkedHashMap) o, key);
                        return o;
                    } else if (o instanceof ArrayList) {
                        o = getObject((ArrayList) o, key);
                        return o;
                    }
                }
            }
            //[]数组层级解析
            if (m instanceof List || m instanceof ArrayList) {
                ls = (ArrayList) m;
                if (i < key.split("\\.").length) {
                    int subKey = Integer.valueOf(key.split("\\.")[i]);
                    o = ls.get(subKey);
                    i++;
                    if (i == key.split("\\.").length) {
                        return o;
                    }
                    if (o instanceof LinkedHashMap) {
                        o = getObject((LinkedHashMap) o, key);
                        return o;
                    } else if (ls.get(i) instanceof ArrayList) {
                        o = getObject((ArrayList) o, key);
                        return o;
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return o;
    }

}
