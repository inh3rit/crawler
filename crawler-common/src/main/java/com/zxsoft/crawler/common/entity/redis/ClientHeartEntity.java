package com.zxsoft.crawler.common.entity.redis;

import com.jfinal.plugin.activerecord.Record;

/**
 * Created by cox on 2015/11/24.
 */
public class ClientHeartEntity extends Record {


    public Integer getCli() {
        return super.getInt("cli");
    }

    public ClientHeartEntity setCli(Integer cli) {
        super.set("cli", cli);
        return this;
    }

    public String getIp() {
        return super.getStr("ip");
    }

    public ClientHeartEntity setIp(String ip) {
        super.set("ip", ip);
        return this;
    }

    public Long getTs() {
        return super.get("ts");
    }

    public ClientHeartEntity setTs(Long ts) {
        super.set("ts", ts);
        return this;
    }

    public Integer getMaxPerform() {
        return super.getInt("max-perform");
    }

    public ClientHeartEntity setMaxPerform(Integer maxPerform) {
        super.set("max-perform", maxPerform);
        return this;
    }

}
