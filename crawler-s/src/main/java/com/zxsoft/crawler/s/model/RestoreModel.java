package com.zxsoft.crawler.s.model;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.redis.RestoreJobEntity;
import com.zxsoft.crawler.common.entity.redis.RunJobEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.kit.rediskey.RestoreJobKit;
import com.zxsoft.crawler.common.kit.rediskey.RunJobKit;
import com.zxsoft.crawler.common.type.CacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by cox on 2015/11/30.
 */
public class RestoreModel {

    private static final Logger log = LoggerFactory.getLogger(RestoreModel.class);
    public static final RestoreModel dao = new RestoreModel(Const.CACHE_NAME);
    public static Cache redis;

    public RestoreModel(String cacheName) {
        RestoreModel.redis = Redis.use(cacheName);
    }

    private Double genScore() {
        return 1.0d / (System.currentTimeMillis() / 60000.0d);
    }


    private List<RestoreJobEntity> getAllRestoreJob(Integer cli, String ip) {
        try {
            Set<String> set = redis.zrevrange(CacheKey.KEY_RUN.getKey(), 0, -1);
            if (CollectionKit.isEmpty(set)) return null;
            List<RestoreJobEntity> jobs = new ArrayList<RestoreJobEntity>();
            for (String s : set) {
                RunJobEntity rje = RunJobKit.resolve(s);
                if (!cli.equals(rje.getCli()) || !ip.equals(rje.getIp())) continue;
                RestoreJobEntity rsj = new RestoreJobEntity();
                rsj.setCli(rje.getCli()).setIp(rje.getIp()).setJob(rje.getJob()).setTs(rje.getTs());
                jobs.add(rsj);
            }
            return jobs;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private Boolean submitRestoreJob(List<RestoreJobEntity> jobs) {
        try {
            if (CollectionKit.isEmpty(jobs)) return true;
            for (RestoreJobEntity job : jobs) {
                redis.zadd(CacheKey.KEY_RESTORE.getKey(), this.genScore(), RestoreJobKit.serialize(job));
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    public Boolean submitRestoreJob(Integer cli, String ip) {
        return this.submitRestoreJob(this.getAllRestoreJob(cli, ip));
    }
}
