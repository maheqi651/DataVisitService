package com.easymap.dao;

import java.math.BigDecimal;
import com.easymap.base.readdatabase.ConnectionDB;

public class QueryDataDao {
	private ConnectionDB conn;

	public QueryDataDao(ConnectionDB conn) {
		this.conn = conn;
	}

	public String getTableNameByCode(String code) {
		String sql = "SELECT ENNAME FROM EZSPATIAL.EZ_STD_LAYERS_LAYER WHERE CODE='"
				+ code + "'";
		
		System.out.println("查询table"+sql);
		 
		Object[] o = conn.executeQuerySingle(sql, null);
		if (o != null)
			return (String) o[0];
		return null;
	}

	
	public Object[] getQueryData(String tableName, String where,
			String fields, String order, long start, long max) {
		long end = start + max + 1;
		String sql = String.format("SELECT * FROM "
				+ "(SELECT row_.*, ROWNUM rownum_ "
				+ "FROM (SELECT %s FROM %s %s %s ) row_ "
				+ "WHERE ROWNUM < %d) " + "WHERE rownum_ >= %d ", fields,
				tableName, where, order, end, start);
/*		System.out.println("打印的表名："+tableName);
		System.out.println("打印的where："+where);
		System.out.println("打印的fields："+fields);*/
		System.out.println("打印的sql语句:"+sql);
		return conn.executeQueryRS1(sql, null);
	}

	public int getTotalNum(String tableName, String where) {
		int totalCount = 0;
		if("V_WB_SWRY".equals(tableName)){
			totalCount = 2000;
		}else{
			String sql = String.format("SELECT COUNT(*) FROM  %s %s  ", tableName,
					where);
			Object[] o = conn.executeQuerySingle(sql, null);
			totalCount=((BigDecimal) o[0]).intValue();
			
		}
/*		String sql = String.format("SELECT COUNT(*) FROM  %s %s  ", tableName,
				where);
		System.out.println("总记录数:"+sql);
		Object[] o = conn.executeQuerySingle(sql, null);
		return ((BigDecimal) o[0]).intValue();*/
		return totalCount;
	}

	public Object[] getQueryData2(String where, String fields, String order,
			String groupby, String from) {
		String sql = String.format("SELECT %s FROM %s WHERE %s %s %s", fields,
				from, where, groupby, order);
		System.out.println("sql:"+sql);
		return conn.executeQueryRS1(sql, null);
	}

}
