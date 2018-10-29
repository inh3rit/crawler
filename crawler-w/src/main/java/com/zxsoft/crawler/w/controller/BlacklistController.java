package com.zxsoft.crawler.w.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.w.entity.UsrCookieEntity;
import com.zxsoft.crawler.w.model.BlacklistModel;
import com.zxsoft.crawler.w.util.Tool;
import com.zxsoft.crawler.w.validator.blacklist.SaveBlacklistValidator;
import com.zxsoft.crawler.w.validator.blacklist.UpdateBlacklistValidator;

import java.util.List;

/**
 * Created by cox on 2015/10/28.
 */
public class BlacklistController extends Controller {


    public void index() {
        Page<Record> p = BlacklistModel.dao.getPageBlacklist(super.getParaToInt("page", 1), 20);
        super.setAttr("p", p);
        super.render("/blacklist/index.html");
    }



    @Before({POST.class, SaveBlacklistValidator.class})
    public void save() {
        UsrCookieEntity uce = super.getAttr(Const.KEY_USR);
        String name = super.getPara("name");
        String regex = super.getPara("regex");
        String summary = super.getPara("summary");
        if (BlacklistModel.dao.hasName(name)) {
            super.renderJson(Tool.pushResult(-1, "name_exist", "黑名單名稱已存在"));
            return;
        }
        if (!BlacklistModel.dao.saveBlacklist(name, regex, summary, uce.getId())) {
            super.renderJson(Tool.pushResult(-1, "save_fail", "保存黑名單失敗"));
            return;
        }
        super.renderJson(Tool.pushResult(1, "save_success", "黑名單保存成功"));
    }

    @Before({POST.class, SaveBlacklistValidator.class, UpdateBlacklistValidator.class})
    public void update() {
        UsrCookieEntity uce = super.getAttr(Const.KEY_USR);
        Integer id = super.getParaToInt("id");
        String name = super.getPara("name");
        String regex = super.getPara("regex");
        String summary = super.getPara("summary", null);
        if (!BlacklistModel.dao.updateBlacklist(id, name, regex, summary, uce.getId())) {
            super.renderJson(Tool.pushResult(-1, "update_fail", "修改黑名單失敗"));
            return;
        }
        super.renderJson(Tool.pushResult("update_success", "黑名單修改成功"));
    }


    @Before({POST.class, UpdateBlacklistValidator.class})
    public void del() {
        Integer id = super.getParaToInt("id");
        if (!BlacklistModel.dao.delBlacklist(id)) {
            super.renderJson(Tool.pushResult(-1, "del_fail", "删除黑名单失败"));
            return;
        }
        super.renderJson(Tool.pushResult("del_success", "刪除黑名單成功"));
    }

    public void sync() {
        List<Record> bls = BlacklistModel.dao.getAllBlacklist();
        Prop conf = PropKit.use(Const.PROCFGFILE);
        BlacklistModel.dao.sync(conf.get("db.redis.key.blacklist"), bls);
        super.renderJson(Tool.pushResult("sync_success", "同步完成"));
    }

}
