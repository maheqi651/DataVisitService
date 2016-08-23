package com.easymap.datacenter.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.easymap.base.scontrol.Action;
import com.easymap.datacenter.model.CheckOracl;
import com.easymap.datacenter.model.Result;


 

public class CheckDataMeta implements Action {
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
	{
	            
		Result result = new Result();
		String regStr = "^(jdbc:oracle:thin:@(?:[0-9]{1,3}.){3}[0-9]{1,3}:[0-9]+:[a-zA-Z0-9._&*!]+)|(jdbc:mysql:)$";
		Pattern p = Pattern.compile(regStr);
		String url = request.getParameter("url");
		Matcher matcher = p.matcher(url);
		String username = request.getParameter("username");
		String sourceId = request.getParameter("sourceId");
		int systemId =0;
		if(request.getParameter("systemId")!=null)
			systemId = Integer.parseInt(request.getParameter("systemId"));
		String password = request.getParameter("password");
		if (url == null) {
			result.setFlag(false);
			result.setResultJson("缺少参数url,无法完成查询请求!");
			renderJSON(result,response);
		}
		
		if (username == null) {
			result.setFlag(false);
			result.setResultJson("缺少参数username,无法完成查询请求!");
			renderJSON(result,response);
		}
		
		
		if (password == null) {
			result.setFlag(false);
			result.setResultJson("缺少参数password,无法完成查询请求!");
			renderJSON(result,response);
		}
		
		if (!matcher.matches()) {
			result.setFlag(false);
			result.setResultJson("数据库连接符格式不正确!");
			renderJSON(result,response);
		}
	 
		if ("".equals(systemId)) {
			result.setFlag(false);
			result.setResultJson("参数systemId为空!");
			renderJSON(result,response);
		}
		
		try {
			result = CheckOracl.CheckDataMetaNew(url, username, password, systemId,sourceId);
		} catch (ClassNotFoundException e) {
			result.setFlag(false);
			result.setResultJson(e.getMessage());
			e.printStackTrace();       
		} catch (SQLException e) {
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
