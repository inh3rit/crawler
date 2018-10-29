package com.zxsoft.crawler.common.entity.sync;

import com.jfinal.plugin.activerecord.Record;

import java.sql.Timestamp;

/**
 * Created by cox on 2015/11/17.
 */
public class TextBlacklistEntity extends Record {

    public TextBlacklistEntity setId(Integer id) {
        super.set("id", id);
        return this;
    }

    public Integer getId() {
        return super.getInt("id");
    }

    public TextBlacklistEntity setName(String name) {
        super.set("name", name);
        return this;
    }

    public String getName() {
        return super.get("name");
    }

    public TextBlacklistEntity setRegex(String regex) {
        super.set("regex", regex);
        return this;
    }

    public String getRegex() {
        return super.getStr("regex");
    }

    public TextBlacklistEntity setSummary(String summary) {
        super.set("summary", summary);
        return this;
    }

    public String getSummary() {
        return super.getStr("summary");
    }

    public TextBlacklistEntity setUsr(String usr) {
        super.set("usr", usr);
        return this;
    }

    public String getUsr() {
        return super.getStr("usr");
    }

    public TextBlacklistEntity setMtime(Timestamp ts) {
        super.set("mtime", ts);
        return this;
    }

    public Timestamp getMtime() {
        return super.getTimestamp("mtime");
    }

    public TextBlacklistEntity setType(String type) {
        super.set("type", type);
        return this;
    }

    public String getType() {
        return super.getStr("type");
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
