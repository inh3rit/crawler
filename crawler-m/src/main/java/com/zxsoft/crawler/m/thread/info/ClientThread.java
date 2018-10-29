package com.zxsoft.crawler.m.thread.info;

import com.zxsoft.crawler.m.model.redis.ClientModel;

/**
 * 爬蟲機器可用性檢測線程
 */
public class ClientThread implements Runnable {

    private Long expire;

    public ClientThread(Long expire) {
        this.expire = expire;
    }

    @Override
    public void run() {
        ClientModel.dao.delExpireClient(expire);
    }
}
