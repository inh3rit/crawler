package com.zxsoft.crawler.w.model;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.*;
import com.zxsoft.crawler.w.util.Db2;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by cox on 2015/8/17.
 */
public class WebsiteModel extends Model<WebsiteModel> {

    public static final WebsiteModel dao = new WebsiteModel();

    public Page<Record> getWebsites(Integer region, String province, String city, String area, String status,
                                    String comment, Integer type, Integer pageNumber, Integer pageSize) {
        String sql1 = "select w.id, comment, site, areaid, cityid, provinceid, region, w.type as type_id, t.remark as type_name, tid, status, cookie";
        StringBuilder sql2 = new StringBuilder();
        sql2.append("from website as w " +
                "left join cw_type as t on w.type=t.type where 1=1 ");
        sql2.append(" and region=").append(region).append(" ");
        if (StrKit.notBlank(province)) sql2.append(" and provinceId='").append(province).append("' ");
        if (StrKit.notBlank(city)) sql2.append(" and cityId='").append(city).append("' ");
        if (StrKit.notBlank(area)) sql2.append(" and areaId='").append(area).append("' ");
        if (StrKit.notBlank(status)) sql2.append(" and status='").append(status).append("' ");
        if (StrKit.notBlank(comment)) sql2.append(" and comment like '%").append(comment).append("%'");
        if (type!=null) sql2.append(" and w.type=").append(type).append(" ");
        sql2.append(" order by ctime desc");
        return Db2.paginate(pageNumber, pageSize, sql1, sql2.toString());
    }

    @Deprecated
    public Page<Record> getWebsites(String condition, Integer pageNumber, Integer pageSize){
        String sql = "select * ";
        return Db2.paginate(pageNumber, pageSize, sql, "from website " + (StrKit.isBlank(condition) ? "" : " where ") + condition + " order by comment");
    }


    public Record getWebsiteById(Integer id) {
        String sql = "select " +
                "w.id, w.tid, w.type, w.region, w.status, w.site, w.comment, w.cookie, " +
                "w.provinceid, w.cityid, w.areaid, l1.name as provincename, " +
                "l2.name as ciyname, l3.name as areaname " +
                "from website as w " +
                "left join location as l1 on w.provinceid=l1.id " +
                "left join location as l2 on w.cityid=l2.id " +
                "left join location as l3 on w.areaid=l3.id " +
                "where w.id=?";
        return Db2.findFirst(sql, id);
    }

//    @Deprecated
//    public Boolean updateWebsite(Record record) {
//        String sql = "update website set tid=?, region=?, site=?, comment=?, site=?, provinceid=?, cityid=?, areaid=? where id=?";
//        Integer res = Db2.update(sql,
//                record.getStr("tid"),
//                record.getStr("region"),
//                record.getStr("site"),
//                record.getStr("comment"),
//                record.getStr("site"),
//                record.getStr("provinceid"),
//                record.getStr("cityid"),
//                record.getStr("areaid"),
//                record.getStr("id")
//                );
//        return res!=0;
//    }
//
//    @Deprecated
//    public Boolean saveWebsite(Record record) {
//        String sql = "insert into website (tid, region, site, comment, status, provinceid, cityid, areaid, type) values (?,?,?,?,?,?,?,?, ?)";
//        Boolean res = Db2.update(sql,
//                record.getStr("tid"),
//                record.getStr("region"),
//                record.getStr("site"),
//                record.getStr("comment"),
//                record.getStr("status"),
//                record.getStr("provinceid"),
//                record.getStr("cityid"),
//                record.getStr("areaid"),
//                "001"
//                )!=0;
//        return res;
//    }


    public Boolean existTid404(Integer tid) {
        String sql = "select 1 from tid404 where tid=?";
        return Db2.findFirst(sql, tid)!=null;
    }

    public Boolean deleteTid404(Integer tid) {
        String sql = "delete from tid404 where tid=?";
        return Db2.update(sql, tid)!=0;
    }


    public Boolean updateWebsite(Integer id, Integer tid, Integer region, String url, String comment,
                                 Integer province, Integer city, Integer area, String status, Integer type,
                                 String cookie) {
        String sql = "update website set tid=?, region=?, site=?, comment=?, provinceid=?, cityid=?, areaid=?, status=?, type=?, cookie=? where id=?";
        return Db2.update(sql, tid, region, url, comment, province, city, area, status, type, cookie, id)!=0;
    }

    public Boolean saveWebsite(Integer tid, Integer region, String url, String comment, String status,
                               Integer province, Integer city, Integer area, Integer type, String cookie) {
        String sql = "insert into website (tid, region, site, comment, status, provinceid, cityid, areaid, type, cookie) values (?,?,?,?,?,?,?,?,?,?)";
        Boolean res = Db2.update(sql, tid, region, url, comment, status, province, city, area, type, cookie)!=0;
        if (res && this.existTid404(tid)) this.deleteTid404(tid);
        return res;
    }


    public Boolean deleteWebsite(final Integer id) {
        Boolean res = Db2.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                String sql = "delete from website where id=?";
                List<Record> sections = SectionModel.dao.getSectionBySite(id);
                if (sections==null) return Db2.update(sql, id)!=0;
                for (Record s : sections)
                    if (!SectionModel.dao.deleteSection(s.getInt("id"))) return false;
                return Db2.update(sql, id)!=0;
            }
        });
        return res;
    }


    /**
     * 获取搜索境外网站
     * @return Record
     */
    public List<Record> getOutsideWebsites() {
        String sql = "select id, comment, site, areaid, cityid, provinceid, region, type, tid, status from website where region=? and status=?";
        return Db2.find(sql, 0, "open");
    }

}
