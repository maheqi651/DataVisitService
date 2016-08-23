package com.easymap.servletSRV;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import com.easymap.base.readxml.Object2XML;
import com.easymap.base.scontrol.Action;
import com.easymap.base.tool.serviceIdMethod;
import com.easymap.modle.SRV.srvTestServiceModle;
import com.easymap.modle.authorization.authorization;
/**
 * 测试服务
 * @author kate
 *
 */
public class testService implements Action {

	public void execute(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		Object2XML o2x = new Object2XML();
		String senderID="";
		String serviceID="";
		String methodName="";
		String s="<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		String str=null;
		try {
			str = IOUtils.toString(request.getInputStream(),"utf-8");
			if("".equals(str)||str==null)
			    str=(String)request.getAttribute("str");
			 if("".equals(str)||str==null)
				str= URLDecoder.decode(request.getParameter("str"), "utf-8");
		} catch (IOException e1) {
			 e1.printStackTrace();
		}
		 
		if(!("".equals(str)||str==null)){
			Document document=o2x.xml2Object(str);
			List<org.dom4j.Element> lists = document.selectNodes("/Request");
			for (org.dom4j.Element element : lists) 
			{  
				Element ele = element.element("SenderID");
				senderID=ele.getTextTrim();
				Element eMethod = element.element("Method");
				if(eMethod!=null){
				Element eName =  eMethod.element("Name");
				methodName = eName.getTextTrim();
				Element eItems = eMethod.element("Items");
				Element eItem = eItems.element("Item");
				Element eIName = eItem.element("Name");
				Element eValue = eItem.element("Value");
				Element eData = eValue.element("Data");
				serviceID=eData.getTextTrim();
				}
			}
		 
				try {
						srvTestServiceModle stsm = new srvTestServiceModle();
						try {
							s+=stsm.getXML(serviceID, senderID, methodName);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
		}else{
			s+="<NODATA>xml无数据</NODATA>";
		}
		
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.print(s);
		out.flush();
		out.close();
		response.addHeader("Content-Type", "text/xml");
	}

}
