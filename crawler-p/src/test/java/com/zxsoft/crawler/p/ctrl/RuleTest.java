package com.zxsoft.crawler.p.ctrl;

import java.util.HashMap;
import java.util.Map;

import org.inh3rit.httphelper.common.HttpConst;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.entity.ProxyEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.zxsoft.crawler.p.kit.remode.UrlRemake;

public class RuleTest {

	@Test
	public void listdomTest() {

		String url = "http://cn.bing.com/search?q=%E6%B4%AA%E6%B0%B4";
		// String url = "http://www.baidu.com/s?wd=%E6%B4%AA%E6%B0%B4&ie=utf-8";
		String listdom = "ol#b_results", linedom = "[class~=(b_algo|b_ans|b_pag)]", urldom = "h2 a";
		// String listdom = "div#content_left", linedom = "div.c-container",
		// urldom = "h3.t > a";

		ProxyEntity proxyEntity = new ProxyEntity("192.168.25.254", 28129, "yproxyq", "zproxyx0#");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "uid=CgqASVYIpMZeVl4FEVxLAg==");

		HttpEntity httpEntity = HttpKit.get(UrlRemake.remake(url), null, headers, null, HttpConst.DEF_CHARSET,
				HttpConst.DEF_TIMEOUT, HttpConst.DEF_SOTIMEOUT);

		Document doc = Jsoup.parse(httpEntity.getHtml());
		Elements lists = doc.select(listdom);
		System.out.println(lists);
		Elements lines = lists.first().select(linedom);
		System.out.println(lines);

		for (Element box : lines) {
			Elements targetUrls = box.select(urldom);
			System.out.println(targetUrls);
			System.out.println("------------------------------------");
		}
	}
}
