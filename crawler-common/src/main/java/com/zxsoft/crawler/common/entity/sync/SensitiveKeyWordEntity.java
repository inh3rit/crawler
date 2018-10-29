/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.common.entity.sync;

import com.jfinal.plugin.activerecord.Record;

/**
 * Created by 施洋青
 * DATE： 16-10-10.
 */
public class SensitiveKeyWordEntity extends Record {

    public SensitiveKeyWordEntity setKwd(String kwd) {
        super.set("kwd", kwd);
        return this;
    }

    public String getKwd() {
        return super.getStr("kwd");
    }
}