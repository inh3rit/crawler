package com.zxsoft.crawler.m.model.redis;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.redis.ClientInfoEntity;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.kit.RecordKit;
import com.zxsoft.crawler.common.type.CacheKey;
import name.iaceob.kit.disgest.Disgest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务交换
 */
public class SwapModel {

    private static final Logger log = LoggerFactory.getLogger(SwapModel.class);
    public static final SwapModel dao = new SwapModel(Const.CACHE_NAME);
    private static Cache redis;

    public SwapModel(String cacheName) {
        SwapModel.redis = Redis.use(cacheName);
    }


    private Double genScore() {
        return 1.0d / (System.currentTimeMillis() / 60000.0d);
    }

    private Double genScore(Integer cli) {
        return cli + (1 - (1.0d / (System.currentTimeMillis() / 60000.0d) * 10000000));
    }


    /**
     * 创建交换任务, slave 机器定时获取
     * @param je 任务
     * @return Boolean
     */
    public Boolean swapJob(ClientInfoEntity client, JobEntity je) {
        if (client == null) return false;
        String jsonJob = RecordKit.toJsonSort(je);
        String jeStr = Disgest.encodeRC4(jsonJob, Const.SALT_JOB);
        jeStr = client.getCli() + "," + jeStr;
        return redis.zadd(CacheKey.KEY_SWAP.getKey(), this.genScore(client.getCli()), jeStr)!=0L;
    }

}
