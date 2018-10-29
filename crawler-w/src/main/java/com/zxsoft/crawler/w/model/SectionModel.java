package com.zxsoft.crawler.w.model;

import com.jfinal.plugin.activerecord.*;
import com.zxsoft.crawler.w.util.Db2;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by cox on 2015/8/18.
 */
public class SectionModel extends Model<SectionModel> {

    public static final SectionModel dao = new SectionModel();

    public Page<Record> getPageSection(Integer site, Integer pageNumber, Integer pageSize) {
        String sql1 = "select s.id, s.url, s.category, s.comment, s.status, s.site, s.autourl, " +
                "s.keywordencode, a.username, c.comment as category_name, w.comment as website_name";
        String sql2 = "from section as s " +
                "left join account as a on s.creator=a. id " +
                "left join category as c on s.category=c.id " +
                "left join website as w on s.site=w.id " +
                "where s.site=?";
        return Db2.paginate(pageNumber, pageSize, sql1, sql2, site);
    }


    public Boolean saveSection(String url, String category, String comment, Integer site, Integer autoUrl, String status, String keywordencode, String account){
        String sql = "insert into section (url, category, comment, status, creator, site, autourl, keywordencode) values (?,?,?,?,?,?,?,?)";
        return Db2.update(sql, url, category, comment, status, account, site, autoUrl, keywordencode)!=0;
    }


    public Boolean updateSection(final Integer id, final String url, final String category, final String comment,
                                 final Integer site, final Integer autoUrl, final String status, final String keywordencode,
                                 final String account) {
        Boolean res = Db2.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                String sql = "update section set url=?, category=?, comment=?, status=?, creator=?, site=?, autourl=?, keywordencode=? where id=?";
                Record section = SectionModel.dao.getSectionById(id);
                // if (!ConfModel.dao.updateConfDetailUrl(section.getStr("url"), url)) return false;
                // if (!ConfModel.dao.updateConfListUrl(section.getStr("url"), url)) return false;
                ConfModel.dao.updateConfDetailUrl(section.getStr("url"), url);
                ConfModel.dao.updateConfListUrl(section.getStr("url"), url);
                return Db2.update(sql, url, category, comment, status, account, site, autoUrl, keywordencode, id)!=0;
            }
        });
        return res;
    }

    public Record getSectionById(Integer id) {
        String sql = "select id, url, category, comment, status, creator, site, autoUrl, keywordEncode from section where id=?";
        return Db2.findFirst(sql, id);
    }

    public Record getFirstSectionBySite(Integer site) {
        List<Record> list = this.getSectionBySite(site);
        if (list==null||list.isEmpty()) return null;
        return list.get(0);
    }

    public List<Record> getSectionBySite(Integer site) {
        String sql = "select id, url, category, comment, status, creator, site, autoUrl, keywordEncode from section where site=?";
        return Db2.find(sql, site);
    }


    public Boolean deleteSection(final Integer id) {
        Boolean res = Db2.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                String sql = "delete from section where id=?";
                Record section = SectionModel.dao.getSectionById(id);
                ConfModel.dao.deleteConfList(section.getStr("url"));
                ConfModel.dao.deleteConfDetail(section.getStr("url"));
                PatrolModel.dao.deleteSiteBySection(section.getInt("id"));
                return Db2.update(sql, id)!=0;
            }
        });
        return res;
    }

    public Page<Record> getPageQuerySection(Integer pageNumber, Integer pageSize, String keyword) {
        String sql1 = "select " +
                "s.id, s.comment as section_name, s.url, w.comment as website_name, s.site, s.autourl, " +
                "s.category as category_id, c.comment as category_name, a.username, s.keywordencode ";
        String sql2 = "from section as s " +
                "left join account as a on s.creator=a.id " +
                "left join category as c on s.category=c.id " +
                "left join website as w on s.site=w.id " +
                "where s.comment like ? or a.username like ? or c.comment like ? or s.url like ? or w.comment like ? " +
                "order by s.ctime desc, w.ctime desc";
        keyword = "%" + keyword + "%";
        return Db2.paginate(pageNumber, pageSize, sql1, sql2, keyword, keyword, keyword, keyword, keyword);
    }


//    public List<Record> getAllOutsiteSection() {
//        String sql = "select " +
//                "w.id as website_id, s.id as section_id, w.comment as site_name, " +
//                "s.comment as section_name " +
//                "from section as s " +
//                "left join website as w on s.site=w.id " +
//                "where w.region=? and w.status=? ";
//        return Db2.find(sql, 0, "open");
//    }


}
