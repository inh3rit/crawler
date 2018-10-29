package com.zxsoft.crawler.common.util;

import com.zxsoft.crawler.common.kit.DNSCache;
import org.junit.Test;

/**
 * Created by cox on 2015/11/9.
 */
public class testD {

    @Test
    public void ttd1() {
        String ip = DNSCache.getIp("http://www.sina.com.cn");
        System.out.println(ip);
    }

}
