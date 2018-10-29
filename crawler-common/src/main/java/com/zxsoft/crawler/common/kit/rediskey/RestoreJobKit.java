package com.zxsoft.crawler.common.kit.rediskey;

import com.zxsoft.crawler.common.entity.redis.RestoreJobEntity;

/**
 * Created by cox on 2015/11/30.
 */
public class RestoreJobKit {


    public static RestoreJobEntity resolve(String str) {
        RestoreJobEntity rje = new RestoreJobEntity();
        String[] cs = str.split(":");
        rje.setCli(Integer.valueOf(cs[0]))
                .setIp(cs[1])
                .setJob(cs[2])
                .setTs(Long.valueOf(cs[3]));
        return rje;
    }


    public static String serialize(RestoreJobEntity rje) {
        StringBuilder sb = new StringBuilder();
        sb.append(rje.getCli())
                .append(":").append(rje.getIp())
                .append(":").append(rje.getJob())
                .append(":").append(rje.getTs());
        return sb.toString();
    }

}
