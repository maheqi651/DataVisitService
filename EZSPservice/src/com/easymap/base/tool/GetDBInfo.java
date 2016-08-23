package com.easymap.base.tool;

import java.util.List;

import com.easymap.base.readdatabase.ConnectionDB;

public class GetDBInfo {
	private String server;
	private String sid;
	private String user;
	private String pwd;
	public GetDBInfo(){}
	public GetDBInfo(String code){
		getDate(code);
	}
	private void getDate(String code){
		String sql="select t3.connectstr as connectstr  from EZ_STD_DEF t," +
				"EZ_STD_Layers_Layer   t1," +
				"ez_theme_std_relation t2," +
				" ez_datasource t3" +
				" where t1.stdid = t.id   and t2.stdcode = t.code" +
				" and t1.code = '"+code+"' and t3.id = t2.datasourceid";
		List<Object> ls =	getResultSet(sql);
		if(ls.size()>0){
			String s=ls.get(0).toString();
			String [] str =s.split("-");
			for(int i=0;i<str.length;i++){
				if(str[i].toLowerCase().indexOf("server")>=0){
					this.server=getValue(str[i]);
				}else if(str[i].toLowerCase().indexOf("sid")>=0){
					this.sid=getValue(str[i]);
				}else if(str[i].toLowerCase().indexOf("user")>=0){
					this.user=getValue(str[i]);
				}else if(str[i].toLowerCase().indexOf("pwd")>=0){
					//this.pwd=getValue(str[i]);
				}
			}
		}
	}
	private String getValue(String s){
		String [] st=s.split(" ");
		if(st.length>0)
			return st[1];
		return "";
	}
	private List<Object> getResultSet(String sql){
		ConnectionDB conn = new ConnectionDB();
		List<Object> ls = conn.excuteQuery(sql.toUpperCase(), null);
		if (ls != null)
			return  ls;
		return null;
	}
	public String getServer() {
		return server;
	}
	public String getSid() {
		return sid;
	}
	public String getUser() {
		return user;
	}
	public String getPwd() {
		return pwd;
	}
	
}
