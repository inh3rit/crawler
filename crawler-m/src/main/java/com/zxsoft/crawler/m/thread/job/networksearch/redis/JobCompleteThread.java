package com.zxsoft.crawler.m.thread.job.networksearch.redis;

import com.zxsoft.crawler.common.entity.redis.DelJobEntity;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.m.model.redis.JobModel;
import com.zxsoft.crawler.m.model.oracle.OracleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by cox on 2015/11/15.
 */
public class JobCompleteThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(JobCompleteThread.class);

    private JobType jobType;

    public JobCompleteThread(JobType type) {
        this.jobType = type;
    }

    private void delOracleJob(DelJobEntity dje) {
        OracleModel.dao.updateTaskExecuteStatus(dje.getJob());
    }

    @Override
    public void run() {
        try {
            List<DelJobEntity> lrje = JobModel.dao.getAllDelJob();
            if (lrje.isEmpty()) return;
            if (this.jobType == JobType.NETWORK_SEARCH) {
                for (DelJobEntity dje : lrje) {
                    this.delOracleJob(dje);
                }
            }
            JobModel.dao.deleteJob(lrje);
            log.debug("delete complete job success");
            lrje.clear();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
