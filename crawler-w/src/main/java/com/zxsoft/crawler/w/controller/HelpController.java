package com.zxsoft.crawler.w.controller;

import com.jfinal.core.Controller;

/**
 * Created by cox on 2015/8/31.
 */
public class HelpController extends Controller {

    public void index() {
        super.render("/help/index.html");
    }

}
