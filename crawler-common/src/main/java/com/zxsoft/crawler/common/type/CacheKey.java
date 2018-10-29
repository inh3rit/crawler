package com.zxsoft.crawler.common.type;

/**
 * Created by cox on 2015/11/24.
 */
public enum CacheKey {
    KEY_HEART("client_heart"), // 心跳
    KEY_RUN("job_run"), // 正在执行的任务
    KEY_DEL("job_del"), // 待删除任务
    KEY_TEST("cw_test"), // test
    KEY_SWAP("job_swap"), // 任务交换
    KEY_RESTORE("job_restore"), // 任务恢复
    KEY_CONF("cw_conf"), // 爬虫配置
    KEY_LOCK_SYNC("cw_lock_sync"), // 數據同步鎖
    KEY_RECORD_PARSE("cw_record_parse"), // 爬虫记录分析
    KEY_RECORD_PARSE_COUNT("cw_record_parse_count"), // 爬虫记录分析
    CW_RECORD_PARSE_COUNT_SYNC("cw_record_parse_count_sync"), // 爬虫记录统计数据同步锁
    ;

    private final String key;

    CacheKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public static CacheKey getCacheKey(String key) {
        for (CacheKey k : CacheKey.values()) {
            if (!k.getKey().toUpperCase().equals(key.toUpperCase())) continue;
            return k;
        }
        return KEY_TEST;
    }

}
