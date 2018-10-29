package com.zxsoft.crawler.common.entity.parse;

import com.jfinal.plugin.activerecord.Record;

/**
 * Created by cox on 2015/9/7.
 */
public class CrawlerEntity extends Record {


    public Integer getStat() {
        return this.getInt("stat");
    }

    public CrawlerEntity setStat(Integer stat) {
        this.set("stat", stat);
        return this;
    }

    public String getMsg() {
        return this.getStr("msg");
    }

    public CrawlerEntity setMsg(String msg) {
        this.set("msg", msg);
        return this;
    }

    public String getExceptionMsg() {
        return this.getStr("exception_msg");
    }

    public CrawlerEntity setExceptionMsg(String emsg) {
        this.set("exception_msg", emsg);
        return this;
    }

}
