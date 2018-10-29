package com.zxsoft.crawler.w.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.w.entity.UsrCookieEntity;
import com.zxsoft.crawler.w.model.TimeRegexModel;
import com.zxsoft.crawler.w.util.Tool;
import com.zxsoft.crawler.w.validator.timereg.SaveTimeRegexValidator;
import com.zxsoft.crawler.w.validator.timereg.UpdateTimeRegexValidator;

import java.sql.Timestamp;

/**
 * Created by cox on 2015/11/20.
 */
public class TimeRegexController extends Controller {

    public void index() {
        Page<Record> p = TimeRegexModel.dao.getPageTimeRegex(super.getParaToInt("page", 1), 20);
        super.setAttr("p", p);
        super.render("/timereg/index.html");
    }


    @Before({POST.class, SaveTimeRegexValidator.class})
    public void save() {
        UsrCookieEntity uce = super.getAttr(Const.KEY_USR);
        String sample = super.getPara("sample").trim();
        String regex = super.getPara("regex").trim();
        Integer sort = super.getParaToInt("sort", 0);
        String mark = super.getPara("mark", "time").trim();
        if (TimeRegexModel.dao.repeatRegex(regex)) {
            super.renderJson(Tool.pushResult(-1, "save_repeat", "此规则以添加无需再次添加"));
            return;
        }
        Timestamp ts = TimeRegexModel.dao.matchTime(sample, regex, mark);
        if (ts==null) {
            super.renderJson(Tool.pushResult(-1, "save_fail_match", "匹配时间错误"));
            return;
        }
        if (!TimeRegexModel.dao.saveRegex(sample, regex, sort, mark, ts, uce.getId())) {
            super.renderJson(Tool.pushResult(-1, "save_fail", "保存规则失败"));
            return;
        }
        super.renderJson(Tool.pushResult("save_success", "规则保存成功"));
    }

    @Before({POST.class, SaveTimeRegexValidator.class, UpdateTimeRegexValidator.class})
    public void update() {
        UsrCookieEntity uce = super.getAttr(Const.KEY_USR);
        String sample = super.getPara("sample").trim();
        String regex = super.getPara("regex").trim();
        Integer sort = super.getParaToInt("sort", 0);
        String mark = super.getPara("mark", "time").trim();
        Integer id = super.getParaToInt("id");

        Timestamp ts = TimeRegexModel.dao.matchTime(sample, regex, mark);
        if (ts==null) {
            super.renderJson(Tool.pushResult(-1, "update_fail_match", "匹配时间错误"));
            return;
        }
        if (!TimeRegexModel.dao.updateRegex(id, sample, regex, sort, mark, ts, uce.getId())) {
            super.renderJson(Tool.pushResult(-1, "update_fail", "修改失败"));
            return;
        }
        super.renderJson(Tool.pushResult("update_success", "修改成功"));
    }

    @Before({POST.class, UpdateTimeRegexValidator.class})
    public void del() {
        Integer id = super.getParaToInt("id");
        if (!TimeRegexModel.dao.deleteTimeRegex(id)) {
            super.renderJson(Tool.pushResult(-1, "del_fail", "删除规则失败"));
            return;
        }
        super.renderJson(Tool.pushResult("del_success", "删除规则成功"));
    }

}
