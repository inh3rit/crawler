package com.zxsoft.crawler.p.kit.remode.api;

import com.zxsoft.crawler.p.kit.remode.*;

/**
 * Created by cox on 2015/11/16.
 */
public enum RemodelingSite {
    UNKNOW("unknow", new RemodelingDefault(), "----"),
    BAIDU("baidu", new RemodelingBaidu(), "(http|https)://www\\.baidu\\.com/link.*?"),
    TIANYA_MOBI("tianya_mobi", new RemodelingTianyaMobi(), "(http|https)://m\\.tianya\\.cn/g/t\\.jsp.*?"),
    EASOU_MOBI("easou_mobi", new RemodelingEasouMobi(), "(http|https)://z\\.easou\\.com.*?"),
    CHINASO_REFRESH("chinaso_refresh", new RemodelingChinasoRefresh(), "(http|https)://www.chinaso.com/search/link.*?");
    private final String key;
    private final String regex;
    private RemodelingApi api;
    RemodelingSite(String key, RemodelingApi api, String regex) {
        this.key = key;
        this.regex = regex;
        this.api = api;
    }
    public RemodelingApi getHandle() {
        return this.api;
    }
    public String getKey() {
        return this.key.toUpperCase();
    }
    public String getRegex() {
        return this.regex;
    }
    public static RemodelingSite getSite(String key) {
        for (RemodelingSite s : RemodelingSite.values()) {
            if (!key.toUpperCase().equals(s.getKey())) continue;
            return s;
        }
        return UNKNOW;
    }
}
