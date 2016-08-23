package com.easymap.base.readdatabase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.easymap.base.tool.ReadProperties;
 

public class ConnectioSDB {
    private static Logger loggers=Logger.getLogger(ConnectioSDB.class);
	private static String DRIVER;
	private static String URLSTR;
	private static String USERNAME;
	private static String USERPASSWORD;
	private static ReadProperties READPROPERTIES;
	private Connection connnection = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private static BasicDataSource	ds=new BasicDataSource();
	static {
		try {
			READPROPERTIES = new ReadProperties();
			URLSTR = READPROPERTIES.getUrl();
			USERNAME = READPROPERTIES.getUsername();
			USERPASSWORD = READPROPERTIES.getPassword();
			DRIVER = READPROPERTIES.getDriver();
			Class.forName(DRIVER);
			
			ds.setDriverClassName(DRIVER);
			ds.setUrl(URLSTR);
			ds.setUsername(USERNAME);
			ds.setPassword(USERPASSWORD);
			ds.setInitialSize(20);
			ds.setMaxActive(100);
			ds.setMaxIdle(30);
			ds.setMaxWait(100000);
		    ds.setRemoveAbandoned(true);
		    ds.setRemoveAbandonedTimeout(10000);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public ConnectioSDB() {
		try {
			//System.out.println("SYSOT目前连接："+ds.getNumActive()+"	空闲连接数："+ds.getNumIdle());
			//loggers.info("目前连接："+ds.getNumActive()+"	空闲连接数："+ds.getNumIdle());
			connnection = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * SQL 查询将查询结果
	 * 
	 * @param sql
	 *            SQL语句
	 * @param params
	 *            参数数组，若没有参数则为null
	 * @return 结果集
	 */
	public Object[] executeQuerySingle(String sql, Object[] params) {
		System.out.println("SYSOT2目前连接："+ds.getNumActive()+"	空闲连接数："+ds.getNumIdle());
		loggers.info("目前连接："+ds.getNumActive()+"	空闲连接数："+ds.getNumIdle()+"  sql:"+sql);
		Object[] object = null;
		try {
			// 调用SQL
			preparedStatement = connnection.prepareStatement(sql);

			// 参数赋值
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					preparedStatement.setObject(i + 1, params[i]);
				}
			}

			// 执行
			resultSet = preparedStatement.executeQuery();
			object = new Object[resultSet.getMetaData().getColumnCount()];
			if (resultSet.next()) {
				for (int i = 0; i < object.length; i++) {
					object[i] = resultSet.getObject(i + 1);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			free(resultSet, preparedStatement, connnection);
		}

		return object;
	}


	/**
	 * 关闭所有资源
	 */
	public static void free(ResultSet rs,PreparedStatement ps,Connection conn){
		try{
			if(rs != null){
				rs.close();
				rs = null;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				if(ps != null){
					ps.close();
					ps = null;
				}
			}catch(SQLException e){
				e.printStackTrace();
			}finally{
				if(conn != null){
					try{
					conn.close();
					conn = null;
					}catch(SQLException e){
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	public static void main(String args[]){
		
		loggers.info("111111");
		
		loggers.debug("debug222");
		
	}
	
}