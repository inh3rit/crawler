package com.zxsoft.crawler.p.ctrl;

import com.jfinal.kit.StrKit;
import com.zxsoft.crawler.common.entity.out.RecordInfoEntity;
import com.zxsoft.crawler.common.entity.out.WriterEntity;
import com.zxsoft.crawler.common.entity.parse.NextPageEntity;
import com.zxsoft.crawler.common.entity.parse.ParseEntity;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.redis.ListRuleEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.kit.Tool;
import com.zxsoft.crawler.common.kit.UrlKit;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.d.kit.DataWriter;
import com.zxsoft.crawler.exception.CrawlerException;
import com.zxsoft.crawler.exception.PageBarNotFoundException;
import com.zxsoft.crawler.p.analytical.AnalyticalKit;
import com.zxsoft.crawler.p.kit.remode.UrlRemake;
import org.inh3rit.httphelper.common.HttpConst;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.entity.ProxyEntity;
import org.inh3rit.httphelper.http.HttpStatus;
import org.inh3rit.httphelper.kit.HttpKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2016/12/16.
 */
public class NetworkSeniorSearchParseCtrlTest {


    private AtomicInteger pageNum = new AtomicInteger(1);
    private AtomicInteger sum = new AtomicInteger(0);


    @Test
    public void test() {
        Map<String, Object> argMap = new HashMap<>();
        // 设置必要参数


        //https://www.baidu.com/s?q1=&q2=A&q3=B&q4=C&rn=50&lm=7&ct=0&ft=&q5=&q6=&tn=baiduadv

        String listdom = "div#content_left", linedom = "div.c-container", urldom = "h3.t > a";
        String datedom = "", synopsisdom = "", updatedom = "", authordom = "";
        String sourceName = "百度高级搜索", type = "百度高级搜索";
        Integer sourceId = 10, sectionId = 51;
        ListRuleEntity listRule = new ListRuleEntity();
        listRule.setListdom(listdom);
        listRule.setLinedom(linedom);
        listRule.setUrldom(urldom);
        listRule.setDatedom(datedom);
        listRule.setSynopsisdom(synopsisdom);
        listRule.setUpdatedom(updatedom);
        listRule.setAuthordom(authordom);

        JobEntity je = new JobEntity();

        je.setWorkerId(0);
        je.setLocationCode(10000);
        je.setProvince_code(10000);
        je.setCity_code(10000);
        je.setCountry_code(1);
        je.setGoInto(true);
        je.setPlatform(6);
        je.setIdentify_md5("iaceob");
        je.setJobType(JobType.NETWORK_SEARCH);
        je.setSource_name(sourceName);
        je.setType(type);
        je.setSource_id(sourceId);
        je.setSectionId(sectionId);
        je.setUrl("https://www.baidu.com/s?q1=%s&q2=A&q3=B&q4=C&rn=50&lm=7&ct=0&ft=&q5=&q6=&tn=baiduadv");
        je.setKeywordEncode("utf-8");
        je.setKeyword("中国劳伦斯奖颁奖");
        je.setListRule(listRule);

        String[] address = new String[]{"http://192.168.32.11:8983/sentiment/index"};
        ProxyEntity pe = null;

        argMap.put("job", je);
        argMap.put("address", address);
        argMap.put("proxy", pe);

        try {
            ParseEntity entity = new NetworkSeniorSearchParseCtrlTest().parse(argMap);
            System.out.println(entity.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ParseEntity parse(Map<String, Object> argMap) throws Exception {

        JobEntity je = (JobEntity) argMap.get("job");
        String[] address = (String[]) argMap.get("address");
        ProxyEntity pe = (ProxyEntity) argMap.get("proxy");

        RecordInfoEntity baseRie = new RecordInfoEntity(je.getSource_id(), je.getType(), je.getWorkerId(),
                je.getIdentify_md5(), je.getKeyword(), je.getIp(), je.getLocation(), je.getSource_name(),
                JobType.NETWORK_SEARCH.getIndex(), je.getCountry_code(), je.getLocationCode(), je.getProvince_code(),
                je.getCity_code());
        baseRie.setPlatform(je.getPlatform());

        ListRuleEntity lre = je.getListRule();
        if (lre == null)
            throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR, "no crawler rule");
        if (StrKit.isBlank(lre.getListdom()))
            throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR, "the listdom rule not found");
        if (StrKit.isBlank(lre.getLinedom()))
            throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR, "the linedom rule not found");
        if (StrKit.isBlank(lre.getUrldom()))
            throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR, "the urldom rule not found");

