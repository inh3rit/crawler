package com.zxsoft.crawler.m.model;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.RedisPlugin;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.redis.ClientInfoEntity;
import com.zxsoft.crawler.common.type.CacheKey;
import com.zxsoft.crawler.m.model.redis.ClientModel;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ClientModelTest {

    private static final Logger log = LoggerFactory.getLogger(ClientModelTest.class);

    @Before
    public void before() {
        String host = "192.168.32.11:6386";
        String[] redisConf = host.split(":");
        RedisPlugin rp = new RedisPlugin(Const.CACHE_NAME,
                redisConf[0], Integer.valueOf(redisConf[1]));
        if (rp.start()) {
            log.info("连接 Redis: {} 成功, CacheName: {}", host, Const.CACHE_NAME);
        } else {
            log.error("连接 Redis: {} 失败, 请检查配置后重试.", host);
        }
    }

    @Test
    public void testGetAllRunJob() throws Exception {
        while (true) {
            try {
                List<ClientInfoEntity> lrje = ClientModel.dao.getAllClientInfo();
                StringBuilder sb = new StringBuilder();
                Integer sum = 0;
                for (ClientInfoEntity rje : lrje) {
                    sb.append("cli: ").append(rje.getCli()).append(", ip: ").append(rje.getIp())
                            .append(", total: ").append(rje.getTotal()).append("\n");
                    sum += rje.getTotal();
                }
                System.out.println(sb.toString());
                this.testGetUseableClient(lrje.size(), sum);
                System.out.println("=======================");
                TimeUnit.SECONDS.sleep(5L);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }


    public void testGetUseableClient(Integer count, Integer sum) throws Exception {
        try {
            ClientInfoEntity client = ClientModel.dao.getUseableClient(12000L * 1000L);
            StringBuilder sb = new StringBuilder();
            sb.append("total ").append(count).append(" client, ").append(" the best client: ").append("cli: ")
                    .append(client.getCli()).append(", ip:").append(client.getIp())
                    .append(", amount: ").append(client.getTotal()).append(", sum: ").append(sum);
            System.out.println(sb.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public void delExpireJob() {
        Cache redis = Redis.use(Const.CACHE_NAME);
        Set<String> set = redis.zrevrange(CacheKey.KEY_RUN.getKey(), 0, -1);
        String hs = "192.168.32.12 st2\n" +
                "192.168.32.13 st3\n" +
                "192.168.32.14 st4\n" +
                "192.168.32.15 st5\n" +
                "192.168.32.16 st6\n" +
                "192.168.32.17 st7\n" +
                "192.168.32.18 st8\n" +
                "192.168.32.19 st9\n" +
                "192.168.32.20 st10\n" +
                "192.168.32.195 st11\n" +
                "192.168.32.196 st12\n" +
                "192.168.32.197 st13\n" +
                "192.168.32.198 st14\n" +
                "192.168.32.199 st15\n";
        String[] h = hs.split("\n");
        List<String> hosts = new ArrayList<>();
        for (String b : h) {
            hosts.add(b.split(" ")[0]);
        }
        for (String s : set) {
            String[] cs = s.split(":");
            for (String ho : hosts) {
                if (!cs[1].equals(ho)) continue;
                System.out.println(s);
                redis.zrem(CacheKey.KEY_RUN.getKey(), s);
            }
        }

    }
}