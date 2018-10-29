package com.zxsoft.crawler.p.ext;

import com.jfinal.kit.StrKit;
import org.inh3rit.nldp.Nldp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cox on 2015/10/10.
 */
public class DateExtractor {

    private static final String FormatNull = "FormatNull";
    private static final String Format_MMMMddyyyy = "MMMM dd, yyyy";
    private static final String Format_dd_MMMM_yyyy = "dd MMMM yyyy";

    public static Date extract(String text) {
        if (StrKit.isBlank(text))
            return null;

        if (text.trim().startsWith("http://")) {
            text = text.replaceAll("http://", "");
        }

        text = text.replaceAll("\u00A0", " ");
        text = text.replaceAll("&nbsp;", " ");
        Date date = null;
        try {
            Nldp nldp = new Nldp(text);
            date = nldp.extractDate();
        } catch (Exception e) {
//            LOG.error("解析时间失败:" + text, e);
        }

        if (text.contains("PM")) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            if (c.get(Calendar.HOUR_OF_DAY) != 12)
                date = new Date(c.getTimeInMillis() + 12 * 60 * 60 * 1000);
        }

        return date;
    }

    public static long extractInMilliSecs(String text) {
        Date date = extract(text);
        return date!=null ? date.getTime() : System.currentTimeMillis();
    }

    /**
     * 检查时间格式给出格式化模式
     *
     * @param dateStr
     * @return
     */
    public static String checkDateFormat(String dateStr) {
        String regex1 = "(January|February|March|April|May|June|July|August|September|October|November|December)*\\s[0-9]{2},\\s[0-9]{4}";
        String regex2 = "[0-9]{2,}\\s(January|February|March|April|May|June|July|August|September|October|November|December)\\s[0-9]{4,}";
        dateStr = dateStr.trim();
        switch (dateStr.matches(regex1) ? 1 :
                (dateStr.matches(regex2) ? 2 : 0)) {
            case 1:
                return Format_MMMMddyyyy;
            case 2:
                return Format_dd_MMMM_yyyy;
            default:
                return FormatNull;
        }
    }

    /**
     * 时间字符串格式化为标准时间格式 yyyy-MM-dd HH:mm:ss
     *
     * @param stringDate
     * @param pattern
     * @return
     */
    public static String dateFormat(String stringDate, String pattern) {
        String dateStr = new String();
        try {
            if (FormatNull.equals(pattern))
                return stringDate;
            pattern = (pattern == null || pattern.isEmpty()) ? Format_MMMMddyyyy : pattern;
            //字符串格式化Date
            Date date = new SimpleDateFormat(pattern, Locale.US).parse(stringDate);
            //Date转字符串
            dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }
}
