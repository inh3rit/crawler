package com.zxsoft.crawler.common.kit.rediskey;

import com.zxsoft.crawler.common.entity.redis.ClientHeartEntity;

/**
 * Created by cox on 2015/12/3.
 */
public class ClientHeartKit {

    public static ClientHeartEntity resolve(String str) {
        String[] cs = str.split(":");
        ClientHeartEntity che = new ClientHeartEntity();
        che.setCli(Integer.valueOf(cs[0]))
                .setIp(cs[1])
                .setMaxPerform(Integer.valueOf(cs[2]))
                .setTs(Long.parseLong(cs[3]));
        return che;
    }


    public static String serialize(ClientHeartEntity che) {
        StringBuilder sb = new StringBuilder();
        sb.append(che.getCli())
                .append(":").append(che.getIp())
                .append(":").append(che.getMaxPerform())
                .append(":").append(che.getTs());
        return sb.toString();
    }

}
