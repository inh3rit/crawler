package com.zxsoft.crawler.p.occupancy;

public class UrlEntity {

	private String domain;
	private int type;

	public UrlEntity(String domain, int type) {
		super();
		this.domain = domain;
		this.type = type;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
