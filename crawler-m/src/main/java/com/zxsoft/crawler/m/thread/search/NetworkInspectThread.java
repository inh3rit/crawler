package com.zxsoft.crawler.m.thread.search;

import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.exception.CrawlerException;
import com.zxsoft.crawler.m.distribution.JobDistribution;
import com.zxsoft.crawler.m.model.MysqlModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 网络巡检定时写入任务线程
 */
public class NetworkInspectThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(NetworkInspectThread.class);

    private ReptileEntity reptile;
    private String identify;
    private Long expire;
    private Long interval;

    public NetworkInspectThread(ReptileEntity reptile, String identify, Long expire, Long interval) {
        this.reptile = reptile;
        this.identify = identify;
        this.expire = expire;
        this.interval = interval;
    }

    @Override
    public void run() {
        List<JobEntity> lje = null;
        Boolean complete;
        try {
            lje = MysqlModel.dao.getAllInspectJob(this.reptile);
            complete = lje == null || lje.isEmpty();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            complete = true;
        }
        do {
            try {
                if (lje == null || lje.isEmpty()) {
                    complete = true;
                    continue;
                }
                Iterator<JobEntity> it = lje.iterator();
                while (it.hasNext()) {
                    JobEntity je = it.next();
                    JobDistribution.instance.emit(JobType.NETWORK_INSPECT, je, this.reptile, this.identify, this.expire);
                    it.remove();
                }
                complete = true;
                log.info("Synchronize network inspect job success");
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
                    case SYSTEM_ERROR:
                        log.error(e.getMessage());
                        break;
                    default:
                        log.error(e.getMessage());
                        break;
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        } while (!complete);
    }

}
