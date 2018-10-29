package com.zxsoft.crawler.common.kit;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by cox on 2015/9/6.
 */
public class DNSCache {

    public static String getIp(String url)  {
        try {
            url = url.endsWith("/") ? url : url + "/";
            String host = url.substring(url.indexOf("//") + 2, url.indexOf('/', url.indexOf("//") + 2));
            InetAddress giriAddress = java.net.InetAddress.getByName(host);
            return giriAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }


}
