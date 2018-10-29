package com.zxsoft.crawler.p.kit.parse;

import com.zxsoft.crawler.p.ext.ContentExtractor;
import org.inh3rit.httphelper.entity.ProxyEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.junit.Test;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseKitTest {

    private void testRegexEasou() {
        try {
            String reg = "sr=(?<url>[http|https][\\s\\S]*?)&";
            String url = "http://z.easou.com/show" +
                    ".m?a=0&sr=http%3A%2F%2Fnews.163.com%2F13%2F0503%2F11%2F8TUQD5KU00014AED" +
                    ".html&p=1&c=10&pos=1&total=681&pwt=3&pt=598134325510144&docid=-9143859853675467426&si" +
                    "=80d9ab10b1ade65fc0700ff29af33bed&am=1&wt=Vip&q=%E9%B8%A0%E6%B1%9F%E5%8C%BA+%E5%A4%B1%E8%B8%AA" +
                    "&esid=GY2vHPSwwpP&wver=c";
            Pattern p = Pattern.compile(reg);
            Matcher m = p.matcher(url);
            if (!m.find()) return;
            String targetUrl = m.group("url");
            System.out.println(URLDecoder.decode(targetUrl, "UTF-8"));
            System.out.println("=====test easou.com ends=====");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRemodeUrl() {
        this.testRegexEasou();
    }

    // 正文提取
    @Test
    public void test1() throws Exception {
        String url = "http://big5.workercn.cn/big5/build.workercn.cn/26892/201801/30/180130080504276.shtml";
        String html = HttpKit.get(url, Charset.forName("GBK")).getHtml();
        String text = ContentExtractor.getContentByHtml(html);
        System.out.println(text);

    }

    // 设置代理访问
    @Test
    public void test2() throws Exception {
        ProxyEntity proxy = new ProxyEntity("192.168.226.116", 25, "yproxyq", "zproxyx");
        String url = "http://orientaldaily.on.cc/cnt/news/20180130/index.html";
        String html = HttpKit.get(url, null, null, proxy, Charset.defaultCharset()).getHtml();
        System.out.println(html);

    }
}