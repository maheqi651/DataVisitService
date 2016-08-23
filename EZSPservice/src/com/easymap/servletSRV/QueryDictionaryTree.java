package com.easymap.servletSRV;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import com.easymap.base.readxml.Object2XML;
import com.easymap.base.scontrol.Action;
import com.easymap.base.tool.serviceIdMethod;
import com.easymap.modle.SRV.DicModle;
import com.easymap.modle.SRV.srvDirectoryModle;
import com.easymap.modle.authorization.authorization;

/**
 * 
 * @author kate
 *
 */
public class QueryDictionaryTree implements Action{

	@SuppressWarnings("unchecked")
	public void execute(HttpServletRequest request, HttpServletResponse response) { 
		try {
			response.setContentType("text/html;charset=GBK");
			 String str = IOUtils.toString(request.getInputStream(),"utf-8");
			 if("".equals(str)||str==null)
				 str=(String)request.getAttribute("str");
			 if("".equals(str)||str==null)
					str= URLDecoder.decode(request.getParameter("str"), "utf-8");
			 String s="<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		  	 if(str!=null){
				String decodeStr = str;  
				Object2XML o2x = new Object2XML();
				Document document=o2x.xml2Object(decodeStr);
				List<org.dom4j.Element> lists = document.selectNodes("/Request/SenderID");
				String SenderID="";
				String type="";
			    Map<String, String> map=new HashMap<String, String>();
				for (org.dom4j.Element element : lists) 
				{  
					SenderID=element.getTextTrim();
				}
				if(SenderID.equals("")){//senderid为空时返回错误信息
					s+="<NODATA>SenderID NOT NULL/NODATA>";
				}else{
				map.put("SenderID", SenderID);
				System.out.println("SenderID--------------:"+str);
				List<org.dom4j.Element> list2 = document.selectNodes("/Request/Method/Name");
				String mn="";
				for (org.dom4j.Element elements : list2) { 
					mn=elements.getTextTrim();
				}
					  
						List<org.dom4j.Element> list1 = document.selectNodes("/Request/Method/Items/*");
						for (org.dom4j.Element element : list1) { 
							Element nameele = element.element("Name");
							Element value = element.element("Value");
							Element valuedata = value.element("Data");
							map.put(nameele.getTextTrim(), valuedata.getTextTrim());
						}
						DicModle sd=	new DicModle();
					s+=sd.getQueryXML(map, SenderID,mn);
				}
			}else{
				s+="<NODATA>NULL/NODATA>";
			}
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = null;
			try {
				out = response.getWriter();
			} catch (IOException e) {
				e.printStackTrace();
			}
			out.print(s);
			out.flush();
			out.close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.addHeader("Content-Type", "text/xml");
		}
}
