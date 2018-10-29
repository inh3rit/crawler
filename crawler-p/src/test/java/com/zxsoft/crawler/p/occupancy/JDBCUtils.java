package com.zxsoft.crawler.p.occupancy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtils {

	public static Connection getConn() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection("jdbc:mysql://45.113.71.211:3306/cw_info?user=admin&password=zxsoft0#&useUnicode=true&characterEncoding=UTF8");
//			return DriverManager.getConnection("jdbc:mysql://192.168.32.11:3306/cw_info?user=cwinfo&password=Pwd_cwinf0&useUnicode=true&characterEncoding=UTF8");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void close(Connection conn, Statement stmt, ResultSet rs) {
		try {
			if (null != conn) {
				conn.close();
				conn = null;
			}
			if (null != stmt) {
				stmt.close();
				stmt = null;
			}
			if (null != rs) {
				rs.close();
				rs = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
