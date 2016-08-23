package com.easymap.datacenter.util;
  
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;

public class DBConnection {
	/**
	 * 连接数据源
	 * @return Connection
	 * @throws Exception
	 */
	
	public static ConnectionDB getConnection(String DB_dbuser, String DB_dbpwd) throws Exception {
		String DB_driver = Constant.DB_driver;
		String DB_url = Constant.DB_url;
		
		return new ConnectionDB(DB_driver, DB_url, DB_dbuser, DB_dbpwd);
	}
	
	public static ConnectionDB getConnection() throws Exception {
		String DB_driver = Constant.DB_driver;
		String DB_url = Constant.DB_url;
		String DB_dbuser = Constant.DB_dbuser;
		String DB_dbpwd = Constant.DB_dbpwd;
		
		return new ConnectionDB(DB_driver, DB_url, DB_dbuser, DB_dbpwd);
	}
	
	public static ConnectionDB getEzspatialConnection() throws Exception {
		String DB_driver = Constant.DB_driver;
		String DB_url = Constant.DB_url;
		String DB_dbuser = Constant.EZSPATIAL_dbuser;
		String DB_dbpwd = Constant.EZSPATIAL_pwd;
		
		return new ConnectionDB(DB_driver, DB_url, DB_dbuser, DB_dbpwd);
	}
	
	public static ConnectionDB getDEConnection() throws Exception {
		//String DB_driver = Constant.DB_driver;
		//String DB_url = Constant.DB_url;
		String DB_driver = Constant.DE_DRIVER;
		String DB_url = Constant.DE_URL;
		String DB_dbuser = Constant.DE_USER;
		String DB_dbpwd = Constant.DE_PASSW0RD;
		
		return new ConnectionDB(DB_driver, DB_url, DB_dbuser, DB_dbpwd);
	}
	
	public static ConnectionDB getEzManagerConnection() throws Exception {
		String DB_driver = Constant.EZ_DRIVER;
		String DB_url = Constant.EZ_URL;
		String DB_dbuser = Constant.EZ_USER;
		String DB_dbpwd = Constant.EZ_PWD;
		
		return new ConnectionDB(DB_driver, DB_url, DB_dbuser, DB_dbpwd);
	}
	
	public static void free(Connection conn, PreparedStatement ps, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();
					ps = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
