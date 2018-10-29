/**
 * redis相关实体包
 * @author xiawenchao
 */
package com.zxsoft.crawler.common.entity.redis;

import com.jfinal.plugin.activerecord.Record;

/**
 * redis删除任务的实体类
 * @author xiawenchao
 * @since 3.4
 */
public class DelJobEntity extends Record {

    public Integer getCli() {
        return super.getInt("cli");
    }

    public DelJobEntity setCli(Integer cli) {
        super.set("cli", cli);
        return this;
    }

    public String getIp() {
        return super.getStr("ip");
    }

    public DelJobEntity setIp(String ip) {
        super.set("ip", ip);
        return this;
    }

    public Long getTs() {
        return super.get("ts");
    }

    public DelJobEntity setTs(Long ts) {
        super.set("ts", ts);
        return this;
    }

    public String getJob() {
        return super.getStr("job");
    }

    public DelJobEntity setJob(String job) {
        super.set("job", job);
        return this;
    }

    public Integer getStat() {
        return super.getInt("stat");
    }

    public DelJobEntity setStat(Integer stat) {
        super.set("stat", stat);
        return this;
    }
}
