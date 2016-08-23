package com.easymap.base.pools;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.easymap.base.readdatabase.ConnectioSDB;
import com.easymap.base.tool.JDBCProperty;
import com.easymap.base.tool.ReadProperties;
import com.easymap.filter.Tools;

public class ConnectionSDB {


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
		Connection conncetion = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			// 调用SQL
		   // long times=System.currentTimeMillis();
			conncetion = JDBC.getConnection();
			//System.out.println("获取connection时间"+(System.currentTimeMillis()-times));
			preparedStatement = conncetion.prepareStatement(sql);
			// 参数赋值
			if (params != null) {
				for(int i = 0; i < params.length; i++) {
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
			free(resultSet,preparedStatement,conncetion);
		}
		
		return object;
	}
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
	
	
	public JDBCProperty getJDBCProperty(List<String> tablecodes,String themeCode) {
		ReadProperties READPROPERTIES = new ReadProperties();
		List<JDBCProperty> jdbcs = READPROPERTIES.getJdbcs();
		Set<JDBCProperty> set = new HashSet<JDBCProperty>();
		//System.out.println("------------themecode-------"+themeCode);
		for (int i = 0; i < tablecodes.size(); i++) {
			BigDecimal themeId = (BigDecimal)executeQuerySingle(
					"SELECT ID FROM EZSPATIAL.EZ_STD_LAYERS_THEME WHERE CODE=?",
					new Object[] { themeCode })[0];
			Object[] obj =  executeQuerySingle(
					"SELECT DBUSER,DSID FROM EZSPATIAL.EZ_STD_LAYERS_LAYER_THEME WHERE THEMEID=? AND CODE=?",
					new Object[] { themeId.intValue(), tablecodes.get(i) });
			System.out.println(themeId.intValue()+"++++++++"+tablecodes.get(i));//01010200008
			String conn = (String) executeQuerySingle(
					"SELECT CONNECTSTR FROM EZSPATIAL.EZ_DATASOURCE WHERE ID=?",
			new Object[] { obj[1] })[0];
		    System.out.println(obj[0]+"-"+obj[1]+"--"+conn);
			String str[] = conn.split(" ");
	    	//System.out.println(str[6].toLowerCase());
			for (int j = 0; j < jdbcs.size(); j++) {
				JDBCProperty jdbc = jdbcs.get(j);
				if ( jdbc.getUsername().equalsIgnoreCase(((String) obj[0]))//((String) obj[0])
						&&  jdbc.getUrl().toUpperCase()
								.contains(str[1].toLowerCase())
						&& jdbc.getUrl().toUpperCase()
								.contains(str[3].toUpperCase())
						&& jdbc.getUrl().toUpperCase()
								.contains(str[5].toUpperCase()))
					{
					set.add(jdbc);
					break;
					}
			}
		}
		JDBCProperty jdcp = null;
		if (set.size() >= 1) {
			jdcp = set.iterator().next();
		}
		//System.out.println("0000------------"+jdcp.getUrl()+""+jdcp.getUsername()+""+jdcp.getPassword());
		return jdcp;
	}
	//获取EZAPATIAL表面
	public String getTableNameByCode(String code) {
		String sql = "SELECT ENNAME FROM  EZSPATIAL.EZ_STD_LAYERS_LAYER WHERE CODE='"
				+ code + "'";
		long times=System.currentTimeMillis();
		Object[] o = executeQuerySingle(sql, null);
		  System.out.println("执行查询表时间："+(System.currentTimeMillis()-times));
		if (o != null)
			return (String) o[0];
		return null;
	}
	
	
}