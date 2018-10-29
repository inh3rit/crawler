package com.zxsoft.crawler.m.thread.job.networksearch.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxsoft.crawler.m.model.oracle.OracleModel;

/**
 * 未完成任务推送线程，每天执行一次
 *@author wg
 */
public class PushUnfinishedTaskThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(PushUnfinishedTaskThread.class);

    @Override
    public void run() {
        try {
            OracleModel.dao.pushUnfinishedTasks();
        } catch (Exception e) {
            log.error("push unfinished task fail");
            log.error(e.getMessage(), e);
        }
    }
}
