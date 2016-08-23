package com.easymap.modle.SRV;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;





import com.easymap.base.pools.ConnectionSDB;
import com.easymap.base.pools.ConnectionDB;
import com.easymap.base.pools.ConvertJDBC;
import com.easymap.base.pools.DBCPPools;
import com.easymap.base.pools.glk.bean.EntryTools;
import com.easymap.base.readdatabase.ConnectioRYBJDB;
import com.easymap.base.readdatabase.ConnectioRYXXDB;
import com.easymap.base.readdatabase.ConnectioSDB;
import com.easymap.base.tool.JDBCProperty;
import com.easymap.base.tool.JDBC_ColumnType;
import com.easymap.filter.Tools;

public class GQueryJDCBJModle {
	String driver="oracle.jdbc.OracleDriver";
	String username="GL_WP";
	String password="GL_WP";
	String url="jdbc:oracle:thin:@10.235.36.163:1521:gzdb021";
	public String getXML(String senderID,
			String methodName, String ID, String ZJHM,String CYZJDM) throws Exception {
		long times =System.currentTimeMillis();
		 
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
		ConnectioRYBJDB ryxxdb=new ConnectioRYBJDB();
		Map<String,Object> result=null;
		if(ID!=null&&!"".equals(ID))
		{//判定id是否为空
			result=findByID(ID,ryxxdb);
		}else{
			if((ZJHM!=null&&!"".equals(ZJHM))||(CYZJDM!=null&&!"".equals(CYZJDM)))
			{
				ryxxdb.setConnecttion(DBCPPools.getInstance().getConnection(driver,url, username, password));
				String sql="select ID from WP_JDC_JBXX where 1=1";
				Object[] oo;
			    if(ZJHM!=null&&!"".equals(ZJHM)&&CYZJDM!=null&&!"".equals(CYZJDM))
			    {
			    	oo=new Object[2];
			    }else{
			    	oo=new Object[1];
			    }
			    
			    if(ZJHM!=null&&!"".equals(ZJHM))
					{
			    	sql+=" and JDCHPHM=? ";
					oo[0]=ZJHM;
					}
				if(CYZJDM!=null&&!"".equals(CYZJDM))
				{
					sql+=" and JDCHPZLDM=? ";
					oo[1]=CYZJDM;
				}
					
				Object[] oresult=ryxxdb.executeQuerySingle(sql, oo);
				if(oresult!=null)
				{
					if(oresult[0]!=null)
					ID=(String)oresult[0];
					if(ID!=null&&!"".equals(ID))
					result=findByID(ID,ryxxdb);
				}
			}
		}
		//分类，code,表明
		
		Map<String,List<Map<String,String>>> resultmap=new HashMap<String,List<Map<String,String>>>();
		resultmap=findresultmap(result,ryxxdb);
		//处理返回
		if(resultmap!=null)
		{
			Set<String> keysets=resultmap.keySet();
			for(String keyset:keysets){
				Element Item =Items.addElement("Item");
				Element Names =Item.addElement("Name");
				Names.setText(keyset);
				Element Value =Item.addElement("Value");
				Value.setAttributeValue("Type", "Tables");
				List<Map<String,String>> liststemp=resultmap.get(keyset);
				if(liststemp!=null)
				{
					for(Map<String,String> temmap:liststemp){
						Element Datatemp = Value.addElement("Data");
						if(temmap!=null)
						{
							Set<String> tempset=temmap.keySet();
							for(String strs1:tempset)
							{
								Datatemp.addAttribute("Code",strs1);
								Datatemp.setText(temmap.get(strs1));
							}
						}
					}
				}
			}
		}
		System.out.println("...........处理时间为："+(System.currentTimeMillis()-times));
		return rootElement.asXML();
	}
	
