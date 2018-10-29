package com.zxsoft.crawler.w.model;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cox on 2015/9/1.
 */
public class MonitorModel {

    private static final Logger log = LoggerFactory.getLogger(MonitorModel.class);
    private static Prop conf = PropKit.use("crawler.properties");

    public static final MonitorModel dao = new MonitorModel();



    public void getSlaveList(String cacheName) {
        Cache redis = Redis.use(cacheName);

    }

}
