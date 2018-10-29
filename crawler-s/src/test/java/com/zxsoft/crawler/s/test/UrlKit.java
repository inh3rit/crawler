package com.zxsoft.crawler.s.test;

import com.jfinal.plugin.activerecord.Record;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UrlKit {

    private static Record proxyOption = new Record();

    private static Record callUrl(String url, String param, String type, String cookie, List<String> prop) {
        try {
            if (proxyOption!=null&&proxyOption.getColumnNames().length>=2) {
                String[] pk = proxyOption.getColumnNames();
                for(int i=pk.length; i-->0;)
                    System.setProperty(pk[i], String.valueOf(proxyOption.get(pk[i])));
            }
            if ("GET".equals(type.toUpperCase())) url += "?" + param;
            HttpURLConnection htc = (HttpURLConnection)new URL(url).openConnection();
            htc.setDoOutput(true);
            htc.setDoInput(true);
            htc.setUseCaches(false);
            htc.setRequestProperty("Cookie", cookie);
            // htc.setRequestProperty("Host", "loop.xiami.com");
            if (prop!=null) {
                for(int i=prop.size(); i-->0;) {
                    String[] pps = prop.get(i).split("=");
                    htc.setRequestProperty(pps[0], pps[1]);
                }
            }
            // htc.setRequestProperty("Host", "login.xiami.com");
            htc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            htc.setRequestMethod("POST".equals(type.toUpperCase()) ? "POST" : "GET");
            DataOutputStream out = new DataOutputStream(htc.getOutputStream());
            if ("POST".equals(type.toUpperCase())) out.write(param.getBytes());
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(htc.getInputStream(), Charset.forName("UTF-8")));
            String line, result = "";
            while((line=in.readLine())!=null) result += line;
            in.close();
            // String cke = htc.getHeaderField("Set-Cookie");
            //获取cookie
            Map<String,List<String>> map=htc.getHeaderFields();
            Set<String> set=map.keySet();
            String ck = "";
            for (Iterator iterator = set.iterator(); iterator.hasNext();) {
                String key = (String) iterator.next();
                if (!"Set-Cookie".equals(key)) continue;
                List<String> list = map.get(key);
                for (String str : list)
                    ck += str.split(";")[0]+";";
            }
            Record res = new Record();
            res.set("result", result);
            res.set("cookie", ck);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            System.setProperty("java.net.useSystemProxies", "true");
            if (proxyOption!=null)
                proxyOption.clear();
        }
    }

    public static void downloadFile(String url, String newFile) {
        try {
            HttpURLConnection htc = (HttpURLConnection)new URL(url).openConnection();
            InputStream is = htc.getInputStream();
            byte[] bs = new byte[1024];
            int len;
            OutputStream os = new FileOutputStream(newFile);
            while ((len = is.read(bs)) != -1)
                os.write(bs, 0, len);
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Record get(String url, String param, String cookie, List<String> prop) {
        return callUrl(url, param, "GET", cookie, prop);
    }

    public static Record get(String url, String param, String cookie) {
        return callUrl(url, param, "GET", cookie, null);
    }

    public static Record get(String url, String cookie) {
        return callUrl(url, "", "GET", cookie, null);
    }

    public static Record get(String url) {
        return callUrl(url, "", "GET", "", null);
    }

    public static Record post(String url, String param, String cookie, List<String> prop) {
        return callUrl(url, param, "POST", cookie, prop);
    }

    public static Record post(String url, String param, String cookie) {
        return callUrl(url, param, "POST", cookie, null);
    }

    public static Record post(String url, String param) {
        return callUrl(url, param, "POST", "", null);
    }


    public static void setProxyHost(String host) {
        proxyOption.set("http.proxyHost", host);
    }
    public static void setProxyPort(Integer port) {
        proxyOption.set("http.proxyPort", port);
    }
    public static void clearProxy() {
        if (proxyOption!=null)
            proxyOption.clear();
    }

    
    public static void main(String[] args) {
        // System.out.println(postUrl("http://localhost/test/xiami/cai.php"));
        Record r = callUrl("http://localhost/test/xiami/cai.php", "a=b&d=3", "get", "", null);
        System.out.print(r.getStr("html")+"\n");
        System.out.print(r.getStr("cookie"));
    }
}
