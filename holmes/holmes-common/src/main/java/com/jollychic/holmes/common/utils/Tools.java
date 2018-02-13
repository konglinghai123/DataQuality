package com.jollychic.holmes.common.utils;

import java.util.Random;

/**
 * Created by WIN7 on 2018/1/5.
 */
public class Tools {
    public static boolean isEmptyString(String str) {
        return str == null || "".equals(str.trim());
    }

    //length表示生成字符串的长度
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
