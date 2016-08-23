package com.easymap.base.test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;


/**
 * 
 * @author xiao
 *注册驱动，并获取注册
 * 
 */
public final class JUtils {
	private static DataSource dataSource = null;


	public static Connection getConnection(String url) throws SQLException {
		Properties prop = new Properties();
		InputStream is = JUtils.class.getClassLoader().getResourceAsStream(url);
		try {
			prop.load(is);
			dataSource = BasicDataSourceFactory.createDataSource(prop);
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return dataSource.getConnection();
	}
	/**
	 * 关闭连接
	 * @param conn
	 * @param ps
	 * @param rs
	 */
	public static void free(Connection conn, PreparedStatement ps, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if (ps != null) {
					ps.close();
					ps = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
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
