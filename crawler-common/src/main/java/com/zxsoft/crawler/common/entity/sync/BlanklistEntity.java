package com.zxsoft.crawler.common.entity.sync;

import com.jfinal.plugin.activerecord.Record;

/**
 * Created by xuyetong on 2016/07/28.
 */
public class BlanklistEntity extends Record {
	private static final long serialVersionUID = 1822731344179987431L;

	public BlanklistEntity setId(Integer id) {
		super.set("id", id);
		return this;
	}

	public Integer getId() {
		return super.getInt("id");
	}

	public BlanklistEntity setDomain(String domain) {
		super.set("domain", domain);
		return this;
	}

	public String getDomain() {
		return super.get("domain");
	}

	public BlanklistEntity setType(Integer type) {
		super.set("type", type);
		return this;
	}

	public Integer getType() {
		return super.getInt("type");
	}

}
