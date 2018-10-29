package com.zxsoft.crawler.p.ext;

import com.jfinal.kit.StrKit;
import com.zxsoft.crawler.common.entity.sync.TimeRegexEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.kit.UnicodeKit;
import com.zxsoft.crawler.exception.CrawlerException;
import info.hb.time.retrive.core.TimeExtract;
import info.hb.time.retrive.domain.MatchedTime;
import org.inh3rit.nldp.Nldp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by cox on 2015/9/21.
 */
public class TimeParse {

    private static final Logger log = LoggerFactory.getLogger(TimeParse.class);

    // 表示url中连续的8位日期，例: http://www.baidu.com/20140311/2356.html
    private static String url_reg_1 = "(20\\d{6})";
    // 表示url中连续的6位日期，例: http://www.baidu.com/201403/2356.html
    private static String url_reg_2 = "(20\\d{4})";
    // 表示url中连续的8位日期，例: http://www.baidu.com/2014-3-11/2356.html
    private static String url_reg_3 = "(20\\d{2}[-|/|_]{1}\\d{1,2}[-|/|_]{1}\\d{1,2})";
    // 表示url中连续的8位日期，例: 例如 http://www.baidu.com/2014-3/2356.html
    private static String url_reg_4 = "(20\\d{2}[-|/|_]{1}\\d{1,2})";
    // 当前时间
    private static Calendar current = Calendar.getInstance();


    /**
     * 首先根据 URL 抓取时间,
     * 若无法抓取尝试使用内容抓取
     *
     * @param url
     * @param content
     * @return
     */
    public static Long parse(String url, String content, List<TimeRegexEntity> treList) throws CrawlerException {
        Long timestamp = 0L;
        if (StrKit.isBlank(content)) return 0L;
        // 获取url上的时间
        String urlTime = TimeParse.parseFromUrl(url);
        // 根据数据库中保存的正则表达式从正文中提取时间
        List<String> regexTimeLst = TimeParse.parseFromRegex(content, treList);
        // 时间提取器尝试从正文提取时间
        List<String> extractTimeLst = TimeParse.parseFromContent(content);

        try {
            // urlTime作为正文中提取时间的参考
            if (null != urlTime) {
                regexTimeLst = timeFilter(urlTime, regexTimeLst);
                extractTimeLst = timeFilter(urlTime, extractTimeLst);

                // 如果正文提前不到时间，urlTime(8位)则作为正文时间
                if (regexTimeLst.size() == 0 && extractTimeLst.size() == 0 && url.length() == 8) {
                    return returnUrlTime(urlTime);
                }
            }

            // 没有urlTime做参照时，优先取正则方式抓取的最新的时间
            Collections.sort(regexTimeLst, (a, b) -> {
                long ts_a = TimeParse.formatTime(a);
                long ts_b = TimeParse.formatTime(b);
                return ts_b - ts_a > 0 ? 1 : -1;
            });
            if (0 < regexTimeLst.size())
                return TimeParse.formatTime(regexTimeLst.get(0));

            //  正则方式抓取不到时，取时间抓取器在内容中抓取的最新时间
            Collections.sort(extractTimeLst, (a, b) -> {
                long ts_a = TimeParse.formatTime(a);
                long ts_b = TimeParse.formatTime(b);
                return ts_b - ts_a > 0 ? 1 : -1;
            });
            if (0 < extractTimeLst.size())
                return TimeParse.formatTime(extractTimeLst.get(0));
        } catch (Exception ex) {
            if (StrKit.notBlank(urlTime))
                return returnUrlTime(urlTime);
            throw new CrawlerException(CrawlerException.ErrorCode.SYSTEM_ERROR, "提前网页发布时间失败!");
        }

        if (StrKit.notBlank(urlTime))
            return returnUrlTime(urlTime);
        throw new CrawlerException(CrawlerException.ErrorCode.SYSTEM_ERROR, "提前网页发布时间失败!");
    }

    // 返回url上抓取是时间的timestamp
    private static long returnUrlTime(String urlTime) {
        Calendar url_c = Calendar.getInstance();
        url_c.set(Calendar.YEAR, Integer.parseInt(urlTime.substring(0, 4)));
        url_c.set(Calendar.MONTH, Integer.parseInt(urlTime.substring(4, 6)));
        url_c.set(Calendar.DATE, Integer.parseInt(urlTime.substring(6)));
        return url_c.getTimeInMillis();
    }

