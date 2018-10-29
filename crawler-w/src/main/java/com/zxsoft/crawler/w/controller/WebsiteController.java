package com.zxsoft.crawler.w.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.w.model.JobTypeModel;
import com.zxsoft.crawler.w.model.WebsiteModel;
import com.zxsoft.crawler.w.util.Tool;
import com.zxsoft.crawler.w.validator.website.SaveWebsiteValidator;
import com.zxsoft.crawler.w.validator.website.UpdateWebsiteValidator;

/**
 * Created by cox on 2015/8/17.
 */
public class WebsiteController extends Controller {

    public void index() {
        Integer region = super.getParaToInt("region", 1);
        String province = super.getPara("provinceId");
        String city = super.getPara("cityId");
        String area = super.getPara("areaId");
        String status = super.getPara("status");
        String comment = super.getPara("comment");
        Integer type = super.getParaToInt("type", null);
        comment = StrKit.notBlank(comment) ? comment.trim() : comment;
        type = type == null || type < JobType.NETWORK_SEARCH.getIndex() || type > JobType.NETWORK_FOCUS.getIndex() ? null : type;
        Page<Record> pw = WebsiteModel.dao.getWebsites(region, province, city, area, status, comment, type,
                super.getParaToInt("page", 1),
                super.getParaToInt("rows", 50));
        super.setAttr("types", JobTypeModel.dao.getAllJobType());
        super.setAttr("p", pw);
        super.render("/website/index.html");
    }

    @Before({POST.class, UpdateWebsiteValidator.class, SaveWebsiteValidator.class})
    public void update() {
        Integer id = super.getParaToInt("id");
        Integer tid = super.getParaToInt("tid");
        Integer region = super.getParaToInt("region", 1);
        String url = super.getPara("site");
        String comment = super.getPara("comment");
        Integer province = super.getParaToInt("provinceId", 10000);
        Integer city = super.getParaToInt("cityId", null);
        Integer area = super.getParaToInt("areaId", null);
        String status = super.getPara("status");
        Integer type = super.getParaToInt("type");
        String cookie = super.getPara("cookie", null);
        if (!WebsiteModel.dao.updateWebsite(id, tid, region, url, comment, province, city, area, status, type, cookie)) {
            super.renderJson(Tool.pushResult(0, "msg_save_error", "修改失败"));
            return;
        }
        super.renderJson(Tool.pushResult("msg_success", "修改成功"));
    }

    @Before({POST.class, SaveWebsiteValidator.class})
    public void save() {
        Integer tid = super.getParaToInt("tid");
        Integer region = super.getParaToInt("region", 1);
        String url = super.getPara("site");
        String comment = super.getPara("comment");
        Integer province = super.getParaToInt("provinceId", 100000);
        Integer city = super.getParaToInt("cityId", null);
        Integer area = super.getParaToInt("areaId", null);
        String status = super.getPara("status");
        Integer type = super.getParaToInt("type");
        String cookie = super.getPara("cookie", null);
        if (!WebsiteModel.dao.saveWebsite(tid, region, url, comment, status, province, city, area, type, cookie)) {
            super.renderJson(Tool.pushResult(0, "msg_save_error", "添加失败"));
            return;
        }
        super.renderJson(Tool.pushResult("msg_success", "添加成功"));
    }

    public void delete() {
        if (!WebsiteModel.dao.deleteWebsite(super.getParaToInt("id"))) {
            super.renderJson(Tool.pushResult(0, "msg_save_error", "删除失败"));
            return;
        }
        super.renderJson(Tool.pushResult("msg_success", "删除成功"));
    }

}
