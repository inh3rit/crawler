package com.zxsoft.crawler.w.controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.w.model.ReptileModel;
import com.zxsoft.crawler.w.model.SlaveModel;
import com.zxsoft.crawler.w.util.RedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by cox on 2015/8/31.
 */
public class MonitorController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(MonitorController.class);

    public void index() {
        List<Record> reptiles = ReptileModel.dao.getAvailableListReptile();
        super.setAttr("reptiles", reptiles);
        super.render("/monitor/index.html");
    }



    public void spider_info() {
        Integer rid = super.getParaToInt("reptile", 0);
        if (rid==0) {
            super.render("/monitor/reptile_fail.html");
            return;
        }
        Record reptile = ReptileModel.dao.getReptileById(rid);
        String cacheName = reptile.getStr("redis");
        if (reptile==null) {
            super.render("/monitor/reptile_fail.html");
            return;
        }
        if (!RedisPool.initRedis(cacheName)) {
            super.render("/monitor/reptile_fail.html");
            return;
        }
        // TODO Redis 中查询 slave 执行信息
        List<Record> sies = SlaveModel.dao.getSlaveInfo(rid);
        List<JobEntity> jes = SlaveModel.dao.getSlaveJobs(cacheName);
        super.setAttr("sies", sies);
        super.setAttr("jes", jes);
        super.render("/monitor/reptile_info.html");
    }



    /*
    public void test(){
        String val = super.getPara("json");
        Integer rid = super.getParaToInt("reptile", 0);
        Record reptile = ReptileModel.dao.getReptileById(rid);
        String cacheName = reptile.getStr("redis");
        // Double sc = Double.parseDouble(super.getPara("sc"));
        if (!RedisPool.initRedis(cacheName)) {
            super.render("/monitor/reptile_fail.html");
            return;
        }
        SlaveModel.dao.test(cacheName);
        super.renderText("Success");
    }
    */


}
