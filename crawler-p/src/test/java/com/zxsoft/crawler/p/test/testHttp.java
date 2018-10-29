package com.zxsoft.crawler.p.test;

import com.alibaba.fastjson.JSONObject;
import org.inh3rit.file.FileUtils;
import org.inh3rit.httphelper.common.HttpConst;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.entity.ProxyEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by cox on 2015/9/25.
 */
public class testHttp {


    @Test
    public void test1() {
        Map<String, String> headers = new HashMap<>();
        String url = "http://zzt123.com.cn/index.php?m=content&c=content&a=tel&menuid=1674&pc_hash=P3KYPt&page=3" +
                "&pc_hash=P3KYPt";
        headers.put(HttpConst.COOKIE, "PHPSESSID=ldo3m3hji20uf0phc5hpe51gk6; " +
                "WeIXM_admin_username=1a454v9HfBgMtUg1shQRlZyZjbwUl9QhXljlo-ovfvYhbg; " +
                "WeIXM_siteid=e939BqfKBYR_Qk00CflP-c3k2kSDGVlH9R9Bxrlg; " +
                "WeIXM_userid=a151hxdDU5xZUcd1g6oWaVSYc9wmqky9usdPLh0W; " +
                "WeIXM_admin_email=5cefFfITUu77BAM9-KZLe9ycx_qPr0YcCpAAV_RWenKJqPh7_UnJdttFg7I4bds; " +
                "WeIXM_sys_lang=c2d1I1Cmbl-oB_cPlvwLNFhtdtpLeZJTCsKRk45HTa96VQ");
        String html = HttpKit.get(url, null, headers, null, null).getHtml();

        System.out.println(html);
    }

    @Test
    public void test2() {
        String html = HttpKit.get("http://192.168.32.11:8983/solr/sentiment/select?q=platform%3A6&fq=lasttime%3A" +
                "%5B2016-10-12T00%3A00%3A00Z+TO+2016-10-12T14%3A00%3A00Z%5D&wt=json&indent=true").getHtml();
        System.out.println(html);

        JSONObject jsonObject = JSONObject.parseObject(html);
        JSONObject response = (JSONObject) jsonObject.get("response");
        int numFound = Integer.parseInt(response.get("numFound").toString());
        System.out.println(numFound);
    }

