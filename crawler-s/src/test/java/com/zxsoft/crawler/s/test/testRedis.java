package com.zxsoft.crawler.s.test;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.RedisPlugin;
import com.zxsoft.crawler.common.c.Const;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cox on 2015/11/15.
 */
public class testRedis {

    private static final Logger log = LoggerFactory.getLogger(testRedis.class);
    private Prop conf = PropKit.use(Const.PROCFGFILE);


    @Before
    public void ttd1After() {
        String host = "192.168.32.11";
        RedisPlugin rp = new RedisPlugin(Const.CACHE_NAME, host, 6386);
        if (rp.start()) {
            log.info("连接 Redis: {} 成功, CacheName: {}", host, Const.CACHE_NAME);
        } else {
            log.error("连接 Redis: {} 失败, 请检查配置后重试.", host);
        }
    }

    @Test
    public void ttd1() {
        // JobEntity je = InfoModel.dao.getBasicInfos(217);
        // log.debug("job: {}", JsonKit.toJson(je));
        Cache redis = Redis.use(Const.CACHE_NAME);

        redis.zadd("test", .5D, "zzzzzzzzzz");
    }



}
