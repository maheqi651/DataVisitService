package com.easymap.modle.SRV;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;





import com.easymap.base.pools.ConnectionSDB;
import com.easymap.base.pools.ConnectionDB;
import com.easymap.base.pools.ConvertJDBC;
import com.easymap.base.pools.DBCPPools;
import com.easymap.base.pools.glk.bean.EntryTools;
import com.easymap.base.readdatabase.ConnectioRYXXDB;
import com.easymap.base.readdatabase.ConnectioSDB;
import com.easymap.base.tool.JDBCProperty;
import com.easymap.base.tool.JDBC_ColumnType;
import com.easymap.filter.Tools;

public class GQueryJDCXXModleSource {
	String driver="oracle.jdbc.OracleDriver";
	String username="GL_WP";
	String password="GL_WP";
	String url="jdbc:oracle:thin:@10.235.36.163:1521:gzdb021";
	public String getXML(String senderID,
			String methodName, String ID, String fields) throws Exception {
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
		if(ID!=null&&!"".equals(ID))
		{//判定id是否为空
			ConnectioRYXXDB ryxxdb=new ConnectioRYXXDB();
			ryxxdb.setConnecttion(DBCPPools.getInstance().getConnection(driver,url, username, password));
			String[] fieldArray =null;
			if(fields!=null)
			{
				fieldArray=fields.split(",");
			}
			
			if(fieldArray!=null)
			{
				
				String tempsql="select srctablename,use,fieldvalue from WP_JDC_ZHLYXX where id=? and fieldname=?";
				String sqlfromlayer="select code,chname from "+Tools.EZSPATIAL+".ez_std_layers_layer where enname=?";
				for(String field:fieldArray)
				{//遍历需查询的字段
					Element Item = Items.addElement("Item");
					Element NameI = Item.addElement("Name");
					NameI.setText(field);//查询属性返回结果
					Element Value = Item.addElement("Value");
					Value.addAttribute("Type", "FieldValues");
					Object[] ryzhlyxx=ryxxdb.executeQueryRS1(tempsql, new Object[]{ID,field});
					if(ryzhlyxx!=null)
					{//不为空
						if(ryzhlyxx.length>1)
						{
							if(ryzhlyxx[1]!=null)
							{
								List<Object[]> list =(List<Object[]>)ryzhlyxx[1];
								//拿出列表
								for(Object[] sjly:list)
								{//ez_std_layers_layer
									String srctablename="";
									String fieldvalue="";
									String use="";
									if(sjly[0]!=null)
										srctablename=(String)sjly[0];
									if(sjly[1]!=null)
										use=(String)sjly[1];
									if(sjly[2]!=null)
										fieldvalue=(String)sjly[2];
									Element Data =Value.addElement("Data");
									if("1".equals(use))
									{//从3张表查询
										Data.setText(getValueFromTri(ryxxdb,field,ID));
									}else{
										Data.setText(fieldvalue);
									}
									Object[] resultlayer=new ConnectioSDB().executeQuerySingle(sqlfromlayer, new Object[]{srctablename});
									//Object[] resultlayer=ryxxdb.executeQuerySingle(sqlfromlayer, new Object[]{srctablename});
									//设置属性
									String Code="";
									String TableName="";
									if(resultlayer!=null)
									{
										if(resultlayer[0]!=null)
											Code=(String)resultlayer[0];
										if(resultlayer[1]!=null)
											TableName=(String)resultlayer[1];
									}
									Data.addAttribute("Code", Code);
									Data.addAttribute("TableName", TableName);
								}
							}
							
						}
					}
				}
			}
		}
		System.out.println("...........处理时间为："+(System.currentTimeMillis()-times));
		return rootElement.asXML();
	}
	
	public String getValueFromTri(ConnectioRYXXDB ryxxdb,String Field,String ID)
	{//从3张表查询
		try {
			ryxxdb.setConnecttion(DBCPPools.getInstance().getConnection(driver,url, username, password));
		} catch (SQLException e) 
		{
			e.printStackTrace();
			return "";
		}
		String sql="";
		if(EntryTools.WPJDCJBXX.contains(Field))
		{//基本信息
			 sql="select "+Field+" from WP_JDC_JBXX where id=?";
		} else{
			 sql="select fieldvalue from WP_JDC_JBXX_MT where id=? and fieldname='"+Field+"'";
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
