package com.easymap.datacenter.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.easymap.datacenter.util.CheckThead;
import com.easymap.datacenter.util.Constant;
import com.easymap.datacenter.util.DBConnection;
import com.google.gson.Gson;



//import test.TestDBconnection;



public class CheckOracl {

	
	//public static int topsize = 5;
	public static boolean ifrefine = true;
	
	public static Result result = new Result(); //对标返回的结果
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, FileNotFoundException {	
		String url = "";	
		String username = "";
		String password = "";
		String tablename = "";
		int asystemid = 9;
		boolean iflocalDebug = false;	

		url ="jdbc:oracle:thin:@10.235.36.172:1521:gzdb011";
		username = "jc_bazy_g";
		password = username;
		tablename = "SELECTEDMETA";

		if(iflocalDebug)
		{
			url ="jdbc:oracle:thin:@zhang_xiantao.HOLD.FOUNDER.COM:1521:orcl";
			username = "zxt";
			password = "123";
			tablename = "test";
//			 url = "jdbc:oracle:" + "thin:@172.18.68.188:1521:ORADB";
//			 username = "EZSPATIAL";
//			 password = "EZSPATIAL";	
		} else {
			 url = "jdbc:oracle:thin:@10.235.36.174:1521:gzdb021";
			 username = "GZ_APP_BZH";
			 password = "GZ_APP_BZH";	
		}
		
		// 单表比对
		//CheckTheOracl(url, username, password, tablename, asystemid);
		
		
		//全库比对
		 result = CheckOracl.CheckDataMeta_old(url, username, password, asystemid);
		 Gson gson = new Gson();
		 System.out.println("全库比对的结果为:------------------------------------:"+gson.toJson(result));
	}
	
	


