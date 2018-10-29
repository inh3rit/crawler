/**
 * 代理实体包
 */
package com.zxsoft.crawler.common.entity.prop;

import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.type.PropKey;
import com.zxsoft.crawler.common.type.PropType;

/**
 *  代理实体类
 *  @author xiawenchao
 *  @version 3.4
 */
public class PropEntity extends Record {

    public PropType getType() {
        return PropType.getType(super.getInt("type"));
    }

    public PropEntity setType(Integer type) {
        super.set("type", type);
        return this;
    }

    public Integer getId() {
        return super.getInt("prop");
    }

    public PropEntity setId(String id) {
        super.set("prop", id);
        return this;
    }

    public Integer getReptile() {
        return super.getInt("reptile");
    }

    public PropEntity setReptile(Integer reptile) {
        super.set("reptile", reptile);
        return this;
    }

    public PropEntity setType(PropType type) {
        return this.setType(type.getIndex());
    }

    public PropKey getName() {
        return PropKey.getConfKey(super.getStr("name"));
    }

    public PropEntity setName(String name) {
        super.set("name", name);
        return this;
    }

//    public PropEntity setName(PropKey name) {
//        return this.setName(name.getKey());
//    }

    public String getVal() {
        return super.getStr("val");
    }

    public PropEntity setVal(String val) {
        super.set("val", val);
        return this;
    }

    public String getMark() {
        return super.getStr("mark");
    }

    public PropEntity setMark(String mark) {
        super.set("mark", mark);
        return this;
    }

    public String getUsr() {
        return super.getStr("usr");
    }

    public PropEntity setUsr(String usr) {
        super.set("usr", usr);
        return this;
    }

	@Override
	public String toString() {
		return "PropEntity [getType()=" + getType() + ", getId()=" + getId() + ", getReptile()=" + getReptile()
				+ ", getName()=" + getName() + ", getVal()=" + getVal() + ", getMark()=" + getMark() + ", getUsr()="
				+ getUsr() + "]";
	}


//    public Timestamp getMtime() {
//        return super.getTimestamp("mtime");
//    }
//
//    public PropEntity setMtime(Timestamp timestamp) {
//        super.set("mtime", timestamp);
//        return this;
//    }

}
