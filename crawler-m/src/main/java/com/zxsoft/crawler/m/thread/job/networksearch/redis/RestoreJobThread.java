package com.zxsoft.crawler.m.thread.job.networksearch.redis;

import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.redis.RestoreJobEntity;
import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.exception.CrawlerException;
import com.zxsoft.crawler.m.distribution.JobDistribution;
import com.zxsoft.crawler.m.model.oracle.OracleModel;
import com.zxsoft.crawler.m.model.redis.JobModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 若爬虫程序更新但是有正在执行的任务未执行完成, 则会一直存在于执行区中
 * 这个线程的机制是 当爬虫机器程序更新完成后会将更新钱停掉的任务写入到 JOB_RESTORE 区中
 * 然后这里定期去找 JOB_RESTORE 中的所有任务, 获取这些任务后会删除恢复区的任务以及 JOB_RUN 中
 * 的未完成任务, 从 ORACLE 中读取来源等信息
 * 接着就和全网搜索任务分发相同, 重新分发给可执行任务的机器
 */
public class RestoreJobThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RestoreJobThread.class);

    private Long interval;
    private ReptileEntity reptile;
    private String identify;
    private Long expire;

    public RestoreJobThread(Long interval, Long expire, ReptileEntity reptile, String identify) {
        this.interval = interval;
        this.expire = expire;
        this.reptile = reptile;
        this.identify = identify;
    }

    @Override
    public void run() {
        List<JobEntity> tasks = null;
        Boolean complete = false;
        do {
            try {
                if (tasks == null || tasks.isEmpty()) {
                    List<RestoreJobEntity> lrje = JobModel.dao.getAllRestoreJob();
                    if (CollectionKit.isEmpty(lrje)) {
                        complete = true;
                        break;
                    }
                    String[] jobs = new String[lrje.size()];
                    for (Integer i = lrje.size(); i-- > 0; ) {
                        jobs[i] = lrje.get(i).getJob();
                    }
                    tasks = OracleModel.dao.getRestoreTask(jobs);
                }
                Iterator<JobEntity> tas = tasks.iterator();
                while (tas.hasNext()) {
                    JobEntity je = tas.next();
                    if (!JobDistribution.instance.emit(JobType.NETWORK_SEARCH, je, this.reptile, this.identify, this.expire)) {
                        log.error("distribution restore job fail, jobId: {}", je.getJobId());
                    }
                    tas.remove();
                }
                complete = true;
                log.info("complete restore job distribution");
            } catch (CrawlerException e) {
                switch (e.code()) {
                    case SYSTEM_ERROR_SLAVE_BUSY:
                        try {
                            log.error("爬虫机繁忙, 等待 {} 秒后尝试重新创建", this.interval);
                            TimeUnit.SECONDS.sleep(this.interval);
                        } catch (InterruptedException e2) {
                            log.error("线程休眠失败", e2);
                        }
                        break;
                    case SUCCESS:
                        break;
                    default:
                        log.error(e.getMessage(), e);
                        break;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } while (!complete);
    }
}
