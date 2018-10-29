package com.zxsoft.crawler.p.ctrl;

import com.jfinal.kit.StrKit;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.redis.ListRuleEntity;
import com.zxsoft.crawler.common.entity.sync.BlacklistEntity;
import com.zxsoft.crawler.common.entity.sync.BlanklistEntity;
import com.zxsoft.crawler.common.entity.sync.TimeRegexEntity;
import com.zxsoft.crawler.common.entity.time.TimeRuleEntity;
import com.zxsoft.crawler.common.kit.DNSCache;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.p.occupancy.JDBCUtils;
import com.zxsoft.crawler.p.test.JDBCUtil;
import org.inh3rit.httphelper.entity.ProxyEntity;
import org.inh3rit.nldp.Nldp;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkSearchParseCtrlTest {

    private static Logger log = LoggerFactory.getLogger(NetworkSearchParseCtrlTest.class);

    private TimeRuleEntity getTimeRule(List<TimeRegexEntity> tres) {
        StringBuilder sb = new StringBuilder();
        for (Integer i = 0; i < tres.size(); i++) {
            String regexStr = tres.get(i).getRegex();
            regexStr = regexStr.replace("?<time>", "?<time" + i + ">");
            sb.append("(").append(regexStr).append(i + 1 == tres.size() ? ")" : ")|");
        }
        TimeRuleEntity tre = new TimeRuleEntity();
        tre.setRegex(Pattern.compile(sb.toString())).setCount(tres.size()).setMark("time");
        return tre;
    }

//    private TimeRuleEntity genTre() {
    private List<TimeRegexEntity> genTre() {
        List<TimeRegexEntity> trea = new ArrayList<TimeRegexEntity>();
        trea.add(new TimeRegexEntity().setId(27).setSample("2015-11-20 11:08:49 来源").setMark("time").setSort(9)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:07:07.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:08:49.0"))
                .setRegex("[\\s\\S]*?(?<time>\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})\\s+来源"));
        trea.add(new TimeRegexEntity().setId(34).setSample("发表于: 15-11-20 11:20").setMark("time").setSort(9)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:14:52.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:20:00.0"))
                .setRegex("发表于\\s*:\\s*(?<time>\\d{1,2}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(35).setSample("发表于: 15-11-20 11:20:2").setMark("time").setSort(9)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:15:10.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:20:02.0"))
                .setRegex("发表于\\s*:\\s*(?<time>\\d{1,2}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(1).setSample("时间：2015-11-20 10:40:20").setMark("time").setSort(8)
                .setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 16:49:12.0"))
                .setResult(Timestamp.valueOf("2015-11-20 10:40:20.0"))
                .setRegex("时间\\s*：\\s*(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(28).setSample("于 2015-11-20 11:28:40 发表").setMark("time").setSort(8)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:11:56.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:28:40.0"))
                .setRegex("于\\s+(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})\\s+发表"));
        trea.add(new TimeRegexEntity().setId(29).setSample("于 2015-11-20 11:19 发表").setMark("time").setSort(8)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:12:15.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:19:00.0"))
                .setRegex("于\\s+(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})\\s+发表"));
        trea.add(new TimeRegexEntity().setId(38).setSample("时间:2015-11-20 10:49").setMark("time").setSort(8)
                .setUsr("4028a0c74f4a328c014f4a3ef94c0001").setMtime(Timestamp.valueOf("2015-11-20 17:34:35.0"))
                .setResult(Timestamp.valueOf("2015-11-20 10:49:00.0"))
                .setRegex("时间\\s*:\\s*(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(40).setSample("发表于： 2015-11-20 10:31").setMark("time").setSort(8)
                .setUsr("4028a0c74f4a328c014f4a3ef94c0001").setMtime(Timestamp.valueOf("2015-11-20 17:34:26.0"))
                .setResult(Timestamp.valueOf("2015-11-20 10:31:00.0"))
                .setRegex("发表于\\s*(?<time>.*?\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(42).setSample("发表于： 15-11-20 11:20").setMark("time").setSort(8)
                .setUsr("4028a0c74f4a328c014f4a3ef94c0001").setMtime(Timestamp.valueOf("2015-11-20 17:35:06.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:20:00.0"))
                .setRegex("发表于\\s*(?<time>.*?\\d{1,2}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(30).setSample("2015年11月20日 08:06:06").setMark("time").setSort(7)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:12:32.0"))
                .setResult(Timestamp.valueOf("2015-11-20 08:06:06.0"))
                .setRegex("(?<time>\\d{2,4}年\\d{1,2}月\\d{1,2}日\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(31).setSample("2015年11月20日 08:06").setMark("time").setSort(7)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:13:04.0"))
                .setResult(Timestamp.valueOf("2015-11-20 08:06:00.0"))
                .setRegex("(?<time>\\d{2,4}年\\d{1,2}月\\d{1,2}日\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(4).setSample("发布时间： 15-11-20 09:23:23").setMark("time").setSort(6)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 16:54:01.0"))
                .setResult(Timestamp.valueOf("2015-11-20 09:23:23.0"))
                .setRegex("[\\s\\S]*?发布时间(?<time>.*?\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(7).setSample("发布时间： 15-11-20 09:23").setMark("time").setSort(6)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 16:55:28.0"))
                .setResult(Timestamp.valueOf("2015-11-20 09:23:00.0"))
                .setRegex("[\\s\\S]*?发布时间(?<time>.*?\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(15).setSample("2015年11月20日 11时20分20秒").setMark("time").setSort(6)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:00:53.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:20:20.0"))
                .setRegex("(?<time>\\d{4}年\\d{1,2}月\\d{1,2}日\\s+\\d{1,2}时\\d{1,2}分\\d{1,2}秒)"));
        trea.add(new TimeRegexEntity().setId(16).setSample("2015年11月20日 11时20分").setMark("time").setSort(6)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:01:17.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:20:00.0"))
                .setRegex("(?<time>\\d{4}年\\d{1,2}月\\d{1,2}日\\s+\\d{1,2}时\\d{1,2}分)"));
        trea.add(new TimeRegexEntity().setId(17).setSample("2015-11-20 11时20分20秒").setMark("time").setSort(6)
                .setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 19:48:21.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:20:20.0"))
                .setRegex("(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s*\\d{1,2}时\\s*\\d{1,2}分\\s*\\d{1,2}秒)"));
        trea.add(new TimeRegexEntity().setId(18).setSample("2015-11-20 11时20分").setMark("time").setSort(6)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:02:16.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:20:00.0"))
                .setRegex("(?<time>\\d{4}-\\d{1,2}-\\d{1,2}\\s*\\d{1,2}时\\s*\\d{1,2}分)"));
        trea.add(new TimeRegexEntity().setId(19).setSample("发表于: 3天前").setMark("time").setSort(5)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:02:42.0"))
                .setResult(Timestamp.valueOf("2015-11-17 00:00:00.0")).setRegex("发表于(?<time>.*?前)"));
        trea.add(new TimeRegexEntity().setId(36).setSample("时间：2015-11-20 10:49").setMark("time").setSort(5)
                .setUsr("4028a0c74f4a328c014f4a3ef94c0001").setMtime(Timestamp.valueOf("2015-11-20 17:27:32.0"))
                .setResult(Timestamp.valueOf("2015-11-20 10:49:00.0"))
                .setRegex("时间\\s*：\\s*(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(37).setSample("时间:2015-11-20 10:49:20").setMark("time").setSort(5)
                .setUsr("4028a0c74f4a328c014f4a3ef94c0001").setMtime(Timestamp.valueOf("2015-11-20 17:31:46.0"))
                .setResult(Timestamp.valueOf("2015-11-20 10:49:20.0"))
                .setRegex("时间\\s*:\\s*(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(39).setSample("发表于： 2015-11-20 10:44:49").setMark("time").setSort(5)
                .setUsr("4028a0c74f4a328c014f4a3ef94c0001").setMtime(Timestamp.valueOf("2015-11-20 17:33:09.0"))
                .setResult(Timestamp.valueOf("2015-11-20 10:44:49.0"))
                .setRegex("发表于\\s*(?<time>.*?\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(41).setSample("发表于 15-11-20 11:20:23").setMark("time").setSort(5)
                .setUsr("4028a0c74f4a328c014f4a3ef94c0001").setMtime(Timestamp.valueOf("2015-11-20 17:34:17.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:20:23.0"))
                .setRegex("发表于\\s*(?<time>.*?\\d{1,2}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(8).setSample("【发布日期：2015-11-20】").setMark("time").setSort(4)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 16:55:48.0"))
                .setResult(Timestamp.valueOf("2015-11-20 00:00:00.0"))
                .setRegex("【发布日期(?<time>.*?\\d{2,4}-\\d{1,2}-\\d{1,2})】"));
        trea.add(new TimeRegexEntity().setId(11).setSample("15年11月20日 11时20分20秒").setMark("time").setSort(4)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 16:58:17.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:20:20.0"))
                .setRegex("(?<time>\\d{2,4}年\\d{1,2}月\\d{1,2}日\\s+\\d{1,2}时\\d{1,2}分\\d{1,2}秒)"));
        trea.add(new TimeRegexEntity().setId(23).setSample("于 2015-11-20 12:00 发布在").setMark("time").setSort(4)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:05:22.0"))
                .setResult(Timestamp.valueOf("2015-11-20 12:00:00.0"))
                .setRegex("于\\s*(?<time>\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})\\s+发布在"));
        trea.add(new TimeRegexEntity().setId(24).setSample("于 2015-11-20 12:00:43 发布在").setMark("time").setSort(4)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:05:40.0"))
                .setResult(Timestamp.valueOf("2015-11-20 12:00:43.0"))
                .setRegex("于\\s*(?<time>\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})\\s+发布在"));
        trea.add(new TimeRegexEntity().setId(3).setSample("发表于:10秒前(小时|天|秒|分钟)").setMark("time").setSort(3)
                .setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 16:49:32.0"))
                .setResult(Timestamp.valueOf("2015-11-20 16:49:22.0")).setRegex("发表于\\s*(?<time>.*?[前])"));
        trea.add(new TimeRegexEntity().setId(12).setSample("15年11月20日 11时20分").setMark("time").setSort(3)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 16:59:12.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:20:00.0"))
                .setRegex("(?<time>\\d{2,4}年\\d{1,2}月\\d{1,2}日\\s+\\d{1,2}时\\d{1,2}分)"));
        trea.add(new TimeRegexEntity().setId(13).setSample("15-11-20 11时20分20秒").setMark("time").setSort(3)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 16:59:54.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:20:20.0"))
                .setRegex("(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s*\\d{1,2}时\\d{1,2}分\\d{1,2}秒)"));
        trea.add(new TimeRegexEntity().setId(14).setSample("15-11-20 11时20分").setMark("time").setSort(3)
                .setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 17:04:01.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:20:00.0"))
                .setRegex("(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s*\\d{1,2}时\\d{1,2}分)"));
        trea.add(new TimeRegexEntity().setId(20).setSample("发表于: 昨天 22:20").setMark("time").setSort(3)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:04:06.0"))
                .setResult(Timestamp.valueOf("2015-11-19 22:20:00.0"))
                .setRegex("发表于\\s*:\\s*(?<time>.*?\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(21).setSample("发表于： 昨天 22:20").setMark("time").setSort(3)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:04:31.0"))
                .setResult(Timestamp.valueOf("2015-11-19 22:20:00.0"))
                .setRegex("发表于\\s*：\\s*(?<time>.*?\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(22).setSample("发表于 昨天 22:20").setMark("time").setSort(3)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:04:51.0"))
                .setResult(Timestamp.valueOf("2015-11-19 22:20:00.0"))
                .setRegex("发表于\\s*(?<time>.*?\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(9).setSample("發表於 24-3-2013 13:01:01").setMark("time").setSort(2)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 16:56:13.0"))
                .setResult(Timestamp.valueOf("2013-03-24 13:01:01.0"))
                .setRegex("發表於\\s+(?<time>\\d{1,2}-\\d{1,2}-\\d{2,4}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(10).setSample("發表於 24-03-2013 13:01").setMark("time").setSort(2)
                .setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 17:02:56.0"))
                .setResult(Timestamp.valueOf("2013-03-24 13:01:00.0"))
                .setRegex("發表於\\s+(?<time>\\d{1,2}-\\d{1,2}-\\d{2,4}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(25).setSample("回复 1楼 2015-11-20 11:26:26").setMark("time").setSort(2)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:06:01.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:26:26.0"))
                .setRegex("回复\\s*1楼\\s*(?<time>\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(26).setSample("回复 1楼 2015-11-20 11:26").setMark("time").setSort(2)
                .setUsr("4028a0c74f4a328c014f5e8f44750002").setMtime(Timestamp.valueOf("2015-11-20 17:06:32.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:26:00.0"))
                .setRegex("回复\\s*1楼\\s*(?<time>\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(2).setSample("2015-11-2 11时").setMark("time").setSort(0)
                .setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 19:41:01.0"))
                .setResult(Timestamp.valueOf("2015-11-02 00:00:00.0"))
                .setRegex("(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s*\\d{1,2}时)"));
        trea.add(new TimeRegexEntity().setId(5).setSample("2015/11/20 10:06").setMark("time").setSort(0)
                .setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 19:40:27.0"))
                .setResult(Timestamp.valueOf("2015-11-20 10:06:00.0"))
                .setRegex("(?<time>\\d{2,4}/\\d{1,2}/\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(6).setSample("2015/11/20 10:06:10").setMark("time").setSort(0)
                .setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 19:40:23.0"))
                .setResult(Timestamp.valueOf("2015-11-20 10:06:10.0"))
                .setRegex("(?<time>\\d{2,4}/\\d{1,2}/\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(32).setSample("2015-11-20 11:21:21").setMark("time").setSort(0)
                .setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 17:23:28.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:21:21.0"))
                .setRegex("(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(33).setSample("2015-11-20 11:21").setMark("time").setSort(0)
                .setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 17:23:23.0"))
                .setResult(Timestamp.valueOf("2015-11-20 11:21:00.0"))
                .setRegex("(?<time>\\d{2,4}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2})"));
        trea.add(new TimeRegexEntity().setId(43).setSample("2013年11月13日06:58 来源：").setMark("time").setSort(5)
                .setUsr("405532476501528576").setMtime(Timestamp.valueOf("2015-11-20 22:06:10.0"))
                .setResult(Timestamp.valueOf("2013-11-13 06:58:00.0"))
                .setRegex("(?<time>\\d{2,4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}.*?来源)"));
//        return this.getTimeRule(trea);
        return trea;
    }

//    /**
//     * 解析页面时间
//     *
//     * @param url        链接
//     * @param content    页面 html
//     * @param listPageTs 从列表页获取的时间
//     * @return
//     */
//    private Long extraTime(String url, String content, Long listPageTs, TimeRuleEntity tre) {
//        if (listPageTs <= 315504000000L)
//            listPageTs = 0L;
//        Long pts = TimeParse.parse(url, content, tre);
//        if (pts != 0L && listPageTs != 0L)
//            return pts;
//        if (pts != 0L)
//            return pts;
//        if (listPageTs != 0L)
//            return listPageTs;
//        return 0L;
//    }

    @Test
    public void parse() throws Exception {

        // 天涯
        // String url =
        // "http://m.tianya.cn/sch/sch.jsp?vu=28456309174&k=%s&t=2";
        // String listdom = "body > div.p", linedom = "p", urldom = "a";
        // String datedom = "", synopsisdom = "p.summary", updatedom = "",
        // authordom = "";
        // String sourceName = "天涯论坛", type = "天涯论坛搜索(手机版)_全网搜索";
        // Integer sourceId = 141, sectionId = 1180;
        // String keywordEncoding = "UTF-8", keyword = "暴雨";

        // 网易新闻
        // String url =
        // "http://news.yodao.com/search?q=%s&start=0&ue=utf8&s=&tl=&keyfrom=news.index.suggest";
        // String listdom = "ul#results", linedom = "li", urldom = "h3 a";
        // String datedom = "span.green.stat", synopsisdom = "", updatedom = "",
        // authordom = "";
        // String sourceName = "网易新闻", type = "网易新闻搜索";
        // Integer sourceId = 936, sectionId = 753;
        // String keywordEncoding = "UTF-8", keyword = "暴雨";

		/*
         * // 搜狐博客 (百度) String url =
		 * "http://www.baidu.com/s?wd=%s site:blog.sohu.com&ie=utf-8"; String
		 * listdom = "div#content_left", linedom = "div.result", urldom =
		 * "h3.t a"; String datedom = "", synopsisdom = "div.c-abstract",
		 * updatedom = "", authordom = ""; String sourceName = "搜狐博客", type =
		 * "搜狐博客搜索"; Integer sourceId = 217, sectionId = 1181; String
		 * keywordEncoding = "UTF-8", keyword = "鸠江区";
		 */

        // 百度搜索
        String url = "http://www.baidu.com/s?wd=%s&ie=utf-8";
        String listdom = "div#content_left", linedom = "div.c-container", urldom = "h3.t > a";
        String datedom = "", synopsisdom = "", updatedom = "", authordom = "";
        String sourceName = "百度搜索", type = "百度搜索";
        Integer sourceId = 10, sectionId = 51;
        String keywordEncoding = "UTF-8", keyword = "男子假扮粉丝约见女主播抢劫 万家资讯";

        // 搜报网 // 搜报网网页打开很慢 超时无法获取
        // String url =
        // "http://www.soubao.net/search/searchList.aspx?keyword=%s&startdate=1900-01-01&enddate=2200-12-31&timesel=custom";
        // String listdom = "div#srh_main", linedom = "ul.newList", urldom = "li
        // h2 a";
        // String datedom = "", synopsisdom = "", updatedom = "", authordom =
        // "";
        // String sourceName = "搜报网", type = "搜报网";
        // Integer sourceId = 1642, sectionId = 1137;
        // String keywordEncoding = "UTF-8", keyword = "暴雨";

        // 中国搜索
//        String url = "http://www.chinaso.com/search/pagesearch.htm?q=%s";
//        String listdom = "ol.seResult", linedom = "li.reItem", urldom = "h2 a";
//        String datedom = "", synopsisdom = "", updatedom = "", authordom = "";
//        String sourceName = "中国搜索", type = "中国搜索";
//        Integer sourceId = 926, sectionId = 1364;
//        String keywordEncoding = "UTF-8", keyword = "暴雨";

        // 新浪新闻
        // String url =
        // "http://search.sina.com.cn/?q=%s&range=all&c=news&sort=time";
        // String listdom = "div#wrap", linedom = "div.box-result", urldom = "h2
        // a";
        // String datedom = "", synopsisdom = "", updatedom = "", authordom =
        // "";
        // String sourceName = "新浪新闻", type = "新浪新闻搜索";
        // Integer sourceId = 888, sectionId = 755;
        // String keywordEncoding = "GBK", keyword = "暴雨";

        // 宜搜搜索
        // String url = "http://i.easou.com/s.m?wver=c&q=%s";
        // String listdom = "#result-wrap", linedom = "div[data-type]", urldom =
        // "a";
        // String datedom = "", synopsisdom = "", updatedom = "", authordom =
        // "";
        // String sourceName = "宜搜搜索", type = "宜搜搜索";
        // Integer sourceId = 125, sectionId = 752;
        // String keywordEncoding = "UTF-8", keyword = "test";

        // 必应
        // String url = "http://cn.bing.com/search?q=%s";
        // String listdom = "ol#b_results", linedom =
        // "[class~=(b_algo|b_ans|b_pag)]", urldom = "h2 a";
        // String datedom = "", synopsisdom = "", updatedom = "", authordom =
        // "";
        // String sourceName = "必应搜索", type = "必应搜索";
        // Integer sourceId = 126, sectionId = 466;
        // String keywordEncoding = "UTF-8", keyword = "暴雨";

        // 360搜索
//        String url = "https://www.so.com/s?a=index&ie=utf-8&shb=1&src=360sou_newhome&q=%s";
//        String listdom = "div#main", linedom = "li.res-list", urldom = "h3>a";
//        String datedom = "", synopsisdom = "", updatedom = "", authordom = "";
//        String sourceName = "360搜索", type = "360搜索";
//        Integer sourceId = 126, sectionId = 433;
//        String keywordEncoding = "UTF-8", keyword = "暴雨";

        // 搜狗搜索
//        String url = "http://www.sogou.com/web?query=%s";
//        String listdom = "div.results", linedom = "div[id~=^rb_*|class~=^vrwrap]", urldom = "h3>a";
//        String datedom = "", synopsisdom = "", updatedom = "", authordom = "";
//        String sourceName = "搜狗搜索", type = "搜狗搜索";
//        Integer sourceId = 126, sectionId = 433;
//        String keywordEncoding = "UTF-8", keyword = "六安 犯法";

        // 有道搜索
//        String url = "http://www.youdao.com/search?q=%s&ue=utf8&keyfrom=web.index";
//        String listdom = "ol#results", linedom = "li.res-list", urldom = "h3";
//        String datedom = "", synopsisdom = "", updatedom = "", authordom = "";
//        String sourceName = "有道搜索", type = "有道搜索";
//        Integer sourceId = 126, sectionId = 433;
//        String keywordEncoding = "UTF-8", keyword = "阜阳 绑架";

        ListRuleEntity lre = new ListRuleEntity();
        lre.setListdom(listdom).setLinedom(linedom).setUrldom(urldom).setDatedom(datedom).setSynopsisdom(synopsisdom)
                .setUpdatedom(updatedom).setAuthordom(authordom);
        JobEntity je = new JobEntity();
        je.setJobType(JobType.NETWORK_SEARCH).setUrl(url).setSource_name(sourceName).setSource_id(sourceId)
                .setSectionId(sectionId).setType(type).setListRule(lre).setWorkerId(0).setLocationCode(10000)
                .setProvince_code(10000).setCity_code(10000).setKeywordEncode(keywordEncoding).setJobId("0")
                .setIdentify_md5("iaceob").setCountry_code(1).setGoInto(true).setKeyword(keyword).setPlatform(6);
        String ip = DNSCache.getIp(url);
        if (StrKit.notBlank(ip)) {
            je.setIp(ip);
            je.setLocation("Test");
        }
        NetworkSearchParseCtrl nspc = new NetworkSearchParseCtrl();
        // nspc.parse(je, "192.168.25.254", 28129, "yproxyq", "zproxyx0#");
        String[] address = new String[]{"http://192.168.32.17:20000/sentiment/index"};
//        ProxyEntity pe = new ProxyEntity("192.168.25.254", 28129); // 省厅
        ProxyEntity pe = new ProxyEntity("36.7.67.33", 28129); // 省厅外网
//        ProxyEntity pe = new ProxyEntity("192.168.43.254", 28129); //北海
        pe.setAccount("yproxyq");
        pe.setPassword("zproxyx0#");
        je.setCookie("uid=CgqASVYIpMZeVl4FEVxLAg==");
        Map<String, Object> argMap = new HashMap<String, Object>();
        argMap.put("job", je);
        argMap.put("address", address);
        argMap.put("proxy", null);
        argMap.put("timeRule", this.genTre());
        argMap.put("blanks", getBlanklistFromDB());
//        argMap.put("blacks", getBlacklistEntity());
        argMap.put("blacks", new BlacklistEntity[]{});
        nspc.parse(argMap);
    }

    @Test
    public void testBlacklistReg() throws Exception {
        String reg = "(http|https)://news.ifeng.com/mainland/special/.*?";
        String url = "http://news.ifeng.com/mainland/special/fanfu/content-3/detail_2013_02/20/22284024_0.shtml";
        System.out.println(url.matches(reg));
    }

//    @Test
//    public void testParseTime() {
//        // HttpHelper helper = new HttpHelper();
//        String url = "http://www.zgfxnews.com/xw/content/2015-11/20/content_157982.htm"; // true
//        url = "http://www.zgfxnews.com/xw/content/2015-11/19/content_157928.htm"; // true
//        url = "http://www.blogjava.net/hgc-ghc/archive/2013/03/28/397084.html"; // true
//        url = "http://www.pocketdigi.com/20110712/383.html"; // true
//        url = "http://www.tutorialspoint.com/java/util/calendar_setfield1.htm"; // read
//        // timed
//        // out
//        url = "http://ah.sina.com.cn/news/hefei/2014-08-06/1350111740.html"; // true
//        url = "http://bbs.365jia.cn/thread-1279359-1-1.html"; // true
//        url = "http://www.ahxf.gov.cn/djgz/lzjs/260415.SHTML"; // true
//        url = "http://politics.people.com.cn/n/2013/1113/c1001-23521355.html"; // true
//        url = "http://www.eorder.net.cn/fanwen59220/"; // true
//        url = "http://tv.sohu.com/20131114/n390147871.shtml"; // true
//        url = "http://bbs.hefei.cc/thread-15563866-1-1.html"; // true
//        url = "http://ah.ifeng.com/news/detail_2015_10/18/4455997_0.shtml"; // true
//        url = "http://bbs.gd163.com.cn/ShowPost.asp?ThreadID=1011912"; // true
//        url = "http://www.jcfydb.com/nav.php?id=255297&page=1";
//
//        HttpEntity he = HttpKit.get(url);
//        String content = Jsoup.parse(he.getHtml()).text();
//        // System.out.println(content);
//
//        Long ts = this.extraTime(url, content, 0L, this.genTre());
//        Timestamp t = new Timestamp(ts);
//        System.out.println(t.toString());
//    }

    @Test
    public void ttd2() {
        String text = "12 七月 2012, 6:10 下午";
        try {
            Date date = null;
            Nldp nldp = new Nldp(text);
            date = nldp.extractDate();
            System.out.println(new Timestamp(date.getTime()).toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void ttd3() {
        String cnt = "被问崩溃 2013年11月13日06:58    来源：北京青";
        Pattern p = Pattern.compile("(?<time>\\d{2,4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}.*?来源)");
        Matcher m = p.matcher(cnt);
        while (m.find()) {
            System.out.println(m.group("time"));
        }
    }

    private BlanklistEntity[] getBlanklist() {
        String urls = FileUtil.read("/home/ubuntu/cache/_www.txt");
        String[] _urls = urls.split("\n");
        BlanklistEntity[] blankes = new BlanklistEntity[_urls.length];
        int i = 0;
        for (String url : _urls) {
            BlanklistEntity blanke = new BlanklistEntity();
            blanke.setDomain(url);
            blankes[i] = blanke;
            i++;
        }
        return blankes;
    }

    private BlanklistEntity[] getBlanklistFromDB() throws SQLException {
        List<BlanklistEntity> blanklistEntityList = new ArrayList<>();
        String sql = "select domain,type from blanklist";
        Connection conn = JDBCUtils.getConn();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            BlanklistEntity entity = new BlanklistEntity();
            entity.setDomain(rs.getString(1));
            entity.setType(rs.getInt(2));
            blanklistEntityList.add(entity);
        }

        return blanklistEntityList.toArray(new BlanklistEntity[blanklistEntityList.size()]);

    }

    private BlacklistEntity[] getBlacklistEntity() throws Exception {
        List<BlacklistEntity> blacklistEntities = new ArrayList<>();
        String sql = "select name,regex,summary,usr from blacklist";
        Connection conn = JDBCUtil.getConn();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            BlacklistEntity entity = new BlacklistEntity();
            entity.setName(rs.getString(1));
            entity.setRegex(rs.getString(2));
            entity.setSummary(rs.getString(3));
            blacklistEntities.add(entity);
        }
        return  blacklistEntities.toArray(new BlacklistEntity[blacklistEntities.size()]);

    }

    public static void main(String[] args) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(new Thread(new SeniorSearchThread()), 0L, 10L, TimeUnit.MINUTES);

    }

    private static ThreadPoolExecutor getThreadPoolExector(int threadsNum) {
        final ThreadPoolExecutor result = new ThreadPoolExecutor(threadsNum, threadsNum, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(threadsNum * 2), new ThreadPoolExecutor.CallerRunsPolicy());
        result.setThreadFactory(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    public void uncaughtException(Thread t, Throwable e) {
                        e.printStackTrace();
                        log.error("Thread exception: " + t.getName(), e);
                        result.shutdown();
                    }
                });
                return t;
            }
        });
        return result;
    }
}

class SeniorSearchThread implements Runnable {

    @Override
    public void run() {
        try {
            new NetworkSearchParseCtrlTest().parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}