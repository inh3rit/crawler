package com.zxsoft.crawler.d.test;

import com.zxsoft.crawler.common.entity.out.RecordInfoEntity;
import com.zxsoft.crawler.common.entity.out.WriterEntity;
import com.zxsoft.crawler.d.api.WriterDataApi;
import com.zxsoft.crawler.d.impl.solr.WriterSolrImpl;
import com.zxsoft.crawler.exception.CrawlerException;
import name.iaceob.kit.json.JsonKit;
import org.inh3rit.httphelper.common.HttpConst;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cox on 2015/10/13.
 */
public class testWriter {


    @Test
    public void ttd1() throws CrawlerException {
        WriterDataApi wda = new WriterSolrImpl();
        String[] urls = new String[1];
        urls[0] = "http://192.168.32.17:20000/sentiment/index";
        String res = "[{\"identify_md5\":\"iaceob\",\"ip\":null,\"city_code\":10000,\"source_type\":1,\"type\":\"必应搜索\",\"server_id\":0,\"province_code\":10000,\"title\":\"[PS3] 暴雨最新新闻资讯 - 电玩巴士\",\"url\":\"http:\\/\\/gf.tgbus.com\\/game\\/info\\/3668.shtml\",\"content\":\"索尼透露神秘图片《暴雨》或将与次世代联姻\\n　　一个简单的折纸照片就会让玩家们想到PS3上的经典名作《暴雨》，而日前索尼在Twitter上也发布了一张神秘照片，一个硕大的折纸雕像。乍一看这照片没什么，但是底下的4ThePlayer让人起疑，这个口号是索尼为宣传PS4打造的口号，配上《暴雨》中令人印象深刻的经典折纸，这是否就意味着我们有\\u2026\\u2026\\n浏览次数:473次|发表时间:2013-11-1510:01|推荐反对\\nDavidCage称是微软的懦弱让XBOX错过《暴雨》\\n　　QuanticDream通过《暴雨》让世人知道还有这么用心制作游戏的公司，但是大家知道吧，在决定让《暴雨》登陆PS3平台之前，QuanticDream也曾让微软看过这个项目，但是当时微软拒绝了这款游戏，如今DavidCage坦言了这其中的事实。　　在伦敦举行的BAFTA年度游戏演讲中，DavidCage表\\u2026\\u2026\\n浏览次数:470次|发表时间:2013-09-0410:50|推荐反对\\n官方高兴宣布《暴雨》全球销量已突破300万套\\n　　相信很多玩家都已经玩过PS3早期的经典之作《暴雨》吧，如果要用两个字来评价该作的话小编我只能选\\u201C艺术\\u201D，即便游戏中也有种种小问题，但是我们通过它看到了电视游戏的未来。在QuanticDream努力开发他们下一款作品《超越：两个灵魂》时，该公司的CEOGuillaumedeFondaumiere在其Twit\\u2026\\u2026\\n浏览次数:508次|发表时间:2013-08-2014:00|推荐反对\\n《暴雨》为索尼赚1亿美元实验作品也可盈利\\n　　QuanticDream多次表达了对索尼的支持，他们此前就说到不会考虑更换东家，将一直为索尼制作游戏，而索尼对于QuanticDream的扶持也没有白费，日前QuanticDream的联合CEOGuillaumedeFondaumiere就向外界透露，该工作室凭借2011年推出的游戏《暴雨》为索尼总共赚得了1亿美金。　\\u2026\\u2026\\n浏览次数:612次|发表时间:2013-04-2210:42|推荐反对\\n《暴雨》作曲因癌症去世一生音乐荣誉无数\\n　　QuanticDream工作室作曲，PS3独占游戏《暴雨》原声音乐创作者NormandCorbeil因胰腺癌于1月25日去世，享年56岁。　　Corbeil的儿子在癌症论坛(TheCancerForums)确认了这一消息，他发表帖子称：\\u201C我的父亲NormandCorbeil在今天下午2点56分去世，他是一个伟大的作曲家，一位出色的父\\u2026\\u2026\\n浏览次数:597次|发表时间:2013-01-2815:29|推荐反对\\n暴雨工作室不满法国税收政策将迁至加拿大\\n　　《暴雨》的制作工作室QuanticDream的总部设在巴黎，他们非常满意那里直到那里的减税政策取消之后。近日由于法国新政策取消减税，这引来了很多电视游戏开发同行的不满。　　今年一月，法国税务局与游戏开发商的协议到期，他们正是取消对电视游戏产品减税的政策。之前的减税政策是有欧\\u2026\\u2026\\n浏览次数:1322次|发表时间:2012-02-0910:55|推荐反对\\n《暴雨》工作室正酝酿新技术预计GDC公布\\n　　QuanticDream在过去的几年中为我们带来了不少经典的游戏，如《靛蓝语言》和《暴雨》。他们与众不同的游戏理念和令人印象深刻的面部表情技术都获得了业界的好评。而近日QuanticDream的负责人DavidCage正酝酿着新的动作，他计划在3月7日举办的GDC2012上公开一个长达一小时的高新技术项\\u2026\\u2026\\n浏览次数:804次|发表时间:2012-01-3009:02|推荐反对\\n《暴雨》新作正在制作?制作组招聘中国演员\\n　　不知道玩家们是否和小编我一样迫不急的的想玩到《暴雨》的最新作，这款游戏真的是PS3上的经典。而关于续作的消息则一直没有被公布，是否会推出续作至今也是个迷，不过也许最近的一些风声透露了有关游戏的事情。　　近日游戏的制作组QuanticDream在法国公布了一份招聘广告，上面写到他\\u2026\\u2026\\n浏览次数:816次|发表时间:2012-01-1610:52|推荐反对\\n统计表示65%的玩家在《暴雨》中救下肖恩\\n　　经典PS3独占游戏《暴雨》追求让玩家们对游戏中的剧情做出自我选择，玩家将决定游戏故事将如何发展。不知道玩家们在第一次体验游戏的时候会做出什么样的选择，因为只有第一次体验游戏，玩家的选择才会是下意识的，是发自内心的。　　近日根据IGN在网上发布的一个数据统计，在所有完成游\\u2026\\u2026\\n浏览次数:1804次|发表时间:2011-12-0610:58|推荐反对\\n《暴雨：导演剪辑版》即将于北美地区发售\\n　　《暴雨》PS3史上最受关注的独占游戏之一，这部由法国游戏开发小组QuanticDream倾力打造的次世代作品给人十分真实的感觉，并以剧情为游戏中心，让玩家们亲自参与到故事之中。本作已经于2010年2月18日发售，并取得了不错的销量。而现在官方宣布《暴雨：导演剪辑版》即将于2011年11月8日在\\u2026\\u2026\\n浏览次数:1110次|发表时间:2011-11-0409:45|推荐反对\\n制作人谈《暴雨2》：我需要有激情才会制作\\n游戏概述：　　本作是由法国的游戏开发小组QuanticDream倾力打造的一款次世代作品，该游戏中的人物表情以及游戏环境等要素都给人十分真实的感觉。游戏以故事为中心，借助于强劲的表情引擎，在《暴雨》中登场的角色都有着相当真实的演出。加上侧重于剧情和故事的游戏系统，因此，在\\u2026\\u2026\\n浏览次数:1385次|发表时间:2011-09-2910:23|推荐反对\\n好莱坞悬疑新片犯罪游戏《暴雨》将电影化\\n　　谁会否认PS3的惊悚探案推理AVG《暴雨》不适合改编成电影？于是活生生现实摆放在我们面前，它终于要成为一部好莱坞大片了。　　最新消息显示，电视剧《纽约重案组》的制片人DavidMilch将执手《暴雨》的电影化改编工作，而标题将从《暴雨（HeavyRain）》改为《雨（Rain）》，这部电影将在DavidMilch完成手头的HBO新剧集《Luck》之后开始剧本创作。　　制作人BobShaye表示，\\u201CDavidMilch拥有充满魅力的方式讲述复杂剧本的实力，所以我们《暴雨》的电影改编找他是再合适不过了。\\u201D\\n浏览次数:1192次|发表时间:2011-01-2809:36|推荐反对\\n《暴雨》中你不知道的事删减部分被曝光\\n　　也许你错过了上周PSN上放出的《暴雨》被删减片段。现在你可以弥补这个遗憾。　　你知道吗，主角Ethan起初在他意识模糊时本应该有几个梦的。这些梦境使他与折纸杀手有着\\u201C超自然\\u201D的联系。　　这段删减片段也解释了为什么MadisonPaige是失眠症患者，并不是因为调查\\u201C标本制作师\\u201D事件造成的。实际上是因为Madison在伊拉克战争期间报道那段时间，对于战争的恐惧使她患上了失眠。\\n浏览次数:2362次|发表时间:2010-11-1009:29|推荐反对\\n《暴雨》销量破150万商业成功但不如预期\\n　　今年2月发售的PS3惊悚AVG《暴雨》近日终于突破了150万的销量，甚至出于索尼和开发商QuanticDream的预期了。《暴雨》制作人DavidCage表示，它在商业性上成功了，实在是出乎意料。实际上本作发售两周就火热销售了60万份，已经比预想的美国销量高了四倍。现在全球销量突破150万，\\u201C我们为\\u2026\\u2026\\n浏览次数:1207次|发表时间:2010-08-1800:54|推荐反对\\n《暴雨》制作小组未在开发续作无新作计划\\n　　《暴雨》的开发小组在《暴雨》获得成功后并没有满足，也没有开发续作的计划。　　《暴雨》的制作人DavidCage说：我们在研究下一代的游戏技术。我们没有满足于《暴雨》的成功，我们需要挑战，需要压力，我要我们的游戏团队能够感受到危险和压力，要在工作中充满激情、坚持我们的信念。　　《暴雨》的开发小组也没有开发之前传言的Horizon，如果有新的游戏作品小组将会第一时间自己公布出来。\\n浏览次数:730次|发表时间:2010-07-0310:20|推荐反对\\n\",\"country_code\":1,\"lasttime\":1444717173019,\"location_code\":10000,\"first_time\":1444717173019,\"location\":null,\"source_id\":285,\"id\":\"DF61D26CA5C5ED0E39EBCB3F0197D6F3\",\"keyword\":\"暴雨\",\"source_name\":\"必应搜索\",\"timestamp\":1384480860000}]";
        // res = "[{\"id\":\"69794CED595C6757DF7E8CEBB1CD717C\",\"platform\":1,\"mid\":\"\",\"username\":\"\",\"nickname\":\"\",\"original_id\":\"\",\"original_uid\":\"\",\"original_name\":\"\",\"original_title\":\"\",\"original_url\":\"http://www.want-daily.com/portal.php?mod=view&aid=158808\",\"url\":\"http://www.want-daily.com/portal.php?mod=view&aid=158808\",\"home_url\":\"\",\"title\":\"聯想淨利腰斬 手機慘賠是禍首  旗下4種品牌須區隔市場 善用摩托羅拉猶可為\",\"type\":\"焦点新闻\",\"isharmful\":false,\"content\":\"大陸聯想集團日前公布最新財報，淨利潤較去年同期慘跌51%，外界將矛頭指向手機業務慘賠成了「啦啦隊」，更嚴重的，聯想欲重組手機業務，受限其手機品牌、型號太多，品牌精神薄弱，無法勾動消費者的使用意願，改革之路勢必舉步維艱，不是簡單的事。 聯想集團上周公布截至今年6月止的2015/2016財年第1季業績，總營業額為107億元（美元，下同），比去年同期成長3%，但淨利潤卻下跌51%至1.05億元，令人訝異；仔細觀察得知，手機事業群為「罪魁禍首」，因其虧損2.92億元，但還有更嚴重的內部問題要解決。 下季列6億美元重整 為了使業務回到成長軌道，聯想除在全球減少約3200名非生產製造員工，也計畫在下一季認列6億元組織重整費用、打消約3億元的手機庫存，這背後顯示，聯想手機滯銷問題嚴重，庫存壓力大。 業界人士分析，聯想去年用29億元收購摩托羅拉行動，本來想用這個金字招牌好好發揮，提升手機市占率排名，未料卻自己開發新品牌，品牌混亂，加上摩托羅拉手機出貨量不如預期，比去年同期縮減31%，聯想面臨嚴峻的挑戰。 據了解，聯想手機事業群有4支品牌，分別為聯想Lenovo，鎖定低階市場及電信營運商；第2個是神奇工場（ZUK），按網路思維打造；第3個是VIBE系列；最後則是主攻中高端市場的摩托羅拉。大陸手機市場擁擠，聯想新款手機ZUK Z1要在「紅海」中殺出血路，外界直言說「難如登天」。 聯想摩托整合要時間 「給聯想一點時間！」IT行業資深分析師孫永傑表示，聯想併購摩托羅拉，對其在印度等新興國家手機市場影響力提升，因為摩托羅拉品牌認同度高。聯想手機業務碰到的最大問題是旗下4個手機品牌該如何區隔，而聯想與摩托羅拉要完全整合，也需要一段過度時間。 「大量的廉價訂製機，透支了聯想手機的品牌！」市場人士認為，聯想手機業績低迷原因，最大的問題在於其手機產品線混亂，要走高階市場難度非常高，因先前推出大量低價手機所害，消費者會將聯想手機與便宜畫上等號。因此，聯想該好好發揮摩托羅拉的品牌價值，圍繞此品牌研發新產品，在市場或許還能占有一席之地。 ▲2014年11月7日，福州顧客在聯想賣場選購聯想產品。（中新社） ▲2006年，聯想手機秋季新品登場。（中新社）\",\"comment_count\":0,\"read_count\":0,\"favorite_count\":0,\"attitude_count\":0,\"repost_count\":0,\"video_url\":\"\",\"pic_url\":\" http://www.want-daily.com/data/attachment/portal/2015 8/20/wdp_1440022566_8951.jpg http://www.want-daily.com/data/attachment/portal/2015 8/20/wdp_1440022567_9119.jpg\",\"voice_url\":\"\",\"timestamp\":1440036449803,\"source_id\":635,\"lasttime\":1440036449803,\"server_id\":0,\"identify_id\":0,\"identify_md5\":\"xiayun\",\"first_time\":1440036449803,\"update_time\":0,\"geo\":\"\",\"receive_addr\":\"\",\"append_addr\":\"\",\"send_addr\":\"\",\"source_name\":\"旺报\",\"source_type\":2,\"country_code\":0,\"location_code\":0,\"province_code\":0,\"city_code\":0}]";
        List<RecordInfoEntity> lre = JsonKit.fromJson(res, ArrayList.class);
        // System.out.println(com.jfinal.kit.JsonKit.toJson(lre));
        List<WriterEntity> wes = wda.writer(urls, lre);
        System.out.println(JsonKit.toJson(wes));

    }


