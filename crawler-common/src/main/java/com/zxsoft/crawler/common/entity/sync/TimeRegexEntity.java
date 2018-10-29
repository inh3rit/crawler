package com.zxsoft.crawler.common.entity.sync;

import com.jfinal.plugin.activerecord.Record;

import java.sql.Timestamp;

/**
 * Created by cox on 2015/11/20.
 */
public class TimeRegexEntity extends Record {

    public Integer getId() {
        return super.getInt("id");
    }

    public TimeRegexEntity setId(Integer id) {
        super.set("id", id);
        return this;
    }

    public String getSample() {
        return super.getStr("sample");
    }

    public TimeRegexEntity setSample(String sample) {
        super.set("sample", sample);
        return this;
    }

    public String getRegex() {
        return super.get("regex");
    }

    public TimeRegexEntity setRegex(String p) {
        super.set("regex", p);
        return this;
    }

    public String getMark() {
        return super.getStr("mark");
    }

    public TimeRegexEntity setMark(String mark) {
        super.set("mark", mark);
        return this;
    }

    public Integer getSort(Integer sort) {
        return super.getInt("sort");
    }

    public TimeRegexEntity setSort(Integer sort) {
        super.set("sort", sort);
        return this;
    }

    public String getUsr() {
        return super.getStr("usr");
    }

    public TimeRegexEntity setUsr(String usr) {
        super.set("usr", usr);
        return this;
    }

    public Timestamp getMtime() {
        return super.getTimestamp("mtime");
    }

    public TimeRegexEntity setMtime(Timestamp ts) {
        super.set("mtime", ts);
        return this;
    }

    public Timestamp getResult() {
        return super.getTimestamp("result");
    }

    public TimeRegexEntity setResult(Timestamp ts) {
        super.set("result", ts);
        return this;
    }

}
