package com.zxsoft.crawler.w.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.type.PropKey;
import com.zxsoft.crawler.w.entity.UsrCookieEntity;
import com.zxsoft.crawler.w.model.PropModel;
import com.zxsoft.crawler.w.model.ReptileModel;
import com.zxsoft.crawler.w.util.Tool;
import com.zxsoft.crawler.w.validator.prop.SavePropValidator;
import com.zxsoft.crawler.w.validator.prop.SaveReptilePropValidator;
import com.zxsoft.crawler.w.validator.prop.UpdatePropValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 此配置控制器是爬虫程序相关配置
 */
public class PropController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(PropController.class);

    public void index() {
        super.render("/prop/index.html");
    }

    @ActionKey("/prop/new")
    public void newProp() {
        List<Record> props = PropModel.dao.getPropList();
        super.setAttr("keys", PropKey.values());
        super.setAttr("props", props);
        super.render("/prop/new.html");
    }


    @ActionKey("/prop/prop_reptile")
    public void selectProp() {
        Integer reptile = super.getParaToInt("reptile", null);
        if (reptile==null) {
            List<Record> reptiles = ReptileModel.dao.getListReptile();
            super.setAttr("reptiles", reptiles);
            super.render("/prop/prop_reptile_none.html");
            return;
        }
        List<Record> props = PropModel.dao.getReptileNoUsePropList(reptile);
        Record rep = ReptileModel.dao.getReptileById(reptile);
        List<Record> rps = PropModel.dao.getReptileProp(reptile);
        super.setAttr("props", props);
        super.setAttr("reptile", rep);
        super.setAttr("rps", rps);
        super.render("/prop/prop_reptile.html");
    }


    @ActionKey("/prop/save")
    @Before({POST.class, SavePropValidator.class})
    public void saveProp() {
        UsrCookieEntity uce = super.getAttr(Const.KEY_USR);
        String key = super.getPara("key");
        String val = super.getPara("val");
        String mark = super.getPara("mark", null);
        PropKey ck = PropKey.getConfKey(key);
        if (!PropModel.dao.saveProp(ck, val, mark, uce.getId())) {
            super.renderJson(Tool.pushResult(-1, "fail_save", "保存配置失败"));
            return;
        }
        super.renderJson(Tool.pushResult("success_save", "配置保存成功"));
    }

    @ActionKey("/prop/update")
    @Before({POST.class, SavePropValidator.class, UpdatePropValidator.class})
    public void updateProp() {
        UsrCookieEntity uce = super.getAttr(Const.KEY_USR);
        String id = super.getPara("id");
//        String key = super.getPara("key");
        String val = super.getPara("val");
        String mark = super.getPara("mark", null);
        if (!PropModel.dao.updateProp(id, val, mark, uce.getId())) {
            super.renderJson(Tool.pushResult(-1, "update_fail", "修改配置失败"));
            return;
        }
        super.renderJson(Tool.pushResult("update_success", "修改配置成功"));
    }

    @ActionKey("/prop/delete")
    @Before({UpdatePropValidator.class})
    public void deleteProp() {
        String id = super.getPara("id");
        if (!PropModel.dao.deletePropInfo(id)) {
            super.renderJson(Tool.pushResult(-1, "del_fail", "删除配置失败"));
            return;
        }
        super.renderJson(Tool.pushResult("del_success", "删除配置成功"));
    }


    @ActionKey("/prop/save_reptile_prop")
    @Before({POST.class, SaveReptilePropValidator.class})
    public void saveReptileProp() {
        UsrCookieEntity uce = super.getAttr(Const.KEY_USR);
        Integer[] props = super.getParaValuesToInt("id");
        Integer retptile = super.getParaToInt("reptile");
        if (!PropModel.dao.saveReptileProp(retptile, props, uce.getId())) {
            super.renderJson(Tool.pushResult(-1, "save_fail", "保存区域配置失败"));
            return;
        }
        super.renderJson(Tool.pushResult("save_success", "区域配置保存成功"));
    }
}
