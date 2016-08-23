package com.easymap.servletSRV;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;

import com.easymap.base.readxml.Object2XML;
import com.easymap.base.scontrol.Action;
import com.easymap.base.tool.serviceIdMethod;
import com.easymap.modle.SRV.HitDateModle;
import com.easymap.modle.authorization.authorization;

/**
 * 信息碰撞
 * 
 * @author kate
 * 
 */
public class HitData implements Action {

	public void execute(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		Object2XML o2x = new Object2XML();
		String senderID = "";
		String serviceID = "";
		String methodName = "";
		response.setContentType("text/html;charset=GBK");
		try {
			request.setCharacterEncoding("GBK");
			String s = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
			Element Items = null;
			if (request.getParameter("str") != null)
			{
				String st = request.getParameter("str");
				String decodeStr = URLDecoder.decode(st, "utf-8");
				Document document = o2x.xml2Object(decodeStr);
				List<org.dom4j.Element> lists = document
						.selectNodes("/Request");
				for (org.dom4j.Element element : lists) {
					Element ele = element.element("SenderID");
					senderID = ele.getTextTrim();
					Element eMethod = element.element("Method");
					if (eMethod != null) {
						Element eName = eMethod.element("Name");
						methodName = eName.getTextTrim();
					}
					Items = element.element("Items");
				}
			}
			 
				try {
					 
						if (!senderID.equals("")) {
							HitDateModle hdm = new HitDateModle();
							try {
								s += hdm.getXML(senderID, methodName, Items);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							s += "<NODATA>无数据</NODATA>";
						}
					 

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					s += "<ERR>异常</ERR>";
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
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.addHeader("Content-Type", "text/xml");
	}

}
