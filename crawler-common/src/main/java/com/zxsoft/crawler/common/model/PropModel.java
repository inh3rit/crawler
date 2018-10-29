/**
 *	爬虫工具包
 *@author wg
 */
package com.zxsoft.crawler.common.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.prop.PropEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.type.PropKey;
import com.zxsoft.crawler.common.type.SyncTable;

/**
 * 配置文件操作类
 * @author wg
 */
public class PropModel {

	/**
	 * 日志操作对象
	 */
    private static final Logger log = LoggerFactory.getLogger(PropModel.class);

    /**
     *	配置文件操作对象
     */
    public static final PropModel dao = new PropModel(Const.CACHE_NAME);

    /**
     * redis操作对象
     */
    private static Cache redis;
    private final Prop conf = PropKit.use(Const.PROCFGFILE);

    /**
     *
     * @param cacheName
     */
    public PropModel(String cacheName) {
        redis = Redis.use(cacheName);
    }


    private List<PropEntity> getPropList() {
        Set set = redis.zrevrange(SyncTable.V_PROP_REPTILE.getName(), 0, -1);
        if (CollectionKit.isEmpty(set)) return null;
        List<PropEntity> pes = new ArrayList<PropEntity>();
        for (Object o : set) {
            PropEntity pe = JSON.parseObject(o.toString(), PropEntity.class);
            pes.add(pe);
        }
        return pes;
    }

    private List<PropEntity> getPropByKey(PropKey key) {
        List<PropEntity> pes = this.getPropList();
        if (CollectionKit.isEmpty(pes)) return null;
        List<PropEntity> per = new ArrayList<PropEntity>();
        for (PropEntity pe : pes) {
            if (!key.equals(pe.getName())) continue;
            per.add(pe);
        }
        pes.clear();
        return per;
    }

//    /**
//     * 放弃使用本地配置文件获取配置, 所有配置均从 web 中配置
//     * @param key
//     * @return
//     */
//    @Deprecated
//    private String getLocalPropKey(PropKey key) {
//        switch (key) {
//            case UNDEFINED:
//                return  "crawler.debug";
//            case DEBUG:
//                return  "crawler.debug";
//            case PROXY_HOST:
//                return "crawler.parse.proxy.host";
//            case PROXY_PORT:
//                return "crawler.parse.proxy.port";
//            case PROXY_USR:
//                return "crawler.parse.proxy.usr";
//            case PROXY_PASSWD:
//                return "crawler.parse.proxy.passwd";
//            case SOLR_ADDRESS:
//                return "crawler.slave.writer.address";
//            default:
//                return  "crawler.debug";
//        }
//    }

//    private String[] getLocalArray(PropKey key) {
//        String vals = this.conf.get(this.getLocalPropKey(key));
//        return StrKit.isBlank(vals) ? null : vals.split(",");
//    }

    public String[] get(PropKey key) {
        List<PropEntity> pes = this.getPropByKey(key);
        if (CollectionKit.isEmpty(pes)) return null;
        String[] res = new String[pes.size()];
        for (Integer i = 0; i < pes.size(); i++)
            res[i] = pes.get(i).getVal();
        return res;
    }

    public String getStr(PropKey key, String def) {
        String[] pes = this.get(key);
        return pes == null || pes.length == 0 ? def : pes[0];
    }

    public String getStr(PropKey key) {
        return this.getStr(key, null);
    }

    public Integer getInt(PropKey key, Integer def) {
        String pv = this.getStr(key);
        return StrKit.isBlank(pv) ? def : Integer.valueOf(pv);
    }

    public Integer getInt(PropKey key) {
        return this.getInt(key, null);
    }

    public Long getLong(PropKey key, Long def) {
        String pv = this.getStr(key);
        return StrKit.isBlank(pv) ? def : Long.valueOf(pv);
    }

    public Long getLong(PropKey key) {
        return this.getLong(key, null);
    }

    public Double getDouble(PropKey key, Double def) {
        String pv = this.getStr(key);
        return StrKit.isBlank(pv) ? def : Double.valueOf(pv);
    }

    public Double getDouble(PropKey key) {
        return this.getDouble(key, null);
    }

    public Boolean getBoolean(PropKey key, Boolean def) {
        String pv = this.getStr(key);
        return StrKit.isBlank(pv) ? def : Boolean.valueOf(pv);
    }

    public Boolean getBoolean(PropKey key) {
        return this.getBoolean(key, null);
    }

}
