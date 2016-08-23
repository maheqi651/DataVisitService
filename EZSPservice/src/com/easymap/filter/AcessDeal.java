package com.easymap.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletResponse;

import org.dom4j.Element;

import com.easymap.memcached.guard.ValueBean;

public class AcessDeal {
	
	   public String[] getTableCode(List<Element> liste){
	    	String[] tablecodestr=null;
	    	for (int i = 0; i < liste.size(); i++) {
				Element e = liste.get(i);
				Element eIName = e.element("Name");
				if (eIName.getTextTrim().equals("DataObjectCode")) {
					tablecodestr=new String[1];
					tablecodestr[0]=e.element("Value").element("Data").getTextTrim();
					break;//ResourceList
				} else if (eIName.getTextTrim().equals("DataReference")) {
					 
					Element value = e.element("Value");
					List<Element> items = value.element("Items").elements("Item");
					List<String> code = new ArrayList<String>();
					for (int j = 0; j < items.size(); j++) {
						Element item = items.get(j);
						String type = item.attributeValue("Type");
						if (type.equals("resource"))
							code.add(item.getTextTrim());
					}
					if(code.size()>0){
						 tablecodestr=new String[code.size()];
						for(int ii=0;ii<code.size(); ii++)
						{
							tablecodestr[ii]=code.get(ii);
						SoutProx.sysoutlog(ii+"条"+code.get(ii));
						}
					}
				}else if(eIName.getTextTrim().equals("ResourceList"))
				{
					Element value = e.element("Value");
					List<Element> items = value.element("Items").elements("Item");
					List<String> code = new ArrayList<String>();
					for (int j = 0; j < items.size(); j++) {
						Element item = items.get(j);
						String type = item.attributeValue("Type");
						if (type.equals("resource"))
							{  
							  Element codeele=item.element("Code");
							  code.add(codeele.getTextTrim());
							}
					}
					if(code.size()>0){
						 tablecodestr=new String[code.size()];
						for(int ii=0;ii<code.size(); ii++)
						{
							tablecodestr[ii]=code.get(ii);
						    SoutProx.sysoutlog(ii+"条"+code.get(ii));
						}
					}
				}
				else if(eIName.getTextTrim().equals("DataResourceID")){
					tablecodestr=new String[1];
					tablecodestr[0]=e.element("Value").element("Data").getTextTrim();
					break;//ResourceList
				}
	    	 }
	    	   //SoutProx.sysoutlog (tablecodestr.length);
	    	   return tablecodestr;
	    }
	    public void doreturn(ServletResponse response,String question)
	    {
	    	String s="";
	    	s = "<?xml version=\"1.0\" encoding=\"utf-8\"?><NOAuthorization>"+question+"</NOAuthorization>";
			//response.setCharacterEncoding("utf-8");
		 
			PrintWriter out = null;
			try {
				out = response.getWriter();
			} catch (IOException e) {
				 
				e.printStackTrace();
			}
			out.print(s);
			out.flush();
			out.close();
			return;
	    }
	    public boolean judgeSpal(String mtname){
			if(mtname.contains("Query")&&!mtname.contains("QueryData"))
		    	   return true;
			return false;
		}
	    public boolean judge(String mtname){
			if(mtname.equals("GetDataResourceInfo")||(mtname.contains("Query")||mtname.contains("HitData"))&&!mtname.contains("QueryDataResource"))
		    	   return true;
			return false;
		}
	    public boolean judgeZY(String mtname){//判断该查询服务是不是查询
	    	if(mtname.equals("GetDataResourceInfo"))
	    	   return true;
	    	   return false;
	    }
	    public boolean judgeQuery(String mtname){//判断该查询服务是不是查询
	    	if((mtname.contains("Query")||mtname.contains("HitData"))&&!mtname.contains("QueryDataResource"))
	    	   return true;
	    	   return false;
	    }
	    public boolean judgeother(String mtname)
	    {
	    	   if(mtname.equals("QueryDictionaryCode")||mtname.equals("EnumDictionaryCode")||mtname.equals("QueryDictionaryTree")||mtname.equals("GetServiceInfo")||mtname.equals("SearchService")||mtname.equals("TestService")||mtname.equals("EnumService")||mtname.equals("EnumDictionary")||mtname.equals("QueryDictionary")||mtname.equals("QueryTableCode"))
	    		   return true;
	    	    return false;
	    }
	    public boolean judgeRYGX(String mtname)
	    {       
	    	   if(mtname.equals("G_Query_JDCXX_Source")||mtname.equals("G_Query_JDCXX")||mtname.equals("G_Query_RYBJ")||mtname.equals("G_Query_RYXX")||mtname.equals("G_Query_RYXX_Source"))
	    		   return true;
	    	    return false;
	    }
	    public boolean judgeGetDataResourceInfo(String mtname)
	    {
	    	   if(mtname.equals("GetDataResourceInfo"))
	    		   return true;
	    	    return false;
	    }
	    public ValueBean getValueBean(String key,String mtd,String[] str,String senderId){
	    	ValueBean vb=new ValueBean();
		    vb.setKey(key);
		    vb.setMhtd(mtd);
		    vb.setStr(str);
		    System.out.println("senderId------111----"+senderId);
		    vb.setSenderId(senderId);
		    vb.setTimes(System.currentTimeMillis());
		    return vb;
	    }
	    public boolean judgeTableCodes(String[] tableCode,String[] tempstr){
	    	boolean flag=true;
	    	
	    	if(tableCode!=null){
	    	for(String strtemp:tableCode){
				boolean flaga=false;
				SoutProx.sysoutlog(strtemp+"-----22");
				if(strtemp.length()>0){
					if(tempstr!=null)
					for(String e:tempstr){
						SoutProx.sysoutlog(e+"-----11");
						if(strtemp.equals(e)&&!"".equals(strtemp))
						{
							flaga=true;
						}
					}
				}
				if(!flaga)
				{
					flag=false;
					break;
				}
			}
	    	}else
	    		return false;
	    	return flag;
	    }
	    
}
