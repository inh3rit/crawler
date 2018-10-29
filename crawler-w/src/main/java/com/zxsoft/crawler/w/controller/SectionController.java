package com.zxsoft.crawler.w.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.w.entity.UsrCookieEntity;
import com.zxsoft.crawler.w.model.CategoryModel;
import com.zxsoft.crawler.w.model.SectionModel;
import com.zxsoft.crawler.w.model.WebsiteModel;
import com.zxsoft.crawler.w.util.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by cox on 2015/8/18.
 */
public class SectionController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(SectionController.class);

    public void index() {
        Integer id = super.getParaToInt("id");
        List<Record> cates = CategoryModel.dao.getCategoryList();
        Record website = WebsiteModel.dao.getWebsiteById(id);
        Page<Record> p = SectionModel.dao.getPageSection(id, super.getParaToInt("page", 1), 20);
        super.setAttr("cates", cates);
        super.setAttr("website", website);
        super.setAttr("p", p);
        super.render("/website/section.html");
    }

    @Before({POST.class})
    public void save() {
        UsrCookieEntity uce = super.getAttr(Const.KEY_USR);
        String url = super.getPara("url");
        String comment = super.getPara("comment");
        String category = super.getPara("category");
        String keywordEncode = super.getPara("keywordEncode");
        Integer autoUrl = super.getParaToInt("autoUrl");
        Integer website = super.getParaToInt("website");
        keywordEncode = "UTF8".equals(keywordEncode.toUpperCase()) ? "UTF-8" : keywordEncode;
        keywordEncode = "GB2312".equals(keywordEncode.toUpperCase()) ? "GBK" : keywordEncode;
        keywordEncode = keywordEncode.toUpperCase();
        log.debug("Keyword encode is :" + keywordEncode);
        if (!SectionModel.dao.saveSection(url, category, comment, website, autoUrl, null, keywordEncode, uce.getId())) {
            super.renderJson(Tool.pushResult(0, "msg_save_fail", "添加失败"));
            return;
        }
        super.renderJson(Tool.pushResult("msg_success", "添加成功"));
    }


    @Before({POST.class})
    public void update(){
        UsrCookieEntity uce = super.getAttr(Const.KEY_USR);
        String url = super.getPara("url");
        String comment = super.getPara("comment");
        String category = super.getPara("category");
        String keywordEncode = super.getPara("keywordEncode");
        Integer autoUrl = super.getParaToInt("autoUrl");
        Integer website = super.getParaToInt("website");
        Integer id = super.getParaToInt("id");
        keywordEncode = "UTF8".equals(keywordEncode.toUpperCase()) ? "UTF-8" : keywordEncode;
        keywordEncode = "GB2312".equals(keywordEncode.toUpperCase()) ? "GBK" : keywordEncode;
        keywordEncode = keywordEncode.toUpperCase();
        log.debug("Keyword encode is :" + keywordEncode);
        if (!SectionModel.dao.updateSection(id, url, category, comment, website, autoUrl, null, keywordEncode, uce.getId())) {
            super.renderJson(Tool.pushResult(0, "msg_save_fail", "修改失败"));
            return;
        }
        super.renderJson(Tool.pushResult("msg_success", "修改成功"));
    }

    public void delete(){
        Integer id = super.getParaToInt("id");
        if (!SectionModel.dao.deleteSection(id)) {
            super.renderJson(Tool.pushResult(0, "msg_delete_fail", "删除失败"));
            return;
        }
        super.renderJson(Tool.pushResult("msg_success", "删除成功"));
    }


    public void query(){
        String keyword = super.getPara("kw", "");
        keyword = keyword.trim();
        List<Record> cates = CategoryModel.dao.getCategoryList();
        Page<Record> p = SectionModel.dao.getPageQuerySection(super.getParaToInt("page", 1), super.getParaToInt("size", 50), keyword);
        super.setAttr("p", p);
        super.setAttr("cates", cates);
        super.render("/website/query_section.html");
    }


//    @ActionKey("/website/section/get_section_by_website")
//    @Before({GetSectionByWebsiteValidator.class})
//    public void getSectionByWebsite() {
//        Integer website = super.getParaToInt("website");
//        List<Record> list = SectionModel.dao.getSectionBySite(website);
//        super.setAttr("list", list);
//        super.render("/section/get_section_by_website.html");
//    }

}
