package com.zxsoft.crawler.p.test;

import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 * Created by cox on 2016/3/9.
 */
public class test163 {


    @Test
    public void ttd1() throws UnsupportedEncodingException {
        String url = "https://mail.163.com/entry/cgi/ntesdoor?funcid=loginone&passtype=1&product=mail163";
        // String paras = "savelogin=0&username=abb@163.com&password=pwd";
        String paras = "savelogin=1&username=abb@163.com&password=pwd";
        // Map<String, String> headers = new HashMap<String, String>();
        // headers.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:45.0) Gecko/20100101 Firefox/45.0");
        for (int i=2; i-->0;) {
            HttpEntity he = HttpKit.post(url, paras);
            if (!he.getHeader("Location").contains("error")) continue;
            System.out.println(he.getHeader("Location") + "\n");
            System.out.println(i);
        }
        // System.out.println(JsonKit.toJson(he.getHeaders()));
    }

}
