package com.zxsoft.crawler.p.kit.remode;

import com.zxsoft.crawler.p.kit.remode.api.RemodelingApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cox on 2015/11/18.
 */
public class RemodelingTianyaMobi implements RemodelingApi {

    private static final Logger log = LoggerFactory.getLogger(RemodelingTianyaMobi.class);
    private static final String URL_REG = "post-(?<item>[\\s\\S]*?)-(?<id>\\d+)-";

    @Override
    public String analysis(String url) {
        try {
            Pattern p = Pattern.compile(URL_REG);
            Matcher m = p.matcher(url);
            if (!m.find()) return url;
            return "http://m.tianya.cn/bbs/art.jsp?item=" + m.group("item") + "&id=" + m.group("id");
        } catch (Exception e) {
            log.error("remodeling tianya url {} error, Exception message {}", url, e.getMessage());
        }
        return url;
    }
}
