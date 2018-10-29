/**
 *	爬虫线程搜索包
 * @author wg
 */
package com.zxsoft.crawler.m.thread.search;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.exception.CrawlerException;
import com.zxsoft.crawler.m.distribution.JobDistribution;
import com.zxsoft.crawler.m.model.oracle.FocusModel;

/**
 * 重点关注主线程
 */
public class NetworkFocusThread implements Runnable {

	/**
	 * 日志操作对象
	 */
    private static final Logger log = LoggerFactory.getLogger(NetworkFocusThread.class);

    /**
     * 数据标识
     */
    private final String identify;

    /**
     * 延时时间
     */
    private final Long expire;

    /**
     * 定时时间
     */
    private final Long interval;

    /**
     * 区域信息
     */
    private final ReptileEntity reptile;

    /**
     * 重点关注线程带参构造方法
     * @param identify
     * @param expire  延时时间
     * @param interval  定时执行时间
     * @param reptile  区域信息
     */
    public NetworkFocusThread(String identify, Long expire, Long interval, ReptileEntity reptile) {
        this.identify = identify;
        this.expire = expire;
        this.interval = interval;
        this.reptile = reptile;
    }


    /**
     *	重点关注线程主方法
     *@author wg
     */
    @Override
    public void run() {
    	//任务列表
        List<JobEntity> tasks = null;
        log.info("network focus thread start");
        Boolean complete = false;
        do {
            try {
            	//获取线程执行时间
                Long startTs = System.currentTimeMillis();
                if (tasks == null)

                	//获取重点关注任务列表
                    tasks = FocusModel.dao.getFocusList();
                if (tasks == null || tasks.isEmpty()) {
                    log.info("未发现任务, 将休眠 {} 秒钟后继续", this.interval);
                    tasks = null;
                    complete = true;
                    break;
                }

                //任务数量
                Integer sum = tasks.size();
                Iterator<JobEntity> tas = tasks.iterator();
                while (tas.hasNext()) {
                    JobEntity je = tas.next();
                    tas.remove();

                    //分发任务成功则继续获取任务
                    if (JobDistribution.instance.emit(JobType.NETWORK_FOCUS, je, this.reptile, this.identify, this.expire)) continue;

                    log.error("distribution job fail, JobId: {}", je.getJobId());
                }
                tasks = null;

                //执行结束时间
                Long endTs = System.currentTimeMillis();
                log.info("Completed {} focus job, use time {} seconds", sum, (endTs - startTs) / 1000);
                complete = true;
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
                    case NETWORK_ERROR:
                        log.error(e.getMessage());
                        break;
                    case VALIDATOR_ERROR_FOCUS:
                        log.error(e.getMessage());
                        break;
                    case CONF_ERROR:
                        log.error(e.getMessage(), e);
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
