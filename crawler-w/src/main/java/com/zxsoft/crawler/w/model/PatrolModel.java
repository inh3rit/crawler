package com.zxsoft.crawler.w.model;

import com.zxsoft.crawler.w.util.Db2;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by cox on 2015/12/3.
 */
public class PatrolModel extends Model<PatrolModel> {

    private static final Logger log = LoggerFactory.getLogger(PatrolModel.class);
    public static final PatrolModel dao = new PatrolModel();

    private void deleteSite(Integer reptile) {
        String sql = "delete from cw_patrol where reptile=?";
        Db2.update(sql, reptile);
    }

    private Boolean saveSite(Integer section, Integer reptile, String usr) {
        String sql = "insert into cw_patrol(reptile, section, usr, prev_time) values (?,?,?,now())";
        return Db2.update(sql, reptile, section, usr) != 0;
    }

    public Boolean saveSite(final Integer[] sections, final Integer reptile, final String usr) {
        Boolean res = Db2.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                PatrolModel.dao.deleteSite(reptile);
                if (sections == null || sections.length == 0) return true;
                for (Integer j : sections)
                    if (!PatrolModel.dao.saveSite(j, reptile, usr))
                        return false;
                return true;
            }
        });
        return res;
    }


    public List<Record> getSectionsByReptile(Integer reptile) {
        String sql = "select   " +
                "w.id as website_id, s.id as section_id, w.comment as site_name,   " +
                "s.comment as section_name, p.reptile " +
                "from section as s   " +
                "left join website as w on s.site=w.id   " +
                "left join cw_patrol as p on s.id=p.section and p.reptile=? " +
                "where w.region=? and w.status=? " +
                "order by w.comment, s.comment ";
        return Db2.find(sql, reptile, 0, "open");
    }


    /**
     * 如果当某个板块删除后, 相应的巡检网站任务也需要删除
     *
     * @param sectionId 板块 ID
     */
    public void deleteSiteBySection(Integer sectionId) {
        String sql = "delete from cw_patrol where section=?";
        Db2.update(sql, sectionId);
    }


}
