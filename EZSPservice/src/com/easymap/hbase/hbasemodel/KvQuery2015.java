package com.easymap.hbase.hbasemodel;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.easymap.ezBigData.exceptions.EzBigDataConfigException;
import com.easymap.ezBigData.exceptions.QueryConditionsConstructException;
import com.easymap.ezMDAS.kvQuery.EzBigDataKvQuery;
import com.easymap.ezMDAS.kvQuery.pojos.KvInfo;
import com.easymap.ezMDAS.kvQuery.pojos.KvPage;
import com.easymap.hbase.util.Constants;
import com.google.common.base.Strings;
import com.google.common.collect.Table;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class KvQuery2015 implements IStreamQAS {
	JSONObject jo;
	SimpleDateFormat sdf = new SimpleDateFormat(Constants.HBASE_DATE_FORMAT);

	public KvQuery2015(JSONObject jsonS) {
		jo = jsonS;
	}
	@Override
	public KvPage execute() throws QueryConditionsConstructException, EzBigDataConfigException {
		// TODO Auto-generated method stub
		String rtString = "";
		Object hbaseInstName = jo.get("hbaseInstance");
		if (hbaseInstName == null  || "".equals(hbaseInstName.toString())) {
			throw new QueryConditionsConstructException(
					"请为hbaseInstance添加正确的值");
		}
		Object hbaseTableName = jo.get("hbaseTable");
		if (hbaseTableName == null || "".equals(hbaseTableName.toString())) {
			throw new QueryConditionsConstructException(
					"请为hbaseTable添加正确的表名");
		}
		EzBigDataKvQuery ebdkq = new EzBigDataKvQuery(hbaseInstName.toString(), hbaseTableName.toString());
		boolean isFuzzy;
		boolean isBatch;
		long pageSize=0;
		int maxVersions=0;
		String returnType;
		long beginTimestamp=0;
		long endTimestamp = Long.MAX_VALUE;
		JSONObject params= jo.getJSONObject("params");
		Object rowkey = params.get("rowkey");
		Object isFuzzyObj = params.get("isFuzzy");
		Object fuzzyRowObj = params.get("fuzzyRow");
		Object returnTypeObj = params.get("returnType");
		Object isBatchObj=params.get("isBatch");
		Object pageSizeObj=params.get("pageSize");
		Object qualifiers=params.get("qualifiers");
		Object maxVersionsObj=params.get("maxVersions");
		Object beginTimestampObj=params.get("beginTime");
		Object endTimestampObj=params.get("endTime");
 
		if (isFuzzyObj == null || "".equals(isFuzzyObj.toString())){
			isFuzzy = false;
		}
		else{
			isFuzzy = Boolean.parseBoolean(isFuzzyObj.toString());
		}
		if (isBatchObj == null || "".equals(isBatchObj.toString())){
			isBatch = false;
		}
		else{
			isBatch = Boolean.parseBoolean(isBatchObj.toString());
		}
		if (pageSizeObj == null || "".equals(pageSizeObj.toString())){
			pageSize = 0;
		}
		else{
			pageSize = Long.parseLong(pageSizeObj.toString());
		}
		
		if (returnTypeObj == null  || "".equals(returnTypeObj.toString())){
			returnType = "normal";
		}
		else{
			returnType = returnTypeObj.toString();
		}
		String[] qualifierParam=null;
		if(qualifiers!=null && !"".equals(qualifiers.toString())){
			qualifierParam = qualifiers.toString().split(",");
		}
		if(maxVersionsObj!=null && !"".equals(maxVersionsObj.toString())){
			maxVersions = Integer.parseInt(maxVersionsObj.toString());
		}
		try {
			if (beginTimestampObj != null
					&& !"".equals(beginTimestampObj.toString())) {
				beginTimestamp = sdf.parse(beginTimestampObj.toString())
						.getTime();
			}
			if (endTimestampObj != null
					&& !"".equals(endTimestampObj.toString())) {
				endTimestamp = sdf.parse(endTimestampObj.toString()).getTime();
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new QueryConditionsConstructException("时间格式错误，请遵照yyyyMMddHHmmss格式");
		}
		System.out.println(rowkey);
		KvPage kp =ebdkq.executeQuery(Strings.nullToEmpty((String) rowkey), beginTimestamp,endTimestamp,
				Strings.nullToEmpty((String)fuzzyRowObj),
				isFuzzy, isBatch, pageSize,maxVersions,qualifierParam);
		//rtString=formatKvInfos2String(kp, returnType);
		return kp;
	}
	public  String formatKvInfos2String(KvPage kp, String returnType){
		JSONObject jomain = new JSONObject();
		JSONObject joresult = new JSONObject();
		JSONObject jotable=new JSONObject();
		JSONArray jatable = new JSONArray();
		JSONObject jopiece=new JSONObject();
		List<KvInfo> lk = kp.getKvInfoList();
		if(returnType.equalsIgnoreCase("timestamp")){
			for(KvInfo ki:lk){//每次循环相当于HBase里一个row
				 jatable.clear();//不行的话就在这里newJSONObject
				Table<String, String, String> t = ki.getTimestampTable();
				for(Entry<String, Map<String,String>> elmss:t.rowMap().entrySet()){
					jopiece.clear();
					jopiece.put("timestamp", elmss.getKey());
					for(Entry<String,String> ess:elmss.getValue().entrySet()){
						jopiece.put(ess.getKey(), ess.getValue());
					}
					jatable.add(jopiece);
				}
				joresult.put(ki.getRowkey(), jatable);
				jomain.put("result", joresult);
			}
		}
		else if(returnType.equalsIgnoreCase("rowkey")){
			
			for(KvInfo ki:lk){//每次循环相当于HBase里一个row
				 jotable.clear();//不行的话就在这里newJSONObject
				Table<String, String, String> t = ki.getTimestampTable();
				for(Entry<String, Map<String,String>> elmss:t.rowMap().entrySet()){
					jopiece.clear();
					for(Entry<String,String> ess:elmss.getValue().entrySet()){
						jopiece.put(ess.getKey(), ess.getValue());
					}
					jotable.put(elmss.getKey(), jopiece);
				}
				joresult.put(ki.getRowkey(), jotable);
				jomain.put("result", joresult);
			}
			
		}
		else{// if(returnType.equalsIgnoreCase("normal"))//将timestamp作为一个普通的字段。
			for(KvInfo ki:lk){//每次循环相当于HBase里一个row
				Table<String, String, String> t = ki.getTimestampTable();
				for(Entry<String, Map<String,String>> elmss:t.rowMap().entrySet()){
					jopiece.clear();
					jopiece.put("timestamp", elmss.getKey());
					for(Entry<String,String> ess:elmss.getValue().entrySet()){
						jopiece.put(ess.getKey(), ess.getValue());
					}
					jatable.add(jopiece);
				}
			}
			jomain.put("result", jatable);
		}
		
		jomain.put("nextRowkey", kp.getNextRowkey());
		return jomain.toString();
		
	}
}

