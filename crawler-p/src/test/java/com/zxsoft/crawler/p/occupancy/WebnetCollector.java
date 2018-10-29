package com.zxsoft.crawler.p.occupancy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.inh3rit.httphelper.common.HttpConst;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.entity.ProxyEntity;
import org.inh3rit.httphelper.http.HttpStatus;
import org.inh3rit.httphelper.kit.HttpKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jfinal.kit.StrKit;
import com.zxsoft.crawler.common.entity.parse.ParseEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.kit.Tool;
import com.zxsoft.crawler.common.kit.UrlKit;
import com.zxsoft.crawler.exception.CrawlerException;
import com.zxsoft.crawler.p.ctrl.FileUtil;
import com.zxsoft.crawler.p.kit.parse.ParseKit;
import com.zxsoft.crawler.p.kit.remode.UrlRemake;

public class WebnetCollector extends ParseKit {

	@Override
	public ParseEntity parse(Map<String, Object> argMap) throws Exception {
		return null;
	}

	public List<UrlEntity> collectByRule() {
		List<UrlEntity> urlList = new ArrayList<UrlEntity>();
		int type = 0;
		String html = HttpKit.get("http://www.hangye5.com/hangyewang/weibo/index.html").getHtml();

		String rule = "div#cate ul.clearfix li a";
		Document doc = Jsoup.parse(html);
		Elements elements = doc.select(rule);

		for (Element e : elements) {
			HttpEntity httpEntity = HttpKit.get(e.attr("href"));
			Document _doc = Jsoup.parse(httpEntity.getHtml());
			String _rule = "div#cate.bd+div ul li a";
			Elements _elements = _doc.select(_rule);
			String url = _elements.first().attr("href");
			urlList.add(new UrlEntity(url, type));
		}
		return urlList;
	}

	public void _collectByRule() {
		String textRule = "ul.listCentent li .CentTxt .rightTxtHead a";
		String hrefRule = "ul.listCentent li .CentTxt .rightTxtHead span";

		for (int i = 1; i < 9; i++) {
			String url = "http://top.chinaz.com/hangye/index_zonghe_boke";
			String suffix = ".html";
			if (i != 1) {
				url = url + "_" + i;
			}
			url = url + suffix;
			HttpEntity httpEntity = HttpKit.get(url);
			Document doc = Jsoup.parse(httpEntity.getHtml());
			Elements textElements = doc.select(textRule);
			Elements hrefElements = doc.select(hrefRule);
			for (Element e : hrefElements) {
				System.out.println(e.text());
			}
		}
	}

