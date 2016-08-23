package com.easymap.hbase.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.easymap.hbase.tool.initsystem.InitSystem;



public class StreamConstants implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain fc) throws IOException, ServletException {
		if(!InitSystem.isWSParameterInitialized()){
			HttpServletRequest hsr = (HttpServletRequest) req;
			String URLS=hsr.getRequestURL().toString();
			InitSystem.InitializeWSParameters(URLS.substring(0, URLS.lastIndexOf("/")+1));	
		}
		fc.doFilter(req, resp);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
