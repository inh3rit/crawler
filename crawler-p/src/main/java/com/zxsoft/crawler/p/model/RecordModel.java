/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.p.model;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.redis.RecordParseCountEntity;
import com.zxsoft.crawler.common.entity.redis.RecordParseEntity;
import com.zxsoft.crawler.common.kit.rediskey.RecordParseJobKit;
import com.zxsoft.crawler.common.type.CacheKey;
import com.zxsoft.crawler.common.type.RecordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by 施洋青
 * DATE： 16-8-24.
 */
public class RecordModel {

    private static final Logger log = LoggerFactory.getLogger(RecordModel.class);
    public static final RecordModel dao = new RecordModel(Const.CACHE_NAME);
    private static Cache redis;

    public RecordModel(String cacheName) {
        RecordModel.redis = Redis.use(cacheName);
    }

    private Double genScore() {
        return 1.0d / (System.currentTimeMillis() / 60000.0d);
    }

    /**
     * 添加记录爬虫正文解析未知的地址
     *
     * @param recordType
     * @param value
     * @return
     */
    public Boolean unableResolveUrl(RecordType recordType, String value) {
        RecordParseEntity recordEntity = new RecordParseEntity();
        recordEntity.setKey(recordType.getIndex());
        recordEntity.setValue(value);
        try {
            //return redis.zadd(CacheKey.KEY_RECORD_PARSE.getKey(), this.genScore(), RecordParseJobKit.serialize(recordEntity)) != 0;
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 记录爬虫解析总量，保存到redis
     * 方法加锁，防止多线程对redis计数破坏
     *
     * @param re
     * @return
     */
    public synchronized Boolean sumRecordParseCount(RecordParseCountEntity re) {
        try {
            // 关闭此方法
            if (true)
                return true;

            if (re == null || re.getCount() <= 0)
                return true;

            Boolean is_lock;
            int is_lock_count = 0;
            try {
                do {
                    // 超时 5*500 主动删除此锁
                    if (is_lock_count >= 5) {
                        redis.del(CacheKey.CW_RECORD_PARSE_COUNT_SYNC.getKey());
                    }

                    // 防止 CacheKey.CW_RECORD_PARSE_COUNT_SYNC 获取无数据发生异常
                    is_lock = redis.get(CacheKey.CW_RECORD_PARSE_COUNT_SYNC.getKey()) == null ? false : true;
                    if (is_lock) {
                        is_lock_count++;
                        //log.debug("爬虫记录锁当前状态:{},休眠:{} 毫秒", (is_lock ? "锁起" : "未锁"), 500);
                        TimeUnit.MILLISECONDS.sleep(500); //休眠0.5秒
                    }
                } while (is_lock);
            } catch (Exception e) {
                is_lock = false;
            }
            redis.set(CacheKey.CW_RECORD_PARSE_COUNT_SYNC.getKey(), !is_lock);
            String h_key = re.getTime() + "_" + re.getKey().toString();
            long count;
            try {
                // 防止 h_key 获取无数据发生异常
                count = redis.hget(CacheKey.KEY_RECORD_PARSE_COUNT.getKey(), h_key);
            } catch (Exception e) {
                count = 0L;
            }
            long h_value = re.getCount() + count;
            //log.debug("{}爬虫记录,类型:{},当前数量:{},增加之前:{},增加之后:{},时间:{}", ((count != 0L) ? "new 更新" : "new 新增"), re.getKey(), re.getCount(), count, h_value, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
            redis.hset(CacheKey.KEY_RECORD_PARSE_COUNT.getKey(), h_key, h_value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            redis.del(CacheKey.CW_RECORD_PARSE_COUNT_SYNC.getKey());
        }
        return false;
    }


    /**
     * 爬虫正文解析模板计数
     *
     * @param recordType
     * @param count
     * @return true:计数成功 false：计数失败
     * @throws Exception
     */
    public Boolean moduleCount(RecordType recordType, Integer count) {
        RecordParseCountEntity countEntity = new RecordParseCountEntity();
        countEntity.setTime(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        countEntity.setKey(recordType.getIndex());
        countEntity.setCount(count);
        //log.debug("模板计数，类型:{},数量:{}", recordType.getRemark(), count);
        return this.sumRecordParseCount(countEntity);
    }
}