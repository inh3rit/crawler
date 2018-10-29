package com.zxsoft.crawler.w.model;

import com.zxsoft.crawler.w.util.Db2;
import com.jfinal.plugin.activerecord.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by cox on 2016/1/4.
 */
public class JobTypeModel {

    private static final Logger log = LoggerFactory.getLogger(JobTypeModel.class);
    public static final JobTypeModel dao = new JobTypeModel();

    public List<Record> getAllJobType() {
        String sql = "select id, type, remark from cw_type order by type";
        return Db2.find(sql);
    }

    public Record getJobTypeByMark(Integer mark) {
        String sql = "select id, type, remark from cw_type where mark=?";
        return Db2.findFirst(sql, mark);
    }

}
