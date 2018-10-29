package com.zxsoft.crawler.s.test.ext;


import com.jfinal.kit.StrKit;
import com.jfinal.plugin.redis.RedisPlugin;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.sync.TimeRegexEntity;
import com.zxsoft.crawler.common.entity.time.TimeRuleEntity;
import com.zxsoft.crawler.common.kit.UnicodeKit;
import org.inh3rit.httphelper.common.HttpConst;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.inh3rit.nldp.Nldp;
import org.jsoup.Jsoup;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TimeParseTest {

    private static final Logger log = LoggerFactory.getLogger(TimeParseTest.class);


    private TimeRuleEntity getTimeRule(List<TimeRegexEntity> tres) {
        StringBuilder sb = new StringBuilder();
        for (Integer i = 0; i < tres.size(); i++) {
            String regexStr = tres.get(i).getRegex();
            regexStr = regexStr.replace("?<time>", "?<time" + i + ">");
            sb.append("(").append(regexStr).append(i + 1 == tres.size() ? ")" : ")|");
        }
        TimeRuleEntity tre = new TimeRuleEntity();
        tre.setRegex(Pattern.compile(sb.toString()))
                .setCount(tres.size()).setMark("time");
        return tre;
    }

    private TimeRuleEntity genTre() {
        List<TimeRegexEntity> trea = new ArrayList<TimeRegexEntity>();
        trea.add(new TimeRegexEntity().setId(27).setSample("2015-11-20 11:08:49 来源").setMark("time").setSort(9).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:07:07.0")).setResult(Timestamp.valueOf("2015-11-20 11:08:49.0")).setRegex("[\\s\\S]*?(?<time>\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})\\s+来源"));
        trea.add(new TimeRegexEntity().setId(34).setSample("发表于: 15-11-20 11:20").setMark("time").setSort(9).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:14:52.0")).setResult(Timestamp.valueOf("2015-11-20 11:20:00.0")).setRegex("发表于\\s*:\\s*(?<time>\\d{1,2}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(35).setSample("发表于: 15-11-20 11:20:2").setMark("time").setSort(9).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:15:10.0")).setResult(Timestamp.valueOf("2015-11-20 11:20:02.0")).setRegex("发表于\\s*:\\s*(?<time>\\d{1,2}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(1).setSample("时间：2015-11-20 10:40:20").setMark("time").setSort(8).setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 16:49:12.0")).setResult(Timestamp.valueOf("2015-11-20 10:40:20.0")).setRegex("时间\\s*：\\s*(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(28).setSample("于 2015-11-20 11:28:40 发表").setMark("time").setSort(8).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:11:56.0")).setResult(Timestamp.valueOf("2015-11-20 11:28:40.0")).setRegex("于\\s+(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})\\s+发表"));
        trea.add(new TimeRegexEntity().setId(29).setSample("于 2015-11-20 11:19 发表").setMark("time").setSort(8).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:12:15.0")).setResult(Timestamp.valueOf("2015-11-20 11:19:00.0")).setRegex("于\\s+(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})\\s+发表"));
        trea.add(new TimeRegexEntity().setId(38).setSample("时间:2015-11-20 10:49").setMark("time").setSort(8).setUsr("4028a0c74f4a328c014f4a3ef94c0001").setMtime(Timestamp.valueOf("2015-11-20 17:34:35.0")).setResult(Timestamp.valueOf("2015-11-20 10:49:00.0")).setRegex("时间\\s*:\\s*(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(40).setSample("发表于： 2015-11-20 10:31").setMark("time").setSort(8).setUsr("4028a0c74f4a328c014f4a3ef94c0001").setMtime(Timestamp.valueOf("2015-11-20 17:34:26.0")).setResult(Timestamp.valueOf("2015-11-20 10:31:00.0")).setRegex("发表于\\s*(?<time>.*?\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(42).setSample("发表于： 15-11-20 11:20").setMark("time").setSort(8).setUsr("4028a0c74f4a328c014f4a3ef94c0001").setMtime(Timestamp.valueOf("2015-11-20 17:35:06.0")).setResult(Timestamp.valueOf("2015-11-20 11:20:00.0")).setRegex("发表于\\s*(?<time>.*?\\d{1,2}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(30).setSample("2015年11月20日 08:06:06").setMark("time").setSort(7).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:12:32.0")).setResult(Timestamp.valueOf("2015-11-20 08:06:06.0")).setRegex("(?<time>\\d{2,4}年\\d{1,2}月\\d{1,2}日\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(31).setSample("2015年11月20日 08:06").setMark("time").setSort(7).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:13:04.0")).setResult(Timestamp.valueOf("2015-11-20 08:06:00.0")).setRegex("(?<time>\\d{2,4}年\\d{1,2}月\\d{1,2}日\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(4).setSample("发布时间： 15-11-20 09:23:23").setMark("time").setSort(6).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 16:54:01.0")).setResult(Timestamp.valueOf("2015-11-20 09:23:23.0")).setRegex("[\\s\\S]*?发布时间(?<time>.*?\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(7).setSample("发布时间： 15-11-20 09:23").setMark("time").setSort(6).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 16:55:28.0")).setResult(Timestamp.valueOf("2015-11-20 09:23:00.0")).setRegex("[\\s\\S]*?发布时间(?<time>.*?\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(15).setSample("2015年11月20日 11时20分20秒").setMark("time").setSort(6).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:00:53.0")).setResult(Timestamp.valueOf("2015-11-20 11:20:20.0")).setRegex("(?<time>\\d{4}年\\d{1,2}月\\d{1,2}日\\s+\\d{1,2}时\\d{1,2}分\\d{1,2}秒)"));
        trea.add(new TimeRegexEntity().setId(16).setSample("2015年11月20日 11时20分").setMark("time").setSort(6).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:01:17.0")).setResult(Timestamp.valueOf("2015-11-20 11:20:00.0")).setRegex("(?<time>\\d{4}年\\d{1,2}月\\d{1,2}日\\s+\\d{1,2}时\\d{1,2}分)"));
        trea.add(new TimeRegexEntity().setId(17).setSample("2015-11-20 11时20分20秒").setMark("time").setSort(6).setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 19:48:21.0")).setResult(Timestamp.valueOf("2015-11-20 11:20:20.0")).setRegex("(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s*\\d{1,2}时\\s*\\d{1,2}分\\s*\\d{1,2}秒)"));
        trea.add(new TimeRegexEntity().setId(18).setSample("2015-11-20 11时20分").setMark("time").setSort(6).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:02:16.0")).setResult(Timestamp.valueOf("2015-11-20 11:20:00.0")).setRegex("(?<time>\\d{4}-\\d{1,2}-\\d{1,2}\\s*\\d{1,2}时\\s*\\d{1,2}分)"));
        trea.add(new TimeRegexEntity().setId(19).setSample("发表于: 3天前").setMark("time").setSort(5).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:02:42.0")).setResult(Timestamp.valueOf("2015-11-17 00:00:00.0")).setRegex("发表于(?<time>.*?前)"));
        trea.add(new TimeRegexEntity().setId(36).setSample("时间：2015-11-20 10:49").setMark("time").setSort(5).setUsr("4028a0c74f4a328c014f4a3ef94c0001").setMtime(Timestamp.valueOf("2015-11-20 17:27:32.0")).setResult(Timestamp.valueOf("2015-11-20 10:49:00.0")).setRegex("时间\\s*：\\s*(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(37).setSample("时间:2015-11-20 10:49:20").setMark("time").setSort(5).setUsr("4028a0c74f4a328c014f4a3ef94c0001").setMtime(Timestamp.valueOf("2015-11-20 17:31:46.0")).setResult(Timestamp.valueOf("2015-11-20 10:49:20.0")).setRegex("时间\\s*:\\s*(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(39).setSample("发表于： 2015-11-20 10:44:49").setMark("time").setSort(5).setUsr("4028a0c74f4a328c014f4a3ef94c0001").setMtime(Timestamp.valueOf("2015-11-20 17:33:09.0")).setResult(Timestamp.valueOf("2015-11-20 10:44:49.0")).setRegex("发表于\\s*(?<time>.*?\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(41).setSample("发表于 15-11-20 11:20:23").setMark("time").setSort(5).setUsr("4028a0c74f4a328c014f4a3ef94c0001").setMtime(Timestamp.valueOf("2015-11-20 17:34:17.0")).setResult(Timestamp.valueOf("2015-11-20 11:20:23.0")).setRegex("发表于\\s*(?<time>.*?\\d{1,2}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(8).setSample("【发布日期：2015-11-20】").setMark("time").setSort(4).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 16:55:48.0")).setResult(Timestamp.valueOf("2015-11-20 00:00:00.0")).setRegex("【发布日期(?<time>.*?\\d{2,4}-\\d{1,2}-\\d{1,2})】"));
        trea.add(new TimeRegexEntity().setId(11).setSample("15年11月20日 11时20分20秒").setMark("time").setSort(4).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 16:58:17.0")).setResult(Timestamp.valueOf("2015-11-20 11:20:20.0")).setRegex("(?<time>\\d{2,4}年\\d{1,2}月\\d{1,2}日\\s+\\d{1,2}时\\d{1,2}分\\d{1,2}秒)"));
        trea.add(new TimeRegexEntity().setId(23).setSample("于 2015-11-20 12:00 发布在").setMark("time").setSort(4).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:05:22.0")).setResult(Timestamp.valueOf("2015-11-20 12:00:00.0")).setRegex("于\\s*(?<time>\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})\\s+发布在"));
        trea.add(new TimeRegexEntity().setId(24).setSample("于 2015-11-20 12:00:43 发布在").setMark("time").setSort(4).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:05:40.0")).setResult(Timestamp.valueOf("2015-11-20 12:00:43.0")).setRegex("于\\s*(?<time>\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})\\s+发布在"));
        trea.add(new TimeRegexEntity().setId(3).setSample("发表于:10秒前(小时|天|秒|分钟)").setMark("time").setSort(3).setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 16:49:32.0")).setResult(Timestamp.valueOf("2015-11-20 16:49:22.0")).setRegex("发表于\\s*(?<time>.*?[前])"));
        trea.add(new TimeRegexEntity().setId(12).setSample("15年11月20日 11时20分").setMark("time").setSort(3).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 16:59:12.0")).setResult(Timestamp.valueOf("2015-11-20 11:20:00.0")).setRegex("(?<time>\\d{2,4}年\\d{1,2}月\\d{1,2}日\\s+\\d{1,2}时\\d{1,2}分)"));
        trea.add(new TimeRegexEntity().setId(13).setSample("15-11-20 11时20分20秒").setMark("time").setSort(3).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 16:59:54.0")).setResult(Timestamp.valueOf("2015-11-20 11:20:20.0")).setRegex("(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s*\\d{1,2}时\\d{1,2}分\\d{1,2}秒)"));
        trea.add(new TimeRegexEntity().setId(14).setSample("15-11-20 11时20分").setMark("time").setSort(3).setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 17:04:01.0")).setResult(Timestamp.valueOf("2015-11-20 11:20:00.0")).setRegex("(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s*\\d{1,2}时\\d{1,2}分)"));
        trea.add(new TimeRegexEntity().setId(20).setSample("发表于: 昨天 22:20").setMark("time").setSort(3).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:04:06.0")).setResult(Timestamp.valueOf("2015-11-19 22:20:00.0")).setRegex("发表于\\s*:\\s*(?<time>.*?\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(21).setSample("发表于： 昨天 22:20").setMark("time").setSort(3).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:04:31.0")).setResult(Timestamp.valueOf("2015-11-19 22:20:00.0")).setRegex("发表于\\s*：\\s*(?<time>.*?\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(22).setSample("发表于 昨天 22:20").setMark("time").setSort(3).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:04:51.0")).setResult(Timestamp.valueOf("2015-11-19 22:20:00.0")).setRegex("发表于\\s*(?<time>.*?\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(9).setSample("發表於 24-3-2013 13:01:01").setMark("time").setSort(2).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 16:56:13.0")).setResult(Timestamp.valueOf("2013-03-24 13:01:01.0")).setRegex("發表於\\s+(?<time>\\d{1,2}-\\d{1,2}-\\d{2,4}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(10).setSample("發表於 24-03-2013 13:01").setMark("time").setSort(2).setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 17:02:56.0")).setResult(Timestamp.valueOf("2013-03-24 13:01:00.0")).setRegex("發表於\\s+(?<time>\\d{1,2}-\\d{1,2}-\\d{2,4}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(25).setSample("回复 1楼 2015-11-20 11:26:26").setMark("time").setSort(2).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:06:01.0")).setResult(Timestamp.valueOf("2015-11-20 11:26:26.0")).setRegex("回复\\s*1楼\\s*(?<time>\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(26).setSample("回复 1楼 2015-11-20 11:26").setMark("time").setSort(2).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:06:32.0")).setResult(Timestamp.valueOf("2015-11-20 11:26:00.0")).setRegex("回复\\s*1楼\\s*(?<time>\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(2).setSample("2015-11-2 11时").setMark("time").setSort(0).setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 19:41:01.0")).setResult(Timestamp.valueOf("2015-11-02 00:00:00.0")).setRegex("(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s*\\d{1,2}时)"));
        trea.add(new TimeRegexEntity().setId(5).setSample("2015/11/20 10:06").setMark("time").setSort(0).setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 19:40:27.0")).setResult(Timestamp.valueOf("2015-11-20 10:06:00.0")).setRegex("(?<time>\\d{2,4}/\\d{1,2}/\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(6).setSample("2015/11/20 10:06:10").setMark("time").setSort(0).setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 19:40:23.0")).setResult(Timestamp.valueOf("2015-11-20 10:06:10.0")).setRegex("(?<time>\\d{2,4}/\\d{1,2}/\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(32).setSample("2015-11-20 11:21:21").setMark("time").setSort(0).setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 17:23:28.0")).setResult(Timestamp.valueOf("2015-11-20 11:21:21.0")).setRegex("(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(33).setSample("2015-11-20 11:21").setMark("time").setSort(0).setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 17:23:23.0")).setResult(Timestamp.valueOf("2015-11-20 11:21:00.0")).setRegex("(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(43).setSample("2013年11月13日06:58 来源：").setMark("time").setSort(5).setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 22:06:10.0")).setResult(Timestamp.valueOf("2013-11-13 06:58:00.0")).setRegex("(?<time>\\d{2,4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}.*?来源)"));
        trea.add(new TimeRegexEntity().setId(30).setSample("2015年11月30日10:16 新华网").setMark("time").setSort(7).setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:12:32.0")).setResult(Timestamp.valueOf("2015-11-20 08:06:06.0")).setRegex("(?<time>\\d{2,4}年\\d{1,2}月\\d{1,2}日\\s*\\d{1,2}:\\d{1,2})"));
        // trea.add(new TimeRegexEntity().setId(2).setSample("2015-11-2").setMark("time").setSort(0).setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 19:41:01.0")).setResult(Timestamp.valueOf("2015-11-02 00:00:00.0")).setRegex("(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2})"));
        // 来源于：
        return this.getTimeRule(trea);
    }


    //@Before
    public void before() {
        String host = "192.168.5.202:6389";
        String[] redisConf = host.split(":");
        RedisPlugin rp = new RedisPlugin(Const.CACHE_NAME,
                redisConf[0], Integer.valueOf(redisConf[1]));
        if (rp.start()) {
            log.info("连接 Redis: {} 成功, CacheName: {}", host, Const.CACHE_NAME);
        } else {
            log.error("连接 Redis: {} 失败, 请检查配置后重试.", host);
        }
    }



//    @Test
//    public void testParseFromRegex() {
//
//        TimeRuleEntity tre = TimeRegexModel.dao.getTimeRule();
//        String url = "http://blog.163.com/gwyexam%40126/blog/static/16291549620123132920648/";
//        HttpEntity he = HttpKit.get(url);
//        String html = he.getHtml();
//        String content = Jsoup.parse(html).text();
//        Long ts = TimeParse.parse(url, content, tre);
//        System.out.println(ts);
//    }



    private Long formatTime(String timeStr) {
        Nldp nldp = new Nldp(timeStr);
        Date date = nldp.extractDate();
        if (!isAvailableTime(date)) return 0L;
        return date.getTime();
    }

    private Boolean isAvailableTime(Date date) {
        return !(date.getTime() >= System.currentTimeMillis());
    }

    private Long parseFromRegex(String content, TimeRuleEntity tre) {
        if (tre==null) return 0L;
        try {
            Matcher m = tre.getRegex().matcher(content);
            if (!m.find()) return 0L;
            for (Integer i=0; i<tre.getCount(); i++) {
                String tst = m.group(tre.getMark() + i);
                if (StrKit.isBlank(tst)) continue;
                return formatTime(tst);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0L;
    }

    private String encodeDateStr(String content) {
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

    private String majorContentStr(String str) {
        String ns = str.replaceAll("\\s+", "");
        return ns;
    }


    private String filterNotTime(String content) throws UnsupportedEncodingException {
        if (StrKit.isBlank(content)) return content;
        content = content.replaceAll("[a-zA-Z]", "");
        content = encodeDateStr(content);
        content = content.replaceAll("[\uFF10-\uFF19\uFF21-\uFF3A\uFF41-\uFF5A\u4E00-\u9FA5，。？“”、！\\?!～【】,・()*|»›（）\\[\\]><《》…■\"#\\{}┊]+", "");
        content = UnicodeKit.toStr(content);
        // 发布在|发表|发布|时间|来源|回复|年|月|日|時|时|分|秒|前|于|於|樓|楼
        // |\-{2,}|―{2,}|\s+
        //
        content = content.replaceAll("/{2,}|\\.{2,}|[　]{2,}|:{2,}|\\-{2,}|―{2,}|：{2,}|；{2,}|：{2,}|年{2,}|月{2,}|日{2,}|时{2,}|分{2,}|秒{2,}|于{2,}|前{2,}|楼{2,}|[回复\\s+]{2,}|\\n|\\s{2,}|\\d{5,}|:\\d{3,}|：\\d{3,}", "");
        return content;
    }


    @Test
    public void testDelChinese() throws UnsupportedEncodingException {
        String url = "http://news.ifeng.com/gundong/detail_2014_02/28/34289026_0.shtml";
        url = "http://www.wuhunews.cn/whnews/200707/62349.html";
        url = "http://www.ahrtv.cn/news/system/2015/05/12/003531077.shtml";
        url = "http://bbs.csdn.net/topics/90304855";
        url = "http://linvar.iteye.com/blog/1222941";
        url = "http://www.zuojj.com/archives/1074.html";
        url = "http://www.easytd.com/jingyanjiaoliu/d_14110916126.html";
        url = "http://news.sina.com.cn/m/wl/2015-11-30/doc-ifxmaznc5783264.shtml";
        HttpEntity he = HttpKit.get(url, null, null, null, null, HttpConst.DEF_TIMEOUT, HttpConst.DEF_SOTIMEOUT, true);
        if (StrKit.isBlank(he.getHtml())) return;

        String input = Jsoup.parse(he.getHtml()).text();

        Long l_1 = System.currentTimeMillis();
        Long ts = this.parseFromRegex(input, this.genTre());
        Long l_2 = System.currentTimeMillis();
        log.debug("extra time first: {}, use time {} ms", new Timestamp(ts), l_2-l_1);

        Long l3 = System.currentTimeMillis();
        String rep = this.filterNotTime(input);
        Long ts2 = this.parseFromRegex(rep, this.genTre());
        Long l4 = System.currentTimeMillis();
        log.debug("After encode content: {}", UnicodeKit.toStr(rep));
        log.debug("Before encode length: {}", input.length());
        log.debug("After encode length: {}", rep.length());
        log.debug("extra Time second: {}, use time {} ms", new Timestamp(ts2), l4-l3);
    }


    @Test
    public void ttd2() {
        Long ts = this.formatTime("2015-05-1207:12来源:");
        System.out.println(new Timestamp(ts));
    }


}