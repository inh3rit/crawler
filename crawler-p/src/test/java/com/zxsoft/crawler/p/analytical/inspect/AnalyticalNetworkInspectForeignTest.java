package com.zxsoft.crawler.p.analytical.inspect;


import com.zxsoft.crawler.common.entity.sync.TimeRegexEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.kit.UrlKit;
import com.zxsoft.crawler.exception.CrawlerException;
import com.zxsoft.crawler.p.ext.DateExtractor;
import com.zxsoft.crawler.p.ext.TimeParse;
import com.zxsoft.crawler.p.kit.remode.UrlRemake;
import com.zxsoft.crawler.p.occupancy.JDBCUtils;
import name.iaceob.kit.disgest.Disgest;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.entity.ProxyEntity;
import org.inh3rit.httphelper.http.HttpStatus;
import org.inh3rit.httphelper.kit.HttpKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tony on 17-7-13.
 */
public class AnalyticalNetworkInspectForeignTest {

    public static void main(String[] args) {
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        AnalyticalNetworkInspectForeignTest test = new AnalyticalNetworkInspectForeignTest();
        List<TimeRegexEntity> timeRuleLst = test.getTimeRule();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(60);
        ProxyEntity pe = new ProxyEntity("192.168.3.201", 25, "yproxyq", "zproxyx");
        test.getRuleFromDB().forEach(rule -> {
//            fixedThreadPool.execute(() -> {
                HttpEntity c_he = HttpKit.get(rule.getUrl(), null, null, pe, Charset.forName("gbk"));
                if (c_he.getResponseCode() == HttpStatus.SC_MOVED_PERMANENTLY) { // 301 永久性重定向，需要从location中获取重定向的url
                    String url = c_he.getHeader("Location");
                    c_he = HttpKit.get(UrlRemake.remake(url), null, null, pe, Charset.forName("utf8"));
                }
                final HttpEntity he = c_he;
                Document doc = Jsoup.parse(he.getHtml());
                int count = 0;
                for (Element line : doc.select(rule.getListdom()).first().select(rule.getLinedom())) {
//                    fixedThreadPool.execute(() -> {
                        String sub_url = "";
                        String sub_date = "";
                        String sup_date = "";
                        String title = "";
                        String content = "";
                        try {
                            Elements url_elms = line.select(rule.getUrldom());
                            if (CollectionKit.isEmpty(url_elms))
                                continue;//return;

                            // extract date from list page
                            if (rule.getDatedom() != null && !rule.getDatedom().equals(""))
                                sup_date = line.select(rule.getDatedom()).first().text();

                            sub_url = line.select(rule.getUrldom()).first().attr("href");
                            sub_url = urlStruct(sub_url, he.getHost(), he.getBasePath(), he.getUrl());
                            HttpEntity _he = HttpKit.get(sub_url, null, null, pe, Charset.forName("gbk"));
                            if (("GB2312").equals(_he.getCharset().toString()))
                                _he = HttpKit.get(sub_url, null, null, pe, Charset.forName("gbk"));
                            Document _doc = Jsoup.parse(_he.getHtml());
                            title = _doc.title();
                            // extract date from detail page
                            if (rule.get_datedom() != null && !rule.get_datedom().equals(""))
                                try {
                                    Elements e_sub_date = null;
                                    for (String sub_date_dom : rule.get_datedom().split(",")) {
                                        e_sub_date = _doc.select(sub_date_dom);
                                        if (e_sub_date != null && e_sub_date.size() != 0)
                                            break;
                                    }
                                    sub_date = e_sub_date.first().text();
                                } catch (Exception e) {
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                    Long ts = DateExtractor.extractInMilliSecs(test.getDateFromUrl(sub_url));
                                    System.out.println("*************************************");
                                    System.out.println("section:".concat(rule.get_comment()));
                                    System.out.println("url:".concat(sub_url).concat("; url-date:").concat(format
                                            .format(new Date(ts))));
                                }
                            // extract content from detail page
                            Elements e_content = null;
                            for (String contentDom : rule.getContentdom().split(",")) {
                                e_content = _doc.select(contentDom);
                                if (e_content != null && e_content.size() != 0)
                                    break;
                            }
                            content = e_content.first().text();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        } finally {
                            System.out.println
                                    ("-------------------------------------------------------------------------");
                            System.out.println(rule.getComment() + " " + rule.get_comment());
                            System.out.println("section:".concat(rule.get_comment()));
                            System.out.println("url:".concat(sub_url));
                            System.out.println("id:".concat(Disgest.encodeMD5(sub_url).toUpperCase()));
                            System.out.println("sup-date:".concat(sup_date));
                            try {
                                System.out.println("sup-date-parse:".concat(dateFormat.format(new Date(TimeParse.parseByRegex
                                        (sup_date, timeRuleLst)))));
                            } catch (CrawlerException e) {
                                e.printStackTrace();
                            }
                            System.out.println("sub-date:".concat(sub_date));
                            try {
                                System.out.println("sub-date-parse:".concat(dateFormat.format(new Date(TimeParse.parseByRegex
                                        (sub_date, timeRuleLst)))));
                            } catch (CrawlerException e) {
                                e.printStackTrace();
                            }
                            System.out.println("title:".concat(title));
                            System.out.println("content:".concat(content));
                        }
//                    });
                    if (++count > 30) break;
                }
//            });
        });
    }

    private List<RuleModel> getRuleFromDB() {
        Connection conn = JDBCUtils.getConn();
        String sql = "SELECT" +
                "  cl.url," +
                "  cl.listdom," +
                "  cl.linedom," +
                "  cl.urldom," +
                "  cl.datedom," +
                "  cd.content," +
                "  cd.date," +
                "  w.comment," +
                "  s.comment" +
                "  FROM cw_patrol AS p LEFT JOIN section AS s ON p.section = s.id" +
                "  LEFT JOIN website AS w ON s.site = w.id" +
                "  LEFT JOIN conf_list AS cl ON s.url = cl.url" +
                "  LEFT JOIN conf_detail AS cd ON s.url = cd.listurl" +
                "  LEFT JOIN cw_site_mark AS sm ON w.tid = sm.tid" +
                "  WHERE p.reptile = '56'; ";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<RuleModel> ruleList = new ArrayList<>();
        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                RuleModel rule = new RuleModel(rs.getString(1), rs.getString(2), rs.getString(3)
                        , rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)
                        , rs.getString(8), rs.getString(9));
                ruleList.add(rule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ruleList;
    }

    private List<TimeRegexEntity> getTimeRule() {
        Connection conn = JDBCUtils.getConn();
        String sql = "SELECT id, sample,regex,mark,sort from timereg;";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<TimeRegexEntity> timeRuleLst = new ArrayList<>();
        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                TimeRegexEntity entity = new TimeRegexEntity();
                entity.setId(rs.getInt(1)).setMark(rs.getString(4))
                        .setRegex(rs.getString(3)).setSample(rs.getString(2)).setSort(rs.getInt(5));
                timeRuleLst.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timeRuleLst;
    }

    private String getDateFromUrl(String url) {
        String url_reg = "(1\\d{1}/\\d{1,2}/\\d{1,2})";
        Pattern p = Pattern.compile(url_reg);
        Matcher m = p.matcher(url);
        if (m.find())
            return "20".concat(m.group());
        return null;
    }

    @Test
    public void testGetDateFromUrl() {
        System.out.println(getDateFromUrl("http://www.epochtimes.com/gb/16/8/5/n9500064.htm"));

    }

    @Test
    public void testStringFormat() {
        System.out.println(String.format("sssss %s ssss", "sb"));
        String url = "http:\\/\\/www.epochtimes.com\\/gb\\/17\\/8\\/7\\/n9505574.htm";
    }

