package com.zxsoft.crawler.p.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.inh3rit.httphelper.common.HttpConst;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.entity.ProxyEntity;
import org.inh3rit.httphelper.kit.HttpKit;
import org.junit.Test;

public class ProxyTest {

	@Test
	// proxy
	// TODO cookie
	public void test() {
		List<Map<String, String>> confList = getConf();
		for (Map<String, String> conf : confList) {
			String url = conf.get("originUrl");
			Map<String, String> paras = new HashMap<String, String>();
			Map<String, String> header = new HashMap<String, String>();
			ProxyEntity proxy = new ProxyEntity("192.168.25.254", 28129, "yproxyq", "zproxyx0#");

			HttpEntity httpEntity = null;
			try {
				httpEntity = HttpKit.get(url, paras, header, proxy, HttpConst.DEF_CHARSET, HttpConst.DEF_TIMEOUT,
						HttpConst.DEF_SOTIMEOUT);
			} catch (Exception e) {
				System.out.printf("url : %s, error : %s", url, e.getCause());
				System.out.println();
				continue;
			}

			System.out.printf("url : %s, reponsecode : %d", url, httpEntity.getResponseCode());
			System.out.println();
		}
	}

	public static List<Map<String, String>> getConf() {
		String queryConfSql = "select cd.host,cl.url,cl.listdom,cl.linedom,cl.urldom from conf_list cl left join conf_detail cd on cl.url=cd.listurl";
		Connection conn = JDBCUtil.getConn();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Map<String, String>> confList = new ArrayList<Map<String, String>>();

		try {
			stmt = conn.prepareStatement(queryConfSql);
			rs = stmt.executeQuery();

			while (rs.next()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("host", rs.getString(1));
				map.put("originUrl", rs.getString(2));
				map.put("listdom", rs.getString(3));
				map.put("linedom", rs.getString(4));
				map.put("urldom", rs.getString(5));

				confList.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return confList;
	}
}
