package com.zxsoft.crawler.p.analytical.networksearch;

import com.jfinal.kit.StrKit;
import com.zxsoft.crawler.common.entity.out.RecordInfoEntity;
import com.zxsoft.crawler.common.entity.redis.DetailRuleEntity;
import com.zxsoft.crawler.common.entity.redis.ListRuleEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.p.analytical.AnalyticalApi;
import name.iaceob.kit.disgest.Disgest;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cox on 2016/1/15.
 */
public class AnalyticalNetworkSearchWeiboTencent implements AnalyticalApi {

	/**
	 * 腾讯微博部分图片直接抓取的链接是缩略图, 通过此方法将其转化为大图链接
	 *
	 * @param url 图片链接地址
	 * @return String
	 */
	private String parseImageUrl(String url) {
		Integer lastF = url.lastIndexOf("/");
		String var2 = url.substring(lastF + 1, url.length());
		if (StrKit.isBlank(var2))
			return url;
		if (!var2.matches("\\d*"))
			return url;
		Integer size = Integer.valueOf(var2);
		String var1 = url.substring(0, lastF);
		size = size < 400 ? 500 : size;
		return var1 + "/" + size;
	}

	/**
	 *
	 * @param lines    列表行
	 * @param lre      列表页规则
	 * @param dre      详细页规则
	 * @param baseRie  最终写入的单条数据基础信息, 这里分析的所有机构应在此对象扩充并放入到 List 中返回
	 * @param url      原始 url 地址 | url host basePath 都是同时传入的, 因为部分站点的链接并非是绝对地址, 而是相对路径, 因此需要分析出绝对地址, 而分析需要依赖此三变量 |
	 * @param host     原始网站主机  | url host basePath 都是同时传入的, 因为部分站点的链接并非是绝对地址, 而是相对路径, 因此需要分析出绝对地址, 而分析需要依赖此三变量 |
	 * @param basePath 原始网站根路径 | url host basePath 都是同时传入的, 因为部分站点的链接并非是绝对地址, 而是相对路径, 因此需要分析出绝对地址, 而分析需要依赖此三变量 |
	 * @param lastTs   最后抓取内容的时间 - 如果需要就传递值
	 * @param goInto   是否进入内页抓取
	 * @param argMap   可变参数集合
	 * @param cookie   cookie
	 * @return List<RecordInfoEntity>
	 */
	@Override
	public List<RecordInfoEntity> parseInfo(Elements lines, ListRuleEntity lre, DetailRuleEntity dre,
			RecordInfoEntity baseRie, String url, String host, String basePath, Long lastTs, Boolean goInto,
			String cookie, Map<String, Object> argMap) {

		List<RecordInfoEntity> lrie = new ArrayList<RecordInfoEntity>();
		for (Element line : lines) {
			RecordInfoEntity rie = new RecordInfoEntity();
			rie.merge(baseRie);

			Elements lineUrls = line.select(lre.getUrldom());
			if (CollectionKit.isEmpty(lineUrls))
				continue;
			Element lineUrlEle = lineUrls.size() > 1 ? lineUrls.get(lineUrls.size() - 1) : lineUrls.first();
			Elements urlEle = lineUrlEle.getElementsByTag("a");
			// 去到链接的标签
			if (CollectionKit.isEmpty(urlEle))
				continue;
			String postUrl = urlEle.attr("href").trim();

			/* 日期 */
			rie.setTimestamp(0L);
			Long date = 0L;
			Elements dates = line.select(lre.getDatedom());
			if (CollectionKit.notEmpty(dates)) {
				date = Long.parseLong(
						dates.size() > 1 ? dates.get(dates.size() - 1).attr("rel") : dates.first().attr("rel"));
				rie.setTimestamp(date * 1000L);
			}

			/* 图片 */
			Elements imgs = line.select("img");
			StringBuilder pics = new StringBuilder();
			for (Integer i = imgs.size(); i-- > 0;) {
				String piu = this.parseImageUrl(imgs.get(i).attr("crs"));
				if (StrKit.isBlank(piu))
					continue;
				pics.append(piu).append(i - 1 >= 0 ? "," : "");
			}

			Elements uns = line.select(lre.getStr("usernamedom"));
			Elements nns = line.select(lre.getStr("nicknamedom"));
			String username, nickname;
			if (uns != null && !uns.isEmpty()) {
				username = uns.get(0).attr("rel");
				nickname = nns.text();
			} else {
				Elements unns = line.select(lre.getStr("unamedom2"));
				if (unns == null || unns.isEmpty()) {
					// 如果无法获取到用户名以及昵称, 则抛弃当前这条
					continue;
				}
				String[] us = unns.get(0).attr("href").split("/");
				username = us[us.length - 1];
				nickname = unns.get(0).text();
			}
			rie.setNickname(nickname);
			rie.setUsername(username);

			rie.setPicUrl(pics.toString());
			rie.setUrl(postUrl);
			rie.setContent(line.select(lre.getSynopsisdom()).text());
			rie.setId(DigestUtils.md5Hex(rie.getUrl().trim()).toUpperCase());

			if (StrKit.isBlank(rie.getContent())) {
				rie.setContent("");
				if (StrKit.notBlank(lre.getStr("replycntdom"))) {
					String replymsgcnt = line.select(lre.getStr("replycntdom")).text();
					replymsgcnt = StrKit.isBlank(replymsgcnt) ? line.select(lre.getStr("replyurl")).attr("href")
							: replymsgcnt;
					replymsgcnt = "转发: " + replymsgcnt;
					rie.setContent(replymsgcnt);
				}
			}

			if (rie.getTimestamp() == 0L)
				continue;
			rie.setLasttime(System.currentTimeMillis());

			lrie.add(rie);
		}
		return lrie;
	}
}
