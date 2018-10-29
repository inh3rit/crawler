package com.zxsoft.crawler.common.kit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cox on 2015/10/14.
 */
public class Tool {

    public static Boolean isNumber(String str) {
        return str.matches("\\d*");
    }

    public static Boolean isDouble(String str) {
        if (!str.contains("."))
            return Tool.isNumber(str);
        return Tool.isNumber(str.replace(".", ""));
    }

    public static Boolean isBoolean(String str) {
        Pattern p = Pattern.compile("true|false");
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static Long parseLong(String str) {
        if (Tool.isNumber(str)) return Long.parseLong(str);
        return System.currentTimeMillis();
    }

    public static int countOccurrencesOf(String str, String sub) {
        if(str != null && sub != null && str.length() != 0 && sub.length() != 0) {
            int count = 0;

            int idx;
            for(int pos = 0; (idx = str.indexOf(sub, pos)) != -1; pos = idx + sub.length()) {
                ++count;
            }

            return count;
        } else {
            return 0;
        }
    }


}