    @Test
    public void testForeach() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i + "");
        }

        list.forEach(i -> {
            if (i.equals("5"))
                return;
            System.out.println(i);
        });

    }

    @Test
    public void testLambda() {
        List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
        Collections.sort(names, (a, b) -> a.compareTo(b));
    }

    @Test
    public void testClock() {
        Clock clock = Clock.systemDefaultZone();
        System.out.println(clock.millis());
        System.out.println(System.currentTimeMillis());
    }

    @Test
    public void testRemoveHtmlLabel() {
//        System.out.println(AnalyticalNetworkInspectForeign.removeHtmlLabel
// ("<p><center></p>\\n<h1>伯克利传统风筝节</h1>\\n<p><img class=\\\"aligncenter size-medium wp-image-9471979\\\"
// src=\\\"http://i.epochtimes.com/assets/uploads/2017/07/01-11-450x300.jpg\\\" alt=\\\"01\\\" width=\\\"450\\\"
// height=\\\"300\\\" /></p>\\n<p></center>好玩的伯克利风筝节（Berkeley Kite Festival &
// Championships）又来喽！放风筝可不是孩子们的专利！你将有机会看到世界上最大的章鱼风筝，比房子还要大，比火车还要长的巨型风筝，还有团体风筝芭蕾，2万平方英尺的来自新西兰的巨型怪物风筝，还有日式风筝……<br
// />\\n没有风筝也不要紧，现场还有各式风筝出售，当然也少不了音乐和美食啦！</p>\\n<p>时间：7月29、30日，周六、日，上午10:00～下午6:00<br />\\n费用：免费入场<br
// />\\n地点：Berkeley Marina<br />\\n地址：201 University Ave., Berkeley<br />\\n网址：goo
// .gl/LJKEoB</p>\\n<p><center></p>\\n<h1>加州大蒜节</h1>\\n<p><img class=\\\"aligncenter size-medium wp-image-9471994\\\"
// src=\\\"http://i.epochtimes.com/assets/uploads/2017/07/02-10-450x225.jpg\\\" alt=\\\"02\\\" width=\\\"450\\\"
// height=\\\"225\\\" /></p>\\n<p></center>一年一度的大蒜爱好者盛会Gilroy Garlic
// Festival
// 加州大蒜节又来了！美食绝对是大蒜节的重心，几十家的美食摊位、巨大的户外厨房，为大家准备了各种大蒜美食，而且还有大蒜冰淇淋，这是什么味道呀？看世界著名的“烟火厨师”上演一场壮观的美食火焰秀，炸出香喷喷的大蒜海鲜美食！除了有各式各样美味的大蒜食品，还有现场的音乐表演和娱乐活动，适合全家人一同参与！</p>\\n<p>时间：7月28-30日，周五-日，上午10:00～下午7:00<br />\\n费用：成人$20；长者$15；孩童$10<br />\\n地点：Christmas Hill Park<br />\\n地址：7050 Miller Avenue, Gilroy<br />\\n网址：goo.gl/C5poio</p>\\n<p><center></p>\\n<h1>国家芝士蛋糕日 Cheesecake半价</h1>\\n<p><img class=\\\"aligncenter size-medium wp-image-9471995\\\" src=\\\"http://i.epochtimes.com/assets/uploads/2017/07/03-11-450x300.jpg\\\" alt=\\\"03\\\" width=\\\"450\\\" height=\\\"300\\\" /></p>\\n<p></center>在美国说起芝士蛋糕（Cheesecake），基本上都会想到芝士蛋糕厂（The Cheesecake Factory）。The Cheesecake Factory将于7月30日、31日盛大庆祝国家芝士蛋糕日，在全国的189家餐厅中，半价提供30多种传奇风味的Cheesecake。</p>\\n<p>时间：7月30、31日，周日、一<br />\\n网址：goo.gl/q64Emb</p>\\n<p>The Cheesecake Factory湾区分店：</p>\\n<p>Union Square –251 Geary Street, San Francisco<br />\\nCorte Madera – 1736 Redwood Hwy<br />\\nSan Mateo – 398 Hillsdale Shopping Center<br />\\nWalnut Creek – Plaza Escuela, 1181 Locust Street<br />\\nPalo Alto – 375 University Avenue<br />\\nPleasanton – 1350 Stoneridge Mall Road<br />\\nSanta Clara – Westfield Shoppingtown Valley Fair, 3041 Stevens Creek Blvd<br />\\nSan Jose – Westfield Shoppingtown Oakridge, 925 Blossom Hill Road</p>\\n<p><center></p>\\n<h1>“一茶”奶茶店3周年店庆活动</h1>\\n<p><img class=\\\"aligncenter size-medium wp-image-9471997\\\" src=\\\"http://i.epochtimes.com/assets/uploads/2017/07/04-10-450x253.jpg\\\" alt=\\\"smoothie poster\\\" width=\\\"450\\\" height=\\\"253\\\" /></p>\\n<p></center>位于菲利蒙（Fremont）小台北和圣荷西（San Jose）市政厅正对面的“一茶鲜捞饮”奶茶店将于8月4、5、6日连续3天举办买三送一和picture reality 3周年店庆活动。他们所有的奶茶食材，全部采用天然的产品，不加任何化学添加剂，同时要吃得可口。食材大部分是通过冷冻柜从台湾进口过来，从而确保了食材的天然和新鲜。可是，相应地成本也比从本地购买原材料增加了许多。<br />\\npicture reality活动是邀请顾客阖家观赏“一茶”最新制作的趣味卡通动画，并且模仿卡通中的银幕重要场景拍照留影。凡将照片分享到Facebook等社交媒体上的，将获得一个免费的topping。</p>\\n<p>时间：8月4、5、6日<br />\\n地址：46809 Warm Springs Blvd., Fremont；电话：510-969-9032<br />\\n231 E. Santa Clara St., San Jose；电话：408-638-0186<br />\\n网址：goo.gl/YXCgGg</p>\\n<p><center></p>\\n<h1>精英理财举办<br />\\n长期护理保险规划讲座</h1>\\n<p><img class=\\\"aligncenter size-full wp-image-9472000\\\" src=\\\"http://i.epochtimes.com/assets/uploads/2017/07/122.jpg\\\" alt=\\\"122\\\" width=\\\"291\\\" height=\\\"435\\\" /></p>\\n<p></center>特别给湾区广大华人推荐最新长期护理保险，该产品结合了长期护理及终身人寿保险，用不到长期护理有人寿保险理赔！ 保单可两人合买，保费只付一单，独家提供的附约可保两人长期护理保障终身，无上限！<br />\\n很多人曾经被保险公司拒保，我们可能是少数能让你成保的公司，如果你有三高，或有糖尿病，或是B型肝炎带菌，有红斑性狼疮骨疏松等病可接受，最老到80岁还可以申请，保单保到120岁！<br />\\n时间： 7月29日，周六，下午2:00～4:00<br />\\n地址：3100 Mowry Ave. Suite 300., Fremont<br />\\n报名请电： 408-466-0233</p>\\n<p><center></p>\\n<h1>“夏威夷七月”金银岛跳蚤市场</h1>\\n<p><img class=\\\"aligncenter size-medium wp-image-9472003\\\" src=\\\"http://i.epochtimes.com/assets/uploads/2017/07/05-8-450x246.jpg\\\" alt=\\\"05\\\" width=\\\"450\\\" height=\\\"246\\\" /></p>\\n<p></center>金银岛跳蚤市场（Treasure Island Flea）是现代城市跳蚤市场，有1万5千多摊位，还有400多策展商，销售原创和设计的产品，还有艺术家和古董/古董收藏家。在现场有各式各样的手工艺精品，除了款式独特之外，价钱也非常实惠。而除了购物之外，还有二十多个餐车，可以品尝到旧金山的各式街头美食和葡萄酒，现场还有很多精彩的表演活动供大家欣赏。可谓是集购物、美食和娱乐于一体，是适合寻宝与全家同乐的活动。</p>\\n<p>时间：7月29、30日，周六、日，上午10:00～下午4:00<br />\\n费用：$3；孩童免费；免费停车场<br />\\n地点：Treasure Island<br />\\n网址：goo.gl/kRucVT</p>\\n<p><center></p>\\n<h1>圣卡洛斯历史街区步行旅游</h1>\\n<p><img class=\\\"aligncenter size-medium wp-image-9472016\\\" src=\\\"http://i.epochtimes.com/assets/uploads/2017/07/06-450x191.png\\\" alt=\\\"06\\\" width=\\\"450\\\" height=\\\"191\\\" /></p>\\n<p> </p>\\n<p></center>圣卡洛斯（San Carlos）的市中心有很多历史性建筑，而且Downtown还有非常多的小商店和小餐馆。San Carlos是座小山城，以前这里曾是旧金山人度假的乡村，所以有很多乡村风格的老房子。<br />\\n圣卡洛斯遗产协会的职员将带着您，步行游览市中心的历史古迹。游览从市政厅公园开始，在圣卡洛斯历史博物馆结束，那里将会有茶点和博物馆之旅。</p>\\n<p>时间：7月29日，周六，上午10:30～中午12:00<br />\\n费用：免费<br />\\n地点：San Carlos City Hall Park<br />\\n地址：1401 San Carlos Ave., San Carlos<br />\\n网址：goo.gl/Qfno8u</p>\\n<p><center></p>\\n<h1>圣荷西健身展</h1>\\n<p><img class=\\\"aligncenter size-medium wp-image-9472008\\\" src=\\\"http://i.epochtimes.com/assets/uploads/2017/07/07-6-450x471.jpg\\\" alt=\\\"07\\\" width=\\\"450\\\" height=\\\"471\\\" /></p>\\n<p></center>一连两天的健身展将在San Jose Convention Center 举行，在场将有300多个摊位展出健身相关产品，包括健身器材、衣装、食品与饮料等等，你将有机会率先看到健美界最新推出的各种产品！</p>\\n<p>时间：7月29、30日，周六、日，上午10:00～下午6:00<br />\\n费用：$25<br />\\n地点：San Jose Convention Center<br />\\n地址：150 West San Carlos Street, San Jose<br />\\n网址：goo.gl/NqPz8c</p>\\n<p><center></p>\\n<h1>Off the Grid 周日野餐派对</h1>\\n<p><img class=\\\"aligncenter size-medium wp-image-9472013\\\" src=\\\"http://i.epochtimes.com/assets/uploads/2017/07/08-5-450x228.jpg\\\" alt=\\\"08\\\" width=\\\"450\\\" height=\\\"228\\\" /></p>\\n<p></center>久等了！Off the Grid 周日野餐派对终于回来了！现场将有29个美食摊位任君挑选喜欢的食物！准备好坐在草地上一边欣赏海湾和金门大桥的景色，一边享用美食和饮料，和家人朋友们一同体验热闹的野餐气氛。</p>\\n<p>时间：7月30日，周日，上午11:00～下午4:00<br />\\n费用：免费入场<br />\\n地点：Presidio Picnic<br />\\n地址：Main Parade Ground Lawn, San Francisco<br />\\n网址：goo.gl/bKbLgo</p>\\n<p><center></p>\\n<h1>圣利安珠Mulford花园艺术与音乐节</h1>\\n<p><img class=\\\"aligncenter size-full wp-image-9472019\\\" src=\\\"http://i.epochtimes.com/assets/uploads/2017/07/09-1.png\\\" alt=\\\"09\\\" width=\\\"433\\\" height=\\\"436\\\" /></p>\\n<p></center>圣利安珠的2017年Mulford花园艺术与音乐节将举办一整天的蓝草音乐、民间摇滚、摇滚和歌手作曲家以及民间舞蹈课程和艺术家摊位。享受整个一天，或只是来看看你最喜欢的音乐类型。每个人都会有一些东西，包括许多摊位销售早期的圣诞礼物和独特的手工制品。你会惊讶于所有艺术家的才华和创造力。</p>\\n<p>时间：7月29日，周六，上午10:00～下午6:00<br />\\n费用：免费入场<br />\\n地点：Mulford Gardens Park<br />\\n地址：13055 Aurora Dr., San Leandro<br />\\n网址：goo.gl/fXnLGt</p>\\n<p><center></p>\\n<h1>湾区第4届瑜伽节</h1>\\n<p><img class=\\\"aligncenter size-medium wp-image-9472020\\\" src=\\\"http://i.epochtimes.com/assets/uploads/2017/07/10-450x585.png\\\" alt=\\\"10\\\" width=\\\"450\\\" height=\\\"585\\\" /> <img class=\\\"aligncenter size-medium wp-image-9472022\\\" src=\\\"http://i.epochtimes.com/assets/uploads/2017/07/10-25-450x301.jpg\\\" alt=\\\"10\\\" width=\\\"450\\\" height=\\\"301\\\" /></p>\\n<p></center>在伯克利体育基地举办的第4届瑜伽节，是一个免费的全日制瑜伽课程，由5个课程组成。带着自己的瑜伽垫，或在活动当天以折扣购买。带毛巾、水和小吃也是明智的。瑜伽专家将教您如何将呼吸与运动、力量和恩典、想像力和形式相结合。<br />\\n记着要提前预约哦！</p>\\n<p>时间：7月29日，周六，上午10:00～下午5:00<br />\\n费用：免费，需要预约<br />\\n地点：Sports Basement<br />\\n地址：2727 Milvia St., Berkeley<br />\\n网址：goo.gl/jgy3tt</p>\\n<p><center><iframe src=\\\"http://www.ntdtv.com/xtr/b5/playlistembed_1335499_0.html\\\" width=\\\"650px\\\" height=\\\"450px\\\" frameborder=\\\"0\\\" allowfullscreen=\\\"allowfullscreen\\\"></iframe></center>（此文发表于1144E期旧金山湾区新闻版）</p>\\n<p><b><a style=\\\"color: #3339ff;\\\" href=\\\"http://zipsurvey.com/Survey.aspx?suid=79300&key=4EF2EA2A\\\">要想定期快速浏览一周新闻集锦，请点这里。</a></b></p>\\n<p>责任编辑：王洪生</p>\\n\""));
    }

    private static String urlStruct(String url, String host, String basePath, String srcUrl) {
        // 留园网url处理
        if (host.contains("http://site.6park.com"))
            host += "/bolun";
        return UrlKit.struct(url, host, basePath, srcUrl);
    }

    @Test
    public void testGet() {
        ProxyEntity proxyEntity = new ProxyEntity("192.168.3.201", 25, "yproxyq", "zproxyx");
        HttpEntity httpEntity = HttpKit.get("http://www.chinaaffairs.org/", null, null, proxyEntity, Charset.forName("utf8"));
        System.out.println(httpEntity.getHtml());

    }

}

