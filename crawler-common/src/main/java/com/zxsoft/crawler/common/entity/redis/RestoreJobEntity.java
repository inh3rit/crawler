package com.zxsoft.crawler.common.entity.redis;

import com.jfinal.plugin.activerecord.Record;

/**
 * Created by cox on 2015/11/30.
 */
public class RestoreJobEntity extends Record {

    public Integer getCli() {
        return super.getInt("cli");
    }

    public RestoreJobEntity setCli(Integer cli) {
        super.set("cli", cli);
        return this;
    }

    public String getIp() {
        return super.getStr("ip");
    }

    public RestoreJobEntity setIp(String ip) {
        super.set("ip", ip);
        return this;
    }

    public String getJob() {
        return super.getStr("job");
    }

    public RestoreJobEntity setJob(String job) {
        super.set("job", job);
        return this;
    }

    public Long getTs() {
        return super.getLong("ts");
    }

    public RestoreJobEntity setTs(Long ts) {
        super.set("ts", ts);
        return this;
    }

}
