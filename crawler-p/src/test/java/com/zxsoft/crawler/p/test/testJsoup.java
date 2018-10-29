package com.zxsoft.crawler.p.test;

import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by cox on 2015/11/16.
 */
public class testJsoup {

	@Test
	public void ttd1() throws IOException {
		String url = "http://wap.dianping.com/hefei/category";
		Connection c = Jsoup.connect(url);
		Document d = c.get();
		System.out.println(d.body().html());

	}

	@Test
	public void ttd2() {
		String url = "http://wap.dianping.com/hefei/category";
		HttpEntity he = HttpKit.get(url);
		System.out.println(he.getHtml());
	}

	@Test
	public void ttd3() {
		HttpEntity entity = HttpKit.get(
				"https://www.baidu.com/link?url=dNYyCXG7JwYqVdL8uXr-L0jNKetLDjotvd0goiXbOPzDhjiXrv7TVmb_kwksp7KTu1E37y5ZhLVge2IuxBGQMa&wd=&eqid=f21454310008213f0000000457b56e2e");
		System.out.println(entity.getResponseCode());
		System.out.println(entity.getHeader("Location"));
	}

	@Test
	public void ttd4() throws IOException {
		Connection.Response res = Jsoup
				.connect(
						"https://www.baidu.com/link?url=NsHkjUPGl_mZhzM-TUlu6ZfyO3os9ubGKhDv9XiMklKxONjEtFTVLyrKWiW50gJzRFL2hChm_hkarUGQkmwPS_")
				.timeout(60000).method(Connection.Method.GET).followRedirects(false).execute();
		String str = res.header("Location");
		System.out.println(str);
	}

	@Test
	public void ttd5() {
		Document doc = Jsoup.parse(HttpKit.get("http://www.sina.com.cn/").getHtml());
		System.out.println(doc.select("meta[name=description]").get(0).attr("content"));
	}

	@Test
	public void ttd6() throws UnsupportedEncodingException {
		String url = "\thttp://blog.boxun.com/hero/201610/yifeng/17_1.shtml";
//		System.out.println(url.split("\\.")[0]);
		String html = HttpKit.get(url, Charset.forName("gbk")).getHtml();

        byte[] temp=html.getBytes("gbk");//这里写原编码方式
        byte[] newtemp=new String(temp,"gbk").getBytes("utf-8");//这里写转换后的编码方式
        String newStr=new String(newtemp,"utf-8");//这里写转换后的编码方式
        System.out.println(newStr);
//		FileKit.writerFile("/home/tony/cache/1.txt", _html, Charset.forName("utf8"));
	}

	@Test
	public void ttd7() {

	}

}