	public static void WriteSystemTableMeta
	(List<SystemTableMeta> alist,String url,String username,String password,String tablename) throws SQLException, ClassNotFoundException{
			
		if(alist == null || alist.size()==0){
			System.err.println("alist size is null or 0" );
			return ;
		}

		float allsize = 0;
		float  rightsize = 0;
		String bmc = "";
		int xid = 0;
		
		PreparedStatement pst = null;
		Connection con =  null;
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			 con = DriverManager.getConnection(url, username,
					password);
			con.setAutoCommit(false);// 关闭自动提交
			
			StringBuffer sql = new StringBuffer();
			sql.append("insert into "+tablename+
					"(zdmc,gsbm,xtid,bmc,zdbs,zdcd,zdlx,ppjg,ppmb,tjsjy) values(?,?,?,?,?,?,?,?,?,?)");
	//		System.out.println("ok connected");
		
			pst = con.prepareStatement(sql.toString());

		
			for(int i=0;i<alist.size();i++){
	//			System.out.println("insert " +alist.get(i).a+":"+alist.get(i).b+" "+numlist.get(i));
				pst.setString(1, alist.get(i).zdmc);
				pst.setString(2, alist.get(i).gsbm);
				pst.setInt(3, alist.get(i).xtid);
				pst.setString(4, alist.get(i).bmc);
				pst.setString(5, alist.get(i).zdbs);
				pst.setString(6, alist.get(i).zdcd);
				pst.setString(7, alist.get(i).zdlx);
				
				pst.setString(8, alist.get(i).ppjg);

				
//				String temp = alist.get(i).amd.Code.source;
//				if(temp==null||temp.equals(""))
//					temp = null;// 无法满足要求
				String temp = alist.get(i).ppjg;
				String pipei = "数据元";
				if(temp!=null){
					int bid = temp.indexOf(pipei);
					int eid = temp.indexOf("(");
					if(bid>0&&bid<eid)
						temp = temp.substring(bid+pipei.length(),eid);
					else 
						temp = null;
				}
				pst.setString(9, temp);
				pst.setString(10, alist.get(i).Gettjsjy(ifrefine));
				
				
				if(alist.get(i).ppjg!=null && alist.get(i).ppjg.startsWith("完全"))
					rightsize++;
				allsize++;
					
				xid = alist.get(i).xtid;
				bmc =alist.get(i).bmc;
				pst.addBatch();
				if(i%10000 ==0){
					pst.executeBatch();
					con.commit();
				}
			
			}

			pst.executeBatch();
			
			con.commit();
			
			String asql = "insert into systemtable (xtid,bmc,ppl) values(?,?,?)";
			pst = con.prepareStatement(asql);
			pst.setInt(1, xid);
			pst.setString(2,bmc);
			float ppl = 0;
			if(allsize==0) ppl =0;
			else ppl = rightsize/allsize;
			
			float toShow = (int) (ppl*10000)/100;
			pst.setString(3,String.valueOf(toShow)+"%");
		
			pst.addBatch();
			pst.executeBatch();
			con.commit();
					
			// readObject(filename);
		} catch (Exception e) {
			e.printStackTrace();
		//	System.err.println(e.toString());
		}finally{
	
			pst.close();
			con.close();
		
		}
		
	
	}
	
	
	/**
	 * 
	 * @param alist
	 * @param url
	 * @param username
	 * @param password
	 * @param tablename
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void WriteSystemTableMetaNew
		(List<SystemTableMeta> alist,String url,String username,String password,String tablename, String sourceId) throws SQLException, ClassNotFoundException{
			
		if(alist == null || alist.size()==0){
			System.err.println("alist size is null or 0" );
			return ;
		}

		float allsize = 0;
		float  rightsize = 0;
		String bmc = "";
		int xid = 0;
		String owner = "";
		
		PreparedStatement pst = null;
		Connection con =  null;
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			 con = DriverManager.getConnection(url, username,
					password);
			con.setAutoCommit(false);// 关闭自动提交
			
			StringBuffer sql = new StringBuffer();
			sql.append("insert into "+tablename+
					"(zdmc,gsbm,xtid,bmc,zdbs,zdcd,zdlx,ppjg,ppmb,tjsjy, sourceid,owner) values(?,?,?,?,?,?,?,?,?,?,?,?)");
			pst = con.prepareStatement(sql.toString());

		
			for(int i=0;i<alist.size();i++){
				pst.setString(1, alist.get(i).zdmc);
				pst.setString(2, alist.get(i).gsbm);
				pst.setInt(3, alist.get(i).xtid);
				pst.setString(4, alist.get(i).bmc);
				pst.setString(5, alist.get(i).zdbs);
				pst.setString(6, alist.get(i).zdcd);
				pst.setString(7, alist.get(i).zdlx);
				
				pst.setString(8, alist.get(i).ppjg);

				String temp = alist.get(i).ppjg;
				String pipei = "数据元";
				if(temp!=null){
					int bid = temp.indexOf(pipei);
					int eid = temp.indexOf("(");
					if(bid>0&&bid<eid)
						temp = temp.substring(bid+pipei.length(),eid);
					else 
						temp = null;
				}
				pst.setString(9, temp);
				pst.setString(10, alist.get(i).Gettjsjy(ifrefine));
				//新加数据源id
				pst.setString(11, sourceId);
				pst.setString(12, alist.get(i).owner);
				
				if(alist.get(i).ppjg!=null && alist.get(i).ppjg.startsWith("完全"))
					rightsize++;
				allsize++;
					
				xid = alist.get(i).xtid;
				bmc =alist.get(i).bmc;
				owner = alist.get(i).owner;
				pst.addBatch();
				if(i%10000 ==0){
					pst.executeBatch();
					con.commit();
				}
			}
			pst.executeBatch();
			con.commit();
			float ppl = 0;
			if(allsize==0) ppl =0;
			else ppl = rightsize/allsize;
			
			float toShow = (int) (ppl*10000)/100;
			String asql = "update systemtable set ppl='"+String.valueOf(toShow)+"%"+"' where xtid="+xid+" and bmc='"+bmc+"' and sourceid='"+sourceId+"' and owner='"+owner+"'";
					//"insert into systemtable (xtid,bmc,ppl,sourceid,owner) values(?,?,?,?,?)";
			System.out.println("to deal update systemtable:"+asql);
			pst = con.prepareStatement(asql);
			pst.execute();
			con.commit();
		} catch (Exception e) {
			e.printStackTrace();
			//recordDBIsCompleteMessage(sourceId, "false", e.getMessage());
		}finally{
			pst.close();
			con.close();
		}
	}
	
	/**
	 * 
	 * @param alist
	 * @param url
	 * @param username
	 * @param password
	 * @param tablename
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void WriteSystemTableNew
		(List<TableBean> alist,String url,String username,String password,String sourceId,int xid) throws SQLException, ClassNotFoundException{
			
		if(alist == null || alist.size()==0){
			System.err.println("alist size is null or 0" );
			return ;
		}
		float allsize = 0;
		float  rightsize = 0;
		PreparedStatement pst = null;
		Connection con =  null;
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			 con = DriverManager.getConnection(url, username,
					password);
			con.setAutoCommit(false);// 关闭自动提交
			String asql = "insert into systemtable (xtid,bmc,ppl,sourceid,owner) values(?,?,?,?,?)";
			pst = con.prepareStatement(asql);
			for(int i=0;i<alist.size();i++){
				TableBean tb=alist.get(i);
				pst.setInt(1, xid);
				pst.setString(2,tb.getTablename());
				float ppl = 0;
				float toShow = (int) (ppl*10000)/100;
				pst.setString(3,String.valueOf(toShow)+"%");
				pst.setString(4, sourceId);
				pst.setString(5, tb.getOwner());
				pst.addBatch();
				if(i%100 ==0){
					pst.executeBatch();
					con.commit();
				}
			}
			pst.executeBatch();
			con.commit();
		} catch (Exception e) {
			e.printStackTrace();
			//recordDBIsCompleteMessage(sourceId, "false", e.getMessage());
		}finally{
			pst.close();
			con.close();
		}
	}
	


// 测试传入的是一个list，本来可以重复调用之前的 单表情况，现在做一下部分的优化设置，估计后来用连接池会比较好
	// 版本2

// tablename必须大写,最原始的，版本一，需要有
	public static void CheckTheOracl(String url,String username,String password,
			String tablename,int asystemid) throws ClassNotFoundException, SQLException{
//		boolean iflocalDebug = false;
//	//	iflocalDebug  = true;// 本地测试
		
		try {
			CheckHelper.Update();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return ;
		}
		
		
		ArrayList<SystemTableMeta> alist = new ArrayList<SystemTableMeta>();
		tablename = tablename.toUpperCase();
		
		//String drivename = "oracle.jdbc.driver.OracleDriver";
		String drivename = Constant.DE_DRIVER;
		Class.forName(drivename);
		
		// 注意，这里的读取需要是本地的表结构
		
		
		
		//Properties pro = new Properties();
		Statement stmt;

		String toDealUrl = Constant.DE_URL;
		String toDealUsername =  Constant.DE_USER;
		String toDealPassword = Constant.DE_PASSW0RD;
		//pro  = null;
		
		Connection con2Del = DriverManager.getConnection(toDealUrl, toDealUsername,	
				toDealPassword);	
		stmt = con2Del.createStatement();		
		stmt.executeQuery("delete from SYSTEMTABLEMETA where xtid='"+asystemid+"' and bmc='"
				+tablename+"'");
		stmt.close();
		con2Del.commit();
	
		stmt = con2Del.createStatement();
		stmt.executeQuery("delete from SYSTEMTABLE where xtid='"+asystemid+"' and bmc='"
				+tablename+"'");
		stmt.close();
		con2Del.commit();
		con2Del.close();		
		System.out.println("deal SYSTEMTABLEMETA and SYSTEMTABLE ok");
		
		
		Connection con = DriverManager.getConnection(url, username,	
				password);			
		System.out.println("ok connected to readlog");
		
		String asql ="select user_tab_columns.column_name, user_tab_columns.data_type,"
				+ "user_tab_columns.CHAR_LENGTH,user_tab_columns.DATA_PRECISION,"
				+ "user_tab_columns.DATA_SCALE, user_col_comments.comments"
				+ " from user_tab_columns ,user_col_comments   "
				+ "where user_tab_columns.table_name = user_col_comments.table_name and "
				+ "user_tab_columns.column_name = user_col_comments.column_name and "
				+ "user_tab_columns.table_name = '"+tablename+"'";
		System.out.println(asql);
		PreparedStatement pst = con
				.prepareStatement(asql);
		ResultSet rs = pst.executeQuery();
		
		int countNum = 0;
		
		try {
		while (rs.next()) {
			SystemTableMeta  alog = new SystemTableMeta();
			countNum++;
			alog.bmc = tablename;
			alog.xtid = asystemid;
			if (countNum % 50000 == 0)
				System.out.println("\n"+countNum+"+++++++++++++++++++++++++++++++++++++++++++++++++++++++");						
			alog.zdbs = ( rs.getString("column_name"));
			// 上面是字段标识,下面是字段名称	
			alog.zdmc = ( rs.getString("comments"));
			
			alog.zdlx =  (rs.getString("data_type"));
			
			
			// 解析类型 String_DT  Value_DT Date_DT
			alog.zdsjylx = CheckHelper.getInstance().MapZdsjylx(alog.zdlx);
			
			
			// 字符型
			if(alog.zdsjylx.equals(CheckHelper.String_DT)){
				alog.zdcd = (String.valueOf(rs.getInt("CHAR_LENGTH")));
			}
			//"数值型"
			else if (alog.zdsjylx.equals(CheckHelper.Value_DT)){
				int big = rs.getInt("DATA_PRECISION");
		//		double b = rs.getDouble("DATA_PRECISION");
				int small = rs.getInt("DATA_SCALE");
				alog.zdcd = (String.valueOf(big - small));
		//		System.out.println(alog.zdmc+":"+b+"\t"+big + ":"+small);
			} else 
				alog.zdcd = "0";
			alog.gsbm = tablename;
			
			alog.setAmd(); //  会把上面解析出来的数据类型给解析了，成为 数值、日期、等几个类别
			
			alist.add(alog);
			//System.out.println("print  "+alog);
			
	}
		// 以上是添加了一个个的需要测试的东西
		for(int i=0;i<alist.size();i++){
			CheckHelper.getInstance().checkBy(alist.get(i).amd);
			SystemTableMeta  atm = alist.get(i);
			metaData amd = alist.get(i).amd;
			if(amd.type==0){
				atm.ppjg = "完全匹配"+atm.GetRecommend();
								}
			else if(amd.type==1){
				atm.ppjg = "部分匹配"+atm.GetRecommend();
				
			}
			else
			{
				atm.ppjg = "无法匹配";
				if(atm.amd.Code.source!=null &&!atm.amd.Code.source.equals(""))
					atm.ppjg = atm.ppjg+",数据元"+amd.Code.source+"。";
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		rs.close();
		pst.close();
		con.close();
	
	}
		
//写入	
		url =	toDealUrl ;
		username = toDealUsername; 
		password = toDealPassword;
		tablename = "SYSTEMTABLEMETA";		
	     WriteSystemTableMeta(alist, url, username, password, tablename);
	
	}




public static Result CheckTheOracl(String url,String username,String password,
		ArrayList<String> tablenamelist,int asystemid) throws ClassNotFoundException, SQLException{

	ArrayList<SystemTableMeta> alist = new ArrayList<SystemTableMeta>();
	
	
	//String drivename = "oracle.jdbc.driver.OracleDriver";
	String drivename = Constant.DE_DRIVER;
	Class.forName(drivename);

	String toDealUrl = Constant.DE_URL ;
	String toDealUsername = Constant.DE_USER;
	String toDealPassword = Constant.DE_PASSW0RD;
	
	// 删掉之前的记录
	Statement stmt;
	Connection con2Del = DriverManager.getConnection(toDealUrl, toDealUsername,	
			toDealPassword);		
	stmt = con2Del.createStatement();	
	stmt.executeQuery("delete from SYSTEMTABLEMETA where xtid='"+asystemid+"'");
	stmt.close();
	con2Del.commit();
	
	stmt = con2Del.createStatement();
	stmt.executeQuery("delete from SYSTEMTABLE where xtid='"+asystemid+"'");
	
	stmt.close();
	con2Del.commit();
	con2Del.close();		
	System.out.println("delete ok");

	Connection con = DriverManager.getConnection(url, username,	
			password);			
	System.out.println("to get talbe property ....");
	
// 读各种表的数据，从list里面找到的	
	for(int i=0;i<tablenamelist.size();i++){
		alist.clear();// 一定要清空之前的
		String tablename = tablenamelist.get(i);
		tablename = tablename.toUpperCase();
		System.out.println("to deal with table: "+ tablename +" "+i+"/"+tablenamelist.size());

		String asql ="select user_tab_columns.column_name, user_tab_columns.data_type,"
				+ "user_tab_columns.CHAR_LENGTH,user_tab_columns.DATA_PRECISION,"
				+ "user_tab_columns.DATA_SCALE, user_col_comments.comments"
				+ " from user_tab_columns ,user_col_comments   "
				+ "where user_tab_columns.table_name = user_col_comments.table_name and "
				+ "user_tab_columns.column_name = user_col_comments.column_name and "
				+ "user_tab_columns.table_name = '"+tablename+"'";
		PreparedStatement pst = con
				.prepareStatement(asql);
		ResultSet rs = pst.executeQuery();		
		int countNum = 0;
				
		
		while (rs.next()) {
			SystemTableMeta  alog = new SystemTableMeta();
			countNum++;
			alog.bmc = tablename;
			alog.xtid = asystemid;
			if (countNum % 50000 == 0)
				System.out.println("\n"+countNum+"+++++++++++++++++++++++++++++++++++++++++++++++++++++++");						
			alog.zdbs = ( rs.getString("column_name"));
			// 上面是字段标识,下面是字段名称	
			alog.zdmc = ( rs.getString("comments"));
			
			alog.zdlx =  (rs.getString("data_type"));
			alog.zdsjylx = CheckHelper.getInstance().MapZdsjylx(alog.zdlx);
			
			if(alog.zdsjylx.equals("字符型")){
				alog.zdcd = (String.valueOf(rs.getInt("CHAR_LENGTH")));
			}
			else if (alog.zdsjylx.equals("数值型")){
				int big = rs.getInt("DATA_PRECISION");
		//		double b = rs.getDouble("DATA_PRECISION");
				int small = rs.getInt("DATA_SCALE");
				alog.zdcd = (String.valueOf(big - small));
		//		System.out.println(alog.zdmc+":"+b+"\t"+big + ":"+small);
			} else 
				alog.zdcd = "0";
			alog.gsbm = tablename;
			alog.setAmd();
			alist.add(alog);
			
	}
		
		//添加一个关闭的操作 20160411
		rs.close();
		pst.close();
		//
		// 以上是添加了一个个的需要测试的东西
		for(int ia=0;ia<alist.size();ia++){
			CheckHelper.getInstance().checkBy(alist.get(ia).amd);
			SystemTableMeta  atm = alist.get(ia);
			metaData amd = alist.get(ia).amd;
			if(amd.type==0){
				atm.ppjg = "完全匹配"+atm.GetRecommend();
								}
			else if(amd.type==1){
				atm.ppjg = "部分匹配"+atm.GetRecommend();
				
				}
			else
			{
				atm.ppjg = "无法匹配";
				
				}
			//System.out.println(atm+"\t"+atm.ppjg);
		}
			
		// 
			
		// 写入：
		url =	toDealUrl ;
		username = toDealUsername; 
		password = toDealPassword;
		tablename = "SYSTEMTABLEMETA";
		WriteSystemTableMeta(alist, url, username, password, tablename);
		 
		 System.out.println("deal this table done");
	}// list 读完
	System.out.println("all done :) ");
	return result; //处理完毕没有报任何异常则反正默认初始化的result值 默认result.getFlag = true;

}// function end

/**
 * 
 * @param url
 * @param username
 * @param password
 * @param tablenamelist
 * @param asystemid
 * @param sourceId
 * @return
 * @throws ClassNotFoundException
 * @throws SQLException
 */