    private static List<String> timeFilter(String urlTime, List<String> timeList) {
        return timeList = timeList.stream().filter((s) -> {
            // 正文提取的时间格式不确定，先转换成timestamp，再格式化成需要的格式
            long ts = TimeParse.formatTime(s);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(ts);
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String day = format.format(c.getTime());
            if (day.substring(0, urlTime.length()).equals(urlTime))
                return true;
            return false;
        }).collect(Collectors.toList());
    }

    /**
     * 对实际提取所需要的内容进行转码
     *
     * @param content 内容
     * @return
     */
    private static String encodeDateStr(String content) {
        Pattern p = Pattern.compile("发布在|发表|发布|时间|来源|回复|年|月|日|時|时|分|秒|前|于|於|樓|楼");
        Matcher m = p.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String dst = m.group();
            m.appendReplacement(sb, UnicodeKit.toUnicode(dst, true));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 将转后的文本还原
     *
     * @param content 内容
     * @return
     */
    private static String decodeDateStr(String content) {
        return UnicodeKit.toStr(content);
    }


    /**
     * 格式正确的时间正则表达式
     */
    // private static String rightTimeReg = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
    private static Calendar strToCalendar(String time, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            Date date = dateFormat.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            // System.out.println(calendar.get(Calendar.YEAR));
            return calendar;
        } catch (ParseException e) {
            log.debug(e.getMessage());
        }
        return null;
    }

    /**
     * 是否是可用的时间
     *
     * @param date
     * @return
     */
    // TODO 目前仅仅是判断爬取的时间与当前时间比较, 关于时区的判断也没有添加, 待有新逻辑在增加
    private static Boolean isAvailableTime(Date date) {
        return !(date.getTime() >= System.currentTimeMillis());
    }

    private static Long formatTime(String timeStr) {
        Nldp nldp = new Nldp(timeStr);
        Date date = nldp.extractDate();
        if (!TimeParse.isAvailableTime(date)) return 0L;
        return date.getTime();
    }

    /**
     * 过滤非时间文本内容
     *
     * @param content 内容
     * @return
     */
    private static String filterNotTime(String content) {
        if (StrKit.isBlank(content)) return content;
        content = content.replaceAll("(?is)<!DOCTYPE.*?>", "");
        content = content.replaceAll("(?is)<script.*?>.*?</script>", "");
        content = content.replaceAll("(?is)<style.*?>.*?</style>", "");
        content = content.replaceAll("(?is)<.*?>", "");
        content = content.replaceAll("[a-zA-Z]", "");
        content = content.replaceAll("&.{2,5};|&#.{2,5};", " ");
        content = content.replaceAll("\\n", " ");
        content = encodeDateStr(content);
        content = content.replaceAll("[\uFF10-\uFF19\uFF21-\uFF3A\uFF41-\uFF5A\u4E00-\u9FA5，。？“”、！\\?!～【】,・()*|»›（）\\[\\]><《》…■\"#\\{}┊]+", "");
        content = decodeDateStr(content);
        // 发布在|发表|发布|时间|来源|回复|年|月|日|時|时|分|秒|前|于|於|樓|楼
        // |\-{2,}|―{2,}|\s+
        //
//        content = content.replaceAll("/{2,}|\\.{2,}|[　]{2,}|:{2,}|\\-{2,}|―{2,}|：{2,}|；{2,}|：{2,}|年{2,}|月{2,}|日{2,}|时{2,}|分{2,}|秒{2,}|于{2,}|前{2,}|楼{2,}|[回复\\s+]{2,}|\\n|\\s{2,}|\\d{5,}|:\\d{3,}|：\\d{3,}", "");
        return content;
    }

