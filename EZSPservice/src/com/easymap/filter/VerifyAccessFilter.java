package com.easymap.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;


import com.easymap.base.readxml.Object2XML;
import com.easymap.base.tool.serviceIdMethod;
import com.easymap.dao.authorizationDataDao;
import com.easymap.memcached.MemCachedFactory;
import com.easymap.memcached.guard.ValueBean;
import com.easymap.modle.SRV.SRVQueryData2Modle;
import com.easymap.modle.authorization.authorization;

public class VerifyAccessFilter implements Filter,
com.sun.org.apache.xalan.internal.xsltc.dom.Filter {
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String pathName = ((HttpServletRequest)request).getServletPath();
		int index = pathName.indexOf("/");
		String strname = pathName.substring(index+1);
		//System.out.println("-----"+strname);
		request.setAttribute(Tools.FLAG, "true");//提供运维验证标记
		if(strname.contains("QueryHBase")||strname.contains(".xml")||strname.contains("HessianHelloWord")
				||strname.contains("CheckDataMeta")||strname.contains("CheckIsComplete")
				||strname.contains("CheckResultByCondition")||strname.contains("ExportCheckResult")
				||strname.contains("TableMapPPL")||strname.contains("QueryMetadataInfo")
				||strname.contains("QueryMetadataStd"))
		{
			chain.doFilter(request, response);//通过
		}else{
			//System.out.println("--1---"+strname);
		AcessDeal acessDeal=new AcessDeal();
		long time1=System.currentTimeMillis();
		Object2XML o2x = new Object2XML();
		String senderID = "";
		String methodName = "";
		String s = "";
		String[] tableCode=null;
		boolean flag=true;
		try {
			String str = IOUtils.toString(request.getInputStream(),"utf-8");//IOUtils.toString(request.getInputStream());
			request.setAttribute("str", str);
			if (str != null) {
				Document document = o2x.xml2Object(str);
				Element root = document.getRootElement();
				Element ele = root.element("SenderID");
				senderID = ele.getTextTrim();
				Element eMethod = root.element("Method");
				if (eMethod != null) {
					Element eName = eMethod.element("Name");
					methodName = eName.getTextTrim();
				}
				if(acessDeal.judgeZY(methodName))
				{
					Element eItems = eMethod.element("Items");
					if(eItems!=null)
					{
						List<Element> eItem = eItems.elements("Item");
						tableCode=acessDeal.getTableCode(eItem);//有可能为空
					}
				}
				if(acessDeal.judgeQuery(methodName))
				{
					Element eItems = root.element("Items");
					if(eItems!=null)
					{
						List<Element> eItem = eItems.elements("Item");
						tableCode=acessDeal.getTableCode(eItem);//有可能为空
					}
				}
			}else{
				acessDeal.doreturn(response,"NULL");
				return;
			}
			if(senderID.equals("")&&methodName.equals("")){
				acessDeal.doreturn(response,"NULL");
				return;
			}else{
				String key=senderID+""+methodName;
				if(acessDeal.judgeother(methodName)){
					  chain.doFilter(request, response);//通过
				}else if(acessDeal.judgeRYGX(methodName)){//关联库查询
					//关联库条件判定
					String methodId=serviceIdMethod.getServiceIdMethod(methodName);
					if(authorization.isAuthorization(senderID,methodId)) 
					{ 
					    chain.doFilter(request, response);//通过
					}else{
						acessDeal.doreturn(response,"NO AUTH");
					}
				}else if(acessDeal.judgeGetDataResourceInfo(methodName)){
				//处理结构化查询权限验证		
					if(MemCachedFactory.instance().getMemCached(key)!=null)
					{   //memcached处理
						 delMemCached(key, acessDeal, tableCode, request, response, chain);
					}else{//数据库处理
							//start
							if(acessDeal.judge(methodName/*查询验证*/))
							{
								authorizationDataDao[] objs=null;
								objs=(authorizationDataDao[])authorization.getAuthorizationTablecode4(senderID);
								String tempstr[]=new String[objs.length];
								for(int i=0;i<objs.length;i++)
								{
									tempstr[i]=objs[i].getTablecode();
									SoutProx.sysoutlog("------table "+i+": "+tempstr[i]);
								}
								if(objs.length<=0) //不存在请求的数据表
									flag=false;
								MemCachedFactory.instance().addToMemCached(key, acessDeal.getValueBean(key, null, tempstr,senderID));
								flag=acessDeal.judgeTableCodes(tableCode, tempstr);
								if(flag)
								{   SoutProx.sysoutlog("time6:"+(System.currentTimeMillis()-time1));
								    chain.doFilter(request, response);//通过
								}else{
									SoutProx.sysoutlog("time7:"+(System.currentTimeMillis()-time1));
									acessDeal.doreturn(response,"TABLECODE NOT IN");
									return;
								}
							}else{
								acessDeal.doreturn(response,"NO AUTH");
								return;
							}
							//end
						}
				}else{
				
				if(acessDeal.judgeSpal(methodName))
				{//如果是专用接口采取特殊命名方式
					request.setAttribute(Tools.FLAG, "false");
				}
				/* 先判断memcached是否存在访问权限   如果存在   
				 * 如果memcached不存在去sql查询  
				 * 如果存在 添加到 memcached 返回会结果xml
				 * 如果不存在 返回xml
				 * author : maheqi
				 */
				if(MemCachedFactory.instance().getMemCached(key)!=null)
				{   //memcached处理
					delMemCached(key, acessDeal, tableCode, request, response, chain);
				}else
				{
					String methodId=serviceIdMethod.getServiceIdMethod(methodName);
					if(authorization.isAuthorization(senderID,methodId)) 
					{   
						//start
						if(acessDeal.judge(methodName/*查询验证*/))
						{   
							
							authorizationDataDao[] objs=null;
							boolean zyjk=false;
							if(request.getAttribute(Tools.FLAG)!=null)
								if(((String)request.getAttribute(Tools.FLAG))=="false")
									zyjk=true;
							if(zyjk/*专用接口*/)
							{
								MemCachedFactory.instance().addToMemCached(key, acessDeal.getValueBean(key, methodId, null,senderID));
								chain.doFilter(request, response);//通过
							}
								//objs=(authorizationDataDao[])authorization.getAuthorizationSpecialTablecodeFilter(methodId);
							else
							{
								objs=(authorizationDataDao[])authorization.getAuthorizationTablecode3(senderID,methodId);
							String tempstr[]=new String[objs.length];
							for(int i=0;i<objs.length;i++)
							{
								tempstr[i]=objs[i].getTablecode();
								SoutProx.sysoutlog("------table "+i+": "+tempstr[i]);
							}
							if(objs.length<=0) //不存在请求的数据表
								flag=false;
							MemCachedFactory.instance().addToMemCached(key, acessDeal.getValueBean(key, methodId, tempstr,senderID));
							flag=acessDeal.judgeTableCodes(tableCode, tempstr);
							if(flag)
							{   SoutProx.sysoutlog("time6:"+(System.currentTimeMillis()-time1));
							    chain.doFilter(request, response);//通过
							}else{
								SoutProx.sysoutlog("time7:"+(System.currentTimeMillis()-time1));
								acessDeal.doreturn(response,"TABLECODE NOT IN");
								return;
							}
							}
						}else{
							
							acessDeal.doreturn(response,"NO AUTH");
							return;
						}
						//end
					}
					else {
						acessDeal.doreturn(response,"NO SERVICE");
						SoutProx.sysoutlog("time4:"+(System.currentTimeMillis()-time1));
						return;
					}
				}
			}
		   }
		} catch (Exception e) {
			e.printStackTrace();
			acessDeal.doreturn(response,"EXCEPTION");
			return;
		}
		}
	}
	
	
	public void delMemCached(String key,AcessDeal acessDeal ,String[] tableCode,ServletRequest request, ServletResponse response,
			FilterChain chain){
		
		if(MemCachedFactory.instance().getMemCached(key) instanceof ValueBean&&MemCachedFactory.instance().getMemCached(key)!=null){
			ValueBean vbs=(ValueBean)MemCachedFactory.instance().getMemCached(key);
			
			if(request.getAttribute(Tools.FLAG)!=null)
				if(((String)request.getAttribute(Tools.FLAG))=="false")
				{
					 try {
							chain.doFilter(request, response);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ServletException e) {
							e.printStackTrace();
						}//通过
				}else{
					String tempstr[]=vbs.getStr(); 
					boolean flag=acessDeal.judgeTableCodes(tableCode, tempstr);
					if(flag)
					{   
					    try {
							chain.doFilter(request, response);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ServletException e) {
							e.printStackTrace();
						}//通过
					}else
					{
						acessDeal.doreturn(response,"TABLECODE NOT IN");
						return;
					}
				}
		}else{
			acessDeal.doreturn(response,"NO AUTH");
			return;
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
