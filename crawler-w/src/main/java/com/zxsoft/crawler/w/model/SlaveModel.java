package com.zxsoft.crawler.w.model;

import com.alibaba.fastjson.JSONException;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.zxsoft.crawler.w.util.Db2;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.w.util.JsonKit;
import com.zxsoft.crawler.w.util.RedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by cox on 2015/9/8.
 */
public class SlaveModel {

    private static final Logger log = LoggerFactory.getLogger(SlaveModel.class);
    public static final SlaveModel dao = new SlaveModel();

    private Prop conf = PropKit.use(Const.PROCFGFILE);

    /*
select s.id, s.cli, s.ip, s.reptile, ifnull(j.amount, 0) as amount from info_slave as s
left join (
select cli, count(cli) as amount from run_job group by cli
) as j on s.cli=j.cli
where s.reptile=8

获取slaveinfo信息
     */

    public List<Record> getSlaveInfo(Integer reptile) {
        String sql = "select s.id, s.cli, s.ip, s.reptile, ifnull(j.amount, 0) as amount, s.mtime " +
                "from info_slave as s " +
                "left join ( " +
                "select cli, count(cli) as amount from run_job group by cli " +
                ") as j on s.cli=j.cli " +
                "where s.reptile=?";
        return Db2.find(sql, reptile);
        /*
        try {
            Cache redis = Redis.use(cacheName);
            Set set = redis.zrevrange(this.conf.get("db.redis.key.slave"), 0, -1);
            Iterator it = set.iterator();
            List<SlaveInfoEntity> list = new ArrayList<>();
            while (it.hasNext()) {
                String json = it.next().toString();
                SlaveInfoEntity sie = JsonKit.fromJson(json, SlaveInfoEntity.class);
                list.add(sie);
            }
            return list;
        } catch (JSONException e) {
            log.error("Json 数据转换失败, 并非标准的 Slave 信息数据", e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
        */
    }


    public List<JobEntity> getSlaveJobs(String cacheName) {
        try {
            Cache redis = Redis.use(cacheName);
            Set set = redis.zrevrange(this.conf.get("db.redis.key.job.network"), 0, -1);
            Iterator it = set.iterator();
            List<JobEntity> list = new ArrayList<>();
            while (it.hasNext()) {
                String json = it.next().toString();
                JobEntity je = JsonKit.fromJson(json, JobEntity.class);
                list.add(je);
            }
            return list;
        } catch (JSONException e) {
            log.error("Json 数据转换失败, 并非标准的 Slave 信息数据", e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        RedisPool.remove(cacheName);
        return null;
    }

    /*
    public void test(String cacheName) {
        try {
            Cache redis = Redis.use(cacheName);
            String json = "{\"identify_md5\":\"iaceob\",\"ip\":\"115.239.210.52\",\"city_code\":100000,\"goInto\":true,\"sectionId\":757,\"province_code\":100000,\"type\":\"百度新闻搜索\",\"keywordEncode\":null,\"platform\":6,\"listRule\":{\"urldom\":\"h3 a\",\"datedom\":\"div.c-summary p\",\"authordom\":\"\",\"linedom\":\"div.result\",\"updatedom\":\"\",\"category\":\"search\",\"listdom\":\"div#content_left\",\"ajax\":false,\"synopsisdom\":\"div.c-summary\"},\"url\":\"http:\\/\\/news.baidu.com\\/ns?word=%s&tn=news&from=news&cl=2&rn=20&ct=1\",\"recurrence\":false,\"jobId\":504630,\"country_code\":1,\"source_id\":927,\"keyword\":\"广州 罪犯\",\"locationCode\":100000,\"jobType\":\"NETWORK_SEARCH\",\"source_name\":\"百度新闻搜索\"}";
            JobEntity je = JsonKit.fromJson(json, JobEntity.class);
            redis.zadd("job_network", 2d, com.jfinal.kit.JsonKit.toJson(je));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    */


}
