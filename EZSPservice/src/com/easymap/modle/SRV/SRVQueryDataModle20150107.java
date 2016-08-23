package com.easymap.modle.SRV;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;





import com.easymap.base.pools.ConnectionSDB;
import com.easymap.base.pools.ConnectionDB;
import com.easymap.base.pools.ConvertJDBC;
import com.easymap.base.pools.DBCPPools;
import com.easymap.base.tool.JDBCProperty;
import com.easymap.base.tool.JDBC_ColumnType;

public class SRVQueryDataModle20150107 {

	public String getXML(String senderID, String dataObjectCode,String themeCode,
			String methodName, String where, String fields, String order,
			long start, long max,boolean istotal) throws Exception {
		List<String> code = new ArrayList<String>();
		code.add(dataObjectCode);
		ConnectionSDB sdb = new ConnectionSDB();
		String tableName = sdb.getTableNameByCode(dataObjectCode);
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("Response");
		Element SenderID = rootElement.addElement("SenderID");
		SenderID.setText(senderID);
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
		JDBCProperty jdcp = sdb.getJDBCProperty(code, themeCode);
		//System.out.println("name:----"+ jdcp.getUsername().trim());
		long times=System.currentTimeMillis();
		ConnectionDB cdb = new ConnectionDB(DBCPPools.getInstance().getConnection(jdcp.getDriver().trim(),jdcp.getUrl().trim(), jdcp.getUsername().trim(), jdcp.getPassword()));
		Object[] rs = cdb.getQueryData(tableName, where, fields, order,start, max);
		System.out.println("查询数据"+(System.currentTimeMillis()-times));
		Map<String, Integer> m = (Map<String, Integer>) rs[0];
		List<Object[]> list = (List<Object[]>) rs[1];
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
			for(int j = 0 ; j < o.length-1 ; j++){
				Element Data = Record.addElement("Data");
				Data.setText(o[j] + "");	
			}
		}
		Element Item2 = Items.addElement("Item");
		Element Name2 = Item2.addElement("Name");
		Name2.setText("Total");
		Element Value2 = Item2.addElement("Value");
		if(istotal)
		{
			ConnectionDB cdb1 = new ConnectionDB(DBCPPools.getInstance().getConnection(jdcp.getDriver().trim(),jdcp.getUrl().trim(), jdcp.getUsername().trim(), jdcp.getPassword()));
			int count = cdb1.getTotalNum(tableName, where);
			Value2.setText(count+"");
		}
		return rootElement.asXML();
	}

}
