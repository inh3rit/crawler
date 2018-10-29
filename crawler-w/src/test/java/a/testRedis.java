package a;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.w.util.RedisPool;
import org.junit.Test;

import java.util.Set;

/**
 * Created by cox on 2015/11/3.
 */
public class testRedis {

    @Test
    public void ttd1() {
        String rhost = "127.0.0.1:6379";
        RedisPool.initRedis(rhost);
        Record r = new Record();
        r.set("abc", "===").set("ef", "[]").set("zyc", "//./d");
        Cache redis = Redis.use(rhost);
        Double score = 1.0d / (System.currentTimeMillis() / 60000.0d);
        redis.zadd("test", score, r);
        Set<Record> set = redis.zrevrange("test", 0, -1);
        for (Record rd : set) {
            System.out.println(JsonKit.toJson(rd));
        }
        redis.zrem("test", r);
    }


    @Test
    public void ttd2() {
        Boolean b = Boolean.valueOf("1");
        System.out.println(b);
    }


}
