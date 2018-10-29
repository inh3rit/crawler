package com.zxsoft.crawler.common.entity.redis;

import com.jfinal.plugin.activerecord.Record;

/**
 * Created by cox on 2015/11/24.
 */
public class ClientInfoEntity extends Record {

    public Integer getCli() {
        return super.getInt("cli");
    }

    public ClientInfoEntity setCli(Integer cli) {
        super.set("cli", cli);
        return this;
    }

    public String getIp() {
        return super.getStr("ip");
    }

    public ClientInfoEntity setIp(String ip) {
        super.set("ip", ip);
        return this;
    }

    public Long getTs() {
        return super.get("ts");
    }

    public ClientInfoEntity setTs(Long ts) {
        super.set("ts", ts);
        return this;
    }

//    public Long getJob() {
//        return super.getLong("job");
//    }
//
//    public RunJobEntity setJob(Long job) {
//        super.set("job", job);
//        return this;
//    }

    public Integer getTotal() {
        return super.getInt("total");
    }

    public ClientInfoEntity setTotal(Integer total) {
        super.set("total", total);
        return this;
    }


}
