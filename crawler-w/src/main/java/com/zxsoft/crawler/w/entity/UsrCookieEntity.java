package com.zxsoft.crawler.w.entity;

import com.jfinal.plugin.activerecord.Record;

/**
 * Created by cox on 2015/12/15.
 */
public class UsrCookieEntity extends Record {

    public String getId() {
        return super.getStr("id");
    }

    public UsrCookieEntity setId(String id) {
        super.set("id", id);
        return this;
    }

    public String getName() {
        return super.getStr("name");
    }

    public UsrCookieEntity setName(String name) {
        super.set("name", name);
        return this;
    }

//    public String getEmail() {
//        return super.getStr("email");
//    }
//
//    public UsrCookieEntity setEmail(String email) {
//        super.set("email", email);
//        return this;
//    }

    public Long getSignTime() {
        return super.getLong("signTime");
    }

    public UsrCookieEntity setSignTime(Long expireTime) {
        super.set("signTime", expireTime);
        return this;
    }


}
