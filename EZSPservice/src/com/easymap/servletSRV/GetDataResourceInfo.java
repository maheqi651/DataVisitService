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

import com.easymap.base.readdatabase.ConnectioSDB;
import com.easymap.base.readxml.Object2XML;
import com.easymap.base.scontrol.Action;
import com.easymap.filter.Tools;
import com.easymap.modle.SRV.GetDataResourceInfoModle;
import com.easymap.modle.SRV.SRVQueryDataModle;
import com.easymap.modle.SRV.srvTestServiceModle;

public class GetDataResourceInfo implements Action {

	public void execute(HttpServletRequest request, HttpServletResponse response) {
		Object2XML o2x = new Object2XML();
		String senderID = "";
		String methodName = "";
		String dataObjectCode = "";
		String themeCode = "";
		String where = "";
		String fields = "";
		String order = "";
		long start = 0;
		long max = 0;
		String s = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		try {
			 String str = IOUtils.toString(request.getInputStream(),"utf-8");
			 if("".equals(str)||str==null)
				 str=(String)request.getAttribute("str");
			 if("".equals(str)||str==null)
					str= URLDecoder.decode(request.getParameter("str"), "utf-8");
			  
			if (str != null) {
				Document document = o2x.xml2Object(str);
				Element root = document.getRootElement();
				Element ele = root.element("SenderID");
				senderID = ele.getTextTrim();
				Element eMethod = root.element("Method");
				Element eName = eMethod.element("Name");
				methodName = eName.getTextTrim();
				Element eItems = eMethod.element("Items");
				List<Element> eItem = eItems.elements("Item");
				for(int i = 0; i < eItem.size(); i++) {
					Element e = eItem.get(i);
					Element eIName = e.element("Name");
					if (eIName.getTextTrim().equals("DataResourceID")) {
						dataObjectCode = e.element("Value").element("Data").getTextTrim();
					}    
				}
			}
			 
			 
			try {
				GetDataResourceInfoModle stsm = new GetDataResourceInfoModle();
				try {
					s+=stsm.getXML(senderID, methodName, dataObjectCode);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		} catch (Exception e)
		{
			e.printStackTrace();
			s += "<ERR>" + e.getMessage() + "</ERR>";
		}
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		out.print(s);
		out.flush();
		out.close();
		response.addHeader("Content-Type", "text/xml");
	}

}
