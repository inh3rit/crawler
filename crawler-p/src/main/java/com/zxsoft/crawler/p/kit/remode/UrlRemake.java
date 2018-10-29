package com.zxsoft.crawler.p.kit.remode;

import com.zxsoft.crawler.p.kit.remode.api.RemodelingSite;

/**
 * Created by cox on 2016/1/11.
 */
public class UrlRemake {


    public static String remake(RemodelingSite site, String url) {
        switch (site) {
            case BAIDU:
                return site.getHandle().analysis(url);
            case TIANYA_MOBI:
                return site.getHandle().analysis(url);
            case EASOU_MOBI:
                return site.getHandle().analysis(url);
            default:
                return site.getHandle().analysis(url);
        }
    }

    public static String remake(String url) {
        for (RemodelingSite site : RemodelingSite.values()) {
            if (!url.matches(site.getRegex())) continue;
            return UrlRemake.remake(site, url);
        }
        return url;
    }


}
