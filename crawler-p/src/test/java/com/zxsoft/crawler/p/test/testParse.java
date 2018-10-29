package com.zxsoft.crawler.p.test;

import com.zxsoft.crawler.p.kit.file.FileKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.nio.charset.Charset;

/**
 * Created by cox on 2016/1/27.
 */
public class testParse {


    @Test
    public void test1() {
        String content = FileKit.readFile("/home/cox/index.html", Charset.forName("GBK"));

//        Map<String, String> headers = new HashMap<String, String>();
//        HttpEntity he = HttpKit.get(UrlRemake.remake("http://test.boxun.com/blog/"), null, headers, null, Charset.forName("GBK"), HttpConst.DEF_TIMEOUT, HttpConst.DEF_SOTIMEOUT);
//        Document doc = Jsoup.parse(he.getHtml());
        Document doc = Jsoup.parse(content);
        Elements eles = doc.select("td[bgcolor=#F5F5F5] table[width=500]:eq(1)");
        Elements lines = eles.select("tr[bgcolor!=#e4e8ed]:gt(0)");
        System.out.println(lines);
    }


}
