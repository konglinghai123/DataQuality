package com.jollychic.holmes.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by WIN7 on 2018/1/16.
 */
public class DateUtils {
    static ThreadLocal<SimpleDateFormat> simpleDateFormatThreadLocal = ThreadLocal.withInitial(SimpleDateFormat::new);
    public static String format(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = simpleDateFormatThreadLocal.get();
        simpleDateFormat.applyPattern(pattern);
        return simpleDateFormat.format(date);
    }

    public static String getFromYearToSecond(Date date) {
        return format(date, "yyyyMMddHHmmss");
    }

    public static String getFromYearToMinute(Date date) {
        return format(date, "yyyyMMddHHmm");
    }

    public static Date parse(String dateStr, String pattern) throws Exception {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = simpleDateFormatThreadLocal.get();
        simpleDateFormat.applyPattern(pattern);
        return simpleDateFormat.parse(dateStr);
    }
    public static String getOneUnitAgoTime(String pattern) {
        Calendar cal = Calendar.getInstance();
        if(pattern.endsWith("MM")) {
            cal.set(Calendar.MONTH , cal.get(Calendar.MONTH)-1);
        } else if(pattern.endsWith("dd")) {
            cal.set(Calendar.DAY_OF_MONTH , cal.get(Calendar.DAY_OF_MONTH)-1);
        } else if(pattern.endsWith("HH")) {
            cal.set(Calendar.HOUR_OF_DAY , cal.get(Calendar.HOUR_OF_DAY)-1);
        }
        String oneUnitAgoTime = new SimpleDateFormat(pattern).format(cal.getTime());//获取到完整的时间
        return oneUnitAgoTime;
    }

    public static String getOneUnitAndDidderDayAgoTime(String pattern, Integer differDay) {
        Calendar cal = Calendar.getInstance();
        if(pattern.endsWith("MM")) {
            cal.set(Calendar.MONTH , cal.get(Calendar.MONTH)-1);
        } else if(pattern.endsWith("dd")) {
            cal.set(Calendar.DAY_OF_MONTH , cal.get(Calendar.DAY_OF_MONTH)-1);
        } else if(pattern.endsWith("HH")) {
            cal.set(Calendar.HOUR_OF_DAY , cal.get(Calendar.HOUR_OF_DAY)-1);
        }
        if(differDay==null || differDay<=0) {
            differDay = 1;
        }
        cal.set(Calendar.DAY_OF_MONTH , cal.get(Calendar.DAY_OF_MONTH)-differDay);
        String oneUnitAndDidderDayAgoTime = new SimpleDateFormat(pattern).format(cal.getTime());//获取到完整的时间
        return oneUnitAndDidderDayAgoTime;
    }

    public static void main(String[] args) {
        System.out.println(getFromYearToMinute(new Date()));
        System.out.println(getOneUnitAgoTime("yyyyMMdd HH"));
        System.out.println(getOneUnitAgoTime("yyyy-MM-dd"));
        System.out.println(getOneUnitAgoTime("yyyy-MM"));
    }

}
