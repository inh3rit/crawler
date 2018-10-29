package com.zxsoft.crawler.m.model.job;


import com.jfinal.kit.StrKit;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.model.PropModel;
import com.zxsoft.crawler.common.type.PropKey;
import com.zxsoft.crawler.exception.CrawlerException;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cox on 2016/1/11.
 */
public class ValidatorModel {

    private static final Logger log = LoggerFactory.getLogger(ValidatorModel.class);

    public static final ValidatorModel dao = new ValidatorModel();


    /**
     * 生成代理信息
     *
     * @return ProxyEntity
     */
    private ProxyEntity genProxy() {
        ProxyEntity pe = null;
        if (StrKit.notBlank(PropModel.dao.getStr(PropKey.PROXY_HOST)) &&
                StrKit.notBlank(PropModel.dao.getStr(PropKey.PROXY_PORT))) {
            pe = new ProxyEntity(PropModel.dao.getStr(PropKey.PROXY_HOST),
                    PropModel.dao.getInt(PropKey.PROXY_PORT));
            if (StrKit.notBlank(PropModel.dao.getStr(PropKey.PROXY_USR)))
                pe.setAccount(PropModel.dao.getStr(PropKey.PROXY_USR));
            if (StrKit.notBlank(PropModel.dao.getStr(PropKey.PROXY_PASSWD)))
                pe.setPassword(PropModel.dao.getStr(PropKey.PROXY_PASSWD));
        }
        return pe;
    }


    /**
     * 验证是否是有效的账户
     *
     * @param sourceName 来源名
     * @param url        链接
     * @param nickName   昵称
     * @param cookie     cookie
     * @return String
     * @throws CrawlerException
     */
    public String validatorFocus(String sourceName, String url, String nickName, String cookie) throws CrawlerException {

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Cookie", cookie);

        HttpEntity he = HttpKit.get(url, null, headers, this.genProxy(), null, HttpConst.DEF_TIMEOUT, HttpConst.DEF_SOTIMEOUT, true);

        // 如果爬取这个用户的主页状态码错误, 表示无法打开
        if (he.getResponseCode() > HttpStatus.SC_MULTIPLE_CHOICES)
            throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR, "can not oen the url [" + url + "], and account [" + nickName + "] ");

        try {
            Document doc = Jsoup.parse(he.getHtml());
            if ("腾讯微博".equals(sourceName.trim())) {
                // 添加的账户名和 url 实际账户名不匹配, 返回 null
                String userName = doc.select(".tit .text_user").text();
                if (StrKit.isBlank(userName)) {
                    userName = doc.select(".clubInfo img[title=点击查看大图和历史头像]").attr("alt");
                }
                if (StrKit.isBlank(userName) || !nickName.trim().equals(userName))
                    return null;
                String uri = url.split("\\?")[0];
                uri = uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri;
                return uri.substring(uri.lastIndexOf("/") + 1, uri.length());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取重点关注人员最后一条发帖时间
     *
     * @param sourceName 来源
     * @param url        链接
     * @param cookie     cookie
     * @return
     * @throws CrawlerException
     */
    public Long getLastPostTime(String sourceName, String url, String cookie) throws CrawlerException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Cookie", cookie);

        HttpEntity he = HttpKit.get(url, null, headers, this.genProxy(), null, HttpConst.DEF_TIMEOUT, HttpConst.DEF_SOTIMEOUT, true);

        // 如果爬取这个用户的主页状态码错误, 表示无法打开
        if (he.getResponseCode() > HttpStatus.SC_MULTIPLE_CHOICES)
            return 0L;
        Document doc = Jsoup.parse(he.getHtml());
        if ("腾讯微博".equals(sourceName)) {
            Elements times = doc.select("#talkList li:eq(0) .time");
            if (CollectionKit.isEmpty(times)) return 0L;
            if (times.size() == 1) return Long.valueOf(times.get(0).attr("rel")) * 1000L;
            if (times.size() > 1) return Long.valueOf(times.get(times.size() - 1).attr("rel")) * 1000L;
        }
        return 0L;
    }

}
