package com.easymap.servletSRV;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import com.easymap.base.pools.glk.bean.EntryTools;
import com.easymap.base.readdatabase.ConnectioSDB;
import com.easymap.base.readxml.Object2XML;
import com.easymap.base.scontrol.Action;
import com.easymap.dao.authorizationDataDao;
import com.easymap.filter.SoutProx;
import com.easymap.filter.Tools;
import com.easymap.modle.SRV.GQueryJDCXXModle;
import com.easymap.modle.SRV.GQueryRYXXModle;
import com.easymap.modle.SRV.SRVQueryDataModle;
import com.easymap.modle.authorization.authorization;

public class GQueryJDCXX implements Action {

	public void execute(HttpServletRequest request, HttpServletResponse response) {
		Object2XML o2x = new Object2XML();
		String senderID = "";
		String methodName = "";
		String dataObjectCode = "";
		String themeCode = "";
		String where = "";
		String fields = "";
		String order = "";
		List<String> fildslist=new ArrayList<String>();
		List<String> translist=new ArrayList<String>();
		boolean istotal=false;
		long start = 0;
		long max = 0;
		int flags=-1;
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
				Element eItems = root.element("Items");
				List<Element> eItem = eItems.elements("Item");
				for(int i = 0; i < eItem.size(); i++) {
					Element e = eItem.get(i);
					Element eIName = e.element("Name");
					if (eIName.getTextTrim().equals("DataObjectCode")) {
						dataObjectCode = e.element("Value").element("Data")
								.getTextTrim();
					}else if (eIName.getTextTrim().equals("DataThemeCode")) {
						themeCode = e.element("Value").element("Data")
								.getTextTrim();
					} else if (eIName.getTextTrim().equals("Condition")) {
						String str1 = e.element("Value").element("Data").getText();
						if (!str1.equals(""))
							where = " where " + str1;
					} else if (eIName.getTextTrim().equals("RequiredItems")) {
						List<Element> eDatas = e.element("Value").elements(
								"Data");
						int counts=0;
						for (int j = 0; j < eDatas.size(); j++) {
							Element eData = eDatas.get(j);
							
							if(EntryTools.WPJDCJBXX.contains(eData.getTextTrim()))
							{//包含在基本信息
								if("ID".equals(eData.getTextTrim())||"id".equals(eData.getTextTrim()))
								{
									flags=counts;
								}
								counts++;
								fields += eData.getTextTrim() + ",";
							}else{//非基本信息表单独处理
								fildslist.add(eData.getTextTrim());
							}
							 if(eDatas.get(j).attribute("Trans")!=null)
							 {
								 if("true".equals(eDatas.get(j).attribute("Trans").getText()))
								 {
									 translist.add(eData.getTextTrim()); 
								 }
							 }
							
						}
						if(flags<0)
						{
							fields="ID,"+fields;
							
							//添加到第flags位
						}
						if (fields.endsWith(","))
							fields = fields.substring(0,
									fields.lastIndexOf(","));
					} else if (eIName.getTextTrim().equals("Order")) {
						List<Element> eDatas = e.element("Value").elements(
								"Data");
						order = " order by ";
						for (int j = 0; j < eDatas.size(); j++) 
						{
							Element eData = eDatas.get(j);
							 
							String strorder=eData.getTextTrim();
							if(strorder.contains("-"))
							{
								order += strorder.replace("-", "")+" DESC";
							}else {
								order += strorder.replace("+", "")+" ASC";
							}
							
						}
/*						if (order.endsWith(","))
							order = order.substring(0, order.lastIndexOf(","));*/
					} else if (eIName.getTextTrim().equals("StartPosition")) {
						start = Long.parseLong(e.element("Value")
								.element("Data").getTextTrim());
					} else if (eIName.getTextTrim().equals("MaxResultCount")) {
						max = Long.parseLong(e.element("Value").element("Data")
								.getTextTrim());
					} else if (eIName.getTextTrim().equals("RequestResultCount")) {
						istotal=true;
					}
				}
			}
				GQueryJDCXXModle sqdr = new GQueryJDCXXModle();
				try {
					s += sqdr.getXML(senderID, methodName,
							where, fields, order, start, max,istotal,fildslist,translist,flags);
				} catch (Exception e) 
				{
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
