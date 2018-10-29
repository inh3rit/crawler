package com.zxsoft.crawler.m;

import java.sql.Timestamp;
import java.text.Collator;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.entity.parse.CrawlerEntity;
import com.zxsoft.crawler.common.entity.sync.BlacklistEntity;
import com.zxsoft.crawler.common.kit.RecordKit;

/**
 * Created by iaceob on 2015/11/14.
 */
public class testRecord {

	@Test
	public void ttd1() {
		Record r = new Record();
		r.set("id", 21222).set("id", r.getInt("id") + 1);
		System.out.println(r.getStr("dd"));
		System.out.println(JsonKit.toJson(r));
	}

	@Test
	public void ttd2() {
		String rst = "2123:192.168.21.23:";
		String[] ss = rst.split(":");
		System.out.println(JsonKit.toJson(ss));
	}

	@Test
	public void ttd3() {
		Record r = new Record();
		r.set("id", 1).set("z", 2).set("a", 3).set("De", 55).set("j", false).set("d", .3).set("da", 90L)
				.set("db", "or\"acke").set("ko", Timestamp.valueOf("2015-02-10 20:10:3"));
		Collator collator = Collator.getInstance(new Locale("en", "US"));
		String[] sortKey = this.sortStrings(collator, r.getColumnNames());
		StringBuilder sb = new StringBuilder();
		for (String sk : sortKey) {
			sb.append(sk).append(":").append(r.get(sk).toString()).append(";");
		}
		System.out.println(sb.toString());
	}

	public String[] sortStrings(Collator collator, String[] words) {
		String tmp;
		for (int i = 0; i < words.length; i++) {
			for (int j = i + 1; j < words.length; j++) {
				if (collator.compare(words[i], words[j]) > 0) {
					tmp = words[i];
					words[i] = words[j];
					words[j] = tmp;
				}
			}
		}
		return words;
	}

	@Test
	public void ttd6() {
		CrawlerEntity ce = new CrawlerEntity();
		ce.setStat(-1);
		Record r = ce;
		System.out.println(JsonKit.toJson(r));
		Record r2 = new Record();
		r2.set("stat", 1);
		CrawlerEntity ce2 = (CrawlerEntity) r2;
		System.out.println(JsonKit.toJson(ce2));
	}

	@Test
	public void ttd7() {
		Record r = new Record();
		r.set("z", 3).set("summary", "http://baidu.com").set("regex", "http://baidu.com").set("id", 29)
				.set("mtime", Timestamp.valueOf("2015-11-17 16:30:09")).set("usr", "405532476501528576")
				.set("test", null);
		Record r2 = RecordKit.sort(r);
		System.out.println(JsonKit.toJson(r));
		System.out.println(JsonKit.toJson(r2));
		System.out.println(RecordKit.toJsonSort(r2));
	}

	@Test
	public void ttd8() {
		Record r = new Record();
		r.set("mtime", Timestamp.valueOf("2015-11-17 16:30:09")).set("z", 3).set("summary", "http://baidu.com")
				.set("regex", "http://bai, ducom=d").set("usr", "405532476501528576").set("test", null).set("id", null);
		System.out.println(r);
		BlacklistEntity r2 = JSON.parseObject(JsonKit.toJson(r), BlacklistEntity.class);
		System.out.println(r2);
		// LinkedHashMap<String, String> jsonMap =
		// JSON.parseObject(JsonKit.toJson(r), new
		// TypeReference<LinkedHashMap<String, String>>() {});
		// for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
		// System.out.println(entry.getKey() + ":" + entry.getValue());
		// }
		Map<String, String> jm = JSON.parseObject(JsonKit.toJson(r), new TypeReference<Map<String, String>>() {
		});
		System.out.println(JsonKit.toJson(r));
		System.out.println(jm);
	}

	@Test
	public void ttd9() {
		Record r = new Record();
		r.set("ck1", "").set("ck2", null);
		System.out.println(r.getStr("ck1"));
		System.out.println(r.getStr("ck2"));
	}

	// @Test
	// public void testSeriaRecord() throws Exception {
	// Record r = new Record();
	// r.set("summary", "http://baidu.com")
	// .set("regex", "http://baidu.com")
	// .set("id", 29).set("mtime", Timestamp.valueOf("2015-11-17 16:30:09"))
	// .set("usr", "405532476501528576").set("test", null);
	// String sr = SeriaRecord.serialize(r);
	// System.out.println(sr);
	// }

	// @Test
	// public void testDeSeriaRecord() throws Exception {
	//
	// }

}
