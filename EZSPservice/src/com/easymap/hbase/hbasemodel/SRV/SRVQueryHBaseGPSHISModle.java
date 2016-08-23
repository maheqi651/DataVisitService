package com.easymap.hbase.hbasemodel.SRV;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;





import com.easymap.base.pools.ConnectionSDB;
import com.easymap.base.pools.ConnectionDB;
import com.easymap.base.pools.ConvertJDBC;
import com.easymap.base.pools.DBCPPools;
import com.easymap.base.tool.JDBCProperty;
import com.easymap.base.tool.JDBC_ColumnType;
import com.easymap.ezMDAS.kvQuery.pojos.KvInfo;
import com.easymap.ezMDAS.kvQuery.pojos.KvPage;
import com.easymap.hbase.hbasemodel.KvQuery;
import com.easymap.hbase.util.Constants;
import com.easymap.hbase.util.LOCKUtil;
import com.google.common.collect.Table;

public class SRVQueryHBaseGPSHISModle {

	public String getXML(String senderID, String dataObjectCode,String themeCode,
			String methodName, String StartTime, String EndTime, String CPHM,
			String Interval,String fields,boolean istotal) throws Exception {
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
		long times=System.currentTimeMillis();
		String[] fieldstr=fields.split(",");
		for(String s : fieldstr)
		{
			Element Data = Value.addElement("Data");
			Data.addAttribute("type", JDBC_ColumnType
					.translate_InteractType(-1));
			Data.setText(s);
		}
		String fuzzyrow=null;
		JSONObject json=new JSONObject();
		System.out.println("tablename:"+tableName);
		json.put("hbaseInstance", Constants.DEFAULT_HBASE_INSTANCE);
		json.put("hbaseTable", tableName);
		JSONObject params= new JSONObject();
		String startRow=""+CPHM+"#"+StartTime;
		String stopRowkey=""+CPHM+"#"+EndTime;
		params.put("isFuzzy", "false");
		params.put("isBatch", "true");
		params.put("rowkey", startRow);
		params.put("fuzzyRow","");
		params.put("stopRowkey",stopRowkey);
		params.put("pageSize", "1000000");
		params.put("maxVersions","1");
		params.put("qualifiers", fields);
		json.put("params",params);	
		//fields
		KvQuery kvp=new KvQuery(json);
		KvPage kvpage=kvp.execute();
		Element Item1 = Items.addElement("Item");
		Element Name1 = Item1.addElement("Name");
		Name1.setText("Result");
		Element Value1 = Item1.addElement("Value");
		Value1.addAttribute("Type", "Records");
		Element Records = Value1.addElement("Records");
		//处理
		//NextRowkey
		doxmlele(Records,kvpage,fieldstr,Interval);
		return rootElement.asXML();
	}
	
	public static void doxmlele(Element records,KvPage kv,String[] fileds,String interval){
		
		List<KvInfo> lk = kv.getKvInfoList();
		int count=0;
		int intervalInt=Integer.parseInt(String.valueOf(interval));
		if(intervalInt<0)
			return;
		System.out.println("-----------------"+fileds[0]);
		for(KvInfo ki:lk){//每次循环相当于HBase里一个row
			  //不行的话就在这里newJSONObject
			Table<String, String, String> t = ki.getTimestampTable();
			for(Entry<String, Map<String,String>> elmss:t.rowMap().entrySet())
			{//过滤下
				//System.out.println("count:"+count);
				if(intervalInt==0)
				{
						Element Record = records.addElement("Record");
						for(String keystr:fileds)
						{
							Element Data = Record.addElement("Data");
							Data.setText(elmss.getValue().get(keystr)+ "");	
						}
				}else{
					if(count%intervalInt==0)
					{
						Element Record = records.addElement("Record");
						for(String keystr:fileds)
						{
							Element Data = Record.addElement("Data");
							Data.setText(elmss.getValue().get(keystr)+ "");	
						}	
					}
				}
				count++;
			}
		}
	}
	public static void main(String[] args)
	{
		   String str="kdjjdj_";
		   str=str.substring(0, str.length()-1);
		   System.out.println(str);
		   
	}

}
