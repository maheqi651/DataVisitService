package com.easymap.datacenter.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Clob;
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

import com.easymap.datacenter.model.ModelMappingNode;


public class ConnectionDB {

	private String DB_driver = Constant.DB_driver;
	private String DB_url = Constant.DB_url;
	private String DB_dbuser = Constant.DB_dbuser;
	private String DB_dbpwd = Constant.DB_dbpwd;
	
	private Connection connection = null;
	
	public ConnectionDB(String driver, String url, String dbuser, String dbpwd) {
		DB_driver = driver;
		DB_url = url;
		DB_dbuser = dbuser;
		DB_dbpwd = dbpwd;
	}
	
	public Connection getConnection(){		
		try {
			//判断有问题			
			//if(conn != null)
			//应该判断连接是否已关闭
			if(connection != null && !connection.isClosed())
     				return connection;
			
			Class.forName(DB_driver).newInstance();
			connection = DriverManager.getConnection(DB_url, DB_dbuser, DB_dbpwd);
			
			if(connection.isClosed() || connection==null)
				throw new Exception("获取数据库连接失败！");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return connection;
	}

//	public ConnectionDB(Connection conn) {
//		connection = conn;
//	}
	
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
			getConnection();
			
			// 调用SQL
			preparedStatement = connection.prepareStatement(sql);

			// 参数赋值
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					preparedStatement.setObject(i + 1, params[i]);
				}
			}

			// 执行
			affectedLine  = preparedStatement.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			// 释放资源
			free(null, preparedStatement, connection);
		}
		return affectedLine;
	}
	
	//批量更新
	public int executeBatchUpdate(String sql, Object[][] params) {
		// 受影响的行数
		int affectedLine = 0;
		int []affectedLineArray;
		PreparedStatement preparedStatement = null;
		try {
			getConnection();
			
			connection.setAutoCommit(false);
			
			// 调用SQL
			preparedStatement = connection.prepareStatement(sql);

			// 参数赋值
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					for (int j=0; j<params[i].length; j++)
					{
						preparedStatement.setObject(j + 1, params[i][j]);
					}
					preparedStatement.addBatch();
				}
			}
			
			// 执行
			affectedLineArray = preparedStatement.executeBatch();
			for (int line:affectedLineArray){
				affectedLine += line;
			}
			
			connection.commit();

			// 执行
			//affectedLine = preparedStatement.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			// 释放资源
			free(null, preparedStatement, connection);
		}
		return affectedLine;
	}
	
	public static void main(String []args){
		List<ModelMappingNode> NodeList = new ArrayList<ModelMappingNode>();
		ModelMappingNode node = new ModelMappingNode();
		node.ModelName = "huye";
		node.ViewName = "tiger";
		node.TotalFieldName = "fsdhfd.gds.gdsf";
		node.TotalPropertyName = "fdsf.trteg.gsdfsd.fsdf";
		NodeList.add(node);
		
		ModelMappingNode node2 = new ModelMappingNode();
		node2.ModelName = "huye2";
		node2.ViewName = "tiger2";
		node2.TotalFieldName = "fsdhfd.gds.gdsf2";
		node2.TotalPropertyName = "fdsf.trteg.gsdfsd.fsdf2";
		NodeList.add(node2);
		
		int count = 0;
		
		String sql = "";
		String basic_sql = "insert into PIEM_MODEL_MAPPING(TableName,Fieldname, Modelname, Propertyname) values(?,?,?,?)";
		Object[][] params = new Object[NodeList.size()][4];
		try {
			ConnectionDB conn = DBConnection.getConnection();
			sql = basic_sql;
			for (int i = 0; i < params.length; i++) {
				params[i][0] = NodeList.get(i).ViewName;
				params[i][1] = NodeList.get(i).TotalFieldName;
				params[i][2] = NodeList.get(i).ModelName;
				params[i][3] = NodeList.get(i).TotalPropertyName;
			}
			
			count = conn.executeBatchUpdate(sql, params);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(count);
	}

	/**
	 * 此方法是替换executeQueryRS方法，确保程序robust
	 * @param sql
	 * @param params
	 * @return
	 * @throws IOException 
	 */
	public Object[] executeQueryRS1(String sql, Object[] params)  {
		Object[] object = new Object[2];
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			getConnection();
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
					}else{
						colName[i - 1] = resultSet.getObject(i);
					}
				}
				list.add(colName);
			}
			//给数组赋值
			object[1] = list;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			free(resultSet, preparedStatement, connection);
		}

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
			getConnection();
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
			getConnection();
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
			getConnection();
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
		long end = start + max + 1;
		String sql = String.format("SELECT * FROM "
				+ "(SELECT row_.*, ROWNUM rownum_ "
				+ "FROM (SELECT %s FROM %s %s %s ) row_ "
				+ "WHERE ROWNUM < %d) " + "WHERE rownum_ >= %d ", fields,
				tableName, where, order, end, start);
//		System.out.println("打印的order:"+order);
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