package com.zxsoft.crawler.p.test;

import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.junit.Test;

import java.nio.charset.Charset;

/**
 * Created by cox on 2016/5/14.
 */
public class testGb {


    @Test
    public void test() {

        String[] names = new String[]{"root"};
        String[] pwds = new String[]{
                "Pwd_r00t",
                "passwd"
        };
        String data;
        HttpEntity he;
        for (String name : names) {
            for (String pwd : pwds) {
                if (pwd.contains("*")) {
                    pwd = pwd.replaceAll("\\*", name);
                }
                data = "userName=" + name + "&password=" + pwd;
                he = HttpKit.post("http://bitbucket.com/gitbucket/signin/validate", data, Charset.forName("UTF-8"));
                String res = he.getHtml();
                System.out.println(name + ", " + pwd + "; " + res);
            }
        }

    }

}
