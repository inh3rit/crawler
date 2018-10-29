package com.zxsoft.crawler.p.ctrl;

import com.jfinal.kit.JsonKit;
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

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by cox on 2015/10/13.
 */
public class NetworkFocusParseCtrl extends ParseKit {

	private static Logger log = LoggerFactory.getLogger(NetworkFocusParseCtrl.class);

	private AtomicInteger pageNum = new AtomicInteger(1);
	private AtomicInteger sum = new AtomicInteger(0);

	@Override
	public ParseEntity parse(Map<String, Object> argMap) throws Exception {

		JobEntity je = (JobEntity) argMap.get("job");
		String[] address = (String[]) argMap.get("address");
		ProxyEntity pe = (ProxyEntity) argMap.get("proxy");

		RecordInfoEntity baseRie = new RecordInfoEntity(je.getSource_id(), je.getType(), je.getWorkerId(),
				je.getIdentify_md5(), je.getKeyword(), je.getIp(), je.getLocation(), je.getSource_name(),
				JobType.NETWORK_FOCUS.getIndex(), je.getCountry_code(), je.getLocationCode(), je.getProvince_code(),
				je.getCity_code());
		baseRie.setNickname(je.getNickName()).setUsername(je.getUsername()).setPlatform(je.getPlatform());

		ListRuleEntity lre = je.getListRule();
		if (lre == null)
			throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR, "The list rule not found");
		if (StrKit.isBlank(lre.getListdom()))
			throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR, "the listdom not");

		String originUrl = je.getUrl();

		Map<String, String> headers = new HashMap<String, String>();
		headers.put(HttpConst.COOKIE, je.getCookie());
		Charset charset = HttpConst.DEF_CHARSET;
		if (StrKit.notBlank(je.getKeywordEncode()))
			charset = Charset.forName(je.getKeywordEncode());

		HttpEntity he = HttpKit.get(originUrl, null, headers, pe, charset, HttpConst.DEF_TIMEOUT,
				HttpConst.DEF_SOTIMEOUT);
		// String firstPageHtml = he.getHtml();
		if (he.getResponseCode() != HttpStatus.SC_OK)
			throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR,
					"fetch url: " + originUrl + " http status not equal " + HttpStatus.SC_OK);
		if (StrKit.isBlank(he.getHtml()))
			throw new CrawlerException(CrawlerException.ErrorCode.NETWORK_ERROR, "fetch page html is blank");

		Document doc = Jsoup.parse(he.getHtml());

		Elements oldLines = null;
		log.debug("fetch focus source name: [{}], url: [{}]", je.getSource_name(), originUrl);
		Boolean firstPage = true;

		while (this.pageNum.get() < this.getTotalPage(je.getSource_name())) {
			if (!firstPage) {
				doc = super.fetchNextPage(doc, this.pageNum.get(), charset, pe, he.getHost(), he.getUri(), he.getUrl(),
						je.getCookie());
				this.pageNum.incrementAndGet();
			}
			Elements lists = doc.select(lre.getListdom());
			if (CollectionKit.isEmpty(lists))
				throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR,
						"use listdom [" + lre.getListdom() + "] from [" + originUrl + "] not extra data");
			Elements line = lists.first().select(lre.getLinedom());
			if (CollectionKit.isEmpty(line))
				throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR,
						"use linedom [" + lre.getLinedom() + "] from [" + originUrl + "] not extra data");
			// 如果上一页的内容和这一页的内容相同表示源网站有问题或者翻页失败
			if (super.isSamePage(lists, oldLines))
				break;

			log.debug("fetch [{}], the page [{}]", je.getType(), this.pageNum.get());

			List<RecordInfoEntity> ifs = AnalyticalKit.chooseAnalytical(je).parseInfo(line, lre, je.getDetailRules(),
					baseRie.clone(), he.getUrl(), he.getHost(), he.getBasePath(), je.getTimestamp(), je.getGoInto(),
					je.getCookie(), argMap);

			// 如果返回没有返回任何数据表示该人未做任何发文记录, 仍是较早之前抓取的内容, 直接跳出
			if (CollectionKit.isEmpty(ifs))
				break;

			this.sum.addAndGet(ifs.size());
			oldLines = lists; // 更新上一次的列表页数据
			firstPage = false; // 当第一次爬取完成后标识接下来的爬取都是翻页操作

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

			log.debug(JsonKit.toJson(ifs));
		}
		log.debug("complete fetch [{}], fetch [{}] page, total number [{}]", je.getType(), this.pageNum.get(),
				this.sum.get());
		return null;
	}

	/**
	 * 针对不同网站翻页的总数
	 *
	 * @param sourceName 来源网站
	 * @return Integer
	 */
	private Integer getTotalPage(String sourceName) {
		if ("腾讯微博".equals(sourceName))
			return 4;
		return 4;
	}

}