        String kwd = je.getKeyword();
        String originUrl = je.getUrl(); // fix: 默认不可为 null,
        // 如果不存在关键字(不保证一定会出现)会导致 HttpKit
        // NullPointException
        if (StrKit.notBlank(kwd)) {
            String charset = je.getKeywordEncode();
            charset = StrKit.isBlank(charset) ? "UTF-8" : charset;
            charset = charset.toUpperCase();
            charset = "GB-2312".equals(charset) ? "GBK" : charset;
            kwd = URLEncoder.encode(kwd, charset);
            originUrl = UrlKit.format(je.getUrl(), kwd);
        }

        Map<String, String> headers = new HashMap<String, String>();
        if (StrKit.notBlank(je.getCookie()))
            headers.put(HttpConst.COOKIE, je.getCookie());
        Charset charset = HttpConst.DEF_CHARSET;
        if (StrKit.notBlank(je.getKeywordEncode()))
            charset = Charset.forName(je.getKeywordEncode());

        HttpEntity he = HttpKit.get(UrlRemake.remake(originUrl), null, headers, pe, charset, HttpConst.DEF_TIMEOUT, HttpConst.DEF_SOTIMEOUT);
        String firstPageHtml = he.getHtml();
        if (he.getResponseCode() != HttpStatus.SC_OK)
            throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR,
                    "fetch url: " + originUrl + " http status not equal " + HttpStatus.SC_OK);
        if (StrKit.isBlank(firstPageHtml))
            throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR, "fetch page html is blank");

        Document doc = Jsoup.parse(firstPageHtml);
        final Long start = System.currentTimeMillis();

        Elements oldLines = null;
        //log.debug("fetch keyword: [{}] from [{}], jobId: [{}]", je.getKeyword(), originUrl, je.getJobId());
        String currentUrl = originUrl;
        Boolean firstPage = true;

        while (System.currentTimeMillis() - start < 5L * 60L * 1000L) {
            if (!firstPage) {
                doc = fetchNextPage(doc, this.pageNum.get(), charset, pe, he.getHost(), he.getUri(), he.getUrl(), je.getCookie());
                this.pageNum.incrementAndGet();
            }
            Elements lists = doc.select(lre.getListdom());
            if (CollectionKit.isEmpty(lists))
                throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR, "use listdom [" + lre.getListdom() + "] from [" + currentUrl + "] not extra data");
            Elements lines = lists.first().select(lre.getLinedom());
            if (CollectionKit.isEmpty(lines))
                throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR, "use linedom [" + lre.getLinedom() + "] from [" + currentUrl + "] not extra data");
            // 如果上一页的内容和这一页的内容相同表示源网站有问题或者翻页失败
            if (isSamePage(lists, oldLines))
                break;

            //log.debug("fetch [{}], the page [{}]", je.getType(), this.pageNum.get());

            // 关键字
            argMap.put("keyword", je.getKeyword());

            // 初始化一个数据爬取结果列表
            List<RecordInfoEntity> ifs = AnalyticalKit.chooseAnalytical(je).parseInfo(lines, lre, je.getDetailRules(), baseRie.clone(), he.getUrl(), he.getHost(), he.getBasePath(), null, je.getGoInto(), je.getCookie(), argMap);

            oldLines = lists; // 更新上一次的列表页数据
            firstPage = false; // 当第一次爬取完成后标识接下来的爬取都是翻页操作

            if (CollectionKit.isEmpty(ifs))
                continue;

            //this.sum.addAndGet(ifs.size());
            List<WriterEntity> lwe = DataWriter.write(address, ifs);
            ifs.clear();

            if (CollectionKit.isEmpty(lwe)) {
                //log.error("write data fail, no write address");
                continue;
            }
            for (WriterEntity we : lwe) {
                if (we.getSuccess() != 0)
                    continue;
                //log.error("write data fail, message: {}", we.getMessage());
            }
        }
        //log.debug("complete fetch [{}], fetch [{}] page, total number [{}]", je.getType(), this.pageNum.get(),  this.sum.get());
        baseRie.clear();
        return new ParseEntity();
    }

    protected Document fetchNextPage(Document doc, Integer pageNumber, Charset charset, ProxyEntity pe, String host,
                                     String basePath, String url, String cookie) throws PageBarNotFoundException, CrawlerException {
        Map<String, String> headers = new HashMap<String, String>();
        if (StrKit.notBlank(cookie))
            headers.put(HttpConst.COOKIE, cookie);

        String nu = null;

        NextPageEntity npe = new NextPageEntity();
        Elements es = doc.select("a:matchesOwn(下一页|下页|下一页>|Next)");

        if (!CollectionKit.isEmpty(es)) {
            // log.debug(es.first().attr("href"));
            String next = es.attr("href");
            nu = UrlKit.struct(next, host, basePath, url);
            if (StrKit.isBlank(nu))
                throw new PageBarNotFoundException("Not have next page url");
            HttpEntity he = HttpKit.get(nu, null, headers, pe, charset, HttpConst.DEF_TIMEOUT, HttpConst.DEF_SOTIMEOUT);
            npe.setUrl(he.getUrl()).setHtml(he.getHtml()).setHeader(he.getHeaders());
        } else {
            Element pagebar = this.getPageBar(doc);
            if (pagebar == null)
                throw new PageBarNotFoundException("Not have next page");
            Elements achors = pagebar.getElementsByTag("a");
            if (CollectionKit.isEmpty(achors))
                throw new PageBarNotFoundException("Not have next page");
            for (Element achor : achors) {
                if (!Tool.isNumber(achor.text()) || Integer.valueOf(achor.text().trim()) != pageNumber + 1)
                    continue;
                String next = achor.attr("href");
                nu = UrlKit.struct(next, host, basePath, url);
                if (StrKit.isBlank(nu))
                    throw new PageBarNotFoundException("Not have next page url");
                HttpEntity he = HttpKit.get(nu, null, headers, pe, charset, HttpConst.DEF_TIMEOUT,
                        HttpConst.DEF_SOTIMEOUT);
                npe.setUrl(he.getUrl()).setHtml(he.getHtml()).setHeader(he.getHeaders());
            }
            achors.clear();
        }

        if (StrKit.isBlank(npe.getUrl()) || StrKit.isBlank(npe.getHtml()))
            throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR,
                    "fetch next page error, page: " + (pageNumber + 1) + " current page url [" + nu + "]");
        Document nextPageDoc = Jsoup.parse(npe.getHtml());
        es.clear();
        npe.clear();
        return nextPageDoc;
    }


    private Element getPageBar(Element document) throws PageBarNotFoundException {
        if (document == null)
            return null;

        Elements eles = document.select("a:matches(上一页|上页|<上一页|下一页|下页|下一页>|尾页|末页|Next)");
        if (!CollectionKit.isEmpty(eles)) { // bug: if only has '下一页'

            // Element element = eles.last();
            Element element = null;

            for (Element ele : eles) {
                // Elements _eles = eles.first().siblingElements();
                Elements _eles = ele.siblingElements();
                if (!CollectionKit.isEmpty(_eles)) {
                    List<Integer> nums = new LinkedList<Integer>();
                    for (Element _ele : _eles) {
                        if (Tool.isNumber(_ele.text())) {
                            nums.add(Integer.valueOf(_ele.text()));
                        }
                    }
                    Collections.sort(nums);
                    Integer[] arr = nums.toArray(new Integer[0]);

                    if (_eles.size() > 2 && arr.length > 2) {
                        // element = eles.first();
                        element = ele;
                        break;
                    }
                }
            }

            if (element == null)
                element = eles.last();

            /** test if  element's parent only have one anchor **/
            int count = 0;
            while (count++ < 20) {
                Element parent = element.parent();
                if (parent.getElementsByTag("a").size() > 1) {
                    break;
                }
                element = parent;
            }
            return element.parent();

        } else { // get pagebar by 1,2,3...
            eles = document.getElementsByTag("a");
            if (CollectionKit.isEmpty(eles))
                return null;
            for (Element element : eles) {
                String eleTxt = element.text();
                if (Tool.isNumber(eleTxt)) {
                    /** test if  element's parent only have one anchor **/
                    int count = 0;
                    while (count++ < 20) {
                        Element parent = element.parent();
                        if (parent.getElementsByTag("a").size() > 1) {
                            break;
                        }
                        element = parent;
                    }

                    /** Easy Finding **/
                    String siblingTxt;
                    if (element.nextElementSibling() != null) {
                        siblingTxt = element.nextElementSibling().text();
                        if (Tool.isNumber(siblingTxt)
                        /*
                         * && "a".equals(element.nextElementSibling().tagName())
						 */
                                ) {
                            try {
                                if (Integer.valueOf(eleTxt) + 1 == Integer.valueOf(siblingTxt))
                                    return element.parent();
                            } catch (Exception e) { // 有时获取到的siblingTxt值大于Integer.MAX_VALUE
                                return null;
                            }
                        }
                    } else if (element.previousElementSibling() != null) {
                        siblingTxt = element.previousElementSibling().text();
                        if (Tool.isNumber(siblingTxt) && "a".equals(element.previousElementSibling().tagName())
                                && Integer.valueOf(eleTxt) - 1 == Integer.valueOf(siblingTxt)) {
                            return element.parent();
                        }
                    }
                }
            }
        }
        throw new PageBarNotFoundException();
    }

    public boolean isSamePage(Elements lines, Elements oldlines) {
        if (lines == null || oldlines == null)
            return false;
        return lines.text().trim().equals(oldlines.text().trim());
    }
}
