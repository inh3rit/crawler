package com.zxsoft.crawler.w.model;

import com.zxsoft.crawler.w.util.Db2;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.kit.CollectionKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Created by cox on 2015/10/28.
 */
public class BlacklistModel {

    private static final Logger log = LoggerFactory.getLogger(BlacklistModel.class);
    public static final BlacklistModel dao = new BlacklistModel();


    public Boolean saveBlacklist(String name, String regex, String summary, String usr) {
        String sql = "insert into blacklist (name, regex, summary, usr) values (?,?,?,?)";
        return Db2.update(sql, name, regex, summary, usr) != 0;
    }


    public Boolean hasName(String name) {
        String sql = "select 1 from blacklist where name=?";
        return Db2.findFirst(sql, name) != null;
    }


    public Boolean updateBlacklist(Integer id, String name, String regex, String summary, String usr) {
        String sql = "update blacklist set name=?, regex=?, summary=?, usr=?, mtime=now() where id=?";
        return Db2.update(sql, name, regex, summary, usr, id) != 0;
    }

    public Page<Record> getPageBlacklist(Integer pageNumber, Integer pageSize) {
        String sql1 = "select id, name, regex, summary, usr, mtime";
        String sql2 = "from blacklist order by mtime desc";
        return Db2.paginate(pageNumber, pageSize, sql1, sql2);
    }

    public List<Record> getAllBlacklist() {
        String sql = "select id, name, regex, summary, usr, mtime from blacklist order by mtime desc";
        return Db2.find(sql);
    }

    public Boolean delBlacklist(Integer id) {
        String sql = "delete from blacklist where id=?";
        return Db2.update(sql, id) != 0;
    }

    private Boolean syncBlacklistToRedis(String cacheName, String tbkey, Record blacklist) {
        Cache redis = Redis.use(cacheName);
        Double score = 1.0d / (System.currentTimeMillis() / 60000.0d);
        return redis.zadd(tbkey, score, blacklist)!=0L;
    }

    @Deprecated
    public void sync(String tbkey, List<Record> list) {
        String[] rncs = ReptileModel.dao.getRedisCacheNames();
        if (rncs==null||rncs.length==0) return;
        for (String cachename : rncs) {
            this.sync(cachename, tbkey, list);
        }
    }

    @Deprecated
    public void sync(String cacheName, String tbkey, List<Record> list) {
        if (list == null || list.isEmpty()) return;
        Cache redis = Redis.use(cacheName);
        Set<Record> bls = redis.zrevrange(tbkey, 0, -1);
        if (CollectionKit.isEmpty(bls)) {
            for (Record ibk : list)
                this.syncBlacklistToRedis(cacheName, tbkey, ibk);
            return;
        }
        for (Record ibk : list) {
            for (Record rbk : bls) {
                if (ibk.getInt("id").equals(rbk.getInt("id"))&&ibk.getStr("regex").equals(rbk.getStr("regex"))) continue;
                redis.zrem(tbkey, rbk);
                this.syncBlacklistToRedis(cacheName, tbkey, ibk);
            }
        }
    }


}