	public Map<String,List<Map<String,String>>> findresultmap(Map<String,Object> result,ConnectioRYBJDB ryxxdb){
		Map<String,List<Map<String,String>>> resultmap=null;
		if(result!=null)
		{   String sqlfromlayer="select code from "+Tools.EZSPATIAL+".ez_std_layers_layer where enname=?";
			  resultmap=new HashMap<String, List<Map<String,String>>>();
			Set<String> keys=result.keySet();
			for(String key:keys){
				if(EntryTools.FROMWPBJ.get(key)!=null)
				{   //c,b
					   String[] temparray=EntryTools.FROMWPBJ.get(key);
					//查code
				/*	try {
						ryxxdb.setConnecttion(DBCPPools.getInstance().getConnection(driver,url, username, password));
					} catch (SQLException e) {
						e.printStackTrace();
						return null;
					}*/
					Object[] oresult=new ConnectioSDB().executeQuerySingle(sqlfromlayer, new Object[]{key});
					
					//Object[] oresult=ryxxdb.executeQuerySingle(sqlfromlayer, new Object[]{key});
					if(oresult!=null)
					{   
						if(oresult[0]!=null)
						{
							String codes=(String)oresult[0];
							Map<String,String> tempmap=new HashMap<String,String>();
							tempmap.put(codes, temparray[0]);
							if(resultmap.get(temparray[1])!=null)
							{
								resultmap.get(temparray[1]).add(tempmap);
							}else{
								List<Map<String,String>> list=new ArrayList<Map<String,String>>();
								list.add(tempmap);
								resultmap.put((String)temparray[1], list);
						  }
						}
					}
				}
			}
		}
		return resultmap;
	}
	public Map<String,Object> findByID(String ID ,ConnectioRYBJDB ryxxdb){
		try {
			ryxxdb.setConnecttion(DBCPPools.getInstance().getConnection(driver,url, username, password));
		} catch (SQLException e) {
			e.printStackTrace();
			return null; 
		}
		String sql="select * from WP_JDC_SYXX where id=?"; 
		Map<String,Object> result=ryxxdb.executeQueryMAP(sql, new Object[]{ID});
	    if(result!=null)
	    {
	    	result.remove("ID");
	    	Set<String> keys=result.keySet();
	    	String[] strs=new String[result.size()];
	    	int count=0;
		    for(String key:keys)
		    {
		    	strs[count]=key;
		    	count++;
		    	//System.out.println(key+":"+result.get(key));
		    }
		    for(String s:strs){
		    	Integer values=Integer.parseInt(result.get(s).toString());
		    	if(values>0)
		    	{
		    		
		    	}
		    	else{
		    		 result.remove(s);
		    	}
		    }
		   
	    }
		
		return result;
	}
	public String getValueFromTri(ConnectioRYXXDB ryxxdb,String Field,String ID)
	{//从3张表查询
		try {
			ryxxdb.setConnecttion(DBCPPools.getInstance().getConnection(driver,url, username, password));
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
		String sql="";
		if(EntryTools.RYJBXXSET.contains(Field))
		{//基本信息
			 sql="select "+Field+" from ry_jbxx where id=?";
		}else if(EntryTools.RYJBXXEXSET.contains(Field)){
			 sql="select "+Field+" from ry_jbxx_ex where id=?";
		}else{
			 sql="select fieldvalue from ry_jbxx_mt where id=? and fieldname='"+Field+"'";
		}
		Object[] result=ryxxdb.executeQueryRS1(sql, new Object[]{ID});
		String resultstr="";
		if(result!=null)
		{
			if(result.length>1)
			{
				if(result[1]!=null)
				{
					List<Object[]> lists=(List<Object[]>)result[1];
					if(lists!=null)
					{
						for(Object[] oo:lists)
						{
							resultstr+=oo[0]+",";
						}
						if (resultstr.endsWith(","))
							resultstr = resultstr.substring(0,
									resultstr.lastIndexOf(","));
						return resultstr;//返回真实值
					}
				}
			}
		}
		return "";
	}
	
}
