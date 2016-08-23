package com.easymap.modle.SRV;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.easymap.base.tool.JDBC_ColumnType;
import com.easymap.base.tool.serviceIdMethod;
import com.easymap.dao.QueryDataDao;

public class SRVQueryData2Modle {
	private QueryDataDao qdd;

	public SRVQueryData2Modle(QueryDataDao qdd) {
		this.qdd = qdd;
	}

	public String getXML(String senderID, String from, String methodName,
			String where, String fields, String order, String groupby)
			throws Exception {
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("Response");
		Element SenderID = rootElement.addElement("SenderID");
		SenderID.setText(senderID);
		Element ServiceID = rootElement.addElement("ServiceID");
		ServiceID.setText(serviceIdMethod.getServiceIdMethod(methodName));
		Element Method = rootElement.addElement("Method");
		Element Name = Method.addElement("Name");
		Name.setText(methodName);
		Element Security = Method.addElement("Security");
		Security.addAttribute("Algorithm", "");
		Security.setText("");
		Element Items = Method.addElement("Items");
		Element Item = Items.addElement("Item");
		Element NameI = Item.addElement("Name");
		NameI.setText("ResultInfo");
		Element Value = Item.addElement("Value");
		Value.addAttribute("Type", "Fields");
		Object[] rs = qdd.getQueryData2(where, fields, order, groupby, from);
		Map<String, Integer> m = (Map<String, Integer>) rs[0];
		List<Object[]> list = (List<Object[]>) rs[1];
		//出去sql查询中ROWNUM_列
		m.remove("ROWNUM_");
		for(String s : m.keySet())
		{
			Element Data = Value.addElement("Data");
			Data.addAttribute("type", JDBC_ColumnType
					.translate_InteractType(m.get(s)));
			Data.setText(s);
		}
		Element Item1 = Items.addElement("Item");
		Element Name1 = Item1.addElement("Name");
		Name1.setText("Result");
		Element Value1 = Item1.addElement("Value");
		Value1.addAttribute("Type", "Records");
		Element Records = Value1.addElement("Records");
		for(int i = 0 ; i < list.size() ; i++){
			Element Record = Records.addElement("Record");
			Object[] o = list.get(i);
			for(int j = 0 ; j < o.length-1 ; j++)
			{
				Element Data = Record.addElement("Data");
				Data.setText(o[j] + "");	
			}
		}
		return rootElement.asXML();
	}

}
