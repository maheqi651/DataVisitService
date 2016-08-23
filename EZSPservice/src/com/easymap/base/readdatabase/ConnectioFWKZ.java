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

import com.easymap.base.tool.ReadProperties;

public class ConnectioFWKZ {

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
			ds.setInitialSize(1);
			ds.setMaxActive(5);
			ds.setMaxIdle(3);
			//ds.setMinIdle(5);
			ds.setMaxWait(60000);
		
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public ConnectioFWKZ() {
		try {
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
	 * 此方法是替换executeQueryRS方法，确保程序robust
	 * @param sql
	 * @param params
	 * @return
	 * @throws IOException 
	 */
	public List<Map<String,Object>> executeQueryMAP(String sql, Object[] params)  {
		//保存键值对
		List<Map<String,Object>> listmap=new ArrayList<Map<String,Object>>();
		Object[] object = new Object[2];
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		System.out.println(sql);
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
			ResultSetMetaData rsmd = resultSet.getMetaData();
			int count = rsmd.getColumnCount();
			//Map<String,Integer> map = new HashMap<String,Integer>();
			List<Object[]> list = new ArrayList<Object[]>();
			String[] temp=new String[count];
			for(int j  = 1 ; j <= count ; j++){
				//map.put(rsmd.getColumnName(j), rsmd.getColumnType(j));
				temp[j-1]=rsmd.getColumnName(j);
				//System.out.println(rsmd.getColumnName(j));
			}
			//给数组赋值
			/* for(String s:temp)
			 {
				 System.out.println(s);
			 }*/
			while(resultSet.next()){
				Map<String,Object> resultmap=new HashMap<String,Object>();
				Object[] colName = new Object[count];
				for(int i = 1 ; i <= count ; i++){
						resultmap.put(temp[i-1], resultSet.getObject(i));
				}
				//list.add(colName);
				listmap.add(resultmap);
			}
			
			//给数组赋值
			//object[1] = list;
			//System.out.println("执行查询条件"+(System.currentTimeMillis()-times));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			free(resultSet, preparedStatement, connnection);
		}
		//System.out.println("执行查询条件-----2222-------"+(System.currentTimeMillis()-times));
		return listmap;
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
	
	
}