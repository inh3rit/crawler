package com.zxsoft.crawler.p.analytical.networksearch;

import com.jfinal.kit.StrKit;
import com.zxsoft.crawler.common.entity.out.RecordInfoEntity;
import com.zxsoft.crawler.common.entity.redis.DetailRuleEntity;
import com.zxsoft.crawler.common.entity.redis.ListRuleEntity;
import com.zxsoft.crawler.common.entity.sync.BlacklistEntity;
import com.zxsoft.crawler.common.entity.sync.BlanklistEntity;
import com.zxsoft.crawler.common.entity.sync.TimeRegexEntity;
import com.zxsoft.crawler.common.entity.sync.UrlRuleEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.kit.UrlKit;
import com.zxsoft.crawler.common.type.RecordType;
import com.zxsoft.crawler.exception.CrawlerException;
import com.zxsoft.crawler.p.analytical.AnalyticalApi;
import com.zxsoft.crawler.p.cleaning.filter.Filter;
import com.zxsoft.crawler.p.cleaning.strategy.CatalogPageSourceStrategy;
import com.zxsoft.crawler.p.ext.DateExtractor;
import com.zxsoft.crawler.p.ext.TimeParse;
import com.zxsoft.crawler.p.ext.UrlRuleTextExtract;
import com.zxsoft.crawler.p.ext.text.ExtractorFactory;
import com.zxsoft.crawler.p.ext.text.IExtractor;
import com.zxsoft.crawler.p.kit.remode.UrlRemake;
import com.zxsoft.crawler.p.model.RecordModel;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import org.apache.commons.codec.digest.DigestUtils;
import org.inh3rit.httphelper.common.HttpConst;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.entity.ProxyEntity;
import org.inh3rit.httphelper.http.HttpStatus;
import org.inh3rit.httphelper.kit.HttpKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by cox on 2016/1/15.
 */
public class AnalyticalNetworkSearchDefault implements AnalyticalApi {

    private static final Logger log = LoggerFactory.getLogger(AnalyticalNetworkSearchDefault.class);

    /**
     * @param lines    列表行
     * @param lre      列表页规则
     * @param dre      详细页规则
     * @param baseRie  最终写入的单条数据基础信息, 这里分析的所有机构应在此对象扩充并放入到 List 中返回
     * @param url      原始 url 地址 | url host basePath 都是同时传入的, 因为部分站点的链接并非是绝对地址, 而是相对路径, 因此需要分析出绝对地址, 而分析需要依赖此三变量 |
     * @param host     原始网站主机  | url host basePath 都是同时传入的, 因为部分站点的链接并非是绝对地址, 而是相对路径, 因此需要分析出绝对地址, 而分析需要依赖此三变量 |
     * @param basePath 原始网站根路径 | url host basePath 都是同时传入的, 因为部分站点的链接并非是绝对地址, 而是相对路径, 因此需要分析出绝对地址, 而分析需要依赖此三变量 |
     * @param lastTs   最后抓取内容的时间 - 如果需要就传递值
     * @param goInto   是否进入内页抓取
     * @param argMap   可变参数集合
     * @param cookie   cookie
     * @return List<RecordInfoEntity>
     */
    @Override
    public List<RecordInfoEntity> parseInfo(Elements lines, ListRuleEntity lre, DetailRuleEntity dre,
                                            RecordInfoEntity baseRie, String url, String host, String basePath, Long
                                                    lastTs, Boolean goInto,
                                            String cookie, Map<String, Object> argMap) {
        String curl;
        List<RecordInfoEntity> lrie = new ArrayList<RecordInfoEntity>();
        try {
            for (Element box : lines) {
                RecordInfoEntity riey = baseRie.clone();
                Elements targetUrls = box.select(lre.getUrldom());
                if (CollectionKit.isEmpty(targetUrls))
                    continue;
                curl = UrlKit.struct(targetUrls.first().attr("href"), host, basePath, url);

                riey.setTimestamp(0L);
                // 尝试从列表页中获取发布时间
                Long ts = 0L;
                if (StrKit.notBlank(lre.getDatedom())) {
                    Elements dtd = box.select(lre.getDatedom());
                    if (CollectionKit.notEmpty(dtd)) {
                        String str = box.select(lre.getDatedom()).first().html();
                        ts = DateExtractor.extractInMilliSecs(str);
                        if (ts != 0L && ts <= System.currentTimeMillis())
                            riey.setTimestamp(ts);
                    }
                }

                if (goInto) {
                    try {
                        riey.merge(this.fetchGoInto(curl, riey.getTimestamp(), argMap));
                    } catch (CrawlerException e) {
                        log.error("fetch detail page error, ErrorMessage: {}, ErrorStack: {}", e.getMessage(), e
                                .getStackTrace().length > 0 ? e.getStackTrace()[0] : null);
                        continue;
                    } catch (Exception e) {
                        log.error("fetch detail page error, ErrorMessage: {}, ErrorStack: {}", e.getMessage(), e);
                        continue;
                    }
                }

                if (riey.getTimestamp() == 0L || StrKit.isBlank(riey.getContent().trim()))
                    continue;

                // 计算百度快照
                Elements anchors = box.getElementsByTag("a");
                if (CollectionKit.notEmpty(anchors)) {
                    for (Element anchor : anchors) {
                        if (anchor.ownText().contains("百度快照")) {
                            riey.setHomeUrl(anchor.absUrl("href"));
                        }
                    }
                }

                if (StrKit.isBlank(riey.getTitle())) {
                    riey.setTitle(targetUrls.first().text());
                }
                riey.setId(DigestUtils.md5Hex(riey.getUrl()).toUpperCase() + riey.getSourceId());
                riey.setIdentifyId(11024L);
                lrie.add(riey);
            }
        } finally {
            lines.clear();
        }
        return lrie;
    }

    /**
     * 爬取详细页
     *
     * @param url        詳細頁面鏈接
     * @param listPageTs 從列表頁根據時間規則解析出來的時間
     * @param argMap     可变参数集合
     * @return 詳細頁信息
     * @throws CrawlerException
     * @throws BoilerpipeProcessingException
     */
    private RecordInfoEntity fetchGoInto(String url, Long listPageTs, Map<String, Object> argMap)
            throws CrawlerException, BoilerpipeProcessingException {

        BlacklistEntity[] bes = (BlacklistEntity[]) argMap.get("blacks");
        BlanklistEntity[] blankes = (BlanklistEntity[]) argMap.get("blanks");
        ProxyEntity pe = (ProxyEntity) argMap.get("proxy");
        List<TimeRegexEntity> treList = (List<TimeRegexEntity>) argMap.get("timeRule");
        UrlRuleEntity[] ures = (UrlRuleEntity[]) argMap.get("urlRules");

        String ruu = UrlRemake.remake(url);
        // 检测是否允许爬取的链接
        if (!this.isInBlackList(bes, ruu))
            throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR_BLACKLIST,
                    "url exist blacklist, [" + ruu + "]");

        HttpEntity he = HttpKit.get(ruu, null, null, pe, null, HttpConst.DEF_TIMEOUT, HttpConst.DEF_SOTIMEOUT, true);
        Integer c = 0;
        // 防止link式url，获取重定向后的真实url
        String location = he.getLocation();
        String _location = location;
        while (StrKit.notBlank(location) && c++ < 3) {
            log.debug("forward to [{}], the number of [{}]", location, c);
            HttpEntity he2 = HttpKit.get(UrlRemake.remake(location), null, null, pe, null, HttpConst.DEF_TIMEOUT,
                    HttpConst.DEF_SOTIMEOUT, true);
            // 如果转发后的链接和之前的链接相同, 原网站自身存在问题
            if (location.equals(he2.getLocation()))
                throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR,
                        "too many redirects [" + location + "]");
            location = he2.getLocation();
            he = he2;
            if (StrKit.notBlank(he2.getHtml()))
                break;

            if (location != null)
                _location = location;
        }

        if (null == _location)
            _location = he.getUrl();

        String detailPageHtml = he.getHtml();

        // 如果状态是 404 403 表示原网页不存在内容, 无须爬取
        if (he.getResponseCode() == HttpStatus.SC_NOT_FOUND || he.getResponseCode() == HttpStatus.SC_FORBIDDEN)
            throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR,
                    "fetch url [" + he.getUrl() + "] http status not equal " + HttpStatus.SC_OK);

        // Http 状态码 100-200 为消息状态 200-300 为请求成功状态 300-400 为重定向状态 400+ 为错误状态
        // 这里判断只要是 300+ 就错误, 上方已经对重定向做过操作, 因此就算出现也应该错误, 其他大于 300
        // 的状态表示访问原网站就有问题
        if (he.getResponseCode() >= HttpStatus.SC_MULTIPLE_CHOICES)
            throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR,
                    "fetch url [" + he.getUrl() + "] http status not equal " + HttpStatus.SC_OK + " and not content");

        if (StrKit.isBlank(detailPageHtml))
            throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR,
                    "fetch detail page content is black from [" + he.getUrl() + "]");

        // 判断抓取的url是否是详细内容页面
        if (isListPage(_location, detailPageHtml))
            throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR_LIST_PAGE,
                    "fetch url [" + he.getUrl() + "] is list page!");

        // 如果存在 url 转发 也需要判断是否可抓取
        if (!this.isInBlackList(bes, he.getUrl())) {
//            this.in_black_list++;
            throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR_BLACKLIST,
                    "url exist blacklist, [" + he.getUrl() + "]");
        }

        // 判断是否是列表页（目录页）
//        if (pageIsCatalogPage(UrlRemake.remake(he.getUrl()), detailPageHtml, he, blankes)) {
//
//        }

        // 提取正文内容
        String content = extraText(he, detailPageHtml, ures, blankes);

        Document detailDoc = Jsoup.parse(detailPageHtml);
        String textContent = detailDoc.text();
        String title = detailDoc.title();
        if (StrKit.isBlank(content))
            throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR,
                    "can not extra content from [" + he.getUrl() + "]");
        title = StrKit.isBlank(title) ? null : title;

        RecordInfoEntity riey = new RecordInfoEntity();
        riey.setUrl(UrlRemake.remake(he.getUrl()));
        riey.setTimestamp(this.extraTime(he.getUrl(), detailPageHtml, listPageTs, treList));
        riey.setLasttime(System.currentTimeMillis());
        riey.setContent(content);
        riey.setTitle(title);
        return riey;
    }


    /**
     * 判断是否是目录页面（列表页面）
     *
     * @param url
     * @param html
     * @return
     */
    public boolean pageIsCatalogPage(String url, String html, HttpEntity he, BlanklistEntity[] blankes) {
        try {
            int type;
            if (-1 == (type = isInBlanklist(blankes, he.getDomain(), -1)))
                throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR_BLANKLIST, "url not exist blanklist," +
                        " [" + he.getUrl() + "]");

            // type == 0 表示是新闻，值判断新闻类的
            if (type == 0) {
                final Map<String, Object> objectMap = new HashMap<>();
                objectMap.put(Filter.URL, url);
                objectMap.put(Filter.HTML, html);
                Map<String, Object> map = CatalogPageSourceStrategy.init().start(objectMap);
                if (map == null || map.isEmpty())
                    return false;
                boolean isCatalogPage = Boolean.parseBoolean(map.get(CatalogPageSourceStrategy.MAP_CODE).toString());
                if (isCatalogPage == false)
                    RecordModel.dao.unableResolveUrl(RecordType.DATA_CLEANING_CATALOGPAGESCOREFILTER, url);
                return !isCatalogPage;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 提取正文内容
     * 1、存在有规则的url中，则根据规则提取
     * 2、根据extractor提取策略提取
     *
     * @param he
     * @param html
     * @param ures
     * @param blankes
     * @return
     * @throws CrawlerException
     */
    private String extraText(HttpEntity he, String html, UrlRuleEntity[] ures, BlanklistEntity[] blankes) throws
            CrawlerException {
        Map<String, UrlRuleEntity> urlRuleMap = new HashMap<String, UrlRuleEntity>();
        for (UrlRuleEntity entity : ures) {
            urlRuleMap.put(entity.getHost(), entity);
        }
        String content = UrlRuleTextExtract.getUrlRuleTextExtract(urlRuleMap).geText(html, he.getUrl());

        if (StrKit.notBlank(content))
            return content;

        // 是否存在白名单中
        int type;
        if (-1 == (type = isInBlanklist(blankes, he.getDomain(), -1))) {
            throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR_BLANKLIST,
                    "url not exist blanklist, [" + he.getUrl() + "]");
        }

        /*
         * 正文抓取策略改变,文本密度 ==> boilerpipe ==> html2article
         * ==>extractor提取策略
         */
        // String content = TextExtract.parse(detailPageHtml);
        // content = DefaultExtractor.INSTANCE.getText(detailPageHtml);
        IExtractor extractor = ExtractorFactory.getInstance(type, Jsoup.parse(html), he.getUrl());
        try {
            content = extractor.getContentByHtml(html);
        } catch (Exception e) {
            throw new CrawlerException(CrawlerException.ErrorCode.SYSTEM_ERROR,
                    "fetch detail page content failed from [" + he.getUrl() + "]");
        }

        return content;
    }

    /**
     * 解析页面时间
     *
     * @param url        链接
     * @param content    页面 html
     * @param listPageTs 从列表页获取的时间
     * @return Timestamp
     */
    private Long extraTime(String url, String content, Long listPageTs, List<TimeRegexEntity> treList) throws
            CrawlerException {
        if (listPageTs <= 315504000000L)
            listPageTs = 0L;
        Long pts = TimeParse.parse(url, content, treList);
        if (pts != 0L && pts < System.currentTimeMillis())
            return pts;
        if (listPageTs != 0L && listPageTs < System.currentTimeMillis())
            return listPageTs;
        throw new CrawlerException(CrawlerException.ErrorCode.SYSTEM_ERROR, "提前网页发布时间失败!");
    }

    /**
     * 是否允许爬取的链接
     *
     * @param bes 黑名单列表
     * @param url 链接
     * @return true false
     */
    private Boolean isInBlackList(BlacklistEntity[] bes, String url) {
        try {
            // 检测 url 是否首页
            URL u = new URL(url);
            if (StrKit.isBlank(u.getPath()) || "/".equals(u.getPath()))
                return false;
            // 检测是否存在黑名单中
            if (bes == null)
                return true;
            for (BlacklistEntity be : bes)
                if (url.matches(be.getRegex()))
                    return false;
            return true;
        } catch (MalformedURLException e) {
            // 进入此异常表示 url 并非是正常的 url 则不允许爬取
            log.debug(e.getMessage());
        }
        return false;
    }

    /**
     * 判断抓取的url的域名是否存在白名单中
     *
     * @param blankes
     * @param domain
     * @return 存在：type
     * 不存在：-1
     */
    private int isInBlanklist(BlanklistEntity[] blankes, String domain, int defaultValue) {
        for (BlanklistEntity blank : blankes) {
            String _domain = blank.getDomain();
            if (purge(domain).contains(purge(_domain)) || purge(_domain).contains(purge(domain))) {
                return blank.getType().intValue();
            }
        }
        return defaultValue;
    }

    /**
     * 净化url,清除url中的干扰项
     *
     * @param str
     * @return
     */
    private String purge(String str) {
        return str.replace("http://", "").replace("www.", "").split("/")[0];
    }

    /**
     * 1、判断是url是否是终点（详细页），以“/”结尾的url是网站的子模块（列表页）
     * 例：http://www.ahwang.cn/anhui/
     * <p/>
     * 2、终点url判断是否是首页，
     * 一、域名后缀名www.ahwang.（cn,com...)
     * 二、index.(html,php,shtml...)
     * <p/>
     * 3、特殊列表页（百度搜索）单独处理
     * description:含有“百度搜索”
     *
     * @param url
     * @return
     */
    private boolean isListPage(String url, String html) {

        char lastChar = url.charAt(url.length() - 1);
        if ('/' == lastChar)
            return true;

        // .转义字符
        String[] chips = url.split("\\.");
        String[] suffixes = {"cn", "com", "net", "org", "edu", "gov", "int", "biz", "info", "pro", "name", "museum",
                "coop", "aero", "xxx", "idv"};
        if (Arrays.asList(suffixes).contains(chips[chips.length - 1]))
            return true;

        String[] _chips = url.split("/");
        if (_chips[_chips.length - 1].contains("index"))
            return true;

        if (url.contains("baidu")) {
            Document doc = Jsoup.parse(html);
            try {
                String description = doc.select("meta[name=description]").get(0).attr("content");
                if (description.contains("百度搜索"))
                    return true;
            } catch (Exception e) {
            }
        }

        return false;
    }
}
