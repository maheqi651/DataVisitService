package com.easymap.base.scontrol;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Element;

import com.easymap.base.tool.readXml;
import com.easymap.dao.authorizationDataDao;
import com.easymap.filter.SoutProx;
import com.easymap.memcached.MemCachedFactory;
import com.easymap.modle.authorization.authorization;

public class ControlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		@SuppressWarnings("unused")
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		String pathName = request.getServletPath();
		System.out.println("---"+request.getQueryString());
		int index = pathName.indexOf(".");
		String str = pathName.substring(1, index);
		if (str.indexOf("/") > 1) {
			String s[] = str.split("/");
			str = s[s.length - 1];
		}
		String ActionName = str;
		String pathNames = request.getRealPath("WEB-INF/servlet.xml");
		Element rootElement = readXml.getreadXml(pathNames).getRootElement();
		Iterator iterator = rootElement.elementIterator("action");
		Element controle = (Element) iterator.next();
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
				action.execute(request, response);
				break;
			}
		}
		 String key="specialinterfacecontrol";
		 String ActionClassName = controle.attributeValue("path");
		 if(MemCachedFactory.instance().getMemCached(key)!=null)
		 {
			 String tempstr[]=(String[])MemCachedFactory.instance().getMemCached(key);
			  
			    	boolean flaga=false;
			    	SoutProx.sysoutlog(ActionClassName+"-----22");
			    	
			    	for(String e:tempstr){
			    		SoutProx.sysoutlog(e+"-----11");
			    		if(ActionName.equals(e)&&!"".equals(ActionName))
			    			{
			    			  Action action = ActionFactory.getActionFactory().getAction(
			  						ActionClassName);
			  				  action.execute(request, response);
			    			  break;
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
			    MemCachedFactory.instance().addToMemCached(key, tempstr);
			    for(String e:tempstr){
		    		SoutProx.sysoutlog(ActionClassName+"-----11");
		    		if(ActionName.equals(e)&&!"".equals(ActionName))
		    			{
		    			  Action action = ActionFactory.getActionFactory().getAction(
		  						ActionClassName);
		  				  action.execute(request, response);
		    			  break;
		    			}
		    	}
			 
		 }
		 
		

	}
}