package com.easymap.base.pools;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;


public class JDBC {
	private static  DataSource dataSource= null;
	/*static String driverName="oracle.jdbc.OracleDriver";
	static String url="jdbc:oracle:thin:@172.18.70.49:1521:oradb";
	static String username="EZSPATIAL";
	static String password="EZSPATIAL";*/
	static {
		 try {
			Properties prop = new Properties();
			InputStream is = JDBC.class.getClassLoader().getResourceAsStream("ezspatial.properties");
			prop.load(is);
			dataSource = BasicDataSourceFactory.createDataSource(prop);
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		} 
		/*dataSource=new BasicDataSource();
		dataSource.setDriverClassName(driverName);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setInitialSize(5);
		dataSource.setMaxActive(100);
		dataSource.setMaxIdle(100);
		dataSource.setMaxWait(6000000);
		dataSource.setRemoveAbandoned(true);
		dataSource.setRemoveAbandonedTimeout(300);*/
	}
    public Connection getc() throws SQLException 
    {   Connection connection=null;
    	long times=System.currentTimeMillis();
    	connection=dataSource.getConnection();
        System.out.println("------获取connection--------"+(System.currentTimeMillis()-times));
    	return connection;
    }
	public static Connection getConnection() throws SQLException {
		//return new JDBC().getc();
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
