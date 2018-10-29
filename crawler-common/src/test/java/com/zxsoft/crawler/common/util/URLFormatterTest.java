package com.zxsoft.crawler.common.util;

import com.zxsoft.crawler.common.kit.UrlKit;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class URLFormatterTest {

        @Test
        public void test0() throws UnsupportedEncodingException {
                String url = "http://www.soubao.net/search/searchList.aspx"
                                                + "?startdate=%tY%tm%td&enddate=%tY%tm%td";
                url = UrlKit.format(url);
                System.out.println(url);
        }
        
        @Test
        public void test1() throws UnsupportedEncodingException {
                String url = "http://www.soubao.net/search/searchList.aspx"
                                                + "?keyword=%s&startdate=%tY%tm%td&enddate=%tY%tm%td";
                url = UrlKit.format(url, "中国");
                System.out.println(url);
        }
}
