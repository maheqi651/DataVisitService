package com.easymap.datacenter.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.easymap.datacenter.model.CheckOracl;
import com.easymap.datacenter.model.TableBean;


public class CheckThead implements Runnable{
	
	private String url;
	
	private String username;
	
	private String password;
	
	private ArrayList<TableBean> alist;
	
	private int asystemid;
	
	private String sourceId;
	
	
	public CheckThead(String url, String username,String  password, ArrayList<TableBean> alist, int asystemid, String sourceId) {
		// TODO Auto-generated constructor stub
		this.url = url;
		this.username = username;
		this.password = password;
		this.alist = alist;
		this.asystemid = asystemid;
		this.sourceId = sourceId;
	}
	
	@Override
	public void run() {
		try {
			CheckOracl.CheckTheOraclNew(url, username, password, alist, asystemid,sourceId);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
