package com.easymap.base.pools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ConnectionDB {

	private Connection connection = null;

	public ConnectionDB(Connection conn) {
		connection = conn;
	}

	public Connection getConnection() {
		return this.connection;
	}

	
	/**
	 * insert update delete SQL语句的执行的统一方法
	 * 
	 * @param sql
	 *            SQL语句
	 * @param params
	 *            参数数组，若没有参数则为null
	 * @return 受影响的行数
	 */
	public int executeUpdate(String sql, Object[] params) {
		// 受影响的行数
		int affectedLine = 0;
		PreparedStatement preparedStatement = null;
		try {
			// 调用SQL
			preparedStatement = connection.prepareStatement(sql);

			// 参数赋值
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					preparedStatement.setObject(i + 1, params[i]);
				}
			}

			// 执行
			affectedLine = preparedStatement.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			// 释放资源
			free(null, preparedStatement, connection);
		}
		return affectedLine;
	}

	/**
	 * 此方法是替换executeQueryRS方法，确保程序robust
	 * @param sql
	 * @param params
	 * @return
	 * @throws IOException 
	 */
	public Object[] executeQueryRS1(String sql, Object[] params)  {
		//long times=System.currentTimeMillis();
		Object[] object = new Object[2];
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			// 调用SQL
			preparedStatement = connection.prepareStatement(sql);

			// 参数赋值
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					preparedStatement.setObject(i + 1, params[i]);
				}
			}

			// 执行
			resultSet = preparedStatement.executeQuery();
			//System.out.println("执行查询条件11111"+(System.currentTimeMillis()-times));
			ResultSetMetaData rsmd = resultSet.getMetaData();
			int count = rsmd.getColumnCount();
			//此Map的作用是存放ResultSetMetaData中的ColumnName和它的ColumnType
			Map<String,Integer> map = new HashMap<String,Integer>();

			//此list存放colName数组
			List<Object[]> list = new ArrayList<Object[]>();
			for(int j  = 1 ; j <= count ; j++){
				map.put(rsmd.getColumnName(j), rsmd.getColumnType(j));
			}
			//给数组赋值
			object[0] = map;
			
			while(resultSet.next()){
				//此Object数组的作用是存放ResultSet中的值
				Object[] colName = new Object[count];
				for(int i = 1 ; i <= count ; i++){
					if(resultSet.getObject(i)!=null && resultSet.getObject(i).toString().length()>16 && resultSet.getObject(i).toString().substring(0, 16).equals("oracle.sql.CLOB@"))
					{
						char[] c = new char[(int) ((Clob) resultSet.getObject(i)).length()];
						((Clob) resultSet.getObject(i)).getCharacterStream().read(c);
						colName[i - 1]= new String(c);
					}else if(resultSet.getObject(i)!=null && resultSet.getObject(i).toString().length()>16 &&resultSet.getObject(i).toString().substring(0, 16).equals("oracle.sql.BLOB@")){
						oracle.sql.BLOB blob = (oracle.sql.BLOB) resultSet.getBlob(i);
						InputStream is = blob.getBinaryStream();
						int length = (int) blob.length();
						byte[] data = new byte[length];
						is.read(data);
						is.close();
						BASE64Encoder  encoder = new BASE64Encoder(); 
						colName[i - 1]=encoder.encode(data);
					}else{
						colName[i - 1] = resultSet.getObject(i);
					}
				}
				list.add(colName);
			}
			
			//给数组赋值
			object[1] = list;
			//System.out.println("执行查询条件"+(System.currentTimeMillis()-times));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			free(resultSet, preparedStatement, connection);
		}
		//System.out.println("执行查询条件-----2222-------"+(System.currentTimeMillis()-times));
		return object;
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
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;		
		try {
			// 调用SQL
			preparedStatement = connection.prepareStatement(sql);

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
			free(resultSet, preparedStatement, connection);
		}

		return object;
	}

	/**
	 * 获取结果集，并将结果放在List中
	 * 
	 * @param sql
	 *            SQL语句
	 * @return List 结果集
	 */
	public List<Object> excuteQuery(String sql, Object[] params) {
		// 创建List
		List<Object> list = new ArrayList<Object>();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;	
		ResultSetMetaData rsmd = null;
		// 结果集列数
		int columnCount = 0;
		
		try {
//			connection = JUtils.getConnetion();
			// 调用SQL
			preparedStatement = connection.prepareStatement(sql);

			// 参数赋值
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					preparedStatement.setObject(i + 1, params[i]);
				}
			}

			// 获得resultSet
			resultSet = preparedStatement.executeQuery();
			rsmd = resultSet.getMetaData();
			// 获得结果集列数
			columnCount = rsmd.getColumnCount();
			while(resultSet.next()){
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					map.put(rsmd.getColumnLabel(i), resultSet.getObject(i));
				}
				list.add(map);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			free(resultSet, preparedStatement, connection);
		}
		return list;
	}

	/**
	 * 存储过程带有一个输出参数的方法
	 * 
	 * @param sql
	 *            存储过程语句
	 * @param params
	 *            参数数组
	 * @param outParamPos
	 *            输出参数位置
	 * @param SqlType
	 *            输出参数类型
	 * @return 输出参数的值
	 */
	public Object excuteQuery(String sql, Object[] params, int outParamPos,
			int SqlType) {
		Object object = null;
		CallableStatement callableStatement = null;
		try {
			// 调用存储过程
			callableStatement = connection.prepareCall(sql);

			// 给参数赋值
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					callableStatement.setObject(i + 1, params[i]);
				}
			}

			// 注册输出参数
			callableStatement.registerOutParameter(outParamPos, SqlType);

			// 执行
			callableStatement.execute();

			// 得到输出参数
			object = callableStatement.getObject(outParamPos);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			// 释放资源
			try{
				if(callableStatement != null){
					callableStatement.close();
					callableStatement = null;
				}
			}catch(SQLException e){
				e.printStackTrace();
			}finally{
				if(connection != null){
					try{
						connection.close();
						connection = null;
					}catch(SQLException e){
						e.printStackTrace();
					}
				}
			}
		}

		return object;
	}
	
	public Object[] getQueryData(String tableName, String where,
			String fields, String order, long start, long max) {
		System.out.println(start+"----"+max);
		long end = start + max;
		String sql = String.format("SELECT * FROM "
				+ "(SELECT row_.*, ROWNUM rownum_ "
				+ "FROM (SELECT %s FROM %s %s %s ) row_ "
				+ "WHERE ROWNUM <= %d) " + "WHERE rownum_ > %d ", fields,
				tableName, where, order, end, start);
 		System.out.println("打印的order:"+order);
	 System.out.println("打印的sql语句:"+sql);
		return executeQueryRS1(sql, null);
	}
	
	public int getTotalNum(String tableName, String where) {
		int totalCount = 0;
		if("V_WB_SWRY".equals(tableName)){
			totalCount = 2000;
		}else if("KK_KKXX".equals(tableName)){
			totalCount = 2000;
		}else{
			String sql = String.format("SELECT COUNT(*) FROM  %s %s  ", tableName,
					where);
			//System.out.println("---------qq--------"+sql);
			Object[] o = executeQuerySingle(sql, null);
			totalCount=((BigDecimal) o[0]).intValue();
		}
		return totalCount;
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