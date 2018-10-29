package com.zxsoft.crawler.p.analytical.inspect;

import com.jfinal.kit.StrKit;
import com.zxsoft.crawler.common.entity.out.RecordInfoEntity;
import com.zxsoft.crawler.common.entity.redis.DetailRuleEntity;
import com.zxsoft.crawler.common.entity.redis.ListRuleEntity;
import com.zxsoft.crawler.common.entity.sync.TimeRegexEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.kit.UrlKit;
import com.zxsoft.crawler.exception.CrawlerException;
import com.zxsoft.crawler.p.analytical.AnalyticalApi;
import com.zxsoft.crawler.p.ext.DateExtractor;
import com.zxsoft.crawler.p.ext.TimeParse;
import com.zxsoft.crawler.p.kit.remode.UrlRemake;
import org.apache.commons.codec.digest.DigestUtils;
import org.inh3rit.httphelper.common.HttpConst;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.http.HttpStatus;
import org.inh3rit.httphelper.kit.HttpKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 境外网络巡检爬虫解析
 */
public class AnalyticalNetworkInspectForeign implements AnalyticalApi {

    private static final Logger log = LoggerFactory.getLogger(AnalyticalNetworkInspectForeign.class);

    /**
     * 境外网络巡检爬虫解析实现
     *
     * @param lines    列表行
     * @param lre      列表页规则
     * @param dre      详细页规则
     * @param baseRie  最终写入的单条数据基础信息, 这里分析的所有机构应在此对象扩充并放入到 List 中返回
     * @param url      原始 url 地址 | url host basePath 都是同时传入的, 因为部分站点的链接并非是绝对地址, 而是相对路径, 因此需要分析出绝对地址, 而分析需要依赖此三变量 |
     * @param host     原始网站主机  | url host basePath 都是同时传入的, 因为部分站点的链接并非是绝对地址, 而是相对路径, 因此需要分析出绝对地址, 而分析需要依赖此三变量 |
     * @param basePath 原始网站根路径 | url host basePath 都是同时传入的, 因为部分站点的链接并非是绝对地址, 而是相对路径, 因此需要分析出绝对地址, 而分析需要依赖此三变量 |
     * @param lastTs   最后抓取内容的时间 - 如果需要就传递值
     * @param goInto   是否进入内页抓取
     * @param cookie   cookie
     * @param argMap   可变参数集合
     * @return
     */
    @Override
    public List<RecordInfoEntity> parseInfo(Elements lines, ListRuleEntity lre, DetailRuleEntity dre,
                                            RecordInfoEntity baseRie, String url, String host, String basePath, Long
                                                    lastTs, Boolean goInto,
                                            String cookie, Map<String, Object> argMap) {

        String curl;
        List<RecordInfoEntity> lrie = new ArrayList<RecordInfoEntity>();
        Map<String, Long> lineMap = new HashMap<>();
        int count = 0;
        for (Element box : lines) {
            Elements targetUrls = box.select(lre.getUrldom());
            if (CollectionKit.isEmpty(targetUrls))
                continue;
            curl = urlStruct(targetUrls.first().attr("href"), host, basePath, url);
            lineMap.put(curl, 0L);
            if (StrKit.notBlank(lre.getDatedom())) {
                Elements dateEles = box.select(lre.getDatedom());
                if (CollectionKit.notEmpty(dateEles)) {
                    try {
                        lineMap.put(curl, (TimeParse.parseByRegex(dateEles.get(0).text(), (List<TimeRegexEntity>)
                                argMap.get("timeRule"))));
                    } catch (CrawlerException e) {
                        log.error(e.getMessage());
                        continue;
                    }
                }
            }

            if (++count > 30) break;  //每次只抓前30条
        }

        lineMap.forEach((detailUrl, timestamp) -> {
            RecordInfoEntity riey = baseRie.clone();
            riey.setTimestamp(timestamp);
            if (goInto) {
                try {
                    riey.merge(this.fetchDetailPage(detailUrl, dre, cookie, riey));
                } catch (Exception e) {
                    e.printStackTrace();
                    log.debug("fetch detail page error, ErrorMessage: {}", e.getMessage());
                    return;
                }
            }
            lrie.add(riey);
        });

        lines.clear();
        return lrie;
    }

