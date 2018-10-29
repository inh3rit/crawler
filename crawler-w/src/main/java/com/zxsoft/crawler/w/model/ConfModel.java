package com.zxsoft.crawler.w.model;

import com.zxsoft.crawler.w.util.Db2;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by cox on 2015/8/19.
 */
public class ConfModel extends Model<ConfModel> {

    private static final Logger log = LoggerFactory.getLogger(ConfModel.class);

    public static final ConfModel dao = new ConfModel();


    public Record getConfListByUrl(String url) {
        String sql = "select * from conf_list where url=?";
        return Db2.findFirst(sql, url);
    }

    /**
     * 改版後詳細頁規則只須配置一次, 無需多條記錄
     * @param url listurl
     * @return List<Record>
     */
    @Deprecated
    public List<Record> getConfDetailListByUrl(String url) {
        String sql = "select * from conf_detail where listurl=?";
        return Db2.find(sql, url);
    }

    public Record getConfDetailByUrl(String url) {
        String sql = "select * from conf_detail where listurl=?";
        return Db2.findFirst(sql, url);
    }

    public Boolean saveConfList(String url, String comment, String category, Integer ajax, Integer fetchinterval,
                                String listdom, String linedom, String authordom, String urldom, String datedom,
                                String updatedom, String synopsisdom, Integer numThreads, String filterurl, Integer auth) {
        String sql = "insert into conf_list(url, comment, category, auth, ajax, numthreads, fetchinterval, filterurl, listdom, linedom, authordom, urldom, datedom, updatedom, synopsisdom) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        log.debug("Execute SQL: " + sql);
        return Db2.update(sql, url, comment, category, auth, ajax, numThreads, fetchinterval, filterurl, listdom,
                linedom, authordom, urldom, datedom, updatedom, synopsisdom)!=0;
    }

    public Boolean updateConfList(String url, String comment, String category, Integer ajax, Integer fetchinterval,
                                  String listdom, String linedom, String authordom, String urldom, String datedom,
                                  String updatedom, String synopsisdom, Integer numThreads, String filterurl, Integer auth) {
        String sql = "update conf_list set comment=?, category=?, auth=?, ajax=?, numthreads=?, fetchinterval=?, filterurl=?, listdom=?, linedom=?, authordom=?, urldom=?, datedom=?, updatedom=?, synopsisdom=? where url=?";
        log.debug("Execute SQL: " + sql);
        return Db2.update(sql, comment, category, auth, ajax, numThreads, fetchinterval, filterurl, listdom,
                linedom, authordom, urldom, datedom, updatedom, synopsisdom, url)!=0;
    }

    /**
     * 当前 URL 是否在 CONF_LIST 中存在
     * @param url
     * @return true 存在 false 不存在
     */
    public Boolean existUrlForConfList(String url) {
        String sql = "select 1 from conf_list where url=?";
        Record exist = Db2.findFirst(sql, url);
        log.debug("url exist: " + exist);
        log.debug("url exist for conf_list is " + (exist!=null));
        return exist!=null;
    }

    public Boolean saveConfDetail(String listurl,
                                  String host,
                                  String replyNum,
                                  String reviewNum,
                                  String forwardNum,
                                  String sources,
                                  Integer fetchOrder,
                                  String master,
                                  String author,
                                  String date,
                                  String content,
                                  String reply,
                                  String replyAuthor,
                                  String replyDate,
                                  String replyContent,
                                  String subReply,
                                  String subReplyAuthor,
                                  String subReplyDate,
                                  String subReplyContent,
                                  Integer ajax,
                                  String encode) {
        String sql = "insert into conf_detail (listurl, host, replyNum, reviewNum, forwardNum, " +
                "sources, fetchOrder, master, author, date, content, reply, replyAuthor, replyDate, " +
                "replyContent, subReply, subReplyAuthor, subReplyDate, subReplyContent, ajax, encode) values " +
                "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        return Db2.update(sql, listurl, host, replyNum, reviewNum, forwardNum, sources,
                fetchOrder, master, author, date, content, reply, replyAuthor, replyDate, replyContent,
                subReply, subReplyAuthor, subReplyDate, subReplyContent, ajax, encode)!=0;
    }

    public Boolean updateConfDetail(String listurl,
                                     String host,
                                     String replyNum,
                                     String reviewNum,
                                     String forwardNum,
                                     String sources,
                                     Integer fetchOrder,
                                     String master,
                                     String author,
                                     String date,
                                     String content,
                                     String reply,
                                     String replyAuthor,
                                     String replyDate,
                                     String replyContent,
                                     String subReply,
                                     String subReplyAuthor,
                                     String subReplyDate,
                                     String subReplyContent,
                                     Integer ajax,
                                     String encode) {
        String sql = "update conf_detail set host=?, replyNum=?, reviewNum=?, forwardNum=?, " +
                "sources=?, fetchOrder=?, master=?, author=?, date=?, content=?, reply=?, replyAuthor=?, " +
                "replyDate=?, replyContent=?, subReply=?, subReplyAuthor=?, subReplyDate=?, subReplyContent=?, " +
                "ajax=?, encode=? where listurl=?";
        return Db2.update(sql, host, replyNum, reviewNum, forwardNum, sources, fetchOrder, master, author,
                date, content, reply, replyAuthor, replyDate, replyContent, subReply, subReplyAuthor,
                subReplyDate, subReplyContent, ajax, encode, listurl )!=0;
    }

    public Boolean existUrlForConfDetail(String url) {
        String sql = "select 1 from conf_detail where listurl=?";
        return Db2.findFirst(sql, url)!=null;
    }

    public void deleteConfList(String url) {
        String sql = "delete from conf_list where url=?";
        Db2.update(sql, url);
    }

    public void deleteConfDetail(String url) {
        String sql = "delete from conf_detail where listurl=?";
        Db2.update(sql, url);
    }

    public Boolean updateConfListUrl(String url, String newUrl) {
        String sql = "update conf_list set url=? where url=?";
        return Db2.update(sql, newUrl, url)!=0;
    }

    public Boolean updateConfDetailUrl(String url, String newUrl) {
        String sql = "update conf_detail set listurl=? where listurl=?";
        return Db2.update(sql, newUrl, url)!=0;
    }

}
