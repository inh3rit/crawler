package com.zxsoft.crawler.p.analytical;

import java.util.List;
import java.util.Map;

import org.jsoup.select.Elements;

import com.zxsoft.crawler.common.entity.out.RecordInfoEntity;
import com.zxsoft.crawler.common.entity.redis.DetailRuleEntity;
import com.zxsoft.crawler.common.entity.redis.ListRuleEntity;

/**
 * Created by cox on 2015/10/14.
 */
public class AnalyticalDefault implements AnalyticalApi {

	/**
	 * 分析一个搜索结果列表页中的所有数据
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
	 * @param cookie   cookie
	 * @param argMap 可变参数集合
	 * @return List<RecordInfoEntity>
	 */
	public List<RecordInfoEntity> parseInfo(Elements lines, ListRuleEntity lre, DetailRuleEntity dre,
			RecordInfoEntity baseRie, String url, String host, String basePath, Long lastTs, Boolean goInto,
			String cookie, Map<String, Object> argMap) {
		return null;
	}

}
