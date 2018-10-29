package com.zxsoft.crawler.s.thread.heart;

import com.zxsoft.crawler.s.core.Slave;
import com.zxsoft.crawler.s.model.ClientModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cox on 2015/9/6.
 */
public class HeartThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HeartThread.class);

    private Integer cli;
    private String ip;
    private Integer maxPerform;

    public HeartThread(Integer cli, String ip, Integer maxPerform) {
        this.cli = cli;
        this.ip = ip;
        this.maxPerform = maxPerform;
    }

    @Override
    public void run() {
        try {
            // 根据现有的心跳包通过算法获取该节点心跳包独有的参数
            if (Slave.heartParameter == null || Slave.heartParameter == 0)
                Slave.heartParameter = this.cli;
            if (!ClientModel.dao.heart(this.cli, this.ip, this.maxPerform)) {
                log.error("heart client error,cli:{}", this.cli);
            }
            log.info("heart client success,cli:{}", this.cli);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
