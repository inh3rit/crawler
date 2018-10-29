package com.zxsoft.crawler.s.model;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.kit.JobEntityKit;
import com.zxsoft.crawler.common.type.CacheKey;
import name.iaceob.kit.disgest.Disgest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by cox on 2015/11/24.
 */
public class SwapModel {

    private static final Logger log = LoggerFactory.getLogger(SwapModel.class);

    public static final SwapModel dao = new SwapModel(Const.CACHE_NAME);
    private static Cache redis;

    public SwapModel(String cacheName) {
        SwapModel.redis = Redis.use(cacheName);
    }


    /**
     * 1、获取心跳包
     * 2、获取本机器所有节点的交换任务
     * 3、根据节点机器码获取当前节点的任务
     * 4、根据心跳包判断节点已失效的交换任务，并验证是否被本机器去其他节点获取？ 是：继续 ；否：获取该任务并执行，继续
     *
     * @param cli
     * @return
     */
    public List<JobEntity> getExecJob(Integer cli) {
        List<JobEntity> jes = new ArrayList<>();
        // 1、获取心跳包
        Set<String> client_heart = redis.zrevrange(CacheKey.KEY_HEART.getKey(), 0, -1);
        // 2、获取本机下所有节点的任务
        Set<String> job_swap = redis.zrevrange(CacheKey.KEY_SWAP.getKey(), 0, -1);
        job_swap.stream().filter(s -> s.startsWith(String.valueOf(cli))).forEach(s -> {
            String jobStr = s.split(",")[1];
            Map jobMap = JSON.parseObject(Disgest.decodeRC4(jobStr, Const.SALT_JOB), HashMap.class);
            log.debug("获取本节点任务,cli:{},cli_p:{},jobMap:{}", cli, jobMap);
            jes.add(JobEntityKit.serialize(jobMap));
            redis.zrem(CacheKey.KEY_SWAP.getKey(), s);
        });
//        for (String s : job_swap) {
//            String[] jbs = s.split(",");
//            Integer jbs_0 = Integer.valueOf(jbs[0]);
//            if (jbs_0.equals(cli)) { // 获取本节点任务
//                Map jobMap = JSON.parseObject(Disgest.decodeRC4(jbs[1], Const.SALT_JOB), HashMap.class);
//                log.debug("获取本节点任务,cli:{},cli_p:{},jbs_0:{},jobMap:{}", cli, jbs_0, jobMap);
//                jes.add(JobEntityKit.serialize(jobMap));
//                redis.zrem(CacheKey.KEY_SWAP.getKey(), s);
//            }
//        }
        client_heart.clear();
        job_swap.clear();
        return jes;
    }

}
