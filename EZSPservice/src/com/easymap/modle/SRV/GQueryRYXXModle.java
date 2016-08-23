package com.easymap.modle.SRV;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;





import com.caucho.hessian.client.HessianProxyFactory;
import com.easymap.base.pools.ConnectionSDB;
import com.easymap.base.pools.ConnectionDB;
import com.easymap.base.pools.ConvertJDBC;
import com.easymap.base.pools.DBCPPools;
import com.easymap.base.pools.glk.bean.EntryTools;
import com.easymap.base.queue.AddSub;
import com.easymap.base.queue.ResultSub;
import com.easymap.base.readdatabase.ConnectioRYXXDB;
import com.easymap.base.readdatabase.ConnectioSDB;
import com.easymap.base.tool.JDBCProperty;
import com.easymap.base.tool.JDBC_ColumnType;
import com.easymap.filter.Tools;
import com.easymap.memcached.hessian.servlet.HessianHelloWord;

public class GQueryRYXXModle {
	public String getXML(String senderID,
			String methodName, String where, String fields, String order,
			long start, long max,boolean istotal,List<String> filedlist,List<String> translist,int flags) throws Exception {
		long times =System.currentTimeMillis();
		String tableName = "ry_jbxx";
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
		 
		
		
		
		
		
		 
		String mid=(String)new ConnectioSDB().executeQuerySingle( "SELECT T.SERVICEID FROM "+Tools.EZSPATIAL+".EZ_SERVICE_INFO T WHERE T.METHODNAME=?",
				new Object[] { methodName})[0];
		HessianProxyFactory proxy=new HessianProxyFactory();
		HessianHelloWord hessianword=null;
		hessianword=(HessianHelloWord)proxy.create(HessianHelloWord.class, EntryTools.HESSIANURL);
	  boolean flag=false;
	  ResultSub resultsub=hessianword.get(senderID, mid, "", "", "");
	  if(!resultsub.isFlag())
	  {
		  Element Error=Value.addElement("Error");
		  SimpleDateFormat dataformat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  Error.setText("近期访问流量超出，系统判定：从"+dataformat.format(resultsub.getBlockTime())+"起,禁止访问"+resultsub.getChkBlockTimeSpan()+"分钟");
		  return rootElement.asXML();
	  } 
	   AddSub addSub=new AddSub();
	   addSub.setAddCount((int)max);
	   addSub.setSenderID(senderID);
	   addSub.setServiceID(mid);
	   hessianword.set(addSub);  
	   
		//new ConnectioSDB();
		String driver="oracle.jdbc.OracleDriver";
		String username="GL_RY_G";
		String password="GL_RY_G";
		String url="jdbc:oracle:thin:@10.235.36.163:1521:gzdb021";
		ConnectionDB cdb = new ConnectionDB(DBCPPools.getInstance().getConnection(driver,url, username, password));
		Object[] rs = cdb.getQueryData(tableName, where, fields, order,start, max);
		System.out.println("查询数据"+(System.currentTimeMillis()-times));
		Map<String, Integer> m = (Map<String, Integer>) rs[0];
		List<Object[]> list = (List<Object[]>) rs[1];
		m.remove("ROWNUM_");
		//反向index保存index，name 关系
		Map<Integer,String> mapindex=new HashMap<Integer, String>(); 
		Map<String,Integer> transmap=new HashMap<String,Integer>(); 
		List<String> exlist=null;
		List<String> mtexlist=null;//一对多
		int idclum=-1;
		String[] fieldsspli=null;
		for(String tmp:translist)
		{
			transmap.put(tmp, 0);//反向存储index
		}
		if(fields!=null)
		{
		     fieldsspli=fields.split(",");
			for(int i=0;i<fieldsspli.length;i++){
				if(transmap.containsKey(fieldsspli[i]))
				{
					mapindex.put(i, fieldsspli[i]);//反向存储index
				}
				if("ID".equals(fieldsspli[i]))
				{
					idclum=i;
				}
			}
		}
		if(m.get("ID")!=null)
		{
			if(flags<0)
			{//后续添加的ID去掉
				m.remove("ID");
			}
		}
		//预先分类
		if(filedlist!=null)
		{
			exlist=new ArrayList<String>();
			mtexlist=new ArrayList<String>();
			for(String fl:filedlist){
				if(EntryTools.RYJBXXEXSET.contains(fl))
				{    //存在人员基本扩展信息
					exlist.add(fl);
				}else{//存在人员多扩展信息
					mtexlist.add(fl);
				}
			}
		}
		
		if(fieldsspli!=null)
		{
			for(String key:fieldsspli)
			{   
				if(m.get(key)!=null)
				{
					Element Data = Value.addElement("Data");
					Data.addAttribute("type", JDBC_ColumnType
							.translate_InteractType(m.get(key)));
					Data.setText(key);
				}
			}
		}
		/*for(String s : m.keySet())
		{
			Element Data = Value.addElement("Data");
			Data.addAttribute("type", JDBC_ColumnType
					.translate_InteractType(m.get(s)));
			Data.setText(s);
		}*/
		//添加额外要返回列
		String exfiedclumstr="";
		if(exlist!=null)
		{
			for(String temp:exlist)
			{
				Element Data = Value.addElement("Data");
				Data.addAttribute("type", "string");
				Data.setText(temp);
				exfiedclumstr+=temp+",";
			}
		}
		if (exfiedclumstr.endsWith(","))
			exfiedclumstr = exfiedclumstr.substring(0,
					exfiedclumstr.lastIndexOf(","));
		if(mtexlist!=null)
		{
			for(String mtemp:mtexlist){
				Element Data = Value.addElement("Data");
				Data.addAttribute("type", "string");
				Data.setText(mtemp);
			}
		}
		
		Element Item1 = Items.addElement("Item");
		Element Name1 = Item1.addElement("Name");
		Name1.setText("Result");
		Element Value1 = Item1.addElement("Value");
		Value1.addAttribute("Type", "Records");
		Element Records = Value1.addElement("Records");
		ConnectioRYXXDB ryxxdb=new ConnectioRYXXDB();
		for(int i = 0 ; i < list.size() ; i++){
			Element Record = Records.addElement("Record");
			Object[] o = list.get(i);
			String ID="";
			for(int j = 0 ; j < o.length-1 ; j++){
				
				if(idclum==j&&flags<0)
				{   //只是保留ID
					ID= o[j]+"";
				}else{
					Element Data = Record.addElement("Data");
					if(idclum==j)
						ID= o[j]+"";
					if(mapindex.get(j)!=null)
					{
						//if(EntryTools.dicmap.get(o[j])!=null)
						/*if(transmap.containsKey(o[j]))
						{
							Data.setText(EntryTools.dicmap.get(mapindex.get(j)+EntryTools.DICMAPKEYFLAG+o[j])+"");//字典翻译
						}else{
							Data.setText(o[j] + "");
						}*/
						Data.setText(EntryTools.dicmap.get(mapindex.get(j)+EntryTools.DICMAPKEYFLAG+o[j])+"");//字典翻译
					}else{
						Data.setText(o[j] + "");
					}
				}
				
					
			}
			//前些列结束
			if(exlist!=null)
			{
				//数据库查询记录
				 if(exlist.size()>0){
						ryxxdb.setConnecttion(DBCPPools.getInstance().getConnection(driver,url, username, password));
						//查询对应的ex扩展信息
						//System.out.println("select "+exfiedclumstr+" from ry_jbxx_ex where id=?");
						Object[] ryxxex=ryxxdb.executeQuerySingle("select "+exfiedclumstr+" from ry_jbxx_ex where id=?", new Object[]{ID});
						int jj=0;
						 
						for(String exstr:exlist)
						{
							Element Data = Record.addElement("Data");
							//判断需要翻译
							if(transmap.containsKey(exstr))
							{//需要字典翻译
								Data.setText(EntryTools.dicmap.get(exstr+EntryTools.DICMAPKEYFLAG+ryxxex[jj])+"");//字典翻译
							}else{
								Data.setText(ryxxex[jj] + "");
							}
							jj++;
						} 
				 }
			
			}
			
			if(mtexlist!=null)
			{
				//数据库查询记录
				if(mtexlist.size()>0)
				{
					ryxxdb.setConnecttion(DBCPPools.getInstance().getConnection(driver,url, username, password));
					Object[] ryxxexmt=ryxxdb.executeQueryRS1("select FIELDNAME,FIELDVALUE from ry_jbxx_mt where id=?", new Object[]{ID});
					List<Object[]> listmt=null;
					if(ryxxexmt!=null)
						listmt=(List<Object[]>)ryxxexmt[1];
					for(String mtexstr:mtexlist)
					{
						Element Data = Record.addElement("Data");
						if(listmt==null)
						{//没有结果情况
							Data.setText("");
						}else{//存在结果
							 List<String> temstrlist=new ArrayList<String>();//记录个数
							 for(int k=0;k<listmt.size();k++)
							 {
								 Object[] ojj=listmt.get(k);
								 if(mtexstr.equals(ojj[0]))
								 {   
									 if(ojj[1]!=null)
									 temstrlist.add((String)ojj[1]);
									 else
										 temstrlist.add(null);
								 }
							 }
							 if(temstrlist.size()>0)
							 {
								if(temstrlist.size()>1)
								{
									for(String tt:temstrlist)
									{
										Element Datas = Data.addElement("Data");
										if(transmap.containsKey(mtexstr))
										 {//字典翻译
											 Datas.setText(EntryTools.dicmap.get(mtexstr+EntryTools.DICMAPKEYFLAG+tt)+"");
										 }else{
											 Datas.setText(tt+"");
										 }
									}
									
								}else{
									if(transmap.containsKey(mtexstr))
									 {//字典翻译
										 Data.setText(EntryTools.dicmap.get(mtexstr+EntryTools.DICMAPKEYFLAG+temstrlist.get(0))+"");
									 }else{
										 Data.setText(temstrlist.get(0)+"");
									 }
								}
							 }
						}
					}
				}
				
			}
			//后续记录
			//匹配列表
		}
		
		if(istotal)
		{
			Element Item2 = Items.addElement("Item");
			Element Name2 = Item2.addElement("Name");
			Name2.setText("Total");
			Element Value2 = Item2.addElement("Value");
			ConnectionDB cdb1 = new ConnectionDB(DBCPPools.getInstance().getConnection(driver,url, username, password));
			int count = cdb1.getTotalNum(tableName, where);
			Value2.setText(count+"");
		}
		System.out.println("...........处理时间为："+(System.currentTimeMillis()-times));
		return rootElement.asXML();
	}

}