    /**
     * 尝试从 URL 中提取发布时间
     *
     * @param url url
     * @return
     */
    private static String parseFromUrl(String url) {
        try {
            Pattern p_whole = Pattern.compile(url_reg_1);
            Matcher m_whole = p_whole.matcher(url);
            if (m_whole.find(0) && m_whole.groupCount() > 0) {
                String time = m_whole.group(0);
                //每一步都不能够超出当前时间
                if (current.compareTo(TimeParse.strToCalendar(time, "yyyyMMdd")) >= 0)
                    return time;
            }

            p_whole = null;
            m_whole = null;
            p_whole = Pattern.compile(url_reg_2);
            m_whole = p_whole.matcher(url);
            if (m_whole.find(0) && m_whole.groupCount() > 0) {
                String time = m_whole.group(0);
                //每一步都不能够超出当前时间
                if (current.compareTo(TimeParse.strToCalendar(time, "yyyyMM")) >= 0)
                    return time;
            }

            p_whole = null;
            m_whole = null;
            Pattern p_sep = Pattern.compile(url_reg_3);
            Matcher m_sep = p_sep.matcher(url);
            if (m_sep.find(0) && m_sep.groupCount() > 0) {
                String time = m_sep.group(0);
                String[] seg = time.split("[-|/|_]{1}");
                time = new StringBuffer().append(Integer.parseInt(seg[0]))
                        .append(String.format("%02d", Integer.parseInt(seg[1])))
                        .append(String.format("%02d", Integer.parseInt(seg[2])))
                        .toString();
                if (current.compareTo(TimeParse.strToCalendar(time, "yyyyMMdd")) >= 0)
                    return time;
            }

            p_sep = null;
            m_sep = null;
            Pattern p_sep_ym = Pattern.compile(url_reg_4);
            Matcher m_sep_ym = p_sep_ym.matcher(url);
            if (m_sep_ym.find(0) && m_sep_ym.groupCount() > 0) {
                String time = m_sep_ym.group(0);
                String[] seg = time.split("[-|/|_]{1}");
                time = new StringBuffer().append(Integer.parseInt(seg[0]))
                        .append(String.format("%02d", Integer.parseInt(seg[1])))
                        .toString();
                if (current.compareTo(TimeParse.strToCalendar(time, "yyyyMM")) >= 0)
                    return time;
            }
        } catch (Exception e) {
            log.debug("extra timestamp from url {} error, ExceptionMessage: {}", url, e.getMessage());
        }
        return null;
    }


    /**
     * 尝试时间提取器
     *
     * @param content 内容
     * @return
     */
    private static List<String> parseFromContent(String content) {
        List<String> timeLst = new ArrayList<>();
        if (content == null || "".equals(content)) return timeLst;
        TimeExtract timeExtract = new TimeExtract();
        List<MatchedTime> times = timeExtract.extractInput(content);
        if (times == null || times.isEmpty()) return timeLst;
        Pattern p = Pattern.compile("(?<time>\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})");
        for (MatchedTime time : times) {
            Matcher m = p.matcher(time.getRawString().toString());
            if (m.find())
                timeLst.add(m.group("time") + ":00");
        }
        return timeLst;
    }

    /**
     * 使用规则提取
     *
     * @param content 内容
     * @param treList     规则
     * @return
     */
    private static List<String> parseFromRegex(String content, List<TimeRegexEntity> treList) {
        List<String> timeLst = new ArrayList<>();
        if (CollectionKit.isEmpty(treList)) return timeLst;
        String fnt = null;
        try {
            fnt = filterNotTime(content);
            for (TimeRegexEntity timeRegexEntity : treList) {
                Pattern p = Pattern.compile(timeRegexEntity.getRegex());
                Matcher m = p.matcher(fnt);
                if (!m.find())
                    continue;
                String time = m.group();
                if (StrKit.notBlank(time))
                    timeLst.add(time);
            }
        } catch (Exception e) {
            log.debug("match [{}] can not extra time, ErrorMessage: [{}]", fnt, e.getMessage());
            fnt = null;
            content = null;
        }
        return timeLst;
    }

    public static Long parseByRegex(String timeText, List<TimeRegexEntity> treList) throws CrawlerException {
        if (CollectionKit.isEmpty(treList))
            throw new CrawlerException(CrawlerException.ErrorCode.SYSTEM_ERROR, "time regex list is null");

        long timestamp = 0L;
        for (TimeRegexEntity timeRegexEntity : treList) {
            Pattern p = Pattern.compile(timeRegexEntity.getRegex());
            Matcher m = p.matcher(timeText);
            if (!m.find())
                continue;
            String time = m.group();
            if (StrKit.notBlank(time)) {
                timestamp = TimeParse.formatTime(time);
                break;
            }
        }

        if (timeText.contains("PM") || timeText.contains("pm")) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(timestamp);
            if (c.get(Calendar.HOUR) != 12) {
                timestamp += 12 * 60 * 60 * 1000;
            }
        }

        return timestamp;
    }

}
