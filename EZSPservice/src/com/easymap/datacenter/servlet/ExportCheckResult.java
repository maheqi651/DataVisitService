package com.easymap.datacenter.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.easymap.base.scontrol.Action;
import com.easymap.datacenter.model.Result;
import com.easymap.datacenter.util.ConnectionDB;
import com.easymap.datacenter.util.DBConnection;
import com.easymap.datacenter.util.ExportData;



 

public class ExportCheckResult implements Action {
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
	{
		String path=request.getRealPath("/");
		Result result = new Result();
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
			}
		}
		
		if (sourceId != null) {
			if (sourceId.trim().equals("")) {
				result.setFlag(false);
				result.setResultJson("参数sourceId不能为空!");
				renderJSON(result,response);
			}
		}
		
		if (owner != null) {
			if (owner.trim().equals("")) {
				result.setFlag(false);
				result.setResultJson("参数owner不能为空!");
				renderJSON(result,response);
			}
		}
		
		if (tableName != null) {
			if (tableName.trim().equals("")) {
				result.setFlag(false);
				result.setResultJson("参数tableName不能为空!");
				renderJSON(result,response);
			}
		}
		
		List<Map<String,String>> InfoList = StandardMangentDuiBiaoResultsFromDB(xtId, sourceId, owner, tableName);	
		
		List<String> titleStrings = new ArrayList<String>();
		titleStrings.add("数据源");
		titleStrings.add("所属用户");
		titleStrings.add("表名");
		titleStrings.add("表匹配率");
		titleStrings.add("字段名称");
		titleStrings.add("中文名称");
		titleStrings.add("字段类型");
		titleStrings.add("字段长度");
		titleStrings.add("匹配情况");
		List<String> keys = new ArrayList<String>();			
		keys.add("SOURCEID");//*
		keys.add("OWNER");//*
		keys.add("BMC");//*
		keys.add("PPL");
		keys.add("ZDBS");
		keys.add("ZDMC");
		keys.add("ZDLX");//*
		keys.add("ZDCD");//*
		keys.add("PPJG");//*
		
		File fos = ExportData.exportExcelOnEachTitleNew(path,"数据元检测结果", titleStrings, keys, InfoList);
		 
		renderBinary(fos,response,tableName);
		  
		    
	}
	public void renderBinary(File file,HttpServletResponse response,String tableName)
	{
		response.setHeader("content-disposition", "attachment;filename="+tableName+".xls");
		try {
			InputStream in=new FileInputStream(file);
			OutputStream out=null;
			int len=0;
			byte[] buffer=new byte[1024];
			out=response.getOutputStream();
			while((len=in.read(buffer))>0)
			{
				out.write(buffer,0,len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static List<Map<String,String>> StandardMangentDuiBiaoResultsFromDB(String xtId, 
			String sourceId, String owner, String tableName){
		List<Map<String,String>> infolist = new ArrayList<Map<String,String>>();
		ConnectionDB conn = null;
		try {

			conn = DBConnection.getDEConnection();
			StringBuilder builder = new StringBuilder();
			builder.append("select t.sourceid, t.owner, t.bmc, t.ppl, tm.ZDBS, tm.ZDMC, tm.ZDLX,tm.ZDCD,"
					+ " tm.PPJG from systemtablemeta tm, systemtable t where tm.sourceid = t.sourceid(+)"
					+ "  and tm.owner = t.owner(+) "
					+ " and tm.gsbm= t.bmc (+)  "
					+ " and t.xtid='"+xtId+"'");
			
			if(tableName != null && !"".equals(tableName.trim())){
				builder.append(" and t.bmc ='"+ tableName + "'" );				
			}			

			if (sourceId != null && !"".equals(sourceId.trim())) {
				builder.append(" and t.sourceid='"+sourceId+"'");	
			}
			
			if (owner != null && !"".equals(owner.trim())) {
				builder.append(" and t.owner='"+owner+"'");	
			}
			
			builder.append(" order by t.sourceid");
			String sql = builder.toString();
			List<Object> list = conn.excuteQuery(sql, null);
			Map<String,String> map ;
			for(int i = 0; i< list.size();i++){
				Object obj = list.get(i);
				map = (Map<String,String>)obj;
				infolist.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return infolist;
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
