package com.zxsoft.crawler.p.ctrl;

import com.zxsoft.crawler.common.entity.redis.DetailRuleEntity;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.redis.ListRuleEntity;
import com.zxsoft.crawler.common.type.JobType;
import name.iaceob.kit.id.IdKit;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.junit.Test;

import java.nio.charset.Charset;

/**
 * Created by cox on 2015/12/16.
 */
public class NetworkInspectParseCtrlTest {

	@Test
	public void testParse() throws Exception {

		/*
		 * // 大纪元 String content = "div#artbody", author = "", date = ""; String
		 * listdom = "div#artlist", linedom = "div.posts.column", urldom =
		 * "div.arttitle.column a", datedom =
		 * "div.large-6.medium-6.small-12.column.post-date", synopsisdom = "";
		 * String url = "http://www.epochtimes.com/gb/nsc418.htm"; String
		 * charset = "utf-8"; Integer sourceId = 320; String sourceName = "大纪元",
		 * type = "国际新闻"; String cookie = ""; Integer platform = 1; String
		 * category = "news";
		 */

		/*
		 * // 看中国 String content = "div.articlebody", author = "", date =
		 * "div.inform div.time"; String listdom =
		 * "div.articleBlock.articleCategoryBlock.block", linedom =
		 * "ul li[id^=node]", urldom = "span a", datedom = "", synopsisdom = "";
		 * String url = "http://www.secretchina.com/taxonomy/1"; String charset
		 * = "utf-8"; Integer sourceId = 326; String sourceName = "看中国", type =
		 * "看时事"; String cookie = ""; Integer platform = 1; String category =
		 * "news";
		 */

		// 明慧网
		String content = "form#Form1", author = "", date = "";
		String listdom = "form#Form1", linedom = "li", urldom = "a", datedom = "", synopsisdom = "";
		String url = "http://m.minghui.org/mmh/fenlei/72/index.html";
		String charset = "utf-8";
		Integer sourceId = 631;
		String sourceName = "明慧网", type = "大陆消息";
		String cookie = "";
		Integer platform = 1;
		String category = "news";

		JobEntity je = new JobEntity();
		ListRuleEntity lre = new ListRuleEntity();
		DetailRuleEntity dre = new DetailRuleEntity();

		dre.setContent(content).setAuthor(author).setDate(date);
		lre.setCategory(category).setListdom(listdom).setLinedom(linedom).setUrldom(urldom).setDatedom(datedom)
				.setSynopsisdom(synopsisdom);
		je.setJobId(String.valueOf(IdKit.run.generateID())).setJobType(JobType.NETWORK_INSPECT).setUrl(url).setCountry_code(0)
				.setSource_id(sourceId).setSource_name(sourceName).setType(type).setCookie(cookie).setPlatform(platform)
				.setListRule(lre).setDetailRules(dre).setWorkerId(8888).setGoInto(true).setKeywordEncode(charset)
				.setIdentify_md5("iaceob");

		NetworkInspectParseCtrl ctrl = new NetworkInspectParseCtrl();
		String[] address = new String[] { "http://192.168.32.17:20000/sentiment/index" };
		// ctrl.parse(je, address, null, new BlacklistEntity[] {}, null, null);

	}

	@Test
	public void testFetchZm() {
		String url = "http://www.2muslim.com/forum.php?mod=viewthread&tid=430156";
		HttpEntity he = HttpKit.get(url, null, null, Charset.forName("GBK"));
		System.out.println(he.getHtml());
	}

}