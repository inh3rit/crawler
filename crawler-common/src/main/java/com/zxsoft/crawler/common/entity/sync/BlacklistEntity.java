package com.zxsoft.crawler.common.entity.sync;

import com.jfinal.plugin.activerecord.Record;

import java.sql.Timestamp;

/**
 * Created by cox on 2015/11/17.
 */
public class BlacklistEntity extends Record {

    public BlacklistEntity setId(Integer id) {
        super.set("id", id);
        return this;
    }

    public Integer getId() {
        return super.getInt("id");
    }

    public BlacklistEntity setName(String name) {
        super.set("name", name);
        return this;
    }

    public String getName() {
        return super.get("name");
    }

    public BlacklistEntity setRegex(String regex) {
        super.set("regex", regex);
        return this;
    }

    public String getRegex() {
        return super.getStr("regex");
    }

    public BlacklistEntity setSummary(String summary) {
        super.set("summary", summary);
        return this;
    }

    public String getSummary() {
        return super.getStr("summary");
    }

    public BlacklistEntity setUsr(String usr) {
        super.set("usr", usr);
        return this;
    }

    public String getUsr() {
        return super.getStr("usr");
    }

    public BlacklistEntity setMtime(Timestamp ts) {
        super.set("mtime", ts);
        return this;
    }

    public Timestamp getMtime() {
        return super.getTimestamp("mtime");
    }


}
