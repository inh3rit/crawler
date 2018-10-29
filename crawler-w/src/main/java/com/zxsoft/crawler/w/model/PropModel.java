package com.zxsoft.crawler.w.model;

import com.zxsoft.crawler.w.util.Db2;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.type.PropKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by cox on 2015/12/18.
 */
public class PropModel {

    private static final Logger log = LoggerFactory.getLogger(PropModel.class);

    public static final PropModel dao = new PropModel();


    private Boolean deleteProp(String id) {
        String sql = "delete from cw_prop where id=?";
        return Db2.update(sql, id)!=0;
    }

    public Boolean deletePropReptileByProp(String prop) {
        String sqlExist = "select 1 from cw_prop_reptile where prop=?";
        if (Db2.findFirst(sqlExist, prop)==null) return true;
        String sqlDelete = "delete from cw_prop_reptile where prop=?";
        return Db2.update(sqlDelete, prop)!=0;
    }

    public Boolean deletePropReptileByReptile(Integer reptile) {
        String sqlExist = "select 1 from cw_prop_reptile where reptile=?";
        if (Db2.findFirst(sqlExist, reptile)==null) return true;
        String sqlDelete = "delete from cw_prop_reptile where reptile=?";
        return Db2.update(sqlDelete, reptile)!=0;
    }

    public Boolean saveProp(PropKey key, String val, String mark, String usr) {
        String sql = "insert into cw_prop(name, val, type, mark, usr) values (?,?,?,?,?)";
        return Db2.update(sql, key.getKey(), val, key.getType().getIndex(), mark, usr)!=0;
    }

    public List<Record> getPropList() {
        String sql = "select id, name, val, mark, usr, mtime from cw_prop order by mtime desc";
        return Db2.find(sql);
    }

    public Boolean updateProp(String id, String val, String mark, String usr) {
        String sql = "update cw_prop set val=?, mark=?, usr=?, mtime=now() where id=?";
        return Db2.update(sql, val, mark, usr, id)!=0;
    }


    public Boolean deletePropInfo(final String id) {
        Boolean res = Db2.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                if (!PropModel.dao.deleteProp(id)) return false;
                return PropModel.dao.deletePropReptileByProp(id);
            }
        });
        return res;
    }


    public List<Record> getReptileNoUsePropList(Integer reptile) {
        String sql = "select " +
                "p.id, p.name, p.val, p.type, p.mark, p.usr, p.mtime " +
                "from cw_prop as p " +
                "left join cw_prop_reptile as pr on p.id=pr.prop and pr.reptile=? " +
                "where pr.prop is null";
        return Db2.find(sql, reptile);
    }

    public List<Record> getReptileProp(Integer reptile) {
        String sql = "select " +
                "pr.prop, pr.reptile, p.name, p.val, p.type, p.mark " +
                "from cw_prop_reptile as pr " +
                "left join cw_prop as p on pr.prop=p.id " +
                "where pr.reptile=? ";
        return Db2.find(sql, reptile);
    }

    private Boolean saveReptilePropInfo(Integer reptile, Integer prop, String cusr) {
        String sql = "insert into cw_prop_reptile (prop, reptile, usr, ctime) values (?,?,?, now())";
        return Db2.update(sql, prop, reptile, cusr)!=0;
    }

    public Boolean saveReptileProp(final Integer reptile, final Integer[] props, final String cusr) {
        Boolean res = Db2.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                if (!PropModel.dao.deletePropReptileByReptile(reptile)) return false;
                for (Integer prop : props)
                    if (!PropModel.dao.saveReptilePropInfo(reptile, prop, cusr))
                        return false;
                return true;
            }
        });
        return res;
    }

}