public static void CheckTheOraclNew(String url,String username,String password,
		ArrayList<TableBean> tablenamelist,int asystemid, String sourceId) throws ClassNotFoundException, SQLException{

	ArrayList<SystemTableMeta> alist = new ArrayList<SystemTableMeta>();
	
	
	//String drivename = "oracle.jdbc.driver.OracleDriver";
	String drivename = Constant.DE_DRIVER;
	Class.forName(drivename);

	String toDealUrl = Constant.DE_URL ;
	String toDealUsername = Constant.DE_USER;
	String toDealPassword = Constant.DE_PASSW0RD;
	
	try {
		// 删掉之前的记录
		Statement stmt;
		Connection con2Del = DriverManager.getConnection(toDealUrl, toDealUsername,	
				toDealPassword);		
		stmt = con2Del.createStatement();	
		//stmt.executeQuery("delete from SYSTEMTABLEMETA where xtid='"+asystemid+"'");
		stmt.executeQuery("delete from SYSTEMTABLEMETA where xtid='"+asystemid+"' and sourceid='"+sourceId+"'");
		System.out.println("to deal sql : delete from SYSTEMTABLEMETA where xtid='"+asystemid+"' and sourceid='"+sourceId+"'");
		stmt.close();
		con2Del.commit();
		
		stmt = con2Del.createStatement();
		//stmt.executeQuery("delete from SYSTEMTABLE where xtid='"+asystemid+"'");
		stmt.executeQuery("delete from SYSTEMTABLE where xtid='"+asystemid+"' and sourceid='"+sourceId+"'");
		System.out.println("to deal sql : delete from SYSTEMTABLE where xtid='"+asystemid+"' and sourceid='"+sourceId+"'");
		stmt.close();
		con2Del.commit();
		//con2Del.close();	
		
		//删除对标检查的表
		stmt = con2Del.createStatement();
		stmt.executeQuery("delete from ischeckcomplete where xtid='"+asystemid+"' and sourceid='"+sourceId+"'");
		System.out.println("to deal sql : delete from ischeckcomplete where xtid='"+asystemid+"' and sourceid='"+sourceId+"'");
		stmt.close();
		con2Del.commit();
		con2Del.close();
		
		System.out.println("delete ok");
	
		Connection con = DriverManager.getConnection(url, username,	
				password);			
		System.out.println("to get talbe property ....");
		//批量插入systemtable
		//author:cloudMa
		WriteSystemTableNew(tablenamelist, toDealUrl, toDealUsername, toDealPassword, sourceId, asystemid);
		
		
		// 读各种表的数据，从list里面找到的	
		for(int i=0;i<tablenamelist.size();i++){
			alist.clear();// 一定要清空之前的
			String tablename = tablenamelist.get(i).getTablename();
			tablename = tablename.toUpperCase();
			System.out.println("to deal with table: "+ tablename +" "+i+"/"+tablenamelist.size());
	
			/*String asql ="select user_tab_columns.column_name, user_tab_columns.data_type,"
					+ "user_tab_columns.CHAR_LENGTH,user_tab_columns.DATA_PRECISION,"
					+ "user_tab_columns.DATA_SCALE, user_col_comments.comments"
					+ " from user_tab_columns ,user_col_comments   "
					+ "where user_tab_columns.table_name = user_col_comments.table_name and "
					+ "user_tab_columns.column_name = user_col_comments.column_name and "
					+ "user_tab_columns.table_name = '"+tablename+"'";*/
			String tableOfUser = Constant.USEROFTABLE;
			String asql = "select col.OWNER, col.COLUMN_NAME, col.DATA_TYPE, col.CHAR_LENGTH, col.DATA_PRECISION,"+
					" col.DATA_SCALE,cmt.comments  from dba_tab_columns col,dba_col_comments cmt"+
					" where col.TABLE_NAME = cmt.table_name "+
					" and col.COLUMN_NAME = cmt.column_name"+
					" and col.OWNER = cmt.owner"+
					" and col.OWNER not in ("+tableOfUser+")"+
					" and col.TABLE_NAME = '"+tablename+"'";
			System.out.println("to deal sql: "+asql);
			PreparedStatement pst = con
					.prepareStatement(asql);
			ResultSet rs = pst.executeQuery();		
			int countNum = 0;
					
			
			while (rs.next()) {
				SystemTableMeta  alog = new SystemTableMeta();
				countNum++;
				alog.bmc = tablename;
				alog.xtid = asystemid;
				if (countNum % 50000 == 0)
					System.out.println("\n"+countNum+"+++++++++++++++++++++++++++++++++++++++++++++++++++++++");						
				alog.zdbs = ( rs.getString("column_name"));
				// 上面是字段标识,下面是字段名称	
				alog.zdmc = ( rs.getString("comments"));
				
				alog.zdlx =  (rs.getString("data_type"));
				alog.zdsjylx = CheckHelper.getInstance().MapZdsjylx(alog.zdlx);
				
				//添加数据元所属的用户信息
				alog.owner = rs.getString("OWNER");
				
				if(alog.zdsjylx.equals("字符型")){
					alog.zdcd = (String.valueOf(rs.getInt("CHAR_LENGTH")));
				}
				else if (alog.zdsjylx.equals("数值型")){
					int big = rs.getInt("DATA_PRECISION");
			//		double b = rs.getDouble("DATA_PRECISION");
					int small = rs.getInt("DATA_SCALE");
					alog.zdcd = (String.valueOf(big - small));
			//		System.out.println(alog.zdmc+":"+b+"\t"+big + ":"+small);
				} else 
					alog.zdcd = "0";
				alog.gsbm = tablename;
				alog.setAmd();
				alist.add(alog);
				
		}
			
			//添加一个关闭的操作 20160411
			rs.close();
			pst.close();
			//
			// 以上是添加了一个个的需要测试的东西
			for(int ia=0;ia<alist.size();ia++){
				CheckHelper.getInstance().checkBy(alist.get(ia).amd);
				SystemTableMeta  atm = alist.get(ia);
				metaData amd = alist.get(ia).amd;
				if(amd.type==0){
					atm.ppjg = "完全匹配"+atm.GetRecommend();
									}
				else if(amd.type==1){
					atm.ppjg = "部分匹配"+atm.GetRecommend();
					}
				else
				{
					atm.ppjg = "无法匹配";
					}
				//System.out.println(atm+"\t"+atm.ppjg);
			}
				
			// 
				
			// 写入：
			url =	toDealUrl ;
			username = toDealUsername; 
			password = toDealPassword;
			tablename = "SYSTEMTABLEMETA";
			WriteSystemTableMetaNew(alist, url, username, password, tablename, sourceId);
			 
			 System.out.println("deal this table done");
		}// list 读完
	
	} catch (Exception e) {
		recordDBIsCompleteMessage(asystemid, sourceId, "false", e.getMessage());
	}
	System.out.println("all done :) ");
	recordDBIsCompleteMessage(asystemid, sourceId, "true", "对标成功!");
	//return result; //处理完毕没有报任何异常则反正默认初始化的result值 默认result.getFlag = true;

}// function end

