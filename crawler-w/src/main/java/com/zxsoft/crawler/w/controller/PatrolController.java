package com.zxsoft.crawler.w.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.w.entity.UsrCookieEntity;
import com.zxsoft.crawler.w.model.PatrolModel;
import com.zxsoft.crawler.w.model.ReptileModel;
import com.zxsoft.crawler.w.util.Tool;
import com.zxsoft.crawler.w.validator.patrol.PatrolSaveJobValidator;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by cox on 2015/12/2.
 */
public class PatrolController extends Controller {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PatrolController.class);

    public void index() {
        List<Record> list = ReptileModel.dao.getAllPatrolCrawl();
        super.setAttr("list", list);
        super.render("/patrol/index.html");
    }

    public void job() {
        Integer reptile = super.getParaToInt("reptile");
        Record rep = ReptileModel.dao.getReptileById(reptile);
        List<Record> so = PatrolModel.dao.getSectionsByReptile(reptile);
        super.setAttr("so", so);
        super.setAttr("reptile", rep);
        super.render("/patrol/job_outside.html");
    }


    @Before({POST.class, PatrolSaveJobValidator.class})
    public void save_job() {
        UsrCookieEntity uce = super.getAttr(Const.KEY_USR);
        Integer reptile = super.getParaToInt("reptile");
        Integer[] sites = super.getParaValuesToInt("section");
        if (!PatrolModel.dao.saveSite(sites, reptile, uce.getId())) {
            super.renderJson(Tool.pushResult(-1, "msg_fail", "保存任务失败"));
            return;
        }
        super.renderJson(Tool.pushResult(1, "msg_success", "保存成功"));
    }

}
