package com.easymap.datacenter.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.easymap.base.scontrol.Action;
import com.easymap.datacenter.model.Result;
import com.easymap.datacenter.model.SystemTable;
import com.easymap.datacenter.util.ConnectionDB;
import com.easymap.datacenter.util.DBConnection;
import com.google.gson.Gson;


 

public class TableMapPPL implements Action {
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
	            
		Result result = new Result();
		String xtId = request.getParameter("xtId");
		String sourceId = request.getParameter("sourceId");
		if (xtId == null) {
			result.setFlag(false);
			result.setResultJson("缺少参数xtId,无法完成查询请求!");
			renderJSON(result,response);
		}
		
		if (xtId.equals("")) {
			result.setFlag(false);
			result.setResultJson("参数xtId不能为空!");
			renderJSON(result,response);
		}
		
		 
		if (sourceId.equals("")) {
			result.setFlag(false);
			result.setResultJson("参数sourceId不能为空!");
			renderJSON(result,response);
		}
		
		ConnectionDB conn = null;
		
		Map<String,List<SystemTable>> resultmap=new HashMap<String,List<SystemTable>>();
		
		try {
			conn = DBConnection.getDEConnection();
			String sql = "select * from systemtable t where t.xtid ="+ xtId + " and t.sourceid = '"+sourceId+"'";
		
	        List<Object> list = conn.excuteQuery(sql, null);
	        Map<String,String> map ;
	        SystemTable meta = null;
			for(int i = 0; i< list.size();i++){
				Object obj = list.get(i);
				map = (Map<String, String>)obj;
				meta = new SystemTable();
				meta.setBmc(map.get("BMC"));
				meta.setPpl(map.get("PPL"));
				meta.setXtId(String.valueOf(map.get("XTID")));
				meta.setYj(map.get("YJ"));
				meta.setOwner(map.get("OWNER"));
				if(resultmap.get(map.get("OWNER"))==null)
				{
					List<SystemTable> metaList = new ArrayList<SystemTable>();
					metaList.add(meta);
					resultmap.put(map.get("OWNER"), metaList);
				}else{
					List<SystemTable> metaList =  resultmap.get(map.get("OWNER"));
					metaList.add(meta);
				}
			}
			Gson gson = new Gson();
			result.setResultJson(gson.toJson(resultmap));
		} catch (Exception e) {
			result.setFlag(false);
			result.setResultJson(e.getMessage());
			e.printStackTrace();
		}
		renderJSON(result,response);
		    
	}
	public void renderJSON(Result result,HttpServletResponse response){
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException ee)
		{
			ee.printStackTrace();
		}
		out.print(result.toString());
		out.flush();
		out.close();
}
}
