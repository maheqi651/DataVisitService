package com.easymap.base.readdatabase;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;

import com.easymap.base.tool.JDBCProperty;
import com.easymap.base.tool.ReadProperties;
import com.easymap.filter.Tools;

public class ConnectionDB {

	private static String DRIVER;
	private static String URLSTR;
	private static String USERNAME;
	private static String USERPASSWORD;
	private static ReadProperties READPROPERTIES;
	private Connection connection = null;
	private PreparedStatement preparedStatement = null;
	private CallableStatement callableStatement = null;
	private ResultSet resultSet = null;
	private static BasicDataSource	ds=new BasicDataSource();
	static {
		try {
			READPROPERTIES = new ReadProperties();
			URLSTR = READPROPERTIES.getUrl();
	 System.out.println("----------------------------------"+URLSTR);
			USERNAME = READPROPERTIES.getUsername();
			USERPASSWORD = READPROPERTIES.getPassword();
			DRIVER = READPROPERTIES.getDriver();
			ds.setDriverClassName(DRIVER);
			ds.setUrl(URLSTR);
			ds.setUsername(USERNAME);
			ds.setPassword(USERPASSWORD);
			ds.setInitialSize(1);
			ds.setMaxActive(50);
			ds.setMaxIdle(20);
			ds.setMaxWait(60000);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public ConnectionDB() {
		
		try {
			connection =ds.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    public static Connection get(String urlstr,String usrname,String pass){
      
    	try {
			return DriverManager.getConnection(urlstr, usrname, pass);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    	
    }
	public ConnectionDB(Connection conn) {
		connection = conn;
	}

	public Connection getConnection() {
		return this.connection;
	}
	//获取链接通过没有权限的
	public Connection getConnectionByWithoutFilter(List<String> tablecodes,String senderid) {
		List<JDBCProperty> jdbcs = READPROPERTIES.getJdbcs();
		Set<JDBCProperty> set = new HashSet<JDBCProperty>();
//		Connection c = null;
		for (int i = 0; i < tablecodes.size(); i++) {
			//System.out.println("----dao--");
			String themeCode="";
			themeCode=(String)new ConnectioSDB().executeQuerySingle( "SELECT T.THEMECODE FROM "+Tools.YW6+".EZ_P_FUNCTION_SERVICE T WHERE T.FUNCCODE=? AND T.TABLECODE=?",
					new Object[] { senderid, tablecodes.get(i)})[0];
			System.out.println(i+"----dao--"+senderid+"  "+ tablecodes.get(i)+" "+themeCode);
			BigDecimal themeId = (BigDecimal) new ConnectioSDB().executeQuerySingle(
					"SELECT ID FROM "+Tools.EZSPATIAL+".EZ_STD_LAYERS_THEME WHERE CODE=?",
					new Object[] { themeCode })[0];
			System.out.println("themeId:"+themeId);
			Object[] obj =  new ConnectioSDB().executeQuerySingle(
					"SELECT DBUSER,DSID FROM "+Tools.EZSPATIAL+".EZ_STD_LAYERS_LAYER_THEME WHERE THEMEID=? AND CODE=?",
					new Object[] { themeId.intValue(), tablecodes.get(i) });
			String conn = (String)  new ConnectioSDB().executeQuerySingle(
					"SELECT CONNECTSTR FROM "+Tools.EZSPATIAL+".EZ_DATASOURCE WHERE ID=?",
					new Object[] { obj[1] })[0];
			System.out.println("conn---------------"+conn);
			String str[] = conn.split(" ");
			for (int j = 0; j < jdbcs.size(); j++) {
				JDBCProperty jdbc = jdbcs.get(j);
				if (jdbc.getUsername().equalsIgnoreCase((String) obj[0])
						&& jdbc.getUrl().toUpperCase()
								.contains(str[1].toLowerCase())
						&& jdbc.getUrl().toUpperCase()
								.contains(str[3].toUpperCase())
						&& jdbc.getUrl().toUpperCase()
								.contains(str[5].toUpperCase()))
					set.add(jdbc);
			}
		}
		try {
			if (set.size() == 1) {
				JDBCProperty jdbc = set.iterator().next();
				System.out.println(jdbc.getUrl());
				System.out.println(jdbc.getUsername());
				System.out.println(jdbc.getPassword());
				return DriverManager.getConnection(jdbc.getUrl(),
						jdbc.getUsername(), jdbc.getPassword());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		return c;
		return null;
	}
	
	public Connection getConnection(List<String> tablecodes,String senderid) {
		List<JDBCProperty> jdbcs = READPROPERTIES.getJdbcs();
		Set<JDBCProperty> set = new HashSet<JDBCProperty>();
//		Connection c = null;
		for (int i = 0; i < tablecodes.size(); i++) {
			//System.out.println("----dao--");
			String themeCode="";
			//SELECT ID FROM EZ_STD_LAYERS_THEME WHERE CODE=?
			//"SELECT T.THEMECODE FROM "+Tools.YW6+".EZ_P_FUNCTION_SERVICE T WHERE T.FUNCCODE=? AND T.TABLECODE=?"
			
			
			
			themeCode=(String)new ConnectioSDB().executeQuerySingle("SELECT T.THEMECODE FROM "+Tools.YW6+".EZ_P_FUNCTION_SERVICE T WHERE T.FUNCCODE=? AND T.TABLECODE=?",
					new Object[] { senderid, tablecodes.get(i)})[0];
			System.out.println(i+"----dao--"+senderid+"  "+ tablecodes.get(i)+" "+themeCode);
			BigDecimal themeId = (BigDecimal) new ConnectioSDB().executeQuerySingle(
					"SELECT ID FROM "+Tools.EZSPATIAL+".EZ_STD_LAYERS_THEME WHERE CODE=?",
					new Object[] { themeCode })[0];
			System.out.println("themeId:"+themeId);
			Object[] obj =  new ConnectioSDB().executeQuerySingle(
					"SELECT DBUSER,DSID FROM "+Tools.EZSPATIAL+".EZ_STD_LAYERS_LAYER_THEME WHERE THEMEID=? AND CODE=?",
					new Object[] { themeId.intValue(), tablecodes.get(i) });
			String conn = (String)  new ConnectioSDB().executeQuerySingle(
					"SELECT CONNECTSTR FROM "+Tools.EZSPATIAL+".EZ_DATASOURCE WHERE ID=?",
					new Object[] { obj[1] })[0];
			System.out.println("conn---------------"+conn);
			String str[] = conn.split(" ");
			for (int j = 0; j < jdbcs.size(); j++) {
				JDBCProperty jdbc = jdbcs.get(j);
				if (jdbc.getUsername().equalsIgnoreCase((String) obj[0])
						&& jdbc.getUrl().toUpperCase()
								.contains(str[1].toLowerCase())
						&& jdbc.getUrl().toUpperCase()
								.contains(str[3].toUpperCase())
						&& jdbc.getUrl().toUpperCase()
								.contains(str[5].toUpperCase()))
					set.add(jdbc);
			}
		}
		try {
			if (set.size() == 1) {
				JDBCProperty jdbc = set.iterator().next();
				System.out.println(jdbc.getUrl());
				System.out.println(jdbc.getUsername());
				System.out.println(jdbc.getPassword());
				return DriverManager.getConnection(jdbc.getUrl(),
						jdbc.getUsername(), jdbc.getPassword());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		return c;
		return null;
	}

	/**
	 * insert update delete SQL 
	 * 
	 * @param sql
	 *            SQL 
	 * @param params
	 *             
	 * @return  
	 */
	public int executeUpdate(String sql, Object[] params) {
		// 鍙楀奖鍝嶇殑琛屾暟
		int affectedLine = 0;

		try {
			// 璋冪敤SQL
			preparedStatement = connection.prepareStatement(sql);

			// 鍙傛暟璧嬪�
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					preparedStatement.setObject(i + 1, params[i]);
				}
			}

			// 鎵ц
			affectedLine = preparedStatement.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			// 閲婃斁璧勬簮
			free(null, preparedStatement, connection);
		}
		return affectedLine;
	}

	/**
	 * 姝ゆ柟娉曟槸鏇挎崲executeQueryRS鏂规硶锛岀‘淇濈▼搴弐obust
	 * @param sql
	 * @param params
	 * @return
	 * @throws IOException 
	 */
	public Object[] executeQueryRS1(String sql, Object[] params)  {
		Object[] object = new Object[2];
		try {
			// 璋冪敤SQL
			preparedStatement = connection.prepareStatement(sql);

			// 鍙傛暟璧嬪�
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					preparedStatement.setObject(i + 1, params[i]);
				}
			}

			// 鎵ц
			resultSet = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = resultSet.getMetaData();
			int count = rsmd.getColumnCount();
			//姝ap鐨勪綔鐢ㄦ槸瀛樻斁ResultSetMetaData涓殑ColumnName鍜屽畠鐨凜olumnType
			Map<String,Integer> map = new HashMap<String,Integer>();

			//姝ist瀛樻斁colName鏁扮粍
			List<Object[]> list = new ArrayList<Object[]>();
			for(int j  = 1 ; j <= count ; j++){
				map.put(rsmd.getColumnName(j), rsmd.getColumnType(j));
			}
			//缁欐暟缁勮祴鍊�
			object[0] = map;
			
			while(resultSet.next()){
				//姝bject鏁扮粍鐨勪綔鐢ㄦ槸瀛樻斁ResultSet涓殑鍊�
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
			//缁欐暟缁勮祴鍊�
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
	 * SQL 鏌ヨ灏嗘煡璇㈢粨鏋�
	 * 
	 * @param sql
	 *            SQL璇彞
	 * @param params
	 *            鍙傛暟鏁扮粍锛岃嫢娌℃湁鍙傛暟鍒欎负null
	 * @return 缁撴灉闆�
	 */
	public Object[] executeQuerySingle(String sql, Object[] params) {
		Object[] object = null;
		try {

			 
//			if(connection.isClosed())
//				connection.
			preparedStatement = connection.prepareStatement(sql);

			// 鍙傛暟璧嬪�
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					preparedStatement.setObject(i + 1, params[i]);
				}
			}

			// 鎵ц
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
	 * 鑾峰彇缁撴灉闆嗭紝骞跺皢缁撴灉鏀惧湪List涓�
	 * 
	 * @param sql
	 *            SQL璇彞
	 * @return List 缁撴灉闆�
	 */
	public List<Object> excuteQuery(String sql, Object[] params) {
		
		// 鍒涘缓List
		List<Object> list = new ArrayList<Object>();
		ResultSetMetaData rsmd = null;
		// 缁撴灉闆嗗垪鏁�
		int columnCount = 0;
		
		try {
//			connection = JUtils.getConnetion();
			// 璋冪敤SQL
			preparedStatement = connection.prepareStatement(sql);

			// 鍙傛暟璧嬪�
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					preparedStatement.setObject(i + 1, params[i]);
				}
			}

			// 鑾峰緱resultSet
			resultSet = preparedStatement.executeQuery();
			rsmd = resultSet.getMetaData();
			// 鑾峰緱缁撴灉闆嗗垪鏁�
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
	 * 瀛樺偍杩囩▼甯︽湁涓�釜杈撳嚭鍙傛暟鐨勬柟娉�
	 * 
	 * @param sql
	 *            瀛樺偍杩囩▼璇彞
	 * @param params
	 *            鍙傛暟鏁扮粍
	 * @param outParamPos
	 *            杈撳嚭鍙傛暟浣嶇疆
	 * @param SqlType
	 *            杈撳嚭鍙傛暟绫诲瀷
	 * @return 杈撳嚭鍙傛暟鐨勫�
	 */
	public Object excuteQuery(String sql, Object[] params, int outParamPos,
			int SqlType) {
		Object object = null;
		try {
			// 璋冪敤瀛樺偍杩囩▼
			callableStatement = connection.prepareCall(sql);

			// 缁欏弬鏁拌祴鍊�
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					callableStatement.setObject(i + 1, params[i]);
				}
			}

			// 娉ㄥ唽杈撳嚭鍙傛暟
			callableStatement.registerOutParameter(outParamPos, SqlType);

			// 鎵ц
			callableStatement.execute();

			// 寰楀埌杈撳嚭鍙傛暟
			object = callableStatement.getObject(outParamPos);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			// 閲婃斁璧勬簮
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

	/**
	 * 鍏抽棴鎵�湁璧勬簮
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