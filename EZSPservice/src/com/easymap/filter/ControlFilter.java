package com.easymap.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;


import com.easymap.base.readxml.Object2XML;
import com.easymap.base.scontrol.Action;
import com.easymap.base.scontrol.ActionFactory;
import com.easymap.base.tool.readXml;
import com.easymap.base.tool.serviceIdMethod;
import com.easymap.dao.authorizationDataDao;
import com.easymap.hbase.tool.initsystem.InitSystem;
import com.easymap.memcached.MemCachedFactory;
import com.easymap.memcached.guard.ValueBean;
import com.easymap.modle.SRV.SRVQueryData2Modle;
import com.easymap.modle.authorization.authorization;
/*
 * 20150428
 * author cloudMa
 */
public class ControlFilter implements Filter,
		com.sun.org.apache.xalan.internal.xsltc.dom.Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if(!InitSystem.isWSParameterInitialized())
		{
			HttpServletRequest hsr = (HttpServletRequest) request;
			String URLS=hsr.getRequestURL().toString();
			InitSystem.InitializeWSParameters(URLS.substring(0, URLS.lastIndexOf("/")+1));	
		} 
		 
		@SuppressWarnings("unused")
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		String pathName = ((HttpServletRequest)request).getServletPath();
		 
		int index = pathName.indexOf("/");
		String str = pathName.substring(index+1);
		String ActionName = str;
		String pathNames = request.getRealPath("WEB-INF/servlet.xml");
		Element rootElement = readXml.getreadXml(pathNames).getRootElement();
		Iterator iterator = rootElement.elementIterator("action");
		//Element controle = (Element) iterator.next();
		Element controle = null;
		//System.out.println("-----XML------"+pathNames);
		if(pathName.contains("hbaseInsts.xml"))
		{
			chain.doFilter(request, response);
		} 
		System.out.println(pathName);
		if(pathName.contains("HessianHelloWord"))
		{
			System.out.println(pathName+"----");
			chain.doFilter(request, response);
		}
		if(iterator.hasNext())
		{
			
			controle =  (Element) iterator.next();
			if("QueryDataServlet".equals(controle.attributeValue("name")))
			{
				controle=(Element)controle.clone();
			}
			if (ActionName.equals(controle.attributeValue("name"))) {
				String ActionClassName = controle.attributeValue("path");
				Action action = ActionFactory.getActionFactory().getAction(
						ActionClassName);
				action.execute((HttpServletRequest)request, (HttpServletResponse)response);
				return;
			}
			for (; iterator.hasNext();) {
				Element other = (Element) iterator.next();
				if("QueryDataServlet".equals(other.attributeValue("name")))
				{
					controle=(Element)other.clone();
				}
				if (ActionName.equals(other.attributeValue("name"))) {
					String ActionClassName = other.attributeValue("path");
					Action action = ActionFactory.getActionFactory().getAction(
							ActionClassName);
					action.execute((HttpServletRequest)request, (HttpServletResponse)response);
					return;
				}
			}
		} else return;
		
		 
		 
		 String key=Tools.CONTROLLINK;
		 String ActionClassName = controle.attributeValue("path");
		 if(MemCachedFactory.instance().getMemCached(key)!=null)
		 {   
			 if(MemCachedFactory.instance().getMemCached(key) instanceof ValueBean&&MemCachedFactory.instance().getMemCached(key)!=null){
				 ValueBean vbs=(ValueBean)MemCachedFactory.instance().getMemCached(key);
				 String tempstr[]=vbs.getStr();
			    	boolean flaga=false;
			    	SoutProx.sysoutlog(ActionClassName+"-----22");
			    	if(tempstr!=null)
			    	for(String e:tempstr){
			    		SoutProx.sysoutlog(e+"-----11");
			    		if(ActionName.equals(e)&&!"".equals(ActionName))
			    			{
			    			  Action action = ActionFactory.getActionFactory().getAction(
			  						ActionClassName);
			    			  action.execute((HttpServletRequest)request, (HttpServletResponse)response);
			    			 
			    			  break;
			    			}
			    	}
			 }
		 }else{
			 authorizationDataDao[] objs=null;
			 objs=(authorizationDataDao[])authorization.getAuthorizationSpecialTablecode();
			 String tempstr[]=new String[objs.length];
			    for(int i=0;i<objs.length;i++)
			    {
			    	tempstr[i]=objs[i].getMethodname();
			    }
			    ValueBean vb=new ValueBean();
			    vb.setKey(key);
			    vb.setMhtd(null);
			    vb.setStr(tempstr);
			    vb.setTimes(System.currentTimeMillis());
			    MemCachedFactory.instance().addToMemCached(key, vb);
			    for(String e:tempstr){
		    		SoutProx.sysoutlog(ActionClassName+"-----11");
		    		if(ActionName.equals(e)&&!"".equals(ActionName))
		    			{
		    			  Action action = ActionFactory.getActionFactory().getAction(
		  						ActionClassName);
		    			  action.execute((HttpServletRequest)request, (HttpServletResponse)response);
		    			  break;
		    			}
		    	}
		 }
    }
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}
	@Override
	public boolean test(int node){
		return false;
	}

}
