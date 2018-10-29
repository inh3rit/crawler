package com.zxsoft.crawler.common.entity.redis;

import com.jfinal.plugin.activerecord.Record;

/**
 * Created by cox on 2015/11/24.
 */
public class RunJobEntity extends Record {

    public Integer getCli() {
        return super.getInt("cli");
    }

    public RunJobEntity setCli(Integer cli) {
        super.set("cli", cli);
        return this;
    }

    public String getIp() {
        return super.getStr("ip");
    }

    public RunJobEntity setIp(String ip) {
        super.set("ip", ip);
        return this;
    }

    public Long getTs() {
        return super.get("ts");
    }

    public RunJobEntity setTs(Long ts) {
        super.set("ts", ts);
        return this;
    }

    public String getJob() {
        return super.getStr("job");
    }

    public RunJobEntity setJob(String job) {
        super.set("job", job);
        return this;
    }

//    public Integer getTotal() {
//        return super.getInt("total");
//    }
//
//    public RunJobEntity setTotal(Integer total) {
//        super.set("total", total);
//        return this;
//    }


}
