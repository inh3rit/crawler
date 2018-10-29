package com.zxsoft.crawler.common.entity.redis;

import com.jfinal.plugin.activerecord.Record;

/**
 * Created by cox on 2015/9/6.
 */
public class DetailRuleEntity extends Record {



//    private  String host;
//    private String replyNum;
//    private String reviewNum;
//    private String forwardNum;
//    private String sources;
//    private Boolean fetchorder = true; // 抓取顺序，true从最后一页开始抓
//    private Boolean ajax = false; // 是否ajax请求
//
//    // 主帖模块
//    private  String master;
//    private String author;
//    private String date;
//    private String content;
//
//    // 回复
//    private String reply;
//    private String replyAuthor;
//    private String replyDate;
//    private String replyContent;
//
//    // 子回复
//    private String subReply;
//    private String subReplyAuthor;
//    private String subReplyDate;
//    private String subReplyContent;
//
//    /**
//     * 网页编码
//     */
//    private String encode;

    
    public String getSubReplyAuthor() {
        return this.getStr("subReplyAuthor");
    }

    public DetailRuleEntity setSubReplyAuthor(String subReplyAuthor) {
        this.set("subReplyAuthor", subReplyAuthor);
        return this;
    }

    public String getHost() {
        return this.getStr("host");
    }

    public DetailRuleEntity setHost(String host) {
        this.set("host", host);
        return this;
    }

    public String getReplyNum() {
        return this.getStr("replyNum");
    }

    public DetailRuleEntity setReplyNum(String replyNum) {
        this.set("replyNum", replyNum);
        return this;
    }

    public String getReviewNum() {
        return this.getStr("reviewNum");
    }

    public DetailRuleEntity setReviewNum(String reviewNum) {
        this.set("reviewNum", reviewNum);
        return this;
    }

    public String getForwardNum() {
        return this.getStr("forwardNum");
    }

    public DetailRuleEntity setForwardNum(String forwardNum) {
        this.set("forwardNum", forwardNum);
        return this;
    }

    public String getSources() {
        return this.getStr("sources");
    }

    public DetailRuleEntity setSources(String sources) {
        this.set("sources", sources);
        return this;
    }

    public Boolean getFetchorder() {
        return this.getBoolean("fetchorder");
    }

    public DetailRuleEntity setFetchorder(Boolean fetchorder) {
        this.set("fetchorder", fetchorder);
        return this;
    }

    public Boolean getAjax() {
        return this.getBoolean("ajax");
    }

    public DetailRuleEntity setAjax(Boolean ajax) {
        this.set("ajax", ajax);
        return this;
    }

    public String getMaster() {
        return this.getStr("master");
    }

    public DetailRuleEntity setMaster(String master) {
        this.set("master", master);
        return this;
    }

    public String getAuthor() {
        return this.getStr("author");
    }

    public DetailRuleEntity setAuthor(String author) {
        this.set("author", author);
        return this;
    }

    public String getDate() {
        return this.getStr("date");
    }

    public DetailRuleEntity setDate(String date) {
        this.set("date", date);
        return this;
    }

    public String getContent() {
        return this.getStr("content");
    }

    public DetailRuleEntity setContent(String content) {
        this.set("content", content);
        return this;
    }

    public String getReply() {
        return this.getStr("reply");
    }

    public DetailRuleEntity setReply(String reply) {
        this.set("reply", reply);
        return this;
    }

    public String getReplyAuthor() {
        return this.getStr("replyAuthor");
    }

    public DetailRuleEntity setReplyAuthor(String replyAuthor) {
        this.set("replyAuthor", replyAuthor);
        return this;
    }

    public String getReplyDate() {
        return this.getStr("replyDate");
    }

    public DetailRuleEntity setReplyDate(String replyDate) {
        this.set("replyDate", replyDate);
        return this;
    }

    public String getReplyContent() {
        return this.getStr("replyContent");
    }

    public DetailRuleEntity setReplyContent(String replyContent) {
        this.set("replyContent", replyContent);
        return this;
    }

    public String getSubReply() {
        return this.getStr("subReply");
    }

    public DetailRuleEntity setSubReply(String subReply) {
        this.set("subReply", subReply);
        return this;
    }

    public String getSubReplyDate() {
        return getStr("subReplyDate");
    }

    public DetailRuleEntity setSubReplyDate(String subReplyDate) {
        this.set("subReplyDate", subReplyDate);
        return this;
    }

    public String getSubReplyContent() {
        return getStr("subReplyContent");
    }

    public DetailRuleEntity setSubReplyContent(String subReplyContent) {
        this.set("subReplyContent", subReplyContent);
        return this;
    }

    public String getEncode() {
        return this.getStr("encode");
    }

    public DetailRuleEntity setEncode(String encode) {
        this.set("encode", encode);
        return this;
    }


    public DetailRuleEntity set(String key, Object value) {
        super.set(key, value);
        return this;
    }

    public <T> T get(String key) {
        return super.get(key);
    }
}
