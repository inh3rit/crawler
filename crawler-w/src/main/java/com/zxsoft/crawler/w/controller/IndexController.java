package com.zxsoft.crawler.w.controller;

import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.w.interceptor.AccountInterceptor;

/**
 * Created by cox on 2015/8/18.
 */
public class IndexController extends Controller {

    public void index() {
        super.redirect("/website");
    }

    @Clear({AccountInterceptor.class})
    public void jsonp() {
        String para = super.getPara("para");
        Record r = new Record();
        r.set("para", para).set("time", System.currentTimeMillis());
        // super.renderError(201);
        super.renderJson(r);
    }

}
