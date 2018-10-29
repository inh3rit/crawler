package com.zxsoft.crawler.common.entity.redis;

import com.jfinal.plugin.activerecord.Record;

/**
 * Created by cox on 2015/9/6.
 */
public class ListRuleEntity extends Record {

//    private Boolean ajax;
//    private String category;
//    private String listdom;
//    private String linedom;
//    private String urldom;
//    private String datedom;
//    private String updatedom;
//    private String synopsisdom;
//    private String authordom;


    public ListRuleEntity() {
        super();
    }

    public ListRuleEntity(String listdom, String linedom, String urldom, String authordom, String updatedom,
                     String datedom, String synopsisdom, Boolean ajax, String category) {
        this.setListdom(listdom).setLinedom(linedom).setUrldom(urldom)
                .setUrldom(authordom).setUpdatedom(updatedom).setDatedom(datedom)
                .setSynopsisdom(synopsisdom).setAjax(ajax).setCategory(category);
    }

    public String getSynopsisdom() {
        return this.getStr("synopsisdom");
    }

    public ListRuleEntity setSynopsisdom(String synopsisdom) {
        this.set("synopsisdom", synopsisdom);
        return this;
    }

    public Boolean getAjax() {
        return this.getBoolean("ajax");
    }

    public ListRuleEntity setAjax(Boolean ajax) {
        this.set("ajax", ajax);
        return this;
    }

    public String getCategory() {
        return this.getStr("category");
    }

    public ListRuleEntity setCategory(String category) {
        this.set("category", category);
        return this;
    }

    public String getListdom() {
        return this.getStr("listdom");
    }

    public ListRuleEntity setListdom(String listdom) {
        this.set("listdom", listdom);
        return this;
    }

    public String getLinedom() {
        return this.getStr("linedom");
    }

    public ListRuleEntity setLinedom(String linedom) {
        this.set("linedom", linedom);
        return this;
    }

    public String getUrldom() {
        return this.getStr("urldom");
    }

    public ListRuleEntity setUrldom(String urldom) {
        this.set("urldom", urldom);
        return this;
    }

    public String getDatedom() {
        return this.getStr("datedom");
    }

    public ListRuleEntity setDatedom(String datedom) {
        this.set("datedom", datedom);
        return this;
    }

    public String getUpdatedom() {
        return this.getStr("updatedom");
    }

    public ListRuleEntity setUpdatedom(String updatedom) {
        this.set("updatedom", updatedom);
        return this;
    }

    public String getAuthordom() {
        return this.getStr("authordom");
    }

    public ListRuleEntity setAuthordom(String authordom) {
        this.set("authordom", authordom);
        return this;
    }

    public ListRuleEntity set(String key, Object value) {
        super.set(key, value);
        return this;
    }

    public <T> T get(String key) {
        return super.get(key);
    }
}
