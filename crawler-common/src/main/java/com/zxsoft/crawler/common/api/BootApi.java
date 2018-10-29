package com.zxsoft.crawler.common.api;

/**
 * 爬虫程序启动接口
 */
public interface BootApi {
    Boolean start();
    void startMonitor(Integer port);
}
