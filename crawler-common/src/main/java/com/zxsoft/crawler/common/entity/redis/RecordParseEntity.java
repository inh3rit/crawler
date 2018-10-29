/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.common.entity.redis;

import com.jfinal.plugin.activerecord.Record;

/**
 * Created by 施洋青
 * DATE： 16-8-24.
 * redis 记录爬虫正文解析实体类
 */
public class RecordParseEntity extends Record {

    public String getValue() {
        return super.getStr("value");
    }

    public RecordParseEntity setValue(String value) {
        super.set("value", value);
        return this;
    }

    public Integer getKey() {
        return super.getInt("key");
    }

    public RecordParseEntity setKey(Integer key) {
        super.set("key", key);
        return this;
    }
}