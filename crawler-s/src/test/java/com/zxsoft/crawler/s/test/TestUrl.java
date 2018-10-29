package com.zxsoft.crawler.s.test;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.inh3rit.httphelper.common.HttpConst;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.jsoup.Jsoup;
import org.junit.Test;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;

import name.iaceob.kit.disgest.Disgest;

/**
 * Created by cox on 2015/9/10.
 */
public class TestUrl {

	private ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

	@Test
	public void testCraw() {
		String url = "http://www.xicidaili.com/nn";
		Record r = UrlKit.get(url);
		System.out.println(JsonKit.toJson(r));
	}

	@Test
	public void testT() {
		Object o = 22;
		Class clazz = o.getClass();
		System.out.println(clazz.getName());
	}

	@Test
	public void fuck() {
		Integer i = 0;
		while (true) {
			try {
				String url = "http://csmoyo.cc/";
				HttpEntity he = HttpKit.get(url);
				System.out.println(":" + i + " : " + he.getResponseCode());
				i += 1;
				if (i < 50)
					continue;
				TimeUnit.SECONDS.sleep(5L);
			} catch (Exception e) {
				continue;
			}
		}
	}

	@Test
	public void testSohuLogin() {
		String url = "http://passport.sohu.com/sso/login.jsp";
		Map<String, String> m = new HashMap<String, String>();
		m.put("userid", "rippo@foxmail.com");
		m.put("password", Disgest.encodeMD5("rippo1985sh"));
		m.put("appid", "9999");
		m.put("pwdtype", "1");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(HttpConst.COOKIE, "pptk=VQCAACGBYWFBJBCVSHED;");
		HttpEntity he = HttpKit.get(url, m, headers, Charset.forName("UTF-8"));
		System.out.println(he.getHtml());
	}

	@Test
	public void testSohuLogin2() {
		String url = "http://passport.sohu.com/sso/login.jsp";
		Map<String, String> m = new HashMap<String, String>();
		m.put("desc", "beginLogin");
		m.put("loginProtocal", "http");
		m.put("userid", "rippo@foxmail.com");
		m.put("password", Disgest.encodeMD5("rippo1985sh"));
		// m.put("appid", "9999");
		m.put("appid", "1077");
		m.put("pwdtype", "1");
		m.put("browserType", "2");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(HttpConst.COOKIE, "pptk=VQCAACGBYWFBJBCVSHED;");
		HttpEntity he = HttpKit.get(url, m, headers, Charset.forName("UTF-8"));
		System.out.println(he.getHtml());

	}

	@Test
	public void ttd2() {
		String url = "http://www.mingjingnews.com/MIB/news/news_list.aspx?ID=N01";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie",
				"__cfduid=db3bc9167a2c23c3202b009d5351b454a1449293312; bdshare_firstime=1449293312493; _ga=GA1.2.254228377.1449478414");
		headers.put("User-Agent", "Mozilla/5.0 (Android; Mobile; rv:38.0) Gecko/38.0 Firefox/38.0");
		headers.put("Host", "www.mingjingnews.com");
		headers.put("Accept-Encoding", "gzip, deflate");
		HttpEntity he = HttpKit.get(url, null, headers, Charset.forName("UTF-8"));
		System.out.println(Jsoup.parse(he.getHtml()).select(".news_main").html());
	}

	class FuckCsmoyoCC implements Runnable {

		private Integer ix;

		public FuckCsmoyoCC(Integer ix) {
			this.ix = ix;
		}

		@Override
		public void run() {
			Integer i = 0;
			while (true) {
				try {
					String url = "http://csmoyo.cc/";
					HttpEntity he = HttpKit.get(url);
					System.out.println(this.ix + ":" + i + " : " + he.getResponseCode());
					i += 1;
					if (i < 50)
						continue;
					TimeUnit.SECONDS.sleep(5L);
				} catch (Exception e) {
					continue;
				}
			}
		}
	}

}
