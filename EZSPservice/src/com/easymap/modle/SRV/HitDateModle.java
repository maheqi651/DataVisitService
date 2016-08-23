package com.easymap.modle.SRV;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.easymap.base.minterface.modle;
import com.easymap.base.readdatabase.ConnectionDB;
import com.easymap.base.tool.GetDBInfo;
import com.easymap.base.tool.JDBC_Operator;
import com.easymap.base.tool.serviceIdMethod;

public class HitDateModle implements modle {
	List<String> code = new ArrayList<String>();

	public Object[] getDateObjModle(Map map) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSet getDateObjModle(String sql) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getXML(String senderID, String methodName, Element SqlItem)
			throws SQLException {
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("Response");
		Element SenderID = rootElement.addElement("SenderID");
		SenderID.setText(senderID);
		Element ServiceID = rootElement.addElement("ServiceID");
		try {
			ServiceID.setText(serviceIdMethod.getServiceIdMethod(methodName));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Element Method = rootElement.addElement("Method");
		Element Name = Method.addElement("Name");
		Name.setText(methodName);
		Element Security = Method.addElement("Security");
		Security.addAttribute("Algorithm", "");
		Security.setText("");
		// ���ò�ѯ���
		String fields = "";
		String from = "";
		String where = "";
		String groupby = "";
		String sql1 = "";
		String sql2 = "";
		String pztj = "";
		String endsql = "";
		List<Element> seItem = SqlItem.elements("Item");
		for (int i = 0; i < seItem.size(); i++) {
			Element e = seItem.get(i);
			Element eIName = e.element("Name");
			if (eIName.getTextTrim().equals("ResourceList")) {
				List<Element> items = e.element("Value").element("Items")
						.elements("Item");
				try {
					sql1 = createSql(items.get(0));
					sql2 = createSql(items.get(1));
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else if (eIName.getTextTrim().equals("Conditions")) {
				sql1 += " where " + createWhere(e);
				sql2 += " where " + createWhere(e);
			} else if (eIName.getTextTrim().equals("HitMethod")) {
				Element data = e.element("Value").element("Data");
				pztj = data.getTextTrim();
			} else if (eIName.getTextTrim().equals("Order")) {
				String s = createGroupBy(e);
				groupby = " ORDER BY " + s.substring(0, s.length() - 1);
			}
		}
		if (pztj.equals("Intersect")) {
			endsql = sql1 + " INTERSECT " + sql2;
		} else {
			endsql = sql1 + " MINUS " + sql2;
		}
		if (!groupby.equals("")) {
			endsql += " " + groupby;
		}
		List<Object> lrs = getResultSet(endsql);
		ResultSet rs = getResultSet1(endsql);
		Element Items = Method.addElement("Items");
		Element Item = Items.addElement("Item");
		Element NameI = Item.addElement("Name");
		NameI.setText("ResultInfo");
		Element vaI = Item.addElement("Value").addAttribute("Type", "Fields");
		ResultSetMetaData rsMetaData = null;
		int columnCount = 0;
		try {
			rsMetaData = rs.getMetaData();
			columnCount = rsMetaData.getColumnCount();
			if (columnCount > 0) {
				for (int i = 1; i < columnCount + 1; i++) {
					String s = rsMetaData.getColumnTypeName(i);
					Element sd = vaI.addElement("Data").addAttribute("type", s);
					sd.setText(rsMetaData.getColumnName(i));
				}
			}
			Element Item2 = Items.addElement("Item");
			Element NameI2 = Item2.addElement("Name");
			NameI2.setText("Result");
			Element Records = Item2.addElement("Value")
					.addAttribute("Type", "Records").addElement("Records");
			if (lrs != null && lrs.size() > 0) {
				for (int i = 0; i < lrs.size(); i++) {
					Element Record = Records.addElement("Record");
					String s = lrs.get(i).toString();
					String[] sa = s.substring(1, s.length() - 1).split(",");
					for (int l = sa.length - 1; l >= 0; l--) {
						Record.addElement("Data").setText(sa[l].split("=")[1]);
					}
				}
			} else {
				Records.setText("�޼�¼");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			vaI.addElement("ERR").setText("��ѯ�쳣");
		}
		rs.close();
		return rootElement.asXML();
	}

	public String getXML(Map map, String senderID, String methodName)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private String createSql(Element item) throws SQLException {
		String from = "";
		String rst = "";
		String table = "";
		String fs = "";
		String Code = item.element("Code").getTextTrim();
		code.add(Code);
		Element ReturnFields = item.element("ReturnFields");
		if (!Code.equals("")) {
			table = getTableName(Code);
		}
		if (ReturnFields != null) {
			List<Element> f = ReturnFields.elements("Field");
			for (int j = 0; j < f.size(); j++) {
				Element itemj = f.get(j);
				fs += itemj.getTextTrim() + ",";
			}
		}
		GetDBInfo g = new GetDBInfo(Code);
		rst = "select " + fs.substring(0, fs.length() - 1) + " from "
				+ g.getUser() + "." + table;
		return rst;
	}

	private String createWhere(Element e) {
		String where = "";
		Element condition = e.element("Value").element("Condition");
		Element item1 = (Element) condition.elements("Item").get(0);
		if (item1.attributeValue("Type").equals("complex")) {
			where += createWhereItem(item1);
		} else if (item1.attributeValue("Type").equals("field")) {
			where += item1.getTextTrim();
		} else {
			where += item1.getTextTrim();
		}
		Element op = condition.element("op");
		where += " " + JDBC_Operator.translate_Operator(op.getTextTrim()) + " ";
		Element item2 = (Element) condition.elements("Item").get(1);
		if (item2.attributeValue("Type").equals("complex")) {
			where += createWhereItem(item2);
		} else if (item2.attributeValue("Type").equals("field")) {
			where += item2.attributeValue("const") + "." + item2.getTextTrim();
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
			where += item1.getTextTrim();
		} else {
			where += item1.getTextTrim();
		}
		Element op = item.element("op");
		where += " " + JDBC_Operator.translate_Operator(op.getTextTrim()) + " ";
		Element item2 = (Element) item.elements("Item").get(1);
		if (item2.attributeValue("Type").equals("complex")) {
			where += createWhereItem(item2);
		} else if (item2.attributeValue("Type").equals("field")) {
			where += item2.getTextTrim();
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
			group += item.getTextTrim() + ",";
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
			order += item.getTextTrim() + ",";
		}
		if (order.endsWith(","))
			order = order.substring(0, order.lastIndexOf(","));
		return order;
	}

	private String getTableName(String code) throws SQLException {
		ConnectionDB conn = new ConnectionDB();
		String sql = "SELECT ENNAME FROM EZ_STD_LAYERS_LAYER WHERE CODE='"
				+ code + "'";
		List o = conn.excuteQuery(sql, null);
		String s = o.get(0).toString().split("=")[1];
		if (o != null)
			return s.substring(0, s.length() - 1);
		return null;
	}

	private List<Object> getResultSet(String sql) {
	/*	 ConnectionDB con = new ConnectionDB();
		con = new ConnectionDB(con.getConnection(code));
		return con.excuteQuery(sql, null); */
		return null;
	}

	private ResultSet getResultSet1(String sql) {
		/*ConnectionDB con = new ConnectionDB();
		con = new ConnectionDB(con.getConnection(code));
		return con.executeQueryRS(sql, null);*/
		return null;
	}
}
