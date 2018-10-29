package com.zxsoft.crawler.common.kit;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 格式化url, 有些版块url地址是动态变化的, 例如根据日期变化的, http://www.secretchina.com/news/yy/MM/dd.
 * 本类的目的是生成实际的url.
 *
 * @author xiayun
 */
public class UrlKit {

    private static Logger LOG = LoggerFactory.getLogger(UrlKit.class);

    /**
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String format(String url) throws UnsupportedEncodingException {
        Date date = new Date();
        int count = Tool.countOccurrencesOf(url, "%t");
        if (count == 0) {
            return url;
        }
        List<Date> list = new ArrayList<Date>();
        for (int i = 0; i < count; i++) {
            list.add(date);
        }

        Date[] strs = list.toArray(new Date[]{});
        try {
            url = String.format(url, strs);
        } catch (Exception e) {
            LOG.error("Format url error.", e);
        }
        return url;
    }

    /**
     * @param url
     * @param keyword 关键字
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String format(String url, String keyword) throws UnsupportedEncodingException {
        Date date = new Date();
        int count = Tool.countOccurrencesOf(url, "%t");
        if (count == 0) {
            url = String.format(url, keyword);
            url = url.replaceAll(" ", "%20");
            return url;
        }
        List<Object> list = new ArrayList<Object>();
        list.add(keyword);
        for (int i = 0; i < count; i++) {
            list.add(date);
        }

        Object[] strs = list.toArray(new Object[]{});
        try {
            url = String.format(url, strs);
        } catch (Exception e) {
            LOG.error("Format url error.", e);
        }
        url = url.replaceAll(" ", "%20");
        return url;
    }

    /**
     * 鏈接重構, 部分鏈接是網站中的相對路徑鏈接, 這裏將此類型鏈接轉換爲絕對路徑鏈接
     *
     * @param url      原鏈接
     * @param host     站點主機
     * @param basePath 當前鏈接的相對上級目錄
     * @param srcUrl   首次訪問鏈接
     * @return
     */
    public static String struct(String url, String host, String basePath, String srcUrl) {
        url = url.trim();
        if (!url.startsWith("http")) {
            if (url.charAt(0) == '/')
                return host + url;
            else
                return host + "/" + url;
        }
        if ("./".equals(url.substring(0, 2)))
            return basePath + url.substring(2, url.length());
        if ("../".equals(url.substring(0, 3))) {
            String bp = basePath;
            bp = bp.charAt(bp.length() - 1) == '/' ? bp.substring(0, bp.lastIndexOf("/")) : bp;
            bp = bp.substring(0, bp.lastIndexOf("/") + 1);
            bp = bp + url.substring(3, url.length());
            return bp;
        }
        if (url.contains("?") && !"http://".equals(url.substring(0, 7)) && !"https://".equals(url.substring(0, 8))) {
            String nsurl = srcUrl.split("\\?")[0];
            nsurl = nsurl.endsWith("/") ? nsurl.substring(0, nsurl.lastIndexOf("/") + 1) : nsurl;
            String[] nurl = url.split("\\?");
            return nsurl + "?" + nurl[1];
        }
        return url;
    }

    /**
     * 仅对文本里的中文字符转换成URL编码
     *
     * @param str
     * @return
     */
    public static String encodeCn(String str) {
        try {
            Matcher matcher = Pattern.compile("[\\u4e00-\\u9fa5]").matcher(str);
            while (matcher.find()) {
                String tmp = matcher.group();
                str = str.replaceAll(tmp, URLEncoder.encode(tmp, "UTF-8"));
            }
            return str;
        } catch (UnsupportedEncodingException uee) {
            LOG.error("string decode error, str:{}, msg:{}", str, uee.toString());
            return null;
        }
    }
}