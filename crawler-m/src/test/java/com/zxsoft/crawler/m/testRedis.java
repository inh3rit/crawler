package com.zxsoft.crawler.m;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.RedisPlugin;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.redis.ListRuleEntity;
import com.zxsoft.crawler.common.kit.RecordKit;
import com.zxsoft.crawler.common.type.CacheKey;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.m.model.redis.InfoModel;
import name.iaceob.kit.disgest.Disgest;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by cox on 2015/11/19.
 */
public class testRedis {

    private static final Logger log = LoggerFactory.getLogger(testRedis.class);

    @Before
    public void ttd1After() {
        // String host = "192.168.32.11";
        String host = "192.168.32.13";
        RedisPlugin rp = new RedisPlugin(Const.CACHE_NAME, host, 6379);
        if (rp.start()) {
            log.info("连接 Redis: {} 成功, CacheName: {}", host, Const.CACHE_NAME);
        } else {
            log.error("连接 Redis: {} 失败, 请检查配置后重试.", host);
        }

        String slave = "192.168.32.14";
        RedisPlugin _rp = new RedisPlugin("slave", slave, 6379);
        if (_rp.start()) {
            log.info("连接 Redis: {} 成功, CacheName: {}", host, Const.CACHE_NAME);
        } else {
            log.error("连接 Redis: {} 失败, 请检查配置后重试.", host);
        }

    }


    @Test
    public void testSet() {
        Cache redis = Redis.use(Const.CACHE_NAME);
        String res1 = redis.set(CacheKey.KEY_LOCK_SYNC.getKey(), true);
        System.out.println(res1);
        Boolean isLock1 = redis.get(CacheKey.KEY_LOCK_SYNC.getKey());
        System.out.println(isLock1);
        String res2 = redis.set(CacheKey.KEY_LOCK_SYNC.getKey(), false);
        System.out.println(res2);
        Boolean isLock2 = redis.get(CacheKey.KEY_LOCK_SYNC.getKey());
        System.out.println(isLock2);
        redis.del(CacheKey.KEY_LOCK_SYNC.getKey());
        Boolean isLock3 = redis.get(CacheKey.KEY_LOCK_SYNC.getKey());
        System.out.println(isLock3);

    }

    @Test
    public void ttd2() {
        JobEntity je = InfoModel.dao.getBasicInfos(933);
        System.out.println(JsonKit.toJson(je));
    }

    @Test
    public void ttd3() throws InterruptedException {
        Cache redis = Redis.use(Const.CACHE_NAME);
//        Set<HostAndPort> clusterNodes = new HashSet<>();
//        clusterNodes.add(new HostAndPort("192.168.32.13", 6386));
//        clusterNodes.add(new HostAndPort("192.168.32.14", 6386));
//        clusterNodes.add(new HostAndPort("192.168.32.15", 6386));
//        clusterNodes.add(new HostAndPort("192.168.32.16", 6386));
//        clusterNodes.add(new HostAndPort("192.168.32.18", 6386));
//        clusterNodes.add(new HostAndPort("192.168.32.19", 6386));
//        JedisCluster cluster = new JedisCluster(clusterNodes);
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(30);
        JobEntity je = getJe();

        for (int i = 0; i < 20; i++) {
            int _i = i;
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < 1000; j++) {
                            String jsonJob = RecordKit.toJsonSort(je);
                            String jeStr = Disgest.encodeRC4(jsonJob, Const.SALT_JOB);
                            jeStr = (_i * 1000 + j) + "," + jeStr;
                            System.out.println("write_" + (_i * 500 + j));
                            redis.zadd(CacheKey.KEY_SWAP.getKey(), 0, jeStr);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        Thread.sleep(1000000);
    }

    private JobEntity getJe() {
        String url = "http://www.baidu.com/s?wd=%s&ie=utf-8";
        String listdom = "div#content_left", linedom = "div.c-container", urldom = "h3.t > a";
        String datedom = "", synopsisdom = "", updatedom = "", authordom = "";
        String sourceName = "百度搜索", type = "百度搜索";
        Integer sourceId = 10, sectionId = 51;
        String keywordEncoding = "UTF-8", keyword = "杀人 论坛";

        ListRuleEntity lre = new ListRuleEntity();
        lre.setListdom(listdom).setLinedom(linedom).setUrldom(urldom).setDatedom(datedom).setSynopsisdom(synopsisdom)
                .setUpdatedom(updatedom).setAuthordom(authordom);
        JobEntity je = new JobEntity();
        je.setJobType(JobType.NETWORK_SEARCH).setUrl(url).setSource_name(sourceName).setSource_id(sourceId)
                .setSectionId(sectionId).setType(type).setListRule(lre).setWorkerId(0).setLocationCode(10000)
                .setProvince_code(10000).setCity_code(10000).setKeywordEncode(keywordEncoding).setJobId("0")
                .setIdentify_md5("iaceob").setCountry_code(1).setGoInto(true).setKeyword(keyword).setPlatform(6);

        return je;
    }

    @Test
    public void ttd4() throws InterruptedException {
        Cache redis = Redis.use(Const.CACHE_NAME);
//        Set<HostAndPort> clusterNodes = new HashSet<>();
//        clusterNodes.add(new HostAndPort("192.168.32.13", 6386));
//        clusterNodes.add(new HostAndPort("192.168.32.14", 6386));
//        clusterNodes.add(new HostAndPort("192.168.32.15", 6386));
//        clusterNodes.add(new HostAndPort("192.168.32.16", 6386));
//        clusterNodes.add(new HostAndPort("192.168.32.18", 6386));
//        clusterNodes.add(new HostAndPort("192.168.32.19", 6386));
//        JedisCluster cluster = new JedisCluster(clusterNodes);
        ExecutorService fixedExecutorService = Executors.newFixedThreadPool(40);
        for (int i = 0; i < 40; i++) {
            int _i = i;
            fixedExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("read_" + _i);
                    long start = System.currentTimeMillis();
                    Set<String> set = redis.zrevrange(CacheKey.KEY_SWAP.getKey(), 0, -1);
                    System.out.println(System.currentTimeMillis() - start);
                }
            });
        }

        Thread.sleep(1000000);
    }

    @Test
    public void ttd5() {
        Cache redis = Redis.use("slave");

        System.out.println("read");
        long start = System.currentTimeMillis();
        Set<String> set = redis.zrevrange(CacheKey.KEY_SWAP.getKey(), 0, -1);
//        Set<String> set = redis.zrange(CacheKey.KEY_SWAP.getKey(), 0, -1);
        System.out.println(set.size());
        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    public void ttd6() {
        String str = "a,b,c,d,e,f,g,h";
        List<String> stringList = new ArrayList<>();
        for (String s : str.split(",")) {
            stringList.add(s);
        }

        for (int i = 0; i < stringList.size(); i++) {
            System.out.println(stringList.get(i));

        }
    }

}