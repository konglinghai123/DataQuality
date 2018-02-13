package com.jollychic.holmes.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by WIN7 on 2018/1/15.
 */
public class ExceptionUtils {
    public static String getErrorStack(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "\r\n" + sw.toString() + "\r\n";
        } catch (Exception e2) {
            return "getErrorStack from exception fail, "+e2;
        }
    }
}
