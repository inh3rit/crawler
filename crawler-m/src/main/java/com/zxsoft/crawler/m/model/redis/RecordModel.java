/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.m.model.redis;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbPro;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.redis.RecordParseCountEntity;
import com.zxsoft.crawler.common.entity.redis.RecordParseEntity;
import com.zxsoft.crawler.common.kit.rediskey.RecordParseJobKit;
import com.zxsoft.crawler.common.type.CacheKey;
import com.zxsoft.crawler.common.type.SourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 施洋青
 * DATE： 16-8-25.
 */
public class RecordModel {

    private static final Logger log = LoggerFactory.getLogger(RecordModel.class);
    public static final RecordModel dao = new RecordModel(Const.CACHE_NAME);
    private static final DbPro db = Db.use(SourceType.MYSQL_CONF.toString());
    private static Cache redis;

    public RecordModel(String cacheName) {
        RecordModel.redis = Redis.use(cacheName);
    }

    /**
     * 获取所有爬虫正文解析记录
     */
    public void getAllRecordInfo() {
        Set<String> set = null;
        try {
            set = redis.zrevrange(CacheKey.KEY_RECORD_PARSE.getKey(), 0, 800);
            if (set.isEmpty()) {
                log.debug("cw_record_parse Set is Empty.");
                return;
            }
            log.debug("cw_record_parse Set size:{}.", set.size());
            List<RecordParseEntity> recordEntities = new ArrayList<>();
            RecordParseEntity resolve;
            for (String s : set) {
                resolve = RecordParseJobKit.resolve(s);
                recordEntities.add(resolve);
                // 保存到 MySql
                db.save("record_parse_content", resolve);
            }
            log.info("cw_record_parse Data is saved to record_parse_content.");
            // 删除已经保存的记录
            boolean isDel = this.delRecordInfo(recordEntities);
            log.info("record parse del {}.", isDel ? "success" : "error");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (set != null)
                set.clear();
        }
    }

    /**
     * 删除爬虫正文解析的所有已保存的记录
     */
    public boolean delRecordInfo(List<RecordParseEntity> entityList) {
//        Set<String> set = null;
        try {
            if (entityList.isEmpty())
                return true;

            // 思路：获取当前redis中所有的记录与当前已知的记录进行对比 如多元素相同则将该元素从redis集合中移除
            /*set = redis.zrevrange(CacheKey.KEY_RECORD_PARSE.getKey(), 0, 800);
            RecordParseEntity resolve = null;
            for (RecordParseEntity entity : entityList) {
                for (String s : set) {
                    resolve = RecordParseJobKit.resolve(s);
                    if (resolve.getKey() == entity.getKey() && resolve.getValue().equals(entity.getValue()))
                        redis.zrem(CacheKey.KEY_RECORD_PARSE.getKey(), s);
                }
            }*/
            for (RecordParseEntity entity : entityList) {
                redis.zrem(CacheKey.KEY_RECORD_PARSE.getKey(), RecordParseJobKit.serialize(entity));
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
//            if (set != null)
//                set.clear();
            if (entityList != null)
                entityList.clear();

        }
        return false;
    }


    /**
     * 将缓存中记录爬虫解析正文总量的数据保存或者更新到MySQL，并且删除已经过期的缓存数据
     *
     * @return
     */
    public boolean saveOrUpdateRecordParseCount() {
        Map map = new HashMap();
        try {
            map = redis.hgetAll(CacheKey.KEY_RECORD_PARSE_COUNT.getKey());
            if (map == null || map.size() <= 0)
                return true;

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String localDate = formatter.format(new Date());
            RecordParseCountEntity countEntity;
            Iterator entries = map.entrySet().iterator();
            Map.Entry entry;
            while (entries.hasNext()) {
                entry = (Map.Entry) entries.next();
                String key = (String) entry.getKey();
                Long value = (Long) entry.getValue();
                String[] time_key = key.split("_");
                countEntity = new RecordParseCountEntity();
                countEntity.setTime(time_key[0]);
                countEntity.setKey(Integer.parseInt(time_key[1]));
                countEntity.setCount(value);

                // 1、当天记录 更新或者保存
                if (localDate.equals(countEntity.getTime())) {
                    List<Record> record = db.find("select * from record_parse_count where time = ? and `key` = ? ", countEntity.getTime(), countEntity.getKey());
                    if (record == null || record.size() <= 0)
                        db.save("record_parse_count", countEntity);
                    else
                        db.update("update record_parse_count set count = ? where time = ? and `key` = ? ", countEntity.getCount(), countEntity.getTime(), countEntity.getKey());
                }
                log.info("new 爬虫解析数量记录,日期:{},类型:{},数据量:{}", countEntity.getTime(), countEntity.getKey(), countEntity.getCount());

                Date dt1 = formatter.parse(localDate, new ParsePosition(0));
                Date dt2 = formatter.parse(countEntity.getTime(), new ParsePosition(0));
                long l = dt1.getTime() - dt2.getTime();

                // 2、如果该记录的日期超过2天则删除
                if (l > 2)
                    redis.hdel(CacheKey.KEY_RECORD_PARSE_COUNT.getKey(), key);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (map != null)
                map.clear();
        }
        return false;
    }
}