package com.zxsoft.crawler.w.controller;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.w.entity.UsrCookieEntity;
import com.zxsoft.crawler.w.interceptor.AccountInterceptor;
import com.zxsoft.crawler.w.model.AccountModel;
import com.zxsoft.crawler.w.util.Tool;
import com.zxsoft.crawler.w.util.UsrCookieKit;
import com.zxsoft.crawler.w.validator.account.AccountValidator;
import name.iaceob.kit.disgest.Disgest;

/**
 * Created by cox on 2015/8/31.
 */
public class AccountController extends Controller {


    @Clear({AccountInterceptor.class})
    public void sign() {
        super.render("/account/sign.html");
    }

    @Clear({AccountInterceptor.class})
    public void reg(){
        super.render("/account/reg.html");
    }


    @Clear({AccountInterceptor.class})
    @Before({POST.class, AccountValidator.class})
    public void doreg(){
        String name = super.getPara("name");
        String passwd = super.getPara("passwd");
        if (AccountModel.dao.existName(name)) {
            super.renderJson(Tool.pushResult(-1, "msg_exit_name", "该名称已被注册"));
            return;
        }
        if (!AccountModel.dao.saveAccount(name, passwd)) {
            super.renderJson(Tool.pushResult(-1, "msg_save_fail", "保存账户失败"));
            return;
        }
        super.renderJson(Tool.pushResult("msg_success", "保存账户成功"));
    }


    @Clear({AccountInterceptor.class})
    @Before({POST.class, AccountValidator.class})
    public void dosign() {
        String name = super.getPara("name");
        String passwd = super.getPara("passwd");
        Record account = AccountModel.dao.getAccountByName(name);
        if (account==null) {
            super.renderJson(Tool.pushResult(-1, "msg_account_null", "未发现当前用户"));
            return ;
        }
        if (!passwd.equals(account.getStr("password"))) {
            super.renderJson(Tool.pushResult(-1, "msg_passwd_fail", "账户或者密码不正确"));
            return;
        }
        UsrCookieEntity uce = new UsrCookieEntity();
        uce.setId(account.getStr("id")).setName(account.getStr("username"))
                .setSignTime(System.currentTimeMillis());
        super.setCookie(Const.HEARTKEY, UsrCookieKit.serialize(uce), 60*60*24*30);
        super.renderJson(Tool.pushResult("msg_success", "登入成功"));
    }

    public void out() {
        super.removeCookie(Const.HEARTKEY);
        // String basePath = super.getRequest().getContextPath();
        super.redirect("/");
    }


}
