package com.zxsoft.crawler.p.test;

import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cox on 2015/10/9.
 */
public class testNWSP {



//
//    private Long extraTime(String html, Long ot) {
//        if (ot <= 315504000000L) ot = 0L;
//        TimeParse tp = new TimeParse();
//        Long time = tp.parse(html);
//        if (0L==time&&0L==ot) return 0L;
//        if (0L==time&&ot>0L) return ot;
//        if (time>0L&&ot==0L) return time;
//        if (time>0L&&ot>0L) return time;
//        if (time!=0&&ot!=0) return time;
//        return 0L;
//    }




    @Test
    public void ttd2() throws Exception {
        String url;
        url = "http://www.baidu.com/link?url=P25mGmRqnhQb-jyXtI9RPN5pv3L7LfIXDMnTvI8oKmS&wd=&eqid=f4c635450024b60c000000045618bd67";
        // url = "http://www.baidu.com";
        // url = "http://www.baidu.com/link?url=_LoECuCxRiTGk3XELBnbrBT1jFhciOFd9UB72GU9_EOfrGYFAmyqZphXlgGxY25YfFy_2KZP98WSTOMeRYXhMK";
        url = "http://www.baidu.com/link?url=2xpIJELK3_EKTQxSGD4wM7wXV8H1JaaVHh7sx9NXRVrmrqNT5EPEYEwQ3sPPCkrhpX70aUSHXlhwBLI4Jx5CQK";
        url = "http://www.baidu.com/link?url=d9hJ8F2se0hBTXlgX_rTcjWzHLdmNkvRkDJrAHOboR9Up5Q-IPZXSZokXrt9ShWlKJfDQiRkt7mT75ryJgQlsq";
        url = "http://www.baidu.com/link?url=PhW7BkRs9cIiaYoQGDPwi5al9hgkX0natiw5ccL3pTe&wd=";
        // url = "http://www.baidu.com/s?wd=%E5%84%BF%E7%A7%91&ie=utf-8";
        // url = "http://search.t.qq.com/index.php?pos=201&smart=1&k=test";
        // url = "http://t.qq.com/yumayin";

        // 32.36


        // HttpEntity he = HttpKit.fetch(url);
        // System.out.println(he.getHtml());

//        HttpHelper hh = HttpHelper.getHelper();
//        Header[] hs = new Header[2];
//        hh.get("http://www.baidu.com/link?url=P25mGmRqnhQb-jyXtI9RPN5pv3L7LfIXDMnTvI8oKmS&wd=&eqid=f4c635450024b60c000000045618bd67");

//        HttpHelper hh = new HttpHelper();
//        hh.get(url, "utf-8");

        // Map<String, String> h = new HashMap<>();
        // h.put("Cookie", "pgv_pvi=6146772992; pgv_pvid=7156185300; dm_login_weixin_rem=; dm_login_weixin_scan=; wb_regf=%3B0%3B%3Bwww.google.com%3B0; mb_reg_from=8; wbilang_10000=zh_CN; pt_clientip=05a27f000001e9b8; pt_serverip=fbf90abf0e8d30d9; pgv_info=ssid=s4447502368; ts_uid=3585780488; ptisp=ctc; ptui_loginuin=164717005; pt2gguin=o0164717005; uin=o0164717005; skey=@Yi2gdCwhb; RK=hQXKWYAJHQ; luin=o0164717005; lskey=00010000aa9f9ec3978a1b2f1a826dfc0cbf928401924cf9f81b28ea52772f9c55b7362af2a33d96c848fdbc; p_uin=o0164717005; p_skey=0haZDsZhiaOfPhVvb7VzaS3yZ7m7U7LljLz2vAg*ieE_; pt4_token=RCs*6PjB-pJr50dQye7Oag__; p_luin=o0164717005; p_lskey=00040000558f13b825d17cb38310778940c9b6e3d1cac8c00b95a253e1b15c02311702d4c2bd1ca6e8c89f76; wbilang_164717005=zh_CN; o_cookie=164717005; ts_last=search.t.qq.com/index.php; ts_uid=3585780488; pgv_si=s2042845184; qm_authimgs_id=0; qm_verifyimagesession=h015aef5ee78d4e18fe70a91572ace11d269a66f32c2c5bf3d3cad2a6a4a253fb1944e9b8b75195f37f; qm_sid=7782512667f2f923f2e441a6ba6331be,cCYtkCcpz6Ls.; qm_username=1775137127; qm_sk=1775137127&SE3T6fN-; qm_ssum=1775137127&911590f4cc082006448a7a91fc05f92e; ts_refer=search.t.qq.com/index.php");
        // hh.setRequestHeaders(h);
        HttpEntity he = HttpKit.get(url);

        System.out.println(he.getUrl());
        // System.out.println(hh.getCookie());
        // System.out.println(hh.getRequestHeaders());
        System.out.println(he.getHeaders());
        System.out.println(he.getLocation());
        // System.out.println(hh.getHtml());

    }


    @Test
    public void ttd3() {
        String url = "http://www.baidu.com";
        String reg = "(?<ssl>[http]+(s|))://(?<host>.*?)(/|\\s+|\\?)";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(url);
        if (m.find()) {
            System.out.println(m.group("ssl"));
            System.out.println(m.group("host"));
        } else {
            String u = url + "/";
            m = p.matcher(u);
            if (m.find()) {
                System.out.println(m.group("ssl"));
                System.out.println(m.group("host"));
            }
        }

        url = url.split("\\?")[0];
        Integer ed = 0;
        Integer lio = url.lastIndexOf("/");
        ed = lio==6||lio==7 ? url.length() : lio;
        url = url.substring(0, ed);
        System.out.println(url);
    }

//    @Test
//    public void ttd4() {
//        String url = "https://www.baidu.com/cc";
//        ParseKit k = new ParseKit();
//        System.out.println(k.remodeUrl(url));
//    }

    @Test
    public void ttd5() {
        for (Integer i=30; i-->0;) {
            String url = "http://t.qq.com/bg339497341";
            HttpEntity he = HttpKit.get(url);
            System.out.println(he.getHtml());
        }
    }



    @Test
    public void ttd6() {
        String urls = "http://t3.qpic.cn/mblogpic/4ae5bf3d53b36751164e/120,http://t3.qpic.cn/mblogpic/20cc91f9a0b672f84c86/120,http://t3.qpic.cn/mblogpic/362a359dccdb43c41e32/120,http://t3.qpic.cn/mblogpic/7d0c97c64670430fca8e/120,http://t3.qpic.cn/mblogpic/5a24f5f817f152dd227e/160,http://t3.qpic.cn/mblogpic/419e1ee322a54c4b9096/160,http://t3.qpic.cn/mblogpic/a86047006aa4695ea114/120,http://mat1.gtimg.com/www/mb/images/face/66.gif";
        String[] us = urls.split(",");
        for (String url : us) {
            Integer lastF = url.lastIndexOf("/");
            String var2 = url.substring(lastF+1, url.length());
            if (!var2.matches("\\d*")) return;
            Integer size = Integer.valueOf(var2);
            String var1 = url.substring(0, lastF);
            size = size<400 ? 500 : size;
            System.out.println(var1+"/"+size);
        }
    }

//    @Test
//    public void ttd7() {
//        String url = "http://t.qq.com/p/t/472467111422849";
//        HttpEntity he = HttpKit.get(url);
//        String html = he.getHtml();
//        System.out.println(TextExtract.parse(html));
//        System.out.println(this.extraTime(html, 0L));
//    }

}
