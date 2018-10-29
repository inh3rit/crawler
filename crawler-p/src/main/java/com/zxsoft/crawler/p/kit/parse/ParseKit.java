package com.zxsoft.crawler.p.kit.parse;

import com.jfinal.kit.StrKit;
import com.zxsoft.crawler.common.entity.parse.NextPageEntity;
import com.zxsoft.crawler.common.entity.parse.ParseEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.kit.Tool;
import com.zxsoft.crawler.common.kit.UrlKit;
import com.zxsoft.crawler.exception.CrawlerException;
import com.zxsoft.crawler.exception.PageBarNotFoundException;
import org.inh3rit.httphelper.common.HttpConst;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.entity.ProxyEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.charset.Charset;
import java.util.*;


/**
 * 搜索实现抽象类
 */
public abstract class ParseKit {

	public abstract ParseEntity parse(Map<String, Object> argMap) throws Exception;

	public boolean isSamePage(Elements lines, Elements oldlines) {
		if (lines == null || oldlines == null)
			return false;
		return lines.text().trim().equals(oldlines.text().trim());
	}

	/**
	 * Get Page Bar of current page[document]
	 *
	 * @return page bar element.
	 * @throws PageBarNotFoundException
	 */
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

	/**
	 * 获取下一页 Document
	 * @param doc 当前页面的 Document
	 * @param pageNumber 当前页码
	 * @param charset 字符集
	 * @param pe 代理
	 * @param host 当前链接主机
	 * @param basePath 当前链接根路径
	 * @param url 当前链接
	 * @param cookie cookie
	 * @return Jsoup Document
	 * @throws PageBarNotFoundException
	 * @throws CrawlerException
	 */
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

}
