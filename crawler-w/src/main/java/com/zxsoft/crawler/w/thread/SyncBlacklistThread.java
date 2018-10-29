package com.zxsoft.crawler.w.thread;

import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.w.model.BlacklistModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by cox on 2015/11/3.
 */
public class SyncBlacklistThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(SyncBlacklistThread.class);

    private String[] cacheNames;
    private String tbkey;
    private Long interval;

    public SyncBlacklistThread(String[] cacheNames, String tbkey, Long interval) {
        this.cacheNames = cacheNames;
        this.tbkey = tbkey;
        this.interval = interval;
    }

    @Override
    public void run() {
        try {
            List<Record> abls = BlacklistModel.dao.getAllBlacklist();
            for (String cacheName : this.cacheNames) {
                BlacklistModel.dao.sync(cacheName, this.tbkey, abls);
            }
            log.debug("黑名单规则同步完成, {} 分钟后再次同步", this.interval);
        } catch (Exception e) {
            log.error("同步黑名单规则失败.");
            log.error(e.getMessage(), e);
        }
    }
}
