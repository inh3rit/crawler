package com.zxsoft.crawler.w.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.w.model.ConfModel;
import com.zxsoft.crawler.w.model.SectionModel;
import com.zxsoft.crawler.w.model.WebsiteModel;
import com.zxsoft.crawler.w.util.Tool;

import java.util.ArrayList;
import java.util.List;

/**
 * 此配置控制器是站点规则相关配置
 */
public class ConfController extends Controller {


    public void index() {
        Integer id = super.getParaToInt("id");
        Record section = SectionModel.dao.getSectionById(id);
        if (section == null) {
            super.render("/fail/fail_404.html");
            return;
        }
        Record website = WebsiteModel.dao.getWebsiteById(section.getInt("site"));
        Record confList = ConfModel.dao.getConfListByUrl(section.getStr("url"));
        Record confDetail = null;
        if (confList != null)
            confDetail = ConfModel.dao.getConfDetailByUrl(confList.getStr("url"));
        super.setAttr("website", website);
        super.setAttr("section", section);
        super.setAttr("confList", confList);
        super.setAttr("confDetail", confDetail);
        super.render("/website/conf.html");
    }


    /**
     * 保存列表页配置
     */
    @Before({POST.class})
    public void save_conf_list() {
        Integer section = super.getParaToInt("section");
        String url = super.getPara("url");
        String comment = super.getPara("comment");
        Integer ajax = super.getParaToInt("ajax", 0);
        Integer fetchinterval = super.getParaToInt("fetchinterval", 60);
        String listdom = super.getPara("listdom", null);
        String linedom = super.getPara("linedom", null);
        String urldom = super.getPara("urldom", null);
        String datedom = super.getPara("datedom", null);
        String updatedom = super.getPara("updatedom", null);
        String synopsisdom = super.getPara("synopsisdom", null);
        String authordom = super.getPara("authordom", null);
        Integer auth = null; // 未知字段含义

        Record sec = SectionModel.dao.getSectionById(section);
        if (sec == null) {
            super.renderJson(Tool.pushResult(-1, "msg_section_null", "获取板块信息失败"));
            return;
        }
        Boolean exist = ConfModel.dao.existUrlForConfList(url);
        Boolean res;
        if (exist)
            res = ConfModel.dao.updateConfList(url, sec.getStr("comment"), sec.getStr("category"), ajax, fetchinterval, listdom,
                    linedom, authordom, urldom, datedom, updatedom, synopsisdom, null, null, auth);
        else
            res = ConfModel.dao.saveConfList(sec.getStr("url"), sec.getStr("comment"), sec.getStr("category"), ajax, fetchinterval, listdom,
                    linedom, authordom, urldom, datedom, updatedom, synopsisdom, null, null, auth);
        if (!res) {
            super.renderJson(Tool.pushResult(-1, "msg_save_fail", "保存列表规则失败"));
            return;
        }
        super.renderJson(Tool.pushResult("msg_success", "提交成功"));
    }

    /**
     * 保存详细页配置
     */
    @Before({POST.class})
    public void save_conf_detail() {
        String encode = super.getPara("encode", null),
                host = super.getPara("host"),
                replyNum = super.getPara("replyNum", null),
                reviewNum = super.getPara("reviewNum", null),
                forwarNum = super.getPara("forwarNum", null),
                sources = super.getPara("sourdes", null),
                master = super.getPara("master", null),
                date = super.getPara("date", null),
                content = super.getPara("content", null),
                reply = super.getPara("reply", null),
                replyAuthor = super.getPara("replyAuthor", null),
                replyDate = super.getPara("replyDate", null),
                replyContent = super.getPara("replyContent", null),
                subReply = super.getPara("subReply", null),
                subReplyAuthor = super.getPara("subReplyAuthor", null),
                subReplyDate = super.getPara("subReplyDate", null),
                subReplyContent = super.getPara("subReplyContent", null),
                listurl = super.getPara("listurl", null),
                oldHost = super.getPara("oldHost", null),
                author = super.getPara("author", null);
        Integer ajax = super.getParaToInt("ajax", 0),
                fetchOrder = super.getParaToInt("fetchOrder", null);

//        if (listurl.endsWith("/"))
//            listurl = listurl.substring(0, listurl.lastIndexOf("/")).trim();

        Boolean exist = ConfModel.dao.existUrlForConfDetail(listurl);
        Boolean res;
        if (exist) {
            res = ConfModel.dao.updateConfDetail(listurl, host, replyNum, reviewNum, forwarNum, sources, fetchOrder, master,
                    author, date, content, reply, replyAuthor, replyDate, replyContent, subReply, subReplyAuthor,
                    subReplyDate, subReplyContent, ajax, encode);
        } else {
            res = ConfModel.dao.saveConfDetail(listurl, host, replyNum, reviewNum, forwarNum, sources, fetchOrder, master,
                    author, date, content, reply, replyAuthor, replyDate, replyContent, subReply, subReplyAuthor,
                    subReplyDate, subReplyContent, ajax, encode);
        }
        if (!res) {
            super.renderJson(Tool.pushResult(-1, "msg_save_fail", "规则提交失败"));
            return;
        }
        super.renderJson(Tool.pushResult("msg_success", "提交成功"));
    }

    public void testConfList() {
//        Integer sectionId = super.getParaToInt("section");
//        String keyword = super.getPara("keyword");
//        Record section = SectionModel.dao.getSectionById(sectionId);
//        Record confList = ConfModel.dao.getConfListByUrl(section.getStr("url"));
//        ListConfVerification v = new ListConfVerification();
//        Record rv = v.verify(confList, keyword, section.getStr("keywordEncode"), section.getBoolean("autoUrl"));
    }

}