@FunctionalInterface
interface TestInterface<F, T> {
    T test(F f);
}

class RuleModel {
    private String url;
    private String listdom;
    private String linedom;
    private String urldom;
    private String datedom;
    private String contentdom;
    private String _datedom;
    private String comment;
    private String _comment;

    public RuleModel() {
    }

    public RuleModel(String url, String listdom, String linedom, String urldom, String datedom, String contentdom,
                     String _datedom, String comment, String _comment) {
        this.url = url;
        this.listdom = listdom;
        this.linedom = linedom;
        this.urldom = urldom;
        this.datedom = datedom;
        this.contentdom = contentdom;
        this._datedom = _datedom;
        this.comment = comment;
        this._comment = _comment;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getListdom() {
        return listdom;
    }

    public void setListdom(String listdom) {
        this.listdom = listdom;
    }

    public String getLinedom() {
        return linedom;
    }

    public void setLinedom(String linedom) {
        this.linedom = linedom;
    }

    public String getUrldom() {
        return urldom;
    }

    public void setUrldom(String urldom) {
        this.urldom = urldom;
    }

    public String getDatedom() {
        return datedom;
    }

    public void setDatedom(String datedom) {
        this.datedom = datedom;
    }

    public String getContentdom() {
        return contentdom;
    }

    public void setContentdom(String contentdom) {
        this.contentdom = contentdom;
    }

    public String get_datedom() {
        return _datedom;
    }

    public void set_datedom(String _datedom) {
        this._datedom = _datedom;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String get_comment() {
        return _comment;
    }

    public void set_comment(String _comment) {
        this._comment = _comment;
    }
}