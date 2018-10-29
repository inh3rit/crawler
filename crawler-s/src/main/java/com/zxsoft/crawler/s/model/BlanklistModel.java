package com.zxsoft.crawler.s.model;

import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.sync.BlanklistEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.type.SyncTable;
import com.zxsoft.crawler.s.kit.JsonKit;

public class BlanklistModel {
	private static final Logger log = LoggerFactory.getLogger(BlanklistModel.class);
	public static final BlanklistModel dao = new BlanklistModel(Const.CACHE_NAME);
	public static Cache redis;

	public BlanklistModel(String cacheName) {
		BlanklistModel.redis = Redis.use(cacheName);
	}

	public BlanklistEntity[] getAllBlanklist() {
		try {
			Set<String> set = redis.zrevrange(SyncTable.BLANKLIST.getName(), 0, -1);
			if (CollectionKit.isEmpty(set))
				return null;
			BlanklistEntity[] blankes = new BlanklistEntity[set.size()];
			Iterator<String> it = set.iterator();
			Integer ix = 0;
			while (it.hasNext()) {
				String jn = it.next();
				Record s = JsonKit.fromJson(jn, Record.class);
				BlanklistEntity blanke = new BlanklistEntity();
				for (String key : s.getColumnNames())
					blanke.set(key, s.get(key));
				blankes[ix] = blanke;
				ix += 1;
			}
			it = null;
			set = null;
			return blankes;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new BlanklistEntity[] {};
	}
}
