package com.zxsoft.crawler.p.test;

import org.inh3rit.httphelper.common.HttpConst;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.entity.ProxyEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cox on 2015/11/16.
 */
public class testP {


    @Test
    public void ttd1() {
        String str = "http://m.tianya.cn/bbs/art.jsp?item=feeling&id=2191281&vu=47654845811";
        Pattern p = Pattern.compile("id=(?<id>\\d*)");
        Matcher m = p.matcher(str);
        if (!m.find()) return;
        System.out.println(m.group("id"));
    }


    @Test
    public void ttd2() {
        String url = "http://m.tianya.cn/g/t.jsp?vu=47654845811&u=http%3A%2F%2Fbbs.tianya.cn%2Fpost-feeling-2191281-1.shtml";
        HttpEntity he = HttpKit.get(url);
        System.out.println(he.getLocation());
    }

    @Test
    public void ttd3() {
        String url = "http://m.tianya.cn/g/t.jsp?vu=47654845811&u=http%3A%2F%2Fbbs.tianya.cn%2Fpost-feeling-2191281-1.shtml";
        Pattern p = Pattern.compile("post-(?<item>[\\s\\S]*?)-(?<id>\\d+)-");
        Matcher m = p.matcher(url);
        if (!m.find()) return;
        System.out.println(m.group("item"));
        System.out.println(m.group("id"));
    }


    @Test
    public void ttd4() {
        String reg = "http://www.9tour.cn/travelsInfo/.*?";
        String url = "http://www.9tour.cn/travelsInfo/9205177/";
        System.out.println(url.matches(reg));
    }

    @Test
    public void ttd5() {
        String url = "https://www.baidu.com/s?url=9LPo2tjWb12Lpd6pGLLBl-PQKrb3ts5HqOWQn2L2mIixJa0hWjhmUVvmwPcyXdD20jgUBqj8rHlW2hERhkbqpK";
        System.out.println(url.substring(0, 8));
    }


    @Test
    public void ttd6() {
        String url = "http://www.baidu.com/link?url=I3hHqNOTcCGj-w5_eIl248QoXh5YvXvGB9qptps6k9Vk8pJ6RRjwWxbrO0YN16_qeSwfBM98lNWcUSZSYgBuKK";
        url = "http://www.baidu.com/link?url=PhW7BkRs9cIiaYoQGDPwi5al9hgkX0natiw5ccL3pTe&wd=";
        url = "http://www.baidu.com/link?url=I3hHqNOTcCGj-w5_eIl248QoXh5YvXvGB9qptps6k9Vk8pJ6RRjwWxbrO0YN16_qeSwfBM98lNWcUSZSYgBuKK&wd=";
        HttpEntity he = HttpKit.get(url);
        System.out.println(he.getUrl());
        System.out.println(he.getHtml());
    }

    @Test
    public void ttd7() {
        String url = "http://www.baidu.com/link?url=I3hHqNOTcCGj-w5_eIl248QoXh5YvXvGB9qptps6k9Vk8pJ6RRjwWxbrO0YN16_qeSwfBM98lNWcUSZSYgBuKK&wd=";
        String regex = "(http|https)://www\\.baidu\\.com";
        System.out.println(url.matches("(http|https)://www\\.baidu\\.com.*?"));
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(url);
        if (!m.find()) return;
        System.out.println(url);
        // System.out.println(url.matches(regex));
    }

    @Test
    public void ttd8() {
        String url = "http://www.baidu.com/link?url=I3hHqNOTcCGj-w5_eIl248QoXh5YvXvGB9qptps6k9Vk8pJ6RRjwWxbrO0YN16_qeSwfBM98lNWcUSZSYgBuKK";
        url = url.contains("?") ? url + "&wd=" : url + "?wd=";
        System.out.println(url);
    }


    @Test
    public void ttd9() {
        String url = "http://www.chinaso.com/search/link?url=QA69Z6aFbdKPPRQxcHj2Y21Y1bzf%2Be%2Bd1vpAxW1x7J%2F5EB6hzsHZwGJ6mXjA1rjwHtXigi6d7YVz1cScSc64It3KV4Hdu7lapJ6Fdi7rx6Q%3D&pos=9&wd=%E8%8A%9C%E6%B9%96+%E6%89%AF%E7%9A%AE";
        HttpEntity he = HttpKit.get(url);
        System.out.println(he.getUrl());
        System.out.println(he.getHtml());
    }

    @Test
    public void ttd10() {
        ProxyEntity pe = new ProxyEntity("192.168.25.254", 28129);
        pe.setAccount("yproxyq");
        pe.setPassword("zproxyx0#");
        Map<String, String> header = new HashMap<String, String>();
        header.put(HttpConst.COOKIE, "uid=CgqASVYIpMZeVl4FEVxLAg==;");
        for (Integer i=10; i-->0;) {
            String url = "http://www.chinaso.com/search/pagesearch.htm?q=%E9%B8%A0%E6%B1%9F%E5%8C%BA+%E5%A4%B1%E8%B8%AA&page=2";
            url = "http://www.chinaso.com/search/pagesearch.htm?q=%E9%B8%A0%E6%B1%9F%E5%8C%BA+%E5%A4%B1%E8%B8%AA&page=" + (i+1);
            HttpEntity he = HttpKit.get(url, null, header, true);
            String html = he.getHtml();
            System.out.println(html.contains("统计代码"));
        }
    }

    @Test
    public void ttd11() {
        String url = "http://m.tianya.cn/bbs/art.jsp?item=news&id=285919";
        HttpEntity he = HttpKit.get(url);
        System.out.println(he.getCharset());
        System.out.println(he.getHtml());
    }

}