/**
 * @des 记录对标的的错误信息
 * @param msg
 */
public static void recordDBIsCompleteMessage(int asystemid, String sourceId, String flag, String msg) {
	Connection connection = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	String sql = "insert into ischeckcomplete(xtid,sourceid,flag, msg) values ('"+asystemid+"','"+sourceId+"', '"+flag+"', '"+msg+"')";
	try {
		connection = DBConnection.getDEConnection().getConnection();
		preparedStatement = connection.prepareStatement(sql);
		preparedStatement.executeUpdate();
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		DBConnection.free(connection, preparedStatement, resultSet);
	}
}




/**
 * 
 * @param url
 * @param username
 * @param password
 * @param asystemid
 * @return Result
 * @des 对标,返回对标成功与否标记
 * @throws ClassNotFoundException
 * @throws SQLException
 */
public static Result CheckDataMeta_old(String url,String username,String password,
 int asystemid) throws ClassNotFoundException,
			SQLException {
	try {
		CheckHelper.Update();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		result.setFlag(false);
		result.setResultJson(e1.getMessage());
		return result; //错误返回错误日志信息
	}
		ArrayList<String> alist = new ArrayList<String>();

		String drivename = "oracle.jdbc.driver.OracleDriver";
		Class.forName(drivename);

		Connection con = DriverManager.getConnection(url, username, password);
		System.out.println("ok connected ,read all tables to be checked ……");

		String asql = "SELECT table_name FROM USER_TABLES";
		System.out.println(asql);
		PreparedStatement pst = con.prepareStatement(asql);
		ResultSet rs = pst.executeQuery();

		try {
			while (rs.next()) {
				alist.add(rs.getString(1));
			}
			// 以上是添加了一个个的需要测试的东西
			//
		} catch (Exception e) {
			result.setFlag(false);
			result.setResultJson(e.getMessage());
			e.printStackTrace();
		} finally {
			rs.close();
			pst.close();
			con.close();
		}
		if (alist.size() == 0) {
			result.setFlag(false);
			result.setResultJson("指定的数据库中没有查询到任何表结构!");
			return result;
		} else {
			System.out.println("Read done. \n To delete old rows in app-system...");
			result = CheckTheOracl(url, username, password, alist, asystemid);
			return result;
		}
	}

