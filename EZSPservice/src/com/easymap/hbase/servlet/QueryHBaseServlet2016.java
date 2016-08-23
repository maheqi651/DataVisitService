package com.easymap.hbase.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.easymap.base.scontrol.Action;
import com.easymap.ezBigData.exceptions.EzBigDataConfigException;
import com.easymap.ezBigData.exceptions.QueryConditionsConstructException;
import com.easymap.ezMDAS.kvQuery.pojos.KvPage;
import com.easymap.hbase.hbasemodel.KvParams;
import com.easymap.hbase.hbasemodel.KvQuery2015;
import com.easymap.hbase.util.Constants;

import net.sf.json.JSONObject;

public class QueryHBaseServlet2016 implements Action {

	
 
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
			 
		    JSONObject json=new JSONObject();
			json.put("hbaseInstance", Constants.DEFAULT_HBASE_INSTANCE);
		    json.put("hbaseTable", "THDB_DHHM");
		    JSONObject params= new JSONObject();
		    params.put("rowkey", "");
		    params.put("isFuzzy", "true");
		    params.put("isBatch", "false");
		    params.put("fuzzyRow", "00000000001?");
		    params.put("pageSize", "10");
		    params.put("maxVersions", "1");
		    json.put("params", params);
			KvQuery2015 kvq=new KvQuery2015(json);
		    KvPage kvpage=null;
		    String restr=null;
		    try {
		    	kvpage=kvq.execute();
			} catch (QueryConditionsConstructException e) {
				e.printStackTrace();
			} catch (EzBigDataConfigException e) {
				e.printStackTrace();
			}
		    System.out.println(kvq.formatKvInfos2String(kvpage,"normal"));
		    
	}

 
	
	


}
