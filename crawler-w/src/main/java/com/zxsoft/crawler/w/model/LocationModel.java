package com.zxsoft.crawler.w.model;

import com.zxsoft.crawler.w.util.Db2;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Created by cox on 2015/8/18.
 */
public class LocationModel extends Model<LocationModel> {

    public static final LocationModel dao = new LocationModel();


    public List<Record> getLocationByPid(Integer pid) {
        String sql = "select id, name from location where pid=?";
        return Db2.find(sql, pid);
    }

    public List<Record> getAllLocation() {
        // 安徽，广西
        String sql = "select id, name from location where pid in (340000,450000) or id in (340000,450000)";
        return Db2.find(sql);
    }


}
