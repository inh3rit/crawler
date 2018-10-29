package com.zxsoft.crawler.common.kit.rediskey;

import com.zxsoft.crawler.common.entity.redis.RunJobEntity;

/**
 * Created by cox on 2015/11/30.
 */
public class RunJobKit {

    public static RunJobEntity resolve(String runJobStr) {
        RunJobEntity rje = new RunJobEntity();
        String[] cs = runJobStr.split(":");
        rje.setCli(Integer.valueOf(cs[0]))
                .setIp(cs[1])
                .setJob(cs[2])
                .setTs(Long.valueOf(cs[3]));
        return rje;
    }

    public static String serialize(RunJobEntity rje) {
        StringBuilder sb = new StringBuilder();
        sb.append(rje.getCli())
                .append(":").append(rje.getIp())
                .append(":").append(rje.getJob())
                .append(":").append(rje.getTs());
        return sb.toString();
    }

}
