package com.zxsoft.crawler.s.model;

import com.jfinal.plugin.redis.RedisPlugin;
import com.zxsoft.crawler.common.c.Const;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by cox on 2015/12/10.
 */
public class RestoreModelTest {

    @Before
    public void af() {
        RedisPlugin rp = new RedisPlugin(Const.CACHE_NAME, "192.168.40.20", 6386);
        rp.start();
    }

    @Test
    public void testSubmitRestoreJob() throws Exception {
        RestoreModel.dao.submitRestoreJob(40106, "192.168.40.106");
    }
}