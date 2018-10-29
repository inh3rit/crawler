package com.zxsoft.crawler.w.util;

import com.jfinal.plugin.redis.RedisPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * redis 连接初始化, 用于爬虫监控链接 redis 服务器
 * 需要在获取 redis 数据的时候进行初始化操作
 * 之后使用的时候 redis 的配置名就是 reptile 表中的 redis 字段值
 *
 * Created by cox on 2015/9/1.
 */
public class RedisPool {

    private static final Logger log = LoggerFactory.getLogger(RedisPool.class);
    private static List<String> redisKey = new ArrayList<>();

    public static Boolean initRedis(String host) {
        try {
            String[] rc = host.split(":");
            if (rc.length<2) return false;
            for(Integer i=redisKey.size(); i-->0;) {
                if (!redisKey.get(i).equals(host)) continue;
                log.info("在连接池中发现 Redis Server: " + host);
                return true;
            }
            String redisHost = rc[0];
            Integer redisPort = Integer.valueOf(rc[1]);
            RedisPlugin rp = new RedisPlugin(host, redisHost, redisPort, 5000);
            if (!rp.start()) return false;
            log.info("Redis Server {} connecton success", host);
            redisKey.add(host);
            return true;
        } catch (NumberFormatException e) {
            log.error("获取 redis 端口失败", e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public static void remove(String cacheName) {
        for(String key : redisKey) {
            if (!cacheName.equals(key)) continue;
            redisKey.remove(key);
        }
    }

    public static void clear() {
        redisKey.clear();
    }

}
