package com.zxsoft.crawler.p.ctrl;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zxsoft.crawler.common.kit.UrlKit;
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

import com.jfinal.kit.StrKit;
import com.zxsoft.crawler.common.entity.out.RecordInfoEntity;
import com.zxsoft.crawler.common.entity.out.WriterEntity;
import com.zxsoft.crawler.common.entity.parse.ParseEntity;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.redis.ListRuleEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.d.kit.DataWriter;
import com.zxsoft.crawler.exception.CrawlerException;
import com.zxsoft.crawler.p.analytical.AnalyticalKit;
import com.zxsoft.crawler.p.kit.parse.ParseKit;
import com.zxsoft.crawler.p.kit.remode.UrlRemake;

/**
 * Created by cox on 2015/12/7.
 */
public class NetworkInspectParseCtrl extends ParseKit {

    private static final Logger log = LoggerFactory.getLogger(NetworkInspectParseCtrl.class);

    @Override
    public ParseEntity parse(Map<String, Object> argMap) throws Exception {
        // 解决407问题
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

        JobEntity je = (JobEntity) argMap.get("job");
        String[] address = (String[]) argMap.get("address");
        ProxyEntity pe = (ProxyEntity) argMap.get("proxy");

        RecordInfoEntity baseRie = new RecordInfoEntity();
        baseRie.setType(je.getType()).setSourceId(je.getSource_id()).setServerId(je.getWorkerId())
                .setFirstTime(System.currentTimeMillis()).setIdentifyMd5(je.getIdentify_md5())
                .setSourceName(je.getSource_name()).setSourceType(JobType.NETWORK_INSPECT.getIndex())
                .setCountryCode(je.getCountry_code()).setPlatform(je.getPlatform());

        ListRuleEntity lre = je.getListRule();
        if (lre == null)
            throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR, "no crawler rule");
        if (StrKit.isBlank(lre.getListdom()))
            throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR, "the listdom rule not found");
        if (StrKit.isBlank(lre.getLinedom()))
            throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR, "the linedom rule not found");
        if (StrKit.isBlank(lre.getUrldom()))
            throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR, "the urldom rule not found");

        Map<String, String> headers = new HashMap<String, String>();
        if (StrKit.notBlank(je.getCookie()))
            headers.put(HttpConst.COOKIE, je.getCookie());
        Charset charset = HttpConst.DEF_CHARSET;
        if (StrKit.notBlank(je.getKeywordEncode()))
            charset = Charset.forName(je.getKeywordEncode());

        String originUrl = je.getUrl();
        originUrl = UrlKit.encodeCn(originUrl);
        HttpEntity he = HttpKit.get(UrlRemake.remake(originUrl), null, headers, null, charset, HttpConst.DEF_TIMEOUT,
                HttpConst.DEF_SOTIMEOUT);
//        ProxyEntity proxy = new ProxyEntity("192.168.155.155", 25, "yproxyq", "zproxyx"); // for test
//        HttpEntity he = HttpKit.get(UrlRemake.remake(originUrl), null, headers, proxy, charset, HttpConst.DEF_TIMEOUT,
//                HttpConst.DEF_SOTIMEOUT);

        if (he.getResponseCode() == HttpStatus.SC_MOVED_PERMANENTLY) { // 301 永久性重定向，需要从location中获取重定向的url
            originUrl = he.getHeader("Location");
            he = HttpKit.get(UrlRemake.remake(originUrl), null, headers, null, charset, HttpConst.DEF_TIMEOUT,
                    HttpConst.DEF_SOTIMEOUT);
//            he = HttpKit.get(UrlRemake.remake(originUrl), null, headers, proxy, charset, HttpConst.DEF_TIMEOUT,
//                    HttpConst.DEF_SOTIMEOUT);
        }
        String firstPageHtml = he.getHtml();

        if (he.getResponseCode() != HttpStatus.SC_OK)
            throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR,
                    "fetch url: " + originUrl + " http status not equal " + HttpStatus.SC_OK);
        if (StrKit.isBlank(firstPageHtml))
            throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR, "fetch page html is blank");

        Document doc = Jsoup.parse(firstPageHtml);

        log.debug("fetch [{}], jobId: [{}]", originUrl, je.getJobId());
        Elements lineBox = doc.select(lre.getListdom());
        if (CollectionKit.isEmpty(lineBox))
            throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR,
                    "use listdom [" + lre.getListdom() + "] from [" + originUrl + "] not extra data");
        Elements line = lineBox.first().select(lre.getLinedom());
        if (CollectionKit.isEmpty(line))
            throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR,
                    "use linedom [" + lre.getLinedom() + "] from [" + originUrl + "] not extra data");

        log.debug("fetch [{}-{}]", je.getSource_name(), je.getType());

        List<RecordInfoEntity> ifs = AnalyticalKit.chooseAnalytical(je).parseInfo(line, lre, je.getDetailRules(),
                baseRie.clone(), he.getUrl(), he.getHost(), he.getBasePath(), null, je.getGoInto(), je.getCookie(),
                argMap);

        if (CollectionKit.isEmpty(ifs))
            return null;
        List<WriterEntity> lwe = DataWriter.write(address, ifs);
        for (WriterEntity we : lwe) {
            if (we.getSuccess() != 0)
                continue;
            log.error("数据写入失败, 错误信息: {}", we.getMessage());
        }
        ifs.clear();
        baseRie.clear();
        return new ParseEntity();
    }

}
