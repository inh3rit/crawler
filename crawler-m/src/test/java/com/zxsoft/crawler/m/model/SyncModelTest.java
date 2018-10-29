package com.zxsoft.crawler.m.model;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.RedisPlugin;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.type.SourceType;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncModelTest {
    private static final Logger log = LoggerFactory.getLogger(SyncModelTest.class);
    private Prop conf = PropKit.use(Const.PROCFGFILE);

    @Before
    public void setUp() throws Exception {

        try {
            DruidPlugin dpm = new DruidPlugin(
                    this.conf.get("db.mysql.url"),
                    this.conf.get("db.mysql.username"),
                    this.conf.get("db.mysql.passwd")
            );
            dpm.addFilter(new StatFilter());
            WallFilter wm = new WallFilter();
            wm.setDbType("mysql");
            dpm.addFilter(wm);

            ActiveRecordPlugin arpm = new ActiveRecordPlugin(SourceType.MYSQL_CONF.getName(), dpm);
            arpm.setDialect(new MysqlDialect());
            arpm.setShowSql(this.conf.getBoolean("crawler.debug", false));

            if (!dpm.start()||!arpm.start()) {
                log.error("connect database mysql: {} fail.", this.conf.get("db.mysql.url"));
            }
            log.info("connect mysql {} success", this.conf.get("db.mysql.url"));


            /*
             * 启动 redis
             */
            String[] redisConf = "127.0.0.1:6379".split(":");
            RedisPlugin rp = new RedisPlugin(Const.CACHE_NAME,
                    redisConf[0], Integer.valueOf(redisConf[1]));
            if (rp.start()) {
                log.info("连接 Redis: {} 成功, CacheName: {}", "127.0.0.1", Const.CACHE_NAME);
            } else {
                log.error("连接 Redis: {} 失败, 请检查配置后重试.", "127.0.0.1");
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    @Test
    public void syt() {
        Record r1 = MysqlModel.dao.getReptileByAlias("ttd");
        Record r2 = MysqlModel.dao.getReptileByAlias("huainan");
        System.out.println(r1.equals(r2));
    }


    @Test
    public void ttd3() {
        String json = "{\"ajax\":false,\"author\":\"div.authi a\",\"content\":\"div.pcb\",\"ctime\":\"2015-10-14 13:41:51\",\"date\":\"div.authi em\",\"encode\":null,\"fetchOrder\":true,\"forwardNum\":\"\",\"host\":\"http:\\/\\/0556wjw.com \",\"listurl\":\"http:\\/\\/0556wjw.com\\/forum-39-1.html\",\"master\":\"div#postlist div[id^=post]\",\"reply\":\"div#postlist\",\"replyAuthor\":\"div.authi a\",\"replyContent\":\"div.pcb\",\"replyDate\":\"\",\"replyNum\":\"div.hm span:eq(4)\",\"reviewNum\":\"div.hm span:eq(1)\",\"sources\":\"\",\"subReply\":\"\",\"subReplyAuthor\":\"\",\"subReplyContent\":\"\",\"subReplyDate\":\"\"}";
        Cache redis = Redis.use(Const.CACHE_NAME);
        Long res = redis.zrem("conf_list", json);
        System.out.println(res);
    }



//    @Test
//    public void ttd5() {
//        Record r = new Record();
//        r.set("summary", "http://baidu.com")
//                .set("regex", "http://baidu.com")
//                .set("id", 29).set("mtime", Timestamp.valueOf("2015-11-17 16:30:09"))
//                .set("usr", "405532476501528576");
//        String sr = SeriaRecord.serialize(r);
//        System.out.println(sr);
//        Cache redis = Redis.use(Const.CACHE_NAME);
//        Long res1 = redis.zadd(Const.TBKEY_TEST, 1D, sr);
//        System.out.println("save to redis result: " + res1);
//
//        Set<String> set = redis.zrevrange(Const.TBKEY_TEST, 0, -1);
//        for (String s : set) {
//            System.out.println(s);
//            Record r2 = SeriaRecord.deserialize(s);
//            System.out.println(JsonKit.toJson(r));
//            System.out.println(JsonKit.toJson(r2));
//            System.out.println(r.equals(r2));
//        }
//    }



}