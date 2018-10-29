package com.zxsoft.crawler.p.ctrl;

import java.util.ArrayList;
import java.util.List;

import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.jsoup.helper.StringUtil;
import org.junit.Test;

public class URLTest {

	@Test
	public void requestCodeTest() {
		String urls = FileUtil.read("/home/ubuntu/cache/www.txt");
		for (String url : urls.split("\n")) {
			System.out.println(url);
			HttpEntity httpEntity;
			try {
				httpEntity = HttpKit.get(url);
			} catch (Exception e) {
				// e.printStackTrace();
				continue;
			}
			System.out.println(httpEntity.getResponseCode());
			if (200 == httpEntity.getResponseCode()) {
				FileUtil.write("/home/ubuntu/cache/_www.txt", url + "\n");
			}
		}
	}

	@Test
	public void unique() {
		String urls = FileUtil.read("/home/ubuntu/cache/www.txt");
		List<String> urlList = new ArrayList<String>();
		List<String> writeList = new ArrayList<String>();
		for (String url : urls.split("\n")) {
			String _url = url.replace("http://", "").replace("www.", "");
			if (!urlList.contains(_url)) {
				urlList.add(_url);
				writeList.add(url);
			}
		}

		String uniquedUrls = StringUtil.join(writeList, "\n");
		FileUtil.write("/home/ubuntu/cache/__www.txt", uniquedUrls);
	}
}
