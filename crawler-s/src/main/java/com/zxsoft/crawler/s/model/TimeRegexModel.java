package com.zxsoft.crawler.s.model;

import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.sync.TimeRegexEntity;
import com.zxsoft.crawler.common.entity.time.TimeRuleEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.type.SyncTable;
import com.zxsoft.crawler.s.kit.JsonKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by cox on 2015/11/20.
 */
public class TimeRegexModel {

    private static final Logger log = LoggerFactory.getLogger(TimeRegexModel.class);
    public static final TimeRegexModel dao = new TimeRegexModel(Const.CACHE_NAME);
    public static Cache redis;

    public TimeRegexModel(String cacheName) {
        TimeRegexModel.redis = Redis.use(cacheName);
    }

    public List<TimeRegexEntity> getAllTimeRegex() {
        try {
            Set<String> set = redis.zrevrange(SyncTable.TIMEREG.getName(), 0, -1);
            List<TimeRegexEntity> ltre = new ArrayList<TimeRegexEntity>();
            for (String s : set) {
                Record r = JsonKit.fromJson(s, Record.class);
                TimeRegexEntity te = new TimeRegexEntity();
                for (String key : r.getColumnNames())
                    te.set(key, r.get(key));
                ltre.add(te);
            }
            set = null;
            return ltre;
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public TimeRuleEntity getTimeRule(List<TimeRegexEntity> tres) {
        if (CollectionKit.isEmpty(tres)) return null;
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

    public TimeRuleEntity getTimeRule() {
        return this.getTimeRule(this.getAllTimeRegex());
    }

}
