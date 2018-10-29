package com.zxsoft.crawler.s.model;

import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.type.SyncTable;
import com.zxsoft.crawler.common.entity.sync.BlacklistEntity;
import com.zxsoft.crawler.s.kit.JsonKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by cox on 2015/11/17.
 */
public class BlacklistModel {

    private static final Logger log = LoggerFactory.getLogger(BlacklistModel.class);
    public static final BlacklistModel dao = new BlacklistModel(Const.CACHE_NAME);
    public static Cache redis;

    public BlacklistModel(String cacheName) {
        BlacklistModel.redis = Redis.use(cacheName);
    }

    public BlacklistEntity[] getAllBlacklist() {
        try {
            Set<String> set = redis.zrevrange(SyncTable.BLACKLIST.getName(), 0, -1);
            if (CollectionKit.isEmpty(set)) return null;
            BlacklistEntity[] bes = new BlacklistEntity[set.size()];
            Iterator<String> it = set.iterator();
            Integer ix = 0;
            while (it.hasNext()) {
                String jn = it.next();
                Record s = JsonKit.fromJson(jn, Record.class);
                BlacklistEntity be = new BlacklistEntity();
                for (String key : s.getColumnNames())
                    be.set(key, s.get(key));
                bes[ix] = be;
                ix += 1;
            }
            it = null;
            set = null;
            return bes;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new BlacklistEntity[]{};
    }



}
