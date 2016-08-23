package com.easymap.servletSRV;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import com.easymap.base.readdatabase.ConnectionDB;
import com.easymap.base.readxml.Object2XML;
import com.easymap.base.scontrol.Action;
import com.easymap.base.tool.JDBC_Operator;
import com.easymap.base.tool.serviceIdMethod;
import com.easymap.dao.QueryDataDao;
import com.easymap.modle.SRV.SRVQueryData2Modle;
import com.easymap.modle.authorization.authorization;

public class QueryData2 implements Action {
	private QueryDataDao qdd;

	public void execute(HttpServletRequest request, HttpServletResponse response) {
		Object2XML o2x = new Object2XML();
		String senderID = "";
		String methodName = "";
		String from = "";
		String where = "";
		String fields = "";
		String order = "";
		String groupby = "";
		String themeid="";
		String s = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		try {
			//String str = URLDecoder.decode(request.getParameter("str"), "utf-8");
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
				for (int i = 0; i < eItem.size(); i++) {
					Element e = eItem.get(i);
					Element eIName = e.element("Name");
					if (eIName.getTextTrim().equals("SelectList")) {
						fields = createField(e);
					} else if (eIName.getTextTrim().equals("DataReference")) {
						from = createFrom(e,senderID);
					} else if (eIName.getTextTrim().equals("QueryCondition")) {
						where = createWhere(e);
					} else if (eIName.getTextTrim().equals("GroupCondition")) {
						groupby += "GROUP BY ";
						groupby += createGroupBy(e);
					} else if (eIName.getTextTrim().equals("Order"))
					{   
						order += "ORDER BY ";
						order += createOrder(e);
					}
				}
			}
                
			    //qdd=  new QueryDataDao(new ConnectionDB(conn.getConnection(code,null)));
				SRVQueryData2Modle sqdr = new SRVQueryData2Modle(qdd);
				try {
					s += sqdr.getXML(senderID, from, methodName, where, fields,
							order, groupby);
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.print(s);
		out.flush();
		out.close();
		response.addHeader("Content-Type", "text/xml");
	}
	private String createFrom(Element e,String senderId) {
		String from = "";
		Element value = e.element("Value");
		List<Element> items = value.element("Items").elements("Item");
		Map<String, String> tables = new HashMap<String, String>();
		List<String> code = new ArrayList<String>();
		for (int i = 0; i < items.size(); i++) 
		{
			Element item = items.get(i);
			String type = item.attributeValue("Type");
			if (type.equals("resource"))
				code.add(item.getTextTrim());
		}
		ConnectionDB conn1 = new ConnectionDB();
		qdd = new QueryDataDao(new ConnectionDB(conn1.getConnection(code,senderId)));
		for (int i = 0; i < items.size(); i++) {
			ConnectionDB conn = new ConnectionDB();
			QueryDataDao qdd1 = new QueryDataDao(conn);
			Element item = items.get(i);
			String alias = item.attributeValue("Alias");
			String type = item.attributeValue("Type");
			String table = "";
			if (type.equals("resource"))
				table = qdd1.getTableNameByCode(item.getTextTrim());
			else
				table = "(" + item.getTextTrim() + ")";
			tables.put(alias, table);
		}
		List<Element> relations = value.element("Relations").elements(
				"Relation");
		for (int i = 0; i < relations.size(); i++) {
			Element relation = relations.get(i);
			Element alias1 = relation.element("Alias1");
			Element alias2 = relation.element("Alias2");
			Element type = relation.element("Type");
			from += "(" + tables.get(alias1.getTextTrim()) + " "
					+ alias1.getTextTrim() + " ";
			from += type.getTextTrim() + " ";
			from += tables.get(alias2.getTextTrim()) + " "
					+ alias2.getTextTrim();
			Element condition = relation.element("Condition");
			if (condition != null) {
				Element field1 = condition.element("Field1");
				Element field2 = condition.element("Field2");
				Element op = condition.element("op");
				from += " ON " + alias1.getTextTrim() + "."
						+ field1.getTextTrim();
				from += " "
						+ JDBC_Operator.translate_Operator(op.getTextTrim())
						+ " ";
				from += alias2.getTextTrim() + "." + field2.getTextTrim();
			}
			from += ")";
		}
		return from;
	}

	private String createWhere(Element e) {
		String where = "";
		Element condition = e.element("Value").element("Condition");
		Element item1 = (Element) condition.elements("Item").get(0);
		if (item1.attributeValue("Type").equals("complex")) {
			where += createWhereItem(item1);
		} else if (item1.attributeValue("Type").equals("field")) {
			where += item1.attributeValue("Alias") + "." + item1.getTextTrim();
		} else {
			where += item1.getTextTrim();
		}
		Element op = condition.element("op");
		where += " " + JDBC_Operator.translate_Operator(op.getTextTrim()) + " ";
		Element item2 = (Element) condition.elements("Item").get(1);
		if (item2.attributeValue("Type").equals("complex")) {
			where += createWhereItem(item2);
		} else if (item2.attributeValue("Type").equals("field")) {
			where += item2.attributeValue("Alias") + "." + item2.getTextTrim();
		} else {
			where += item2.getTextTrim();
		}
		return where;
	}

	private String createWhereItem(Element item) {
		String where = "";
		Element item1 = (Element) item.elements("Item").get(0);
		if (item1.attributeValue("Type").equals("complex")) {
			where += createWhereItem(item1);
		} else if (item1.attributeValue("Type").equals("field")) {
			where += item1.attributeValue("Alias") + "." + item1.getTextTrim();
		} else {
			where += item1.getTextTrim();
		}
		Element op = item.element("op");
		where += " " + JDBC_Operator.translate_Operator(op.getTextTrim()) + " ";
		Element item2 = (Element) item.elements("Item").get(1);
		if (item2.attributeValue("Type").equals("complex")) {
			where += createWhereItem(item2);
		} else if (item2.attributeValue("Type").equals("field")) {
			where += item2.attributeValue("Alias") + "." + item2.getTextTrim();
		} else {
			where += item2.getTextTrim();
		}
		return where;
	}
	private String createGroupBy(Element e) {
		String group = "";
		Element value = e.element("Value");
		List<Element> items = value.element("Items").elements("Item");
		for (int j = 0; j < items.size(); j++) {
			Element item = items.get(j);
			String alias = item.attributeValue("Alias");
			group += alias + "." + item.getTextTrim() + ",";
		}
		if (group.endsWith(","))
			group = group.substring(0, group.lastIndexOf(","));
		Element condition = e.element("Condition");
		if (condition != null) {
			Element item1 = (Element) condition.elements("Item").get(0);
			Element item2 = (Element) condition.elements("Item").get(1);
			Element op = condition.element("op");
			group += " HAVING " + item1.attributeValue("Attr") + "("
					+ item1.attributeValue("Alias") + "." + item1.getTextTrim()
					+ ")";
			group += " " + JDBC_Operator.translate_Operator(op.getTextTrim())
					+ " ";
			group += item2.getTextTrim();
		}
		return group;
	}

	private String createOrder(Element e) {
		String order = "";
		Element value = e.element("Value");
		List<Element> items = value.element("Items").elements("Item");
		for (int j = 0; j < items.size(); j++) {
			Element item = items.get(j);
			String alias = item.attributeValue("Alias");
			order += alias + "." + item.getTextTrim() + ",";
		}
		if (order.endsWith(","))
			order = order.substring(0, order.lastIndexOf(","));
		return order;
	}

	private String createField(Element e) {
		String fields = "";
		Element value = e.element("Value");
		Element prefix = value.element("Prefix");
		if (prefix != null)
			fields += prefix.element("ResultFilter").getTextTrim() + " ";
		List<Element> items = value.element("Items").elements("Item");
		for (int j = 0; j < items.size(); j++) {
			Element item = items.get(j);
			String alias = item.attributeValue("Alias");
			String attr = item.attributeValue("Attr");
			fields += attr + "(" + alias + "." + item.getTextTrim() + "),";
		}
		if (fields.endsWith(","))
			fields = fields.substring(0, fields.lastIndexOf(","));
		return fields;
	}

}
