package com.zxsoft.crawler.s.model;

import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.sync.SensitiveKeyWordEntity;
import com.zxsoft.crawler.common.entity.sync.UrlRuleEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.type.SyncTable;
import com.zxsoft.crawler.s.kit.JsonKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;

public class SensitiveKeyWordModel {
    private static final Logger log = LoggerFactory.getLogger(SensitiveKeyWordModel.class);
    public static final SensitiveKeyWordModel dao = new SensitiveKeyWordModel(Const.CACHE_NAME);
    public static Cache redis;

    public SensitiveKeyWordModel(String cacheName) {
        SensitiveKeyWordModel.redis = Redis.use(cacheName);
    }

    public SensitiveKeyWordEntity[] getAllSensitiveKeyWord() {
        try {
            Set<String> set = redis.zrevrange(SyncTable.SENSITIVE_KEYWORDS.getName(), 0, -1);
            if (CollectionKit.isEmpty(set))
                return null;
            SensitiveKeyWordEntity[] skws = new SensitiveKeyWordEntity[set.size()];
            Iterator<String> it = set.iterator();
            Integer ix = 0;
            SensitiveKeyWordEntity skw;
            while (it.hasNext()) {
                String jn = it.next();
                Record s = JsonKit.fromJson(jn, Record.class);
                skw = new SensitiveKeyWordEntity();
                for (String key : s.getColumnNames())
                    skw.set(key, s.get(key));
                skws[ix] = skw;
                ix += 1;
            }
            it = null;
            set = null;
            return skws;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new SensitiveKeyWordEntity[]{};
    }
}
