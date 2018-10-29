package com.zxsoft.crawler.w.model;

import com.jfinal.kit.StrKit;
import com.zxsoft.crawler.w.util.Db2;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.w.util.RedisPool;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cox on 2015/8/28.
 */
public class ReptileModel extends Model<ReptileModel> {

    public static final ReptileModel dao = new ReptileModel();


    public List<Record> getListReptile() {
        String sql = "select r.id, r.name, redis, r.type as type_id, t.remark as type_name, alias, url, usr, active, r.location from reptile as r " +
                "left join cw_type as t on r.type=t.type " +
                "order by r.type, r.alias, r.name ";
        return Db2.find(sql);
    }

    public List<Record> getAvailableListReptile() {
        String sql = "select id, name, redis, type from reptile where type is not null or type<>0";
        return Db2.find(sql);
    }

    public Boolean saveReptile(String name, String redis, JobType type, String alias,
                               String url, String usr, String passwd, Integer active, Integer location) {
        String sql = "insert into reptile(name, redis, type, alias, url, usr, passwd, active, location, mtime) values (?,?,?,?,?,?,?,?,?,now())";
        return Db2.update(sql, name, redis, type.getIndex(), alias, url, usr, passwd, active, location)!=0;
    }

    public Boolean updateReptile(Integer id, String name, String redis, JobType type, String alias,
                                 String url, String usr, String passwd, Integer active, Integer location) {
        String sql = "update reptile set name=?, redis=?, type=?, alias=?, url=?, usr=?, passwd=ifnull(?, passwd), active=?, location=?, mtime=now() where id=?";
        return Db2.update(sql, name, redis, type.getIndex(), alias, url, usr, passwd, active, location, id)!=0;
    }

    private Boolean deleteReptileInfo(Integer id) {
        String sql = "delete from reptile where id=?";
        return Db2.update(sql, id)!=0;
    }

    public Boolean deleteReptile(final Integer id) {
        Boolean res = Db2.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                if (!ReptileModel.dao.deleteReptileInfo(id)) return false;
                return PropModel.dao.deletePropReptileByReptile(id);
            }
        });
        return res;
    }

    public Record getReptileById(Integer id) {
        String sql = "select id, name, alias, redis, type from reptile where id=?";
        return Db2.findFirst(sql, id);
    }

    /**
     * web 服务器不能连接至每个区域的爬虫, 导致
     * 直接在 web 进行监控出现问题
     * @return
     */
    @Deprecated
    public String[] getRedisCacheNames() {
        List<Record> rps = ReptileModel.dao.getListReptile();
        if (rps==null||rps.size()==0) return null;
        Set<String> rncs = new HashSet<String>();
        for (Integer i=rps.size(); i-->0;) {
            // rcns[i] = rps.get(i).getStr("redis");
            String rhost = rps.get(i).getStr("redis");
            if (StrKit.isBlank(rhost)) continue;
            if (!RedisPool.initRedis(rhost)) continue;
            rncs.add(rhost);
        }
        return rncs.toArray(new String[rncs.size()]);
    }


    /**
     * 获取所有网络巡检爬虫
     * @return
     */
    public List<Record> getAllPatrolCrawl() {
        String sql = "select id, name, redis, type, alias, url, usr, active from reptile where type=?";
        return Db2.find(sql, 2);
    }

}
