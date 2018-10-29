package com.zxsoft.crawler.w.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.w.model.LocationModel;
import com.zxsoft.crawler.w.model.TimeRegexModel;
import com.zxsoft.crawler.w.util.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

/**
 * Created by cox on 2015/8/18.
 */
public class ServiceController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(ServiceController.class);


    public void location() {
        super.renderJson(LocationModel.dao.getLocationByPid(super.getParaToInt("pid", 0)));
    }

    public void verify_time_regex() {
        String sample = super.getPara("sample");
        String regex = super.getPara("regex");
        String mark = super.getPara("key", "time");
        if (StrKit.isBlank(sample)) {
            super.renderJson(Tool.pushResult(-1, "null_sample", "未发现验证样本"));
            return;
        }
        if (StrKit.isBlank(regex)) {
            super.renderJson(Tool.pushResult(-1, "verify_fail", "规则验证失败"));
            return;
        }
        if (!regex.contains(mark)) {
            super.renderJson(Tool.pushResult(-1, "verify_fail_mark", "规则中未发现内容标记"));
            return;
        }
        try {
            sample = sample.trim();
            regex = regex.trim();
            mark = mark.trim();
            Timestamp ts = TimeRegexModel.dao.matchTime(sample, regex, mark);
            if (ts==null) {
                super.renderJson(Tool.pushResult(-1, "verify_fail_match", "匹配时间错误"));
                return;
            }
            Record r = new Record();
            r.set("ts", ts.getTime()).set("str", ts.toString());
            super.renderJson(Tool.pushResult(1, "verify_success", "规则验证成功", r));
        } catch (Exception e) {
            String msg = e.getMessage();
            if (StrKit.notBlank(msg))
                msg = msg.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;");
            log.error(msg);
            super.renderJson(Tool.pushResult(-1, "verify_fail", "规则验证失败, 错误信息: " + msg));
        }
    }

}
