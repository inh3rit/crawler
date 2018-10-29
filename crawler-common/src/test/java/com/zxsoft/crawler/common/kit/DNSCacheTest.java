package com.zxsoft.crawler.common.kit;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Created by iaceob on 2015/11/29.
 */
public class DNSCacheTest {


    @Test
    public void testGetIp() throws Exception {
        Long ts1 = System.currentTimeMillis();
        String ip = DNSCache.getIp("http://google.com");
        System.out.println(ip);
        Long ts2 = System.currentTimeMillis();
        System.out.println(ts2-ts1);
    }


    @Test
    public void ttd2() {
//
//        ShellCommand shell = new ShellCommand();
//
//        String result = shell.run("dig +short NS mkyong.com");
//        System.out.println(result);
    }

}