package com.zxsoft.crawler.m.model.job;

import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

/**
 * Created by cox on 2016/1/26.
 */
public class ValidatorModelTest {

    @Test
    public void testGetWeiboTencentName() {
        HttpEntity he = HttpKit.get("http://e.t.qq.com/yunchenggongan110", true);
        Document doc = Jsoup.parse(he.getHtml());
        Elements es = doc.select(".clubInfo img[title=点击查看大图和历史头像]");
        es.text();
    }

}