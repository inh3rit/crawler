package com.zxsoft.crawler.common.entity.time;

import com.jfinal.plugin.activerecord.Record;

import java.util.regex.Pattern;

/**
 * Created by cox on 2015/11/25.
 */
public class TimeRuleEntity extends Record {

    public Pattern getRegex() {
        return super.get("regex");
    }

    public TimeRuleEntity setRegex(Pattern p) {
        super.set("regex", p);
        return this;
    }

    public Integer getCount() {
        return super.getInt("count");
    }

    public TimeRuleEntity setCount(Integer count) {
        super.set("count", count);
        return this;
    }

    public String getMark() {
        return super.getStr("mark");
    }

    public TimeRuleEntity setMark(String mark) {
        super.set("mark", mark);
        return this;
    }
}
