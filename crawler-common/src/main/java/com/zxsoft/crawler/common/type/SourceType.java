package com.zxsoft.crawler.common.type;

/**
 * Created by cox on 2015/8/5.
 */
public enum SourceType {

    MYSQL_CONF("MYSQL_CONF"),
    MYSQL_BUSI("MYSQL_BUSI");

    private final String name;

    private SourceType(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

}
