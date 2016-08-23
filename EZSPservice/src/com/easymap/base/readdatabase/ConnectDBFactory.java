package com.easymap.base.readdatabase;

import java.sql.Connection;
import java.sql.DriverManager;

import com.easymap.base.tool.ReadProperties;


public class ConnectDBFactory {
	public static Connection getConnection() throws Exception {
		ReadProperties readProperties = new ReadProperties(); 
		 String url = readProperties.getUrl();
		 String user = readProperties.getUsername();
		 String password = readProperties.getPassword();
		 String driver =readProperties.getDriver();
		
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, user, password);
		if (conn.isClosed()) {
			throw new Exception("Jndi Connection is closed.");
		}
		return conn;
	}
 }
