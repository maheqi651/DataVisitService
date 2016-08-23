package com.easymap.memcached.guard;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.Set;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.dom4j.Document;
import org.dom4j.Element;

import com.easymap.base.pools.glk.bean.DicMapLoad;
import com.easymap.base.pools.glk.bean.EntryTools;
import com.easymap.base.queue.engine.MyAbstractITaskEngine;
import com.easymap.base.readxml.Object2XML;
import com.easymap.base.tool.ReadProperties;
import com.easymap.dao.authorizationDataDao;
import com.easymap.filter.AcessDeal;
import com.easymap.filter.SoutProx;
import com.easymap.filter.Tools;
import com.easymap.memcached.MemCachedFactory;
import com.easymap.memcached.hessian.servlet.ProxyHessianFactory;
import com.easymap.modle.authorization.authorization;

 

/**
 * Author cloudMa
 * 20150429 safe guard 
 */
public class SafeGuard implements ServletContextListener {
    
    /**
     * Default constructor. 
     */
   
    public SafeGuard() {
        // TODO Auto-generated constructor stub
    }
	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
    	 
    	String confpath =Tools.CONTEXTPATH;
    	//String confpath = arg0.getServletContext().getRealPath("/")+"config\\config.properties";
    	//confpath=confpath.replace("/", "\\");
		InputStream confis = arg0.getServletContext().getResourceAsStream(confpath);
		try {
			ReadProperties.bundle = new PropertyResourceBundle(confis);
		} catch (Exception e) {
			e.printStackTrace();
		}                                   
		Tools.EZSPATIAL=ReadProperties.get("EZSPATIAL");
		
		EntryTools.WPXMLPATH=ReadProperties.get("WPRELATIONXML");
		EntryTools.XMLPATH=ReadProperties.get("RELATIONXML");
		EntryTools.ISSTARTFW=ReadProperties.get("ISSTARTFW");
		EntryTools.HESSIANURL=ReadProperties.get("HESSIANURL");
		Tools.YW6=ReadProperties.get("YW6");
	    String DICCOLUM=ReadProperties.get("DICCOLUM");
	    Tools.DICCOLUM=DICCOLUM.split(",");
    	authorizationDataDao[] objs=(authorizationDataDao[])authorization.getAuthorizationSpecialTablecode();
    	String tempstr[]=new String[objs.length];
		for(int i=0;i<objs.length;i++)
		{
			tempstr[i]=objs[i].getMethodname();
			SoutProx.sysoutlog("------table "+i+": "+tempstr[i]);
		}
        AcessDeal acessDeal=new AcessDeal();
        MemCachedFactory.instance().addToMemCached(Tools.CONTROLLINK, acessDeal.getValueBean(Tools.CONTROLLINK, null, tempstr,null));
    	Timer timer = new Timer();
        timer.schedule(new SafeTask(), 0,Tools.CHECKTIME*60*1000);
       //启动定时器
       //启动字典加载程序
        try {
     	    System.out.println("字典翻译加载开始.........");
			DicMapLoad.dealDicMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		dealRelationXML2();
		dealWPXML2(); //物品关系加载
		//启动访问次数维护程序
		if("true".equals(EntryTools.ISSTARTFW)){
			 ProxyHessianFactory.getInstance().start(); 
		}
	  }
    public String getxml(String path){
		  path=path.replace("\\", "/");
		  System.out.println(path);
		  BufferedReader br=null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf-8"));
			} catch (UnsupportedEncodingException e2) {
				e2.printStackTrace();
			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			} 
	     
	     String data = null; 
	     String s ="";
	       try {
			while((data = br.readLine())!=null){ 
			    	s+=data;
			    }
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		return s;
	}
    
    public void dealRelationXML(){
    	String xmlstr=getxml(EntryTools.XMLPATH);
    	Object2XML o2x = new Object2XML();
    	Document document = o2x.xml2Object(xmlstr);
    	Element root = document.getRootElement();
		List<Element> tableslist = root.elements("tables");
		for(Element tables:tableslist)
		{
			String tablesname=tables.attributeValue("name");
			List<Element> tablelist=tables.elements("table");
			for(Element table:tablelist){
				String[] str=new String[2];
				str[0]=table.attribute("chname").getText();
				str[1]=tablesname;
				EntryTools.FROMBJ.put(table.getTextTrim(), str);
			}
		}
		Set<String> keys=EntryTools.FROMBJ.keySet();
		for(String key:keys)
		{
			System.out.println(key+":"+EntryTools.FROMBJ.get(key)[0]+":"+EntryTools.FROMBJ.get(key)[1]);
		}
    }
    
    
    public void dealRelationXML2(){
    	String xmlstr=getxml(EntryTools.XMLPATH);
    	Object2XML o2x = new Object2XML();
    	Document document = o2x.xml2Object(xmlstr);
    	Element root = document.getRootElement();
		List<Element> categorylist = root.elements("category");
		for(Element category:categorylist)
		{
			List<Element> labellist=category.elements("label");
		 	for(Element label:labellist){
				//遍历
				
				String lablename=label.attribute("name").getText();
				List<Element> tablelist=label.elements("table");  
				for(Element table:tablelist)
				{		 
				String[] str=new String[2];
				str[0]=table.attribute("cname").getText();
				str[1]=lablename;
				//System.out.println(lablename+"---=====");
				EntryTools.FROMBJ.put(table.attribute("name").getText(), str);
				}
			}
		}
		Set<String> keys=EntryTools.FROMBJ.keySet();
		for(String key:keys)
		{
			System.out.println(key+":"+EntryTools.FROMBJ.get(key)[0]+":"+EntryTools.FROMBJ.get(key)[1]);
		}
		//System.out.println("----");
    }
    
    
    
    public void dealWPXML2()
    {
    	String xmlstr=getxml(EntryTools.WPXMLPATH);
    	Object2XML o2x = new Object2XML();
    	Document document = o2x.xml2Object(xmlstr);
    	Element root = document.getRootElement();
		List<Element> categorylist = root.elements("category");
		for(Element category:categorylist)
		{
			List<Element> labellist=category.elements("label");
		    for(Element label:labellist){
				//遍历
				
				String lablename=label.attribute("name").getText();
				List<Element> tablelist=label.elements("table");  
				for(Element table:tablelist)
				{		 
				String[] str=new String[2];
				str[0]=table.attribute("cname").getText();
				str[1]=lablename;
				//System.out.println(lablename+"---=====");
				EntryTools.FROMWPBJ.put(table.attribute("name").getText(), str);
				}
			}
		}
		Set<String> keys=EntryTools.FROMWPBJ.keySet();
		for(String key:keys)
		{
			System.out.println(key+":"+EntryTools.FROMWPBJ.get(key)[0]+":"+EntryTools.FROMWPBJ.get(key)[1]);
		}
		//System.out.println("----");
		
    }
	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }
	public static void main(String[] str){
		new SafeGuard().dealRelationXML2();
		
		
		
	}
}
