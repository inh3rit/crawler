/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.common.entity.redis;

import com.jfinal.plugin.activerecord.Record;

/**
 * Created by 施洋青
 * DATE： 16-8-29.
 */
public class RecordParseCountEntity extends Record {

    public String getTime() {
        return super.getStr("time");
    }

    public RecordParseCountEntity setTime(String time) {
        super.set("time", time);
        return this;
    }

    public Integer getKey() {
        return super.getInt("key");
    }

    public RecordParseCountEntity setKey(Integer key) {
        super.set("key", key);
        return this;
    }

    public long getCount() {
        return super.getLong("count");
    }

    public RecordParseCountEntity setCount(long count) {
        super.set("count", count);
        return this;
    }

}