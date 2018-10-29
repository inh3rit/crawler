/**
 * 区域实体包
 */
package com.zxsoft.crawler.common.entity.reptile;

import java.sql.Timestamp;

import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.type.JobType;

/**
 * 区域实体类
 */
public class ReptileEntity extends Record {

	public Integer getId() {
		return super.getInt("id");
	}

	public ReptileEntity setId(Integer id) {
		super.set("id", id);
		return this;
	}

	public String getName() {
		return super.getStr("name");
	}

	public ReptileEntity setName(String name) {
		super.set("name", name);
		return this;
	}

	public String getRedis() {
		return super.getStr("redis");
	}

	public ReptileEntity setRedis(String redis) {
		super.set("redis", redis);
		return this;
	}

	public JobType getType() {
		return super.get("type");
	}

	public ReptileEntity setType(Integer type) {
		super.set("type", JobType.getIndex(type));
		return this;
	}

	public String getAlias() {
		return super.getStr("alias");
	}

	public ReptileEntity setAlias(String alias) {
		super.set("alias", alias);
		return this;
	}

	public String getUrl() {
		return super.getStr("url");
	}

	public ReptileEntity setUrl(String url) {
		super.set("url", url);
		return this;
	}

	public String getUsr() {
		return super.getStr("usr");
	}

	public ReptileEntity setUsr(String usr) {
		super.set("usr", usr);
		return this;
	}

	public String getPasswd() {
		return super.getStr("passwd");
	}

	public ReptileEntity setPasswd(String passwd) {
		super.set("passwd", passwd);
		return this;
	}

	public Integer getActive() {
		return super.getInt("active");
	}

	public ReptileEntity setActive(Integer active) {
		super.set("active", active);
		return this;
	}

	public Timestamp getMtime() {
		return super.get("mtime");
	}

	public ReptileEntity setMtime(Timestamp mtime) {
		super.set("mtime", mtime);
		return this;
	}

	public Integer getLocation() {
		return super.getInt("location");
	}
	public ReptileEntity setLocation(Integer location) {
		super.set("location", location);
		return this;
	}

	@Override
	public String toString() {
		return "ReptileEntity [getId()=" + getId() + ", getName()=" + getName() + ", getRedis()=" + getRedis()
				+ ", getType()=" + getType() + ", getAlias()=" + getAlias() + ", getUrl()=" + getUrl() + ", getUsr()="
				+ getUsr() + ", getPasswd()=" + getPasswd() + ", getActive()=" + getActive() + ", getMtime()="
				+ getMtime() + ",getLocation()=" + getLocation() + "]";
	}

}
