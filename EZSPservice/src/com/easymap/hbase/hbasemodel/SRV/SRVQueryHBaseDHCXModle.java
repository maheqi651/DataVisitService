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

public class SRVQueryHBaseDHCXModle {

	public String getXML(String senderID, String dataObjectCode,String themeCode,
			String methodName, List<Element> where, String fields, String order,
			String startRow, long max,boolean istotal) throws Exception {
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
		System.out.println("查询数据"+(System.currentTimeMillis()-times));
		//顺序处理
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
			 json.put("hbaseInstance", Constants.DEFAULT_HBASE_INSTANCE);
			 json.put("hbaseTable", tableName);
			 JSONObject params= new JSONObject();
			if(where!=null)
			{
				if(where.size()>0)
				{
					params.put("isFuzzy", "true");
				    params.put("isBatch", "false");
					fuzzyrow="";
					for(int i=0;i<where.size();i++)
					{
						fuzzyrow+=where.get(i).getTextTrim()+"_";
					}
					if(fuzzyrow.length()>0)
					{
					fuzzyrow=fuzzyrow.substring(0, fuzzyrow.length()-1);
					fuzzyrow+="?";
					}
				}else{
					    params.put("isFuzzy", "false");
					    params.put("isBatch", "true");
				}
			}else{
				    params.put("isFuzzy", "false");
				    params.put("isBatch", "true");
			}
			    params.put("rowkey", startRow);
			    params.put("fuzzyRow", fuzzyrow);
			    params.put("stopRowkey", "");
			    params.put("pageSize", max);
			    params.put("maxVersions", "1");
			    params.put("qualifiers", fields);
			    json.put("params", params);	
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
				doxmlele(Records,kvpage,fieldstr);
				Element Item5 = Items.addElement("Item");
				Element Name5 = Item5.addElement("Name");
				Name5.setText("NextRowkey");
				Element Value5 = Item5.addElement("Value");
				Value5.addAttribute("Type", "arrayof_string");
				Element Data5 = Value5.addElement("Data");
				Data5.setText(LOCKUtil.encodeRow(kvpage.getNextRowkey()));
				return rootElement.asXML();
	}
	
	public static void doxmlele(Element records,KvPage kv,String[] fileds){
		
		List<KvInfo> lk = kv.getKvInfoList();
		System.out.println("-----------------"+fileds[0]);
		for(KvInfo ki:lk){//每次循环相当于HBase里一个row
			  //不行的话就在这里newJSONObject
			Table<String, String, String> t = ki.getTimestampTable();
			for(Entry<String, Map<String,String>> elmss:t.rowMap().entrySet())
			{
				Element Record = records.addElement("Record");
				for(String keystr:fileds)
				{
					Element Data = Record.addElement("Data");
					Data.setText(elmss.getValue().get(keystr)+ "");	
				}
				/*for(Entry<String,String> ess:elmss.getValue().entrySet())
				{
					
				}*/
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
