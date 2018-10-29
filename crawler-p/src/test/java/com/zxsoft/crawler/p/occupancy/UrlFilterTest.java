package com.zxsoft.crawler.p.occupancy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.junit.Test;


public class UrlFilterTest {

	@Test
	public void filterUrl() {
		WebnetCollector collector = new WebnetCollector();
		List<UrlEntity> newUrlList = new ArrayList<UrlEntity>();

		List<String> currUrlList = getUrlsFromDB();
		List<UrlEntity> collectedUrlList = collector.collectBySearchEngine();
		// List<UrlEntity> collectedUrlList = collector.collectByRule();

		for (UrlEntity entity : collectedUrlList) {
			System.out.println(entity.getDomain());
			HttpEntity httpEntity = null;
			try {
				httpEntity = HttpKit.get(
						entity.getDomain().contains("http://") ? entity.getDomain() : "http://" + entity.getDomain());
			} catch (Exception e) {
				System.out.println("无法访问!");
				continue;
			}
			// 返回值==200
			if (200 != httpEntity.getResponseCode())
				continue;

			// 数据库中不存在
			boolean isContains = false;
			for (String currUrl : currUrlList) {
				if (purge(currUrl).equals(purge(httpEntity.getDomain())))
					isContains = true;
			}
			if (!isContains)
				newUrlList.add(entity);
		}

		insert(newUrlList);
	}

	private List<UrlEntity> getUrlsFromText(String filePath) {
		List<UrlEntity> urlList = new ArrayList<UrlEntity>();
		String text = FileUtil.read(filePath);
		for (String entity : text.split("\n")) {
			String url = entity.split(",")[0];
			String type = entity.split(",")[1];
			urlList.add(new UrlEntity(url, Integer.parseInt(type)));
		}
		return urlList;
	}

	private List<String> getUrlsFromDB() {
		List<String> domainList = new ArrayList<String>();
		String querySql = "select domain from blanklist";

		Connection conn = JDBCUtils.getConn();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(querySql);
			rs = stmt.executeQuery();

			while (rs.next()) {
				domainList.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return domainList;
	}

	/**
	 * 净化url,清除url中的干扰项
	 * @param str
	 * @return
	 */
	private String purge(String str) {
		return str.replace("http://", "").replace("www.", "").split("/")[0];
	}

	/**
	 * 批量插入(语句拼接)
	 * @param urlList
	 */
	private void insert(List<UrlEntity> urlList) {
		int i = 0;
		int cnt = 0;
		StringBuffer sb = new StringBuffer();
		if (urlList.size() == 0)
			return;
		for (UrlEntity urlEntity : urlList) {
			if (i != 0)
				sb.append(",");
			sb.append("('").append(urlEntity.getDomain()).append("',").append(urlEntity.getType()).append(")");

			i++;
			cnt++;

			if (cnt == urlList.size() - 1 || i > 50) {
				insert(sb.toString());
				i = 0;
				sb = new StringBuffer();
			}
		}

	}

	/**
	 * 批量插入
	 * @param values
	 */
	private void insert(String values) {
		String insSql = "insert into blanklist(domain,type) values" + values;

		Connection conn = JDBCUtils.getConn();
		PreparedStatement stmt = null;
		int cnt = 0;
		try {
			stmt = conn.prepareStatement(insSql);
			cnt = stmt.executeUpdate();

			System.out.println(cnt);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
