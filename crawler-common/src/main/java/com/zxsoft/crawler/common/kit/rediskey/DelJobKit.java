package com.zxsoft.crawler.common.kit.rediskey;

import com.zxsoft.crawler.common.entity.redis.DelJobEntity;

/**
 * Created by cox on 2015/11/30.
 */
public class DelJobKit {

    public static DelJobEntity resolve(String str) {
        DelJobEntity dje = new DelJobEntity();
        String[] cs = str.split(":");
        dje.setCli(Integer.valueOf(cs[0]))
                .setIp(cs[1])
                .setJob(cs[2])
                .setStat(Integer.valueOf(cs[3]))
                .setTs(Long.valueOf(cs[4]));
        return dje;
    }

    public static String serialize(DelJobEntity dje) {
        StringBuilder sb = new StringBuilder();
        sb.append(dje.getCli())
                .append(":").append(dje.getIp())
                .append(":").append(dje.getJob())
                .append(":").append(dje.getStat())
                .append(":").append(dje.getTs());
        return sb.toString();
    }

}