    /**
     * 分析详细页
     *
     * @param url    链接
     * @param dre    详细页规则
     * @param cookie cookie
     * @param riey
     * @return RecordInfoEntity
     */
    private RecordInfoEntity fetchDetailPage(String url, DetailRuleEntity dre, String cookie, RecordInfoEntity riey)
            throws CrawlerException {
        String ruu = UrlRemake.remake(url);
        ruu = UrlKit.encodeCn(ruu);
        Map<String, String> header = new HashMap<String, String>();
        if (StrKit.notBlank(cookie))
            header.put(HttpConst.COOKIE, cookie);

//        ProxyEntity proxy = new ProxyEntity("192.168.155.155", 25, "yproxyq", "zproxyx"); // for test
//        HttpEntity he = HttpKit.get(ruu, null, header, proxy, Charset.forName("UTF-8"));
        HttpEntity he = HttpKit.get(ruu, null, header, true);
        if (("GB2312").equals(he.getCharset().toString()))
            he = HttpKit.get(ruu, null, header, Charset.forName("GBK"));
        header.clear(); // request header 使用完成后清空

        String detailPageHtml = he.getHtml();

        /* Http 状态码 100-200 为消息状态 200-300 为请求成功状态 300-400 为重定向状态 400+ 为错误状态
         这里判断只要是 300+ 就错误, 上方已经对重定向做过操作, 因此就算出现也应该错误, 其他大于 300 的状态表示访问原网站就有问题
         */
        if (he.getResponseCode() >= HttpStatus.SC_MULTIPLE_CHOICES)
            throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR,
                    "fetch url [" + he.getUrl() + "] http status not equal " + HttpStatus.SC_OK + " and not content");

        if (StrKit.isBlank(detailPageHtml))
            throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR,
                    "fetch detail page content is black from [" + he.getUrl() + "]");

        Document detailDoc = Jsoup.parse(detailPageHtml);
        Elements eles = null;
        for (String contentDom : dre.getContent().split(",")) {
            eles = detailDoc.select(contentDom);
            if (eles != null && eles.size() > 0)
                break;
        }
        if (CollectionKit.isEmpty(eles))
            throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR, "use detail contentdom:["
                    + dre.getContent() + "] can not extra content from [" + he.getUrl() + "]");

        /*
         * 如果在列表页中未能取到时间, 则在内容页中使用规则获取
         */
        if (riey.getTimestamp() == null || riey.getTimestamp() == 0L) {
            if (StrKit.notBlank(dre.getDate())) {
                Elements tsEles = null;
                for (String dateDom : dre.getDate().split(",")) {
                    tsEles = detailDoc.select(dateDom);
                    if (tsEles != null && tsEles.size() > 0)
                        break;
                }
                Long ts = 0L;
                if (CollectionKit.notEmpty(tsEles)) {
                    String stringDate = tsEles.get(0).text();
                    stringDate = DateExtractor.dateFormat(stringDate, DateExtractor.checkDateFormat(stringDate));
                    ts = DateExtractor.extractInMilliSecs(stringDate);
                }
                if (ts == null || ts.equals(0L))
                    throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR,
                            "can not extra time from [" + he.getUrl() + "]");
                riey.setTimestamp(ts);
            }
        }

        riey.setUrl(he.getUrl()).setId(DigestUtils.md5Hex(he.getUrl()).toUpperCase()).setTitle(detailDoc.title()).setLasttime(System
                .currentTimeMillis())
                .setContent(eles.first().text());
        return riey;
    }

    private static String urlStruct(String url, String host, String basePath, String srcUrl) {
        // 留园网url处理
        if (host.contains("http://site.6park.com"))
            host += "/bolun";
        // 明报url处理
        if (host.contains("http://indepth.mingpao.com"))
            host = "http://indepth.mingpao.com/php";
        // 希望之声
        if (host.contains("http://www.soundofhope.org")) {
            if (url.contains("//www.soundofhope.org"))
                url = url.replace("//www.soundofhope.org", "");
        }
        return UrlKit.struct(url, host, basePath, srcUrl);
    }
}
