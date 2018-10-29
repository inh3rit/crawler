/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.m.thread.job.networksearch.redis;

import com.zxsoft.crawler.m.model.redis.RecordModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by 施洋青
 * DATE： 16-8-25.
 */
public class RecordParseJobThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RecordParseJobThread.class);

    @Override
    public void run() {
        try {
            RecordModel.dao.saveOrUpdateRecordParseCount();
            RecordModel.dao.getAllRecordInfo();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}