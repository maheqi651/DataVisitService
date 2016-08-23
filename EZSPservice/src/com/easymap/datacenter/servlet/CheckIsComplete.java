package com.easymap.datacenter.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.easymap.base.scontrol.Action;
import com.easymap.datacenter.model.Result;
import com.easymap.datacenter.util.DBConnection;


 

public class CheckIsComplete implements Action {
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		Result result = new Result();
		String sourceId = request.getParameter("sourceId");
		String xtId = request.getParameter("xtId");
		
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
		
		if (sourceId == null) {
			result.setFlag(false);
			result.setResultJson("缺少参数sourceId,无法完成查询请求!");
			renderJSON(result,response);
		} else {
			if (sourceId.trim().equals("")) {
				result.setFlag(false);
				result.setResultJson("参数sourceId不能为空!");
				renderJSON(result,response);
			}
		}

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String sql = "select t.sourceid, t.flag, t.msg  from ischeckcomplete t where t.xtid='"+xtId+"' and t.sourceid = '"+sourceId+"'";
		try {
			connection = DBConnection.getDEConnection().getConnection();
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				String flag = resultSet.getString(2);
				String msg = resultSet.getString(3);
				boolean resultFlag = false;
				if ("true".equals(flag)) {
					resultFlag = true;
				}
				result.setFlag(resultFlag);
				result.setResultJson(msg);
			} else {
				result.setFlag(false);
				result.setResultJson("对标未完成!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.free(connection, preparedStatement, resultSet);
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
