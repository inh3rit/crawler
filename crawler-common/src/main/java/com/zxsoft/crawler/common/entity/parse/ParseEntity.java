package com.zxsoft.crawler.common.entity.parse;

import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.entity.out.WriterEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by cox on 2015/10/14.
 */
public class ParseEntity extends Record {


    public Map<Long, Long> getTimestamp() {
        return super.get("timestamp");
    }

    public ParseEntity setTimestamp(Map<Long, Long> timestamp) {
        super.set("timestamp", timestamp);
        return this;
    }

    public List<WriterEntity> getWriterData() {
        return super.get("writer-data");
    }

    public ParseEntity setWriterData(List<WriterEntity> wes) {
        super.set("writer-data", wes);
        return this;
    }


}
