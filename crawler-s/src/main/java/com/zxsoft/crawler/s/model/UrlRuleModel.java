package com.zxsoft.crawler.s.model;

import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.sync.UrlRuleEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.type.SyncTable;
import com.zxsoft.crawler.s.kit.JsonKit;

public class UrlRuleModel {
	private static final Logger log = LoggerFactory.getLogger(UrlRuleModel.class);
	public static final UrlRuleModel dao = new UrlRuleModel(Const.CACHE_NAME);
	public static Cache redis;

	public UrlRuleModel(String cacheName) {
		UrlRuleModel.redis = Redis.use(cacheName);
	}

	public UrlRuleEntity[] getAllUrlRule() {
		try {
			Set<String> set = redis.zrevrange(SyncTable.URL_RULE.getName(), 0, -1);
			if (CollectionKit.isEmpty(set))
				return null;
			UrlRuleEntity[] ures = new UrlRuleEntity[set.size()];
			Iterator<String> it = set.iterator();
			Integer ix = 0;
			while (it.hasNext()) {
				String jn = it.next();
				Record s = JsonKit.fromJson(jn, Record.class);
				UrlRuleEntity ure = new UrlRuleEntity();
				for (String key : s.getColumnNames())
					ure.set(key, s.get(key));
				ures[ix] = ure;
				ix += 1;
			}
			it = null;
			set = null;
			return ures;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new UrlRuleEntity[] {};
	}
}
