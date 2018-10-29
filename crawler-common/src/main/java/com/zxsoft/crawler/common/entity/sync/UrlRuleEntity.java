package com.zxsoft.crawler.common.entity.sync;

import com.jfinal.plugin.activerecord.Record;

/**
 * 
 * @author xuyetong
 * @time 2016/08/15
 *
 */
public class UrlRuleEntity extends Record {
	private static final long serialVersionUID = -4832935327046032612L;

	public UrlRuleEntity setId(Integer id) {
		super.set("id", id);
		return this;
	}

	public Integer getId() {
		return super.getInt("id");
	}

	public UrlRuleEntity setHost(String host) {
		super.set("host", host);
		return this;
	}

	public String getHost() {
		return super.getStr("host");
	}

	public UrlRuleEntity setSiteUrl(String site_url) {
		super.set("site_url", site_url);
		return this;
	}

	public String getSiteUrl() {
		return super.getStr("site_url");
	}

	public UrlRuleEntity setContentElement(String content_element) {
		super.set("content_element", content_element);
		return this;
	}

	public String getContentElement() {
		return super.getStr("content_element");
	}

	public UrlRuleEntity setTimeElement(String time_element) {
		super.set("time_element", time_element);
		return this;
	}

	public String getTimeElement() {
		return super.getStr("time_element");
	}

}
