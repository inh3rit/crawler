package com.zxsoft.crawler.common.kit;


import java.util.List;

/**
 * Created by cox on 2015/9/6.
 */
public class IpKit {

    public static String getIp4() {
        List<String> l = IPUtil.getIPv4();
        if (l==null||l.isEmpty()) return "undefined";
        return l.get(0);
    }

}
