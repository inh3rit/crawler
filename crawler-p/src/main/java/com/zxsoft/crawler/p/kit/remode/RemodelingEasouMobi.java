package com.zxsoft.crawler.p.kit.remode;

import com.zxsoft.crawler.p.kit.remode.api.RemodelingApi;

import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cox on 2015/11/18.
 */
public class RemodelingEasouMobi implements RemodelingApi {

    private static final String URL_REG = "sr=(?<url>[http|https][\\s\\S]*?)&";

    @Override
    public String analysis(String url) {
        try {
            Pattern p = Pattern.compile(URL_REG);
            Matcher m = p.matcher(url);
            if (!m.find()) return url;
            return URLDecoder.decode(m.group("url"), "UTF-8");
        } catch (Exception e) {
            return url;
        }
    }
}
