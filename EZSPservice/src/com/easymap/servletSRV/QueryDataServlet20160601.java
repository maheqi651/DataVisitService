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
import com.easymap.dao.authorizationDataDao;
import com.easymap.filter.SoutProx;
import com.easymap.filter.Tools;
import com.easymap.modle.SRV.SRVQueryDataModle;
import com.easymap.modle.authorization.authorization;

public class QueryDataServlet20160601 implements Action {

	public void execute(HttpServletRequest request, HttpServletResponse response) {
		Object2XML o2x = new Object2XML();
		String senderID = "";
		String methodName = "";
		String dataObjectCode = "";
		String themeCode = "";
		String where = "";
		String fields = "";
		String order = "";
		boolean istotal=true;
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
						for (int j = 0; j < eDatas.size(); j++) 
						{
							Element eData = eDatas.get(j);
							fields += eData.getTextTrim() + ",";
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
						istotal=false;
					}
				}
			}
			//SELECT T.THEMECODE FROM "+Tools.YW6+".EZ_P_FUNCTION_SERVICE T WHERE T.FUNCCODE=? AND T.TABLECODE=?
	       
			if(request.getAttribute(Tools.FLAG)!=null)   //提供运维验证标记;
			{
				 String mid=(String)new ConnectioSDB().executeQuerySingle( "SELECT T.SERVICEID FROM "+Tools.EZSPATIAL+".EZ_SERVICE_INFO T WHERE T.METHODNAME=?",
							new Object[] { methodName})[0];
				 //long times =System.currentTimeMillis();
				if(Boolean.parseBoolean((String)request.getAttribute(Tools.FLAG))==true){
				          themeCode=(String)new ConnectioSDB().executeQuerySingle("SELECT T.THEMECODE FROM "+Tools.YW6+".EZ_P_FUNCTION_SERVICE T WHERE T.FUNCCODE=? AND T.TABLECODE=? AND T.SERCODE=?",
						new Object[] { senderID, dataObjectCode,mid})[0];
				          
				          
				}else{//专用通道
					//专用通道要通过运维分配数据
					      /*themeCode=(String)new ConnectioSDB().executeQuerySingle("SELECT T.THEMECODE FROM "+Tools.YW6+".EZ_P_FUNCTION_SERVICE T WHERE T.FUNCCODE=? AND T.TABLECODE=? AND T.SERCODE=?",
							new Object[] { senderID, dataObjectCode,mid})[0];
							*/
					 System.out.println("专用通道-----");
					 Object[] ob =   new ConnectioSDB().executeQuerySingle( "SELECT T.THEMECODE,T.TABLECODE,T.SERVICEID FROM "+Tools.EZSPATIAL+".EZ_SERVICE_INFO T WHERE T.METHODNAME=?",
					 new Object[] { methodName});
					 themeCode=(String)ob[0];
					 dataObjectCode=(String)ob[1];
					 String serviceid=(String)ob[2];
					 //添加条件限制给各个分局提供
					 authorizationDataDao[] objs=null;
					 objs=(authorizationDataDao[])authorization.getAuthorizationTablecode33(senderID,serviceid);
					 String sqlcondation=" ";
					 if(objs!=null)
					 {
						    for(int i=0;i<objs.length;i++)
							{
						    	if(objs[i].getAccessauth()!=null) 
						    	{
						    		sqlcondation=objs[i].getAccessauth();
						    		System.out.println(where);
						    		if(where.contains("where")||where.contains("WHERE"))
						    		{
						    			where+=" and "+sqlcondation;
						    		}else{
						    			where+=" where "+sqlcondation;
						    		}
						    		break;
						    	}
							}
					 }
					 
					 
				}
				//System.out.println("-----执行前期thmecode------"+(System.currentTimeMillis()-times));
			}else{//脱离运维走ezspatial
				//themeCode=(String)new ConnectioSDB().executeQuerySingle("SELECT T.THEMECODE FROM "+Tools.YW6+".EZ_P_FUNCTION_SERVICE T WHERE T.FUNCCODE=? AND T.TABLECODE=?",
					//	new Object[] { senderID, dataObjectCode})[0];
				            themeCode=(String)new ConnectioSDB().executeQuerySingle( "SELECT T.THEMECODE FROM "+Tools.EZSPATIAL+".EZ_SERVICE_INFO T WHERE T.METHODNAME=?",
							new Object[] { methodName})[0];
			}
			
	            System.out.println(themeCode+"++++"+senderID+"++++"+dataObjectCode);
				SRVQueryDataModle sqdr = new SRVQueryDataModle();
				try {
					s += sqdr.getXML(senderID, dataObjectCode, themeCode,methodName,
							where, fields, order, start, max,istotal);
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
