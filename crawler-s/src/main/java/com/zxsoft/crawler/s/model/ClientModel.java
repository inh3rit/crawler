package com.zxsoft.crawler.s.model;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.redis.ClientHeartEntity;
import com.zxsoft.crawler.common.entity.redis.DelJobEntity;
import com.zxsoft.crawler.common.kit.rediskey.ClientHeartKit;
import com.zxsoft.crawler.common.kit.rediskey.DelJobKit;
import com.zxsoft.crawler.common.type.CacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by iaceob on 2015/11/15.
 */
public class ClientModel {

    private static final Logger log = LoggerFactory.getLogger(ClientModel.class);
    public static final ClientModel dao = new ClientModel(Const.CACHE_NAME);
    private static Cache redis;

    public ClientModel(String cacheName) {
        ClientModel.redis = Redis.use(cacheName);
    }

    private Double genScore() {
        return 1.0d / (System.currentTimeMillis() / 60000.0d);
    }

    /**
     * cli:ip:maxPerform:timestamp
     *
     * @param cli
     * @param ip
     * @param maxPerform
     * @return
     */
    private Boolean doHeart(Integer cli, String ip, Integer maxPerform) {
        ClientHeartEntity che = new ClientHeartEntity();
        che.setCli(cli).setIp(ip).setMaxPerform(maxPerform).setTs(System.currentTimeMillis());
        String dh = ClientHeartKit.serialize(che);
        log.info("Client heart: {}", dh);
        che.clear();
        return redis.zadd(CacheKey.KEY_HEART.getKey(), this.genScore(), dh) != 0L;
    }

    /**
     * cli:ip:maxPerform:timestamp
     *
     * @param cli
     * @param ip
     * @param maxPerform
     * @return
     */
    public Boolean heart(Integer cli, String ip, Integer maxPerform) {
        if (!redis.exists(CacheKey.KEY_HEART.getKey()))
            return this.doHeart(cli, ip, maxPerform);
        Set<String> set = redis.zrevrange(CacheKey.KEY_HEART.getKey(), 0, -1);
        if (set == null || set.isEmpty())
            return this.doHeart(cli, ip, maxPerform);
        for (String s : set) {
            ClientHeartEntity che = ClientHeartKit.resolve(s);
            if (!cli.equals(che.getCli()) || !ip.equals(che.getIp()))
                continue;
            redis.zrem(CacheKey.KEY_HEART.getKey(), s);
        }
        set.clear();
        return this.doHeart(cli, ip, maxPerform);
    }

    /**
     * 添加完成的任务到删除表中
     * cli:ip:job:stat:timestamp
     *
     * @param cli
     * @param ip
     * @param job
     * @param stat
     * @return
     */
    public Boolean completeRunJob(Integer cli, String ip, String job, Integer stat) {
        try {
            DelJobEntity dje = new DelJobEntity();
            dje.setCli(cli).setIp(ip).setJob(job).setStat(stat).setTs(System.currentTimeMillis());
            return redis.zadd(CacheKey.KEY_DEL.getKey(), this.genScore(), DelJobKit.serialize(dje)) != 0L;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
}
