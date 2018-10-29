package com.zxsoft.crawler.w.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.w.model.JobTypeModel;
import com.zxsoft.crawler.w.model.LocationModel;
import com.zxsoft.crawler.w.model.ReptileModel;
import com.zxsoft.crawler.w.util.Tool;
import com.zxsoft.crawler.w.validator.reptile.SaveReptileValidator;
import com.zxsoft.crawler.w.validator.reptile.UpdateReptileValidator;

import java.util.List;

/**
 * Created by cox on 2015/8/28.
 */
public class ReptileController extends Controller {


    public void index(){
        List<Record> reptiles = ReptileModel.dao.getListReptile();
        super.setAttr("types", JobTypeModel.dao.getAllJobType());
        super.setAttr("reptiles", reptiles);
        super.render("/reptile/index.html");
        super.setAttr("locations", LocationModel.dao.getAllLocation());
    }

    @Before({POST.class, SaveReptileValidator.class})
    public void save() {
        String name = super.getPara("name");
        String redis = super.getPara("redis");
        JobType type = JobType.getIndex(super.getParaToInt("type", 0));
        String alias = super.getPara("alias");
        String url = super.getPara("url", null);
        String usr = super.getPara("usr", null);
        String passwd = super.getPara("passwd", null);
        Integer active = super.getParaToInt("active", 40);
        Integer location = super.getParaToInt("location", null);
        alias = StrKit.notBlank(alias) ? alias.trim().toUpperCase() : alias;
        url = StrKit.notBlank(url) ? url.trim() : url;
        usr = StrKit.notBlank(usr) ? usr.trim() : usr;
        passwd = StrKit.notBlank(passwd) ? passwd.trim() : passwd;
        redis = redis.trim();
        if (!ReptileModel.dao.saveReptile(name, redis, type, alias, url, usr, passwd, active, location)) {
            super.renderJson(Tool.pushResult(-1, "save_fail", "添加新区域失败"));
            return;
        }
        super.renderJson(Tool.pushResult("save_success", "添加新区域成功"));
    }


    @Before({POST.class, SaveReptileValidator.class, UpdateReptileValidator.class})
    public void update() {
        String name = super.getPara("name");
        String redis = super.getPara("redis");
        JobType type = JobType.getIndex(super.getParaToInt("type", 0));
        String alias = super.getPara("alias");
        String url = super.getPara("url", null);
        String usr = super.getPara("usr", null);
        String passwd = super.getPara("passwd", null);
        Integer active = super.getParaToInt("active", 40);
        Integer id = super.getParaToInt("id");
        Integer location = super.getParaToInt("location", null);
        alias = StrKit.notBlank(alias) ? alias.trim().toUpperCase() : alias;
        url = StrKit.notBlank(url) ? url.trim() : url;
        usr = StrKit.notBlank(usr) ? usr.trim() : usr;
        passwd = StrKit.notBlank(passwd) ? passwd.trim() : passwd;
        redis = redis.trim();
        if (!ReptileModel.dao.updateReptile(id, name, redis, type, alias, url, usr, passwd, active, location)) {
            super.renderJson(Tool.pushResult(-1, "msg_update_fail", "修改失败"));
            return;
        }
        super.renderJson(Tool.pushResult("msg_success", "修改成功"));
    }

    public void delete() {
        Integer id = super.getParaToInt("id");
        if (!ReptileModel.dao.deleteReptile(id)) {
            super.renderJson(Tool.pushResult(-1, "msg_delete_fail", "删除失败"));
            return;
        }
        super.renderJson(Tool.pushResult("msg_success", "删除成功"));
    }

}
