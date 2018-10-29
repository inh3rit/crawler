package com.zxsoft.crawler.m.thread.job.networksearch.redis;

import com.zxsoft.crawler.m.model.redis.JobModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* 此方法只在服务更新时有任务执行表中存在残留任务时做删除
*/
public class DeleteExpireJobThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(DeleteExpireJobThread.class);

    @Override
    public void run() {
        try {
            JobModel.dao.delExpireJob();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
