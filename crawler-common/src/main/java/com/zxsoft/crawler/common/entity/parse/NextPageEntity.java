package com.zxsoft.crawler.common.entity.parse;

import com.jfinal.plugin.activerecord.Record;

import java.util.List;
import java.util.Map;

/**
 * Created by cox on 2015/11/27.
 */
public class NextPageEntity extends Record {


    public String getHtml() {
        return super.getStr("html");
    }

    public NextPageEntity setHtml(String html) {
        super.set("html", html);
        return this;
    }

    public String getUrl() {
        return super.getStr("url");
    }

    public NextPageEntity setUrl(String url) {
        super.set("url", url);
        return this;
    }

    public Map<String, List<String>> getHeader() {
        return super.get("header");
    }

    public NextPageEntity setHeader(Map<String, List<String>> header) {
        super.set("header", header);
        return this;
    }

}
