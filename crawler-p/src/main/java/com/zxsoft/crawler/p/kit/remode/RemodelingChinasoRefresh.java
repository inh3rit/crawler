package com.zxsoft.crawler.p.kit.remode;

import com.zxsoft.crawler.p.kit.remode.api.RemodelingApi;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cox on 2015/11/27.
 */
public class RemodelingChinasoRefresh implements RemodelingApi {

    private static final Logger log = LoggerFactory.getLogger(RemodelingChinasoRefresh.class);

    @Override
    public String analysis(String url) {
        try {
            HttpEntity he = HttpKit.get(url);
            String html = he.getHtml();
            String var1 = html.substring(0, html.lastIndexOf("\""));
            return var1.substring(var1.lastIndexOf("\"") + 1, var1.length());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return url;
    }
}