    public static void main(String[] args) {
        String url = "http://zzt123.com.cn/index.php?m=content&c=content&a=tel&menuid=1674&pc_hash=kVcX8I&page={0}" +
                "&pc_hash=kVcX8I";
        Map<String, String> headers = new HashMap<String, String>() {{
            put(HttpConst.COOKIE, "WeIXM_admin_username=a87aal6chXDnseWEqrYaVHWqbAf5ZOR8ODdAs-fcC1ykbg; " +
                    "WeIXM_siteid=de186paRAZqck34N-2-PFHK7d3UU_7XTkzw3tviT; " +
                    "WeIXM_userid=73bbPvIbgg87-dlQgG_uZ03q3pJir_OqunSNdFDF; " +
                    "WeIXM_admin_email=ee87btHrnxbtB0oyFKQhx5QVE9IiJJ8URjj_RORJm-TqLVVMBtvLhPIXNr7gvt4; " +
                    "WeIXM_sys_lang=69cch3xqqigQPXenseK0gSZM9zBArleTZJTyrw6tr1rEGA; " +
                    "PHPSESSID=ldo3m3hji20uf0phc5hpe51gk6");
        }};
//        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        for (int i = 10569; i < 609573; i++) {
            final String ii = String.valueOf(i + 1);
            try {
//            threadPool.execute(() -> {
                StringBuffer sb = new StringBuffer();
                String _url = MessageFormat.format(url, ii);
                String html = HttpKit.get(_url, null, headers, null, Charset.forName("utf8"), 20000, 20000).getHtml();
                System.out.print("_" + ii);
                Document doc = Jsoup.parse(html);
                Elements elements = doc.select("table[width=100%] td[width=120]");
                elements.stream()
                        .filter(e -> !e.text().contains("查看"))
                        .forEach(e -> sb.append(e.text()).append("\n"));
                FileUtils.appendAsNewLine(sb.toString(), "/home/tony/phoneNum.txt");
//            });
            } catch (Exception e) {
                System.out.println();
                System.out.println("-" + ii);
                System.out.println();
            }
        }
    }

    @Test
    public void test3() {
        System.out.println(HttpKit.get("https://link.aizhan.com/?url=www.iqiyi.com", null, null, null, Charset
                .forName("utf8")
        ).getHtml());
    }

    @Test
    public void test4() {
        ProxyEntity proxyEntity = new ProxyEntity("192.168.226.116", 25, "yproxyq", "zproxyx");
        HttpEntity httpEntity = HttpKit.post("http://comment" +
                ".dy.163.com/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads/D3MDA7FP05219Q30/comments" +
                "/225634082/action/upvote?ntoken=36ce6bd1-10b4-49a3-98e4-daf17e3469c4&ibc=newspc", null, "", new
                HashMap<>(), proxyEntity, Charset
                .defaultCharset(), true);
        System.out.println();
    }

    @Test
    public void test5() {
        for (int i = 0; i < 100; i++) {
            try {
                HttpEntity httpEntity = HttpKit.get("http://api.ip.data5u.com/dynamic/get" +
                        ".html?order=c6a625a797525752a221fd06271ba69e&sep=6");

                String str = httpEntity.getHtml().trim().replaceAll(";", "");
                System.out.println(str);
                String host = str.split(":")[0];
                String port = str.split(":")[1];
                ProxyEntity proxyEntity = new ProxyEntity(host, Integer.parseInt(port));
//                HttpEntity httpEntity0 = HttpKit.post("http://comment" +
//                        ".dy.163.com/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads/D3MDA7FP05219Q30" +
//                        "/comments/gentoken?ibc=newspc", new HashMap<>(), null, Charset.defaultCharset(), true);
//                String token = httpEntity0.getHtml().split(":")[1];
                HttpEntity httpEntity$ = HttpKit.post("http://comment" +
                                ".dy.163.com/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads" +
                                "/D3MDA7FP05219Q30/comments/225653954/action/upvote?ntoken=3925023b-0974-48b6-bac6" +
                                "-8fbc83170d35&ibc=newspc  ", new HashMap<>(),
                        "", new HashMap<String, String>() {{
                            put("Referer", "http://comment.dy.163.com/dy_w…dia_bbs/D3MDA7FP05219Q30.html");
                            put(HttpConst.USER_AGENT, "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:57.0) " +
                                    "Gecko/20100101 Firefox/57.0");
                            put(HttpConst.COOKIE, "usertrack=c+5+hljwP6mPWxYRA7LVAg==; " +
                                    "_ntes_nnid=2743fe4e740948e13611625d06d196c6,1492139946683; " +
                                    "_ntes_nuid=2743fe4e740948e13611625d06d196c6; _ga=GA1.2.759797340.1492139947; " +
                                    "vjuids=8b97e8e20.15ba7d341fc.0.974cf2d697d9f8; vjlast=1493169292.1511226931.11; " +
                                    "vinfo_n_f_l_n3=3aab01a0b8a2298e.1.7.1493169291929.1510818367309.1511231841980; " +
                                    "__s_=1; " +
                                    "UM_distinctid=15fc3c9b1227a1-0ac59a281acc6-63227242-1fa400-15fc3c9b123642; " +
                                    "__gads=ID=dfbf00cabafb67b3:T=1510818296:S=ALNI_MYUM3Q-rHWwo3o8x6R7Xa-7dA_bXw; " +
                                    "Province=021; City=021; ne_analysis_trace_id=1511229229972; " +
                                    "NNSSPID=ebbc8b83c9194200acf4b301a0a88955; " +
                                    "JSESSIONID-WNC-98XSE=d0d0cb594400ad4e34ee5f3dbd64458eafbd65c3a90aec5c438299d1b078fbeaf125a5941d5a55afed116d388dbf93f49c845de0134fdb742d9cedd999f03bc4ac07007cd278c048c5e09a9cbfc23aaf6d79e8c112fc0ffd8b8d80b634d48c11b93bc08b2a99b8447b0988750192f561c63d65a8e570d6e69893074870c7c5a214cf3635%3A1511241497336; _lkiox7665q_=26; WEB_TOKEN=3925023b-0974-48b6-bac6-8fbc83170d35");
                        }}, proxyEntity, Charset.defaultCharset(), true);
                System.out.println(httpEntity$.getResponseCode());
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    @Test
    public void test8() throws InterruptedException {
        AtomicInteger integer = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger allCount = new AtomicInteger(0);
        ExecutorService threadPool = Executors.newFixedThreadPool(40);
        String cookie = HttpKit.post("http://comment" +
                ".news.163.com/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads/D3LP5KGU0001875P/comments" +
                "/gentoken?ibc=newspc", "").getHtml();
        String token = JSONObject.parseObject(cookie).getString("gentoken");
        while (true) {
            try {
                HttpEntity httpEntity = HttpKit.get("http://vtp.daxiangdaili.com/ip/?tid=555552254190432&num=100");
                String proxys = httpEntity.getHtml();
                for (String proxyStr : proxys.split("\n")) {
                    if (integer.get() >= 40) {
                        Thread.sleep(1000);
                        continue;
                    }
                    integer.incrementAndGet();
                    threadPool.execute(() -> {
                        try {
                            allCount.incrementAndGet();
                            String str = proxyStr.trim().replaceAll(";", "");
                            System.out.println(str);
                            String host = str.split(":")[0];
                            String port = str.split(":")[1];
                            ProxyEntity proxyEntity = new ProxyEntity(host, Integer.parseInt(port));
                            HttpEntity httpEntity$ = HttpKit.post("http://comment" +
                                    ".news.163.com/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads" +
                                    "/D3LP5KGU0001875P/comments/225866891/action/upvote?ntoken=" + token +
                                    "&ibc=newspc", new HashMap<>(), "", new HashMap<String, String>() {{
//                                put("Referer", "http://comment.dy.163.com/dy_w…dia_bbs/D3MDA7FP05219Q30.html");
                                put(HttpConst.USER_AGENT, "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:57.0) " +
                                        "Gecko/20100101 Firefox/57.0");
                                put(HttpConst.COOKIE, "WEB_TOKEN=" + token);
                            }}, proxyEntity, Charset.defaultCharset(), true);
                            if (httpEntity$.getResponseCode() == 200)
                                successCount.incrementAndGet();

                            System.out.println("result:" + httpEntity$.getResponseCode());
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            integer.decrementAndGet();
                        }
                    });
                }
            } catch (Exception e) {
                continue;
            }
        }
    }

    @Test
    public void test6() {
        HttpEntity httpEntity$ = HttpKit.post("http://comment" +
                        ".dy.163.com/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads/D3MDA7FP05219Q30" +
                        "/comments/225673083/action/upvote?ntoken=a70e3566-65c7-4028-baf5-dcf017bcc123&ibc=newspc",
                new HashMap<>(),
                "", new HashMap<String, String>() {{
                    put("Referer", "http://comment.dy.163.com/dy_wemedia_bbs/D3MDA7FP05219Q30.html");
                    put(HttpConst.USER_AGENT, "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:57.0) Gecko/20100101 " +
                            "Firefox/57.0");
                    put(HttpConst.COOKIE, "WEB_TOKEN=a70e3566-65c7-4028-baf5-dcf017bcc123");
                }}, null, Charset.defaultCharset(), true);
        System.out.println(httpEntity$.getResponseCode());
    }


}
