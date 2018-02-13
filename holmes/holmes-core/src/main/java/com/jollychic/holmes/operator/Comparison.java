package com.jollychic.holmes.operator;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by WIN7 on 2018/1/17.
 */
public class Comparison {
    public final static List<String> OPERATORS_1 = Lists.newArrayList("<", "<=", "=", "==", ">=", ">", "!=", "<>");
    public final static List<String> OPERATORS_2 =Lists.newArrayList("equals", "not equals", "equal", "not equal");
    public final static List<String> OPERATORS_3 =Lists.newArrayList("is null", "is not null", "is NULL", "is NOT NULL");

    public static boolean compare(Object actual, Object expect, String operator) {
        boolean result = false;
        if(OPERATORS_1.contains(operator)) {
            if(actual==null) {
                actual = 0;
            }
            if(expect==null) {
                expect = 0;
            }
            Double actualDouble = Double.valueOf(String.valueOf(actual));
            Double expectDouble = Double.valueOf(String.valueOf(expect));
            switch (operator) {
                case "==":
                case "=": {
                    if(Math.abs(actualDouble.compareTo(expectDouble))<=0.000001) {
                        result = true;
                    }
                    break;
                }
                case "<>":
                case "!=": {
                    if(Math.abs(actualDouble.compareTo(expectDouble))>0.000001) {
                        result = true;
                    }
                    break;
                }
                case "<": {
                    if(actualDouble.compareTo(expectDouble)<0) {
                        result = true;
                    }
                    break;
                }
                case "<=": {
                    if(actualDouble.compareTo(expectDouble)<=0) {
                        result = true;
                    }
                    break;
                }
                case ">=": {
                    if(actualDouble.compareTo(expectDouble)>=0) {
                        result = true;
                    }
                    break;
                }
                case ">": {
                    if(actualDouble.compareTo(expectDouble)>0) {
                        result = true;
                    }
                    break;
                }
            }
        } else if(OPERATORS_2.contains(operator)) {
            String actualString = String.valueOf(actual);
            String expectString = String.valueOf(actual);
            switch (operator) {
                case "equal":
                case "equals": {
                    if (actualString.equals(expectString)) {
                        result = true;
                    }
                    break;
                }
                case "not equal":
                case "not equals": {
                    if (!actualString.equals(expectString)) {
                        result = true;
                    }
                    break;
                }
            }
        } else if(OPERATORS_3.contains(operator)) {
            switch (operator) {
                case "is null":
                case "is NULL": {
                    if (actual == null) {
                        result = true;
                    }
                    break;
                }
                case "is not null":
                case "is NOT NULL": {
                    if (actual != null) {
                        result = true;
                    }
                    break;
                }
            }
        }

        return result;
    }

    public static Double divide(Object o1, Object o2) {
        if(o1==null) {
            o1 = 0;
        }
        if(o2==null) {
            o2 = 0;
        }
        BigDecimal b1 = new BigDecimal(String.valueOf(o1));
        BigDecimal b2 = new BigDecimal(String.valueOf(o2));
        if(b2.compareTo(new BigDecimal(0))==0) {
            return null;
        }
        return new Double(b1.divide(b2, 3, BigDecimal.ROUND_HALF_UP).doubleValue());
    }
}
