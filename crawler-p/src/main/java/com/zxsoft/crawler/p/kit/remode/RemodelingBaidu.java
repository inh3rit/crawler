package com.zxsoft.crawler.p.kit.remode;

import com.zxsoft.crawler.p.kit.remode.api.RemodelingApi;

/**
 * Created by cox on 2015/11/18.
 */
public class RemodelingBaidu implements RemodelingApi {

    @Override
    public String analysis(String url) {
        url = url.contains("?") ? url + "&wd=" : url + "?wd=";
        return url;
    }
}
