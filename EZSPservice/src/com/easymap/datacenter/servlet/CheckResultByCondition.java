package com.easymap.datacenter.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.easymap.base.scontrol.Action;
import com.easymap.datacenter.model.Result;
import com.easymap.datacenter.util.ConnectionDB;
import com.easymap.datacenter.util.DBConnection;
import com.easymap.datacenter.util.SystemTableMeta;
import com.google.gson.Gson;

 

public class CheckResultByCondition implements Action {
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		ConnectionDB conn = null;
		Result result = new Result();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select * from systemtablemeta t where 1 = 1 ");
		String xtId = request.getParameter("xtId");
		String tableName = request.getParameter("tableName");
		String sourceId = request.getParameter("sourceId");
		String owner= request.getParameter("owner");
		
		if (xtId == null) {
			result.setFlag(false);
			result.setResultJson("缺少参数xtId,无法完成查询请求!");
			renderJSON(result,response);
		} else {
			if (xtId.trim().equals("")) {
				result.setFlag(false);
				result.setResultJson("参数xtId不能为空!");
				renderJSON(result,response);
			} else {
				sqlBuffer.append(" and t.xtid = '"+xtId+"'");
			}
		}
		
		if (tableName != null) {
			if (tableName.trim().equals("")) {
				result.setFlag(false);
				result.setResultJson("参数tableName不能为空!");
				renderJSON(result,response);
			} else {
				sqlBuffer.append(" and t.gsbm = '"+tableName+"'");
			}
		}
		
		if (sourceId != null) {
			if (sourceId.trim().equals("")) {
				result.setFlag(false);
				result.setResultJson("参数sourceId不能为空!");
				renderJSON(result,response);
			} else {
				sqlBuffer.append(" and t.sourceId = '"+sourceId+"'");
			}
		}
		
		if (owner != null) {
			if (owner.trim().equals("")) {
				result.setFlag(false);
				result.setResultJson("参数owner不能为空!");
				renderJSON(result,response);
			} else {
				sqlBuffer.append(" and t.owner = '"+owner+"'");
			}
		}
		
		
		List<SystemTableMeta> metaList = new ArrayList<SystemTableMeta>();
		try {
			conn = DBConnection.getDEConnection();
	        List<Object> list = conn.excuteQuery(sqlBuffer.toString(), null);
	        Map<String,String> map ;
	        SystemTableMeta meta = null;
			for(int i = 0; i< list.size();i++){
				Object obj = list.get(i);
				map = (Map<String, String>)obj;
				meta = new SystemTableMeta();
				meta.setBmc(map.get("BMC"));
				meta.setGsbm(map.get("GSBM"));
				meta.setPpjg(map.get("PPJG"));
				meta.setPpmb(map.get("PPMB"));
				meta.setShyj(map.get("SHYJ"));
				meta.setTjsjy(map.get("TJSJY"));
				meta.setXtid(String.valueOf(map.get("XTID")));
				meta.setZdbs(map.get("ZDBS"));
				meta.setZdcd(map.get("ZDCD"));
				meta.setZdlx(map.get("ZDLX"));
				meta.setZdmc(map.get("ZDMC"));
				meta.setZdsjylx(map.get("ZDSJYLX"));
				meta.setOwner(map.get("OWNER"));
				metaList.add(meta);
			}
			Gson gson = new Gson();
			result.setResultJson(gson.toJson(metaList));
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
