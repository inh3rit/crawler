/**
 * 爬虫搜索线程包
 */
package com.zxsoft.crawler.m.thread.search;

import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
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
 * 全网搜索线程
 * @author wg
 */
public class NetworkSearchThread implements Runnable {

	/**
	 * 日志操作对象
	 */
    private static final Logger log = LoggerFactory.getLogger(NetworkSearchThread.class);

    /**
     * 线程休眠时间，单位（秒）
     */
    private final Long interval;

    /**
     * 区域信息实体
     */
    private final ReptileEntity reptile;

    /**
     * 数据标识
     */
    private final String identify;

    /**
     * 昨天给我
     */
    private final Long expire;

    /**
     * 全网搜索带参构造方法
     * @param interval
     * @param expire
     * @param reptile
     * @param identify
     */
    public NetworkSearchThread(Long interval, Long expire, ReptileEntity reptile, String identify) {
        this.interval = interval;
        this.expire = expire;
        this.reptile = reptile;
        this.identify = identify;
    }

    @Override
    /**
     * 全网搜索线程功能执行
     */
    public void run() {
        List<JobEntity> tasks = null;
        log.info("network search thread start");
        do {
            try {
                Long startTs = System.currentTimeMillis();
                if (tasks == null)
                    tasks = OracleModel.dao.queryTaskQueue(Const.PAGE_SIZE, this.reptile.getLocation());
                if (tasks == null || tasks.isEmpty()) {
                    log.info("未发现任务, 将休眠 {} 秒钟后继续", this.interval);
                    tasks = null;
                    TimeUnit.SECONDS.sleep(this.interval);
                    continue;
                }
                Integer sum = tasks.size();

                Iterator<JobEntity> tas = tasks.iterator();
                while (tas.hasNext()) {
                    JobEntity je = tas.next();
                    tas.remove();

                    // 检测当前 job 中的 来源是否不存在
                    if (JobModel.dao.isTid404(je.getSectionId(), this.reptile)) continue;

                    if (JobDistribution.instance.emit(JobType.NETWORK_SEARCH, je, this.reptile, this.identify, this.expire))
                        continue;
                    log.error("distribution job fail, JobId: {}", je.getJobId());
                }
                tasks = null;
                Long endTs = System.currentTimeMillis();
                log.info("Completed a total of {} assignments, the next round of assignments, share time {} seconds, will be sleep {} seconds", sum, (endTs - startTs) / 1000, this.interval);
                TimeUnit.SECONDS.sleep(this.interval);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
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
                    default:
                        log.error(e.getMessage(), e);
                        break;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } while (true);
    }
}
