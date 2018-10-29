package com.zxsoft.crawler.w.util;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by iaceob on 14-12-21.
 */
public class Tool {


    public static String decodeURL(String url) {
        return decodeURL(url, "UTF-8");
    }

    public static String decodeURL(String url, String enc) {
        try {
            return URLDecoder.decode(url, enc);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return url;
        }
    }

    public static String conversionXSS(String str) {
        if (str==null||"".equals(str)) return "";
        StringBuffer esText = new StringBuffer();
        for(int i=0; i<str.length(); i++) {
            switch(str.charAt(i)) {
                case '\'':
                    esText.append("&#39;");
                    break;
                case '"':
                    esText.append("&quot;");
                    break;
                case '<':
                    esText.append("&lt;");
                    break;
                case '>':
                    esText.append("&gt;");
                    break;
                case '\n':
                    esText.append(" ");
                    break;
                default:
                    esText.append(str.charAt(i));
            }
        }
        return esText.toString();
    }


    public static Integer parsePageNumber(Integer page) {
        return page<1 ? 1 : page;
    }

    public static String pushResult(String code, String msg) {
        return pushResult(1, code, msg);
    }

    public static String pushResult(Integer status, String code, String msg) {
        return pushResult(status, code, msg, null);
    }

    public static String pushResult(Integer status, String code, String msg, Object extra) {
        return "{\"status\": " + status + ", \"code\": \"" + code + "\", \"msg\": \"" + msg + "\", \"extra\": " + JsonKit.toJson(extra) + "}";
    }

    public static Double strToDouble(String str) {
        return strToDouble(str, 0.0D);
    }

    public static Double strToDouble(String str, Double def) {
        return isDouble(str) ? Double.valueOf(str) : def;
    }

    public static Integer strToInt(String target) {
        return strToInt(target, 0);
    }

    public static Integer strToInt(String target, Integer def) {
        return isNumber(target) ? Integer.valueOf(target) : def;
    }

    public static Boolean isDouble(String str) {
        Pattern pattern = Pattern.compile("^[-+]?(\\d+(\\.\\d*)?|\\.\\d+)([eE]([-+]?([012]?\\d{1,2}|30[0-7])|-3([01]?[4-9]|[012]?[0-3])))?[dD]?$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static Boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("\\d*");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static String[] listToArray(List<String> lt) {
        String[] arr = new String[lt.size()];
        for(int i=lt.size(); i-->0;)
            arr[i] = lt.get(i);
        return arr;
    }

    public static Record parseHeader(HttpServletRequest request) {
        Enumeration headers = request.getHeaderNames();
        Record h = new Record();
        String hk;
        while (headers.hasMoreElements()) {
            hk = String.valueOf(headers.nextElement());
            h.set(hk, request.getHeader(hk));
        }
        return h;
    }

    public static Record parasToRecord(Map<String, String[]> paras) {
        Record r = new Record();
        Set<String> set = paras.keySet();
        for(String key : set)
            r.set(key.toLowerCase(), paras.get(key)[0]);
        return r;
    }

}