    @Test
    public void ttd3() {
        String json = "{\"num\": 1, \"records\": [{\"id\":\"69794CED595C6757DF7E8CEBB1CD717C\",\"platform\":1,\"mid\":\"\",\"username\":\"\",\"nickname\":\"\",\"original_id\":\"\",\"original_uid\":\"\",\"original_name\":\"\",\"original_title\":\"\",\"original_url\":\"http://www.want-daily.com/portal.php?mod=view&aid=158808\",\"url\":\"http://www.want-daily.com/portal.php?mod=view&aid=158808\",\"home_url\":\"\",\"title\":\"聯想淨利腰斬 手機慘賠是禍首  旗下4種品牌須區隔市場 善用摩托羅拉猶可為\",\"type\":\"焦点新闻\",\"isharmful\":false,\"content\":\"大陸聯想集團日前公布最新財報，淨利潤較去年同期慘跌51%，外界將矛頭指向手機業務慘賠成了「啦啦隊」，更嚴重的，聯想欲重組手機業務，受限其手機品牌、型號太多，品牌精神薄弱，無法勾動消費者的使用意願，改革之路勢必舉步維艱，不是簡單的事。 聯想集團上周公布截至今年6月止的2015/2016財年第1季業績，總營業額為107億元（美元，下同），比去年同期成長3%，但淨利潤卻下跌51%至1.05億元，令人訝異；仔細觀察得知，手機事業群為「罪魁禍首」，因其虧損2.92億元，但還有更嚴重的內部問題要解決。 下季列6億美元重整 為了使業務回到成長軌道，聯想除在全球減少約3200名非生產製造員工，也計畫在下一季認列6億元組織重整費用、打消約3億元的手機庫存，這背後顯示，聯想手機滯銷問題嚴重，庫存壓力大。 業界人士分析，聯想去年用29億元收購摩托羅拉行動，本來想用這個金字招牌好好發揮，提升手機市占率排名，未料卻自己開發新品牌，品牌混亂，加上摩托羅拉手機出貨量不如預期，比去年同期縮減31%，聯想面臨嚴峻的挑戰。 據了解，聯想手機事業群有4支品牌，分別為聯想Lenovo，鎖定低階市場及電信營運商；第2個是神奇工場（ZUK），按網路思維打造；第3個是VIBE系列；最後則是主攻中高端市場的摩托羅拉。大陸手機市場擁擠，聯想新款手機ZUK Z1要在「紅海」中殺出血路，外界直言說「難如登天」。 聯想摩托整合要時間 「給聯想一點時間！」IT行業資深分析師孫永傑表示，聯想併購摩托羅拉，對其在印度等新興國家手機市場影響力提升，因為摩托羅拉品牌認同度高。聯想手機業務碰到的最大問題是旗下4個手機品牌該如何區隔，而聯想與摩托羅拉要完全整合，也需要一段過度時間。 「大量的廉價訂製機，透支了聯想手機的品牌！」市場人士認為，聯想手機業績低迷原因，最大的問題在於其手機產品線混亂，要走高階市場難度非常高，因先前推出大量低價手機所害，消費者會將聯想手機與便宜畫上等號。因此，聯想該好好發揮摩托羅拉的品牌價值，圍繞此品牌研發新產品，在市場或許還能占有一席之地。 ▲2014年11月7日，福州顧客在聯想賣場選購聯想產品。（中新社） ▲2006年，聯想手機秋季新品登場。（中新社）\",\"comment_count\":0,\"read_count\":0,\"favorite_count\":0,\"attitude_count\":0,\"repost_count\":0,\"video_url\":\"\",\"pic_url\":\" http://www.want-daily.com/data/attachment/portal/2015 8/20/wdp_1440022566_8951.jpg http://www.want-daily.com/data/attachment/portal/2015 8/20/wdp_1440022567_9119.jpg\",\"voice_url\":\"\",\"timestamp\":1440036449803,\"source_id\":635,\"lasttime\":1440036449803,\"server_id\":0,\"identify_id\":0,\"identify_md5\":\"xiayun\",\"first_time\":1440036449803,\"update_time\":0,\"geo\":\"\",\"receive_addr\":\"\",\"append_addr\":\"\",\"send_addr\":\"\",\"source_name\":\"旺报\",\"source_type\":2,\"country_code\":0,\"location_code\":0,\"province_code\":0,\"city_code\":0}]}";
        json = com.zxsoft.crawler.d.file.FileKit.readFile("/home/cox/tmp/ttd/test.json");
        String url = "http://192.168.32.17:20000/sentiment/index";
        url = "http://121.31.12.34:28093/sentiment/index";
        url = "http://192.168.32.11:8900/sentiment/index";
        url = "http://192.168.32.17:20000/sentiment/index";

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpConst.CONTENT_TYPE, "application/json");
        HttpEntity he = HttpKit.post(url, null, json, headers, Charset.forName("UTF-8"));
        System.out.println(he.getHtml());
    }



//    @Test
//    public void ttd2() {
//        String url = "http://192.168.4.140:8090/webapp/valiLogin",
//                user = "a";
//        HttpHelper http = new HttpHelper();
////        Map<String, String> m = new HashMap<>();
////        m.put("username", user);
////        m.put("passwd", user);
//        String m = "username=a&passwd=a";
//        http.post(url, m);
//        System.out.println(http.getCookie());
//        System.out.println(http.getResponseHeaders());
//        System.out.println(http.getHtml());
//    }

}