/**
 * 
 * @param url
 * @param username
 * @param password
 * @param asystemid
 * @param sourceId 数据源id 用户对个数据库
 * @return
 * @throws ClassNotFoundException
 * @throws SQLException
 */
public static Result CheckDataMetaNew(String url,String username,String password,
		 int asystemid, String sourceId) throws ClassNotFoundException,
					SQLException {
			try {
				CheckHelper.Update();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				result.setFlag(false);
				result.setResultJson(e1.getMessage());
				return result; //错误返回错误日志信息
			}
				ArrayList<TableBean> alist = new ArrayList<TableBean>();

				String drivename = "oracle.jdbc.driver.OracleDriver";
				Class.forName(drivename);

				Connection con = DriverManager.getConnection(url, username, password);
				System.out.println("ok connected ,read all tables to be checked ……");

				//String asql = "SELECT table_name FROM USER_TABLES";
				String tableOfUser = Constant.USEROFTABLE;
				String asql = "select table_name ,owner from dba_tables t where t.owner not in("+tableOfUser+")";
				System.out.println(asql);
				PreparedStatement pst = con.prepareStatement(asql);
				ResultSet rs = pst.executeQuery();

				try {
					while (rs.next()) {
						TableBean tb=new TableBean();
						tb.setTablename(rs.getString(1));
						tb.setOwner(rs.getString(2));
						alist.add(tb);
					}
					// 以上是添加了一个个的需要测试的东西
					//
				} catch (Exception e) {
					result.setFlag(false);
					result.setResultJson(e.getMessage());
					e.printStackTrace();
				} finally {
					rs.close();
					pst.close();
					con.close();
				}
				if (alist.size() == 0) {
					result.setFlag(false);
					result.setResultJson("指定的数据库中没有查询到任何表结构!");
					return result;
				} else {
					System.out.println("Read done. \n To delete old rows in app-system...");
					//常州需求数据库连接成功之后即返回成功
					/*result = CheckTheOraclNew(url, username, password, alist, asystemid,sourceId);
					return result;*/
					
					CheckThead runable = new CheckThead(url, username, password, alist, asystemid, sourceId);
					Thread thread = new Thread(runable);
					thread.start();
					result.setFlag(true);
					result.setResultJson("数据库连接成功!");
					return result;
				}
			}

}



