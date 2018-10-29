package com.zxsoft.crawler.m.thread.job.sync;

import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
import com.zxsoft.crawler.common.type.SyncTable;
import com.zxsoft.crawler.m.model.redis.SyncModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by iaceob on 2015/11/14.
 */
public class SyncThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(SyncThread.class);
    private SyncTable[] tables;
    private ReptileEntity reptile;

    public SyncThread(SyncTable[] tables, ReptileEntity reptile) {
        this.tables = tables;
        this.reptile = reptile;
    }

    @Override
    public void run() {
        SyncModel.dao.syncInfo(this.tables, this.reptile);
        log.info("Website info sync complete");
    }
}
