package com.zxsoft.crawler.m.model.redis;

import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.redis.*;
import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
import com.zxsoft.crawler.common.kit.rediskey.DelJobKit;
import com.zxsoft.crawler.common.kit.rediskey.RestoreJobKit;
import com.zxsoft.crawler.common.kit.rediskey.RunJobKit;
import com.zxsoft.crawler.common.type.CacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by cox on 2015/12/5.
 */
public class JobModel {

    private static final Logger log = LoggerFactory.getLogger(JobModel.class);
    public static final JobModel dao = new JobModel(Const.CACHE_NAME);
    private static Cache redis;

    public JobModel(String cacheName) {
        JobModel.redis = Redis.use(cacheName);
    }


    private Double genScore() {
        return 1.0d / (System.currentTimeMillis() / 60000.0d);
    }

    private Double genScore(Integer cli) {
        return cli + (1 - (1.0d / (System.currentTimeMillis() / 60000.0d) * 10000000));
    }


    private String genJobStr(Integer cli, String ip, String job) {
        StringBuilder sb = new StringBuilder();
        sb.append(cli).append(":").append(ip).append(":")
                .append(job).append(":").append(System.currentTimeMillis());
        return sb.toString();
    }


    /**
     * 創建正在执行的任务, 用于统计 Slave 机器当前执行的数量
     *
     * @param client ClientInfoEntity 从机信息
     * @param je     任務內容
     * @return Boolean
     */
    public Boolean createJob(ClientInfoEntity client, JobEntity je) {
        try {
            redis.zadd(CacheKey.KEY_RUN.getKey(), this.genScore(client.getCli()), this.genJobStr(client.getCli(), client.getIp(), je.getJobId()));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }


    /**
     * cli:ip:job:stat:timestamp
     * 获取待删除的任务, 需要注意, 这里获取后即会把带删除任务 key 中的数据删除, 第二次将会获取不到
     *
     * @return
     */
    public List<DelJobEntity> getAllDelJob() {
        Set<String> set1 = redis.zrevrange(CacheKey.KEY_DEL.getKey(), 0, -1);
        List<DelJobEntity> lrje = new ArrayList<DelJobEntity>();
        for (String s : set1) {
            redis.zrem(CacheKey.KEY_DEL.getKey(), s);
            lrje.add(DelJobKit.resolve(s));
        }
        return lrje;
    }

    /**
     * 删除 redis 中正在执行的任务
     * cli:ip:job:timestamp # job_run
     * cli:ip:job:timestamp # job_del
     *
     * @return
     */
    public Boolean deleteJob(List<DelJobEntity> ldje) {
        if (ldje.isEmpty()) return true;
        Set<String> set2 = null;
        try {
            set2 = redis.zrevrange(CacheKey.KEY_RUN.getKey(), 0, -1);
            for (String s2 : set2) {
                RunJobEntity rje = RunJobKit.resolve(s2);
                for (DelJobEntity dje : ldje) {
                    if (rje.getCli().equals(dje.getCli()) &&
                            rje.getIp().equals(dje.getIp()) &&
                            rje.getJob().equals(dje.getJob())) {
                        redis.zrem(CacheKey.KEY_RUN.getKey(), s2);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (set2 != null)
                set2.clear();
        }
        return false;
    }


    /**
     * cli:ip:job:timestamp # job_run
     * 删除过期任务, 此方法只在有可能更新时部分任务残留,不受 JOB_DEL 控制时强制删除
     */
    public void delExpireJob() {
        Set<String> set = null;
        try {
            //获取正在执行的任务列表，再进行排序
            set = redis.zrevrange(CacheKey.KEY_RUN.getKey(), 0, -1);
            List<DelJobEntity> ldje = new ArrayList<DelJobEntity>();
            for (String s : set) {
                RunJobEntity rje = RunJobKit.resolve(s);
                if (!ClientModel.dao.isExpire(rje.getTs(), 1000L * 60L * 20L)) continue;
                DelJobEntity dje = new DelJobEntity();
                dje.setCli(rje.getCli())
                        .setIp(rje.getIp())
                        .setJob(rje.getJob())
                        .setTs(rje.getTs());
                ldje.add(dje);
                // if (delRes) continue;
                // log.error("Call ClientModel.deleteJob function delete job {} error", Long.parseLong(cs[3]));
                // if (redis.zrem(CacheKey.KEY_RUN.getKey(), s) != 0L) continue;
                // log.error("Delete job {} error", Long.parseLong(cs[3]));
            }
            Boolean delRes = this.deleteJob(ldje);
            if (delRes) return;
            log.error("Delete expire job fail");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (set != null)
                set.clear();
        }
    }


    /**
     * 删除恢复区任务, 以及执行中的任务
     *
     * @param rje 恢复区任务
     */
    private void delRestoreJob(RestoreJobEntity rje) {
        redis.zrem(CacheKey.KEY_RESTORE.getKey(), RestoreJobKit.serialize(rje));
        List<RunJobEntity> rjes = ClientModel.dao.getAllRunJob();
        for (RunJobEntity r : rjes) {
            if (r.getCli().equals(rje.getCli()) &&
                    r.getIp().equals(rje.getIp()) &&
                    r.getJob().equals(rje.getJob())) {
                redis.zrem(CacheKey.KEY_RUN.getKey(), RunJobKit.serialize(r));
            }
        }
    }

    /**
     * 获取所有待恢复订单, 需要注意这里获取后就直接将恢复表中的任务给删除了
     * 另外也同步删除了 JOB_RUN 中的相同记录
     *
     * @return
     */
    public List<RestoreJobEntity> getAllRestoreJob() {
        List<RestoreJobEntity> list = new ArrayList<RestoreJobEntity>();
        try {
            Set<String> set = redis.zrevrange(CacheKey.KEY_RESTORE.getKey(), 0, -1);
            for (String s : set) {
                RestoreJobEntity rje = RestoreJobKit.resolve(s);
                list.add(rje);
                // redis.zrem(CacheKey.KEY_RESTORE.getKey(), s);
                this.delRestoreJob(rje);
            }
            set.clear();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }


    /**
     * tid 是否不存在
     *
     * @param tid     tid
     * @param reptile 区域
     * @return Boolean
     */
    public Boolean isTid404(Integer tid, ReptileEntity reptile) {
        List<Record> tid404List = SyncModel.dao.getAllTid404();
        if (tid404List == null) return false;
        for (Record tid404 : tid404List)
            if (tid404.getInt("reptile").equals(reptile.getId()) && tid.equals(tid404.getInt("tid")))
                return true;
        return false;
    }

}
