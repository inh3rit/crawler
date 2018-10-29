package com.zxsoft.crawler.w.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.w.entity.UsrCookieEntity;
import com.zxsoft.crawler.w.util.UsrCookieKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;

public class AccountInterceptor  implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(AccountInterceptor.class);

    @Override
    public void intercept(Invocation invocation) {

        Controller c = invocation.getController();
        Cookie heart = c.getCookieObject(Const.HEARTKEY);
        if (heart==null) {
            c.redirect("/account/sign");
            return;
        }

        try {
            UsrCookieEntity uce = UsrCookieKit.resolve(heart.getValue());
            if (uce==null) {
                c.redirect("/account/sign");
                return;
            }
            c.getRequest().setAttribute(Const.KEY_USR, uce);
            invocation.invoke();
            return;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        c.redirect("/account/sign");



//        Controller c = invocation.getController();
//        // String basePath = c.getRequest().getContextPath();
//        Cookie heart = c.getCookieObject(Const.HEARTKEY);
//        if (heart==null) {
//            AccountKit.clear();
//            c.redirect("/account/sign");
//            return;
//        }
//        try {
//            String heartContent = Disgest.decodeRC4(heart.getValue(), Const.HEARTENCRYKEY);
//            String[] accountInfo = heartContent.split(Const.HEARTESPLIT);
//            Long cookieTime = Long.parseLong(accountInfo[2]);
//            if (cookieTime+1000l*60l*60l*24l*30l< System.currentTimeMillis()) {
//                AccountKit.clear();
//                c.redirect("/account/sign");
//                return;
//            }
//            AccountKit.setId(accountInfo[0]);
//            AccountKit.setName(accountInfo[1]);
//            invocation.invoke();
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }

    }


}
