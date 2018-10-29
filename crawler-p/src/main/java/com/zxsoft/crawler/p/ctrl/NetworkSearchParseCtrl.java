package com.zxsoft.crawler.p.ctrl;

import com.jfinal.kit.StrKit;
import com.zxsoft.crawler.common.entity.out.RecordInfoEntity;
import com.zxsoft.crawler.common.entity.out.WriterEntity;
import com.zxsoft.crawler.common.entity.parse.ParseEntity;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.redis.ListRuleEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.kit.UrlKit;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.d.kit.DataWriter;
import com.zxsoft.crawler.exception.CrawlerException;
import com.zxsoft.crawler.p.analytical.AnalyticalKit;
import com.zxsoft.crawler.p.kit.parse.ParseKit;
import com.zxsoft.crawler.p.kit.remode.UrlRemake;
import org.inh3rit.httphelper.common.HttpConst;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.entity.ProxyEntity;
import org.inh3rit.httphelper.http.HttpStatus;
import org.inh3rit.httphelper.kit.HttpKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全網搜索控制器
 */
public class NetworkSearchParseCtrl extends ParseKit {

    private static final Logger log = LoggerFactory.getLogger(NetworkSearchParseCtrl.class);
    private AtomicInteger pageNum = new AtomicInteger(1);
    private AtomicInteger sum = new AtomicInteger(0);

    @Override
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

        HttpEntity he = HttpKit.get(UrlRemake.remake(originUrl), null, headers, pe, charset, HttpConst.DEF_TIMEOUT,
                HttpConst.DEF_SOTIMEOUT);
        String firstPageHtml = he.getHtml();
        if (he.getResponseCode() != HttpStatus.SC_OK)
            throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR,
                    "fetch url: " + originUrl + " http status not equal " + HttpStatus.SC_OK);
        if (StrKit.isBlank(firstPageHtml))
            throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR, "fetch page html is blank");

        Document doc = Jsoup.parse(firstPageHtml);
        final Long start = System.currentTimeMillis();

        Elements oldLines = null;
        log.debug("fetch keyword: [{}] from [{}], jobId: [{}]", je.getKeyword(), originUrl, je.getJobId());
        String currentUrl = originUrl;
        Boolean firstPage = true;
        while (System.currentTimeMillis() - start < 5L * 60L * 1000L) {
            if (!firstPage) {
                doc = super.fetchNextPage(doc, this.pageNum.get(), charset, pe, he.getHost(), he.getUri(), he.getUrl(),
                        je.getCookie());
                this.pageNum.incrementAndGet();
            }
            Elements lists = doc.select(lre.getListdom());
            if (CollectionKit.isEmpty(lists))
                throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR,
                        "use listdom [" + lre.getListdom() + "] from [" + currentUrl + "] not extra data");
            Elements lines = lists.first().select(lre.getLinedom());
            if (CollectionKit.isEmpty(lines))
                throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR,
                        "use linedom [" + lre.getLinedom() + "] from [" + currentUrl + "] not extra data");
            // 如果上一页的内容和这一页的内容相同表示源网站有问题或者翻页失败
            if (super.isSamePage(lists, oldLines))
                break;

            log.debug("fetch [{}], the page [{}]", je.getType(), this.pageNum.get());

            // 关键字
            argMap.put("keyword", je.getKeyword());

            // 初始化一个数据爬取结果列表
            List<RecordInfoEntity> ifs = AnalyticalKit.chooseAnalytical(je).parseInfo(lines, lre, je.getDetailRules(),
                    baseRie.clone(), he.getUrl(), he.getHost(), he.getBasePath(), null, je.getGoInto(), je.getCookie(),
                    argMap);

            oldLines = lists; // 更新上一次的列表页数据
            firstPage = false; // 当第一次爬取完成后标识接下来的爬取都是翻页操作

            if (CollectionKit.isEmpty(ifs))
                continue;

            this.sum.addAndGet(ifs.size());
            List<WriterEntity> lwe = DataWriter.write(address, ifs);
            ifs.clear();

            if (CollectionKit.isEmpty(lwe)) {
                log.error("write data fail, no write address");
                continue;
            }
            for (WriterEntity we : lwe) {
                if (we.getSuccess() != 0)
                    continue;
                log.error("write data fail, message: {}", we.getMessage());
            }
        }
        log.debug("complete fetch [{}], fetch [{}] page, total number [{}]", je.getType(), this.pageNum.get(),
                this.sum.get());
        baseRie.clear();
        return new ParseEntity();
    }

}
