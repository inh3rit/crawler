package com.zxsoft.crawler.p.test.time;

import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.junit.Test;

import java.sql.Timestamp;

/**
 * Created by cox on 2015/10/22.
 */
public class testParseTime {



    @Test
    public void ttd1() {
        String url = "http://news.sina.com.cn/c/2007-06-24/024112079463s.shtml";
        url = "http://www.4c.cn/thread-1245328166-1-1.html";
        url = "http://www.pocketdigi.com/20120701/880.html";
        HttpEntity he = HttpKit.get(url);
        String content = he.getHtml();
        url = he.getUrl();
        String time = FetchPubTime.getPubTimeVarious(url, content);
        System.out.println(time);
    }


    @Test
    public void ttd2() {
        String url = "http://sup.zgny.com.cn/2015-4-2/4713015.shtml";
        String time = FetchPubTime.getPubTimeFromUrl(url);
        Timestamp t = Timestamp.valueOf(time);
        System.out.println(t);
        System.out.println(t.getTime());
    }


}