	public List<UrlEntity> collectBySearchEngine() {
		List<UrlEntity> urlList = new ArrayList<UrlEntity>();
		try {
			SearchEngine engine = assemEngine();
			AtomicInteger pageNum = new AtomicInteger(1);
			String cookie = "uid=CgqASVYIpMZeVl4FEVxLAg==";

			String _charset = engine.getKeywordEncode();
			_charset = StrKit.isBlank(_charset) ? "UTF-8" : _charset;
			_charset = _charset.toUpperCase();
			_charset = "GB-2312".equals(_charset) ? "GBK" : _charset;
			String kwd = URLEncoder.encode(engine.getKeyword(), _charset);
			String originUrl = format(engine.getUrl(), kwd);
			Charset charset = HttpConst.DEF_CHARSET;
			charset = Charset.forName(engine.getKeywordEncode());
			HttpEntity he = HttpKit.get(UrlRemake.remake(originUrl), null, new HashMap<String, String>(), getProxy(),
					charset, HttpConst.DEF_TIMEOUT, HttpConst.DEF_SOTIMEOUT);
			String firstPageHtml = he.getHtml();
			if (he.getResponseCode() != HttpStatus.SC_OK)
				throw new Exception("fetch url: " + originUrl + " http status not equal " + HttpStatus.SC_OK);
			if (StrKit.isBlank(firstPageHtml))
				throw new Exception("etch page html is blank");
			Document doc = Jsoup.parse(firstPageHtml);
			final Long start = System.currentTimeMillis();

			Elements oldLines = null;
			System.out.println("fetch keyword: [" + engine.getKeyword() + "] from [{}], jobId: [" + originUrl + "]");
			String currentUrl = originUrl;
			Boolean firstPage = true;
			while (System.currentTimeMillis() - start < 8L * 60L * 1000L) {
				if (!firstPage) {
					doc = super.fetchNextPage(doc, pageNum.get(), charset, null, he.getHost(), he.getUri(), he.getUrl(),
							cookie);
					pageNum.incrementAndGet();
				}
				Elements lists = doc.select(engine.getListdom());
				if (CollectionKit.isEmpty(lists))
					throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR,
							"use listdom [" + engine.getListdom() + "] from [" + currentUrl + "] not extra data");
				Elements lines = lists.first().select(engine.getLinedom());
				if (CollectionKit.isEmpty(lines))
					throw new CrawlerException(CrawlerException.ErrorCode.CONF_ERROR,
							"use linedom [" + engine.getLinedom() + "] from [" + currentUrl + "] not extra data");
				// 如果上一页的内容和这一页的内容相同表示源网站有问题或者翻页失败
				if (super.isSamePage(lists, oldLines))
					break;

				System.out.println("fetch [" + engine.getType() + "], the page [" + pageNum.get() + "]");

				// 循环列表页中的url
				for (Element box : lines) {
					Elements targetUrls = box.select(engine.getUrldom());
					if (CollectionKit.isEmpty(targetUrls))
						continue;
					String curl = UrlKit.struct(targetUrls.first().attr("href"), he.getHost(), he.getBasePath(),
							he.getUrl());

					System.out.println("----------------------------------------------------------");
					System.out.println(curl);
					HttpEntity httpEntity = null;
					try {
						httpEntity = HttpKit.get(curl);
					} catch (Exception e) {
						continue;
					}
					// 判断域名是否是我们需要的url
					String domain = httpEntity.getDomain();
					int type = find(domain);
					if (type != -1) {
						urlList.add(new UrlEntity(domain, type));
					}
					System.out.println("----------------------------------------------------------");
				}
				firstPage = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return urlList;
	}

	/**
	 * 格式化关键字
	 * @param url
	 * @param keyword
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String format(String url, String keyword) throws UnsupportedEncodingException {
		Date date = new Date();
		int count = Tool.countOccurrencesOf(url, "%t");
		if (count == 0) {
			url = String.format(url, keyword);
			url = url.replaceAll(" ", "%20");
			return url;
		}
		List<Object> list = new ArrayList<Object>();
		list.add(keyword);
		for (int i = 0; i < count; i++) {
			list.add(date);
		}

		Object[] strs = list.toArray(new Object[] {});
		try {
			url = String.format(url, strs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		url = url.replaceAll(" ", "%20");
		return url;
	}

	private ProxyEntity getProxy() {
		ProxyEntity pe = new ProxyEntity("192.168.25.254", 28129);
		pe.setAccount("yproxyq");
		pe.setPassword("zproxyx0#");
		return pe;
	}

	private List<String> getUrlList() {
		List<String> urlList = new ArrayList<String>();
		String urls = FileUtil.read("/home/ubuntu/cache/__www.txt");
		for (String url : urls.split("\n")) {
			urlList.add(purge(url));
		}
		return urlList;
	}

	private String purge(String url) {
		url = url.replace("http://", "").replace("www.", "").split("/")[0];
		return url;
	}

	private int find(String url) {
		String newsRule = "(?<url>\\S+(news|articles*)\\S+)";
		String bbsRule = "(?<url>\\S+(bbs|tieba)\\S+)";
		String blogRule = "(?<url>\\S+(blogs*)\\S+)";
		String microBlogRule = "(?<url>\\S+(weibo)\\S+)";
		Pattern p1 = Pattern.compile(newsRule);
		Pattern p2 = Pattern.compile(bbsRule);
		Pattern p3 = Pattern.compile(blogRule);
		Pattern p4 = Pattern.compile(microBlogRule);
		Matcher m1 = p1.matcher(url);
		Matcher m2 = p2.matcher(url);
		Matcher m3 = p3.matcher(url);
		Matcher m4 = p4.matcher(url);
		if (m1.find())
			return 0;
		if (m2.find())
			return 1;
		if (m3.find())
			return 2;
		if (m4.find())
			return 3;
		return -1;
	}

	/**
	 * 装配引擎类
	 * @return
	 */
	private SearchEngine assemEngine() {
		// 天涯
		// String url =
		// "http://m.tianya.cn/sch/sch.jsp?vu=28456309174&k=%s&t=2";
		// String listdom = "body > div.p", linedom = "p", urldom = "a";
		// String datedom = "", synopsisdom = "p.summary", updatedom = "",
		// authordom = "";
		// String sourceName = "天涯论坛", type = "天涯论坛搜索(手机版)_全网搜索";
		// Integer sourceId = 141, sectionId = 1180;
		// String keywordEncoding = "UTF-8", keyword = "打砸";

		// 网易新闻
		// String url =
		// "http://news.yodao.com/search?q=%s&start=0&ue=utf8&s=&tl=&keyfrom=news.index.suggest";
		// String listdom = "ul#results", linedom = "li", urldom = "h3 a";
		// String datedom = "span.green.stat", synopsisdom = "", updatedom = "",
		// authordom = "";
		// String sourceName = "网易新闻", type = "网易新闻搜索";
		// Integer sourceId = 936, sectionId = 753;
		// String keywordEncoding = "UTF-8", keyword = "打砸";

		/*
		 * // 搜狐博客 (百度) String url =
		 * "http://www.baidu.com/s?wd=%s site:blog.sohu.com&ie=utf-8"; String
		 * listdom = "div#content_left", linedom = "div.result", urldom =
		 * "h3.t a"; String datedom = "", synopsisdom = "div.c-abstract",
		 * updatedom = "", authordom = ""; String sourceName = "搜狐博客", type =
		 * "搜狐博客搜索"; Integer sourceId = 217, sectionId = 1181; String
		 * keywordEncoding = "UTF-8", keyword = "鸠江区";
		 */

		// 百度搜索
		String url = "http://www.baidu.com/s?wd=%s&ie=utf-8";
		String listdom = "div#content_left", linedom = "div.c-container", urldom = "h3.t > a";
		String datedom = "", synopsisdom = "", updatedom = "", authordom = "";
		String sourceName = "百度搜索", type = "百度搜索";
		Integer sourceId = 10, sectionId = 51;
		String keywordEncoding = "UTF-8", keyword = "安徽";

		// 搜报网 // 搜报网网页打开很慢 超时无法获取
		// String url =
		// "http://www.soubao.net/search/searchList.aspx?keyword=%s&startdate=1900-01-01&enddate=2200-12-31&timesel=custom";
		// String listdom = "div#srh_main", linedom = "ul.newList", urldom = "li
		// h2 a";
		// String datedom = "", synopsisdom = "", updatedom = "", authordom =
		// "";
		// String sourceName = "搜报网", type = "搜报网";
		// Integer sourceId = 1642, sectionId = 1137;
		// String keywordEncoding = "UTF-8", keyword = "暴雨";

		// 中国搜索
		// String url = "http://www.chinaso.com/search/pagesearch.htm?q=%s";
		// String listdom = "ol.seResult", linedom = "li.reItem", urldom = "h2
		// a";
		// String datedom = "", synopsisdom = "", updatedom = "", authordom =
		// "";
		// String sourceName = "中国搜索", type = "中国搜索";
		// Integer sourceId = 926, sectionId = 1364;
		// String keywordEncoding = "UTF-8", keyword = "暴雨";

		// 新浪新闻
		// String url =
		// "http://search.sina.com.cn/?q=%s&range=all&c=news&sort=time";
		// String listdom = "div#wrap", linedom = "div.box-result", urldom = "h2
		// a";
		// String datedom = "", synopsisdom = "", updatedom = "", authordom =
		// "";
		// String sourceName = "新浪新闻", type = "新浪新闻搜索";
		// Integer sourceId = 888, sectionId = 755;
		// String keywordEncoding = "GBK", keyword = "暴雨";

		// 宜搜搜索
		// String url = "http://i.easou.com/s.m?wver=c&q=%s";
		// String listdom = "#result-wrap", linedom = "div[data-type]", urldom =
		// "a";
		// String datedom = "", synopsisdom = "", updatedom = "", authordom =
		// "";
		// String sourceName = "宜搜搜索", type = "宜搜搜索";
		// Integer sourceId = 125, sectionId = 752;
		// String keywordEncoding = "UTF-8", keyword = "test";

		// String url = "http://cn.bing.com/search?q=%s";
		// String listdom = "ol#b_results", linedom =
		// "[class~=(b_algo|b_ans|b_pag)]", urldom = "h2 a";
		// String datedom = "", synopsisdom = "", updatedom = "", authordom =
		// "";
		// String sourceName = "必应搜索", type = "必应搜索";
		// Integer sourceId = 126, sectionId = 466;
		// String keywordEncoding = "UTF-8", keyword = "暴雨";

		SearchEngine engine = new SearchEngine();
		engine.setUrl(url);
		engine.setListdom(listdom);
		engine.setLinedom(linedom);
		engine.setUrldom(urldom);
		engine.setDatedom(datedom);
		engine.setSynopsisdom(synopsisdom);
		engine.setUpdatedom(updatedom);
		engine.setAuthordom(authordom);
		engine.setSourceName(sourceName);
		engine.setType(type);
		engine.setSourceId(sourceId);
		engine.setSectionId(sectionId);
		engine.setKeywordEncode(keywordEncoding);
		engine.setKeyword(keyword);

		return engine;
	}

}
