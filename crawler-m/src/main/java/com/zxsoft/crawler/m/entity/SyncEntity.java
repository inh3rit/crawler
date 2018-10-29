package com.zxsoft.crawler.m.entity;

import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.type.SyncTable;

import java.util.List;

/**
 * Created by iaceob on 2015/11/14.
 */
public class SyncEntity extends Record {

    public SyncTable getTable() {
        return super.get("table");
    }
    public SyncEntity setTable(SyncTable table) {
        super.set("table", table);
        return this;
    }
    public List<Record> getData() {
        return super.get("data");
    }
    public SyncEntity setData(List<Record> data) {
        super.set("data", data);
        return this;
    }

}
