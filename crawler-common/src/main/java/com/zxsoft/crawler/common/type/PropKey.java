package com.zxsoft.crawler.common.type;

/**
 * 爬虫运行配置关键字枚举类
 */
public enum PropKey {
    UNDEFINED("undefined", PropType.UNKNOW),
    DEBUG("debug", PropType.BOOLEAN),
    PROXY_HOST("proxy.host", PropType.IPV4),
    PROXY_PORT("proxy.port", PropType.NUMBER),
    PROXY_USR("proxy.usr", PropType.TEXT),
    PROXY_PASSWD("proxy.passwd", PropType.TEXT),
    SOLR_ADDRESS("out.data.solr", PropType.URL),
    // SEARCH_NETWORK_MAX_EXECUTE("search.network.max.execute", PropType.NUMBER), // 未能很好实现, 暂不启用
    SYNC_TABLE("sync.table", PropType.SYNC_TABLE),
    DATA_IDENTIFY("data.identify", PropType.TEXT),
    HEART_EXPIRE("heart.expire", PropType.NUMBER),
    INTERVAL_SEARCH("interval.search", PropType.NUMBER),
    INTERVAL_INSPECT("interval.inspect", PropType.NUMBER),
    JOB_TYPE("job.type", PropType.JOB_TYPE),
    MONITOR_OPEN("monitor.open", PropType.BOOLEAN),
    MONITOR_PORT("monitor.port", PropType.NUMBER);
    private final String key;
    private final PropType type;
    PropKey(String key, PropType type) {
        this.key = key;
        this.type = type;
    }
    public String getKey() {
        return this.key.toUpperCase();
    }
    public PropType getType() {
        return this.type;
    }
    public static PropKey getConfKey(String key) {
        for (PropKey k : PropKey.values()) {
            if (!key.toUpperCase().equals(k.getKey())) continue;
            return k;
        }
        return UNDEFINED;
    }
}
