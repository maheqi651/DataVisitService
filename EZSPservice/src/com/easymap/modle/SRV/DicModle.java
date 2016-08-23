package com.easymap.modle.SRV;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.easymap.base.hcontrol.ResultSetToObject;
import com.easymap.base.minterface.modle;
import com.easymap.base.tool.serviceIdMethod;
import com.easymap.dao.DicBean;
import com.easymap.dao.SRVdirectory;
import com.easymap.filter.Tools;
import com.easymap.modle.authorization.authorization;


public class DicModle implements modle{
	public DicModle(){}
	public Object[] srvDirectoryModle(Map map) throws Exception{
		ResultSetToObject pdb = new ResultSetToObject();
		Object[] obT = null;
		Object[] ob = null;
		String sqlT="";
		String objUrlT="";
		int index=0;
		if(map.get("index")!=null)
		{  
			index=Integer.parseInt((String)map.get("index"));
		}
		int count=10;
		if(map.get("count")!=null)
		{
			count=Integer.parseInt((String)map.get("count"));
		}
		
		//String strclum[]=Tools.DICCOLUM;
		sqlT="select * from (select  row_.*, ROWNUM rownum_   from (select   t.* " 
			+" from "+Tools.EZSPATIAL+".ez_dic_def t where t.id> "+index+" @1 @2 order by t.id asc )  row_  WHERE ROWNUM< "+count+" ) WHERE rownum_ >= 1 ";
			if(map.get("NAME")!=null)
			{
				sqlT=sqlT.replace("@1", " and t.name like '%"+(String)map.get("NAME")+"%'");
			}else{
				sqlT=sqlT.replace("@1", "");
			}
			
			if(map.get("CODE")!=null)
			{
				sqlT=sqlT.replace("@2", " and t.code like '%"+(String)map.get("CODE")+"%'");
			}else{
				sqlT=sqlT.replace("@2", "");
			}
			
		System.err.println(sqlT);
			objUrlT="com.easymap.dao.DicBean";
		obT = pdb.parseDataEntityBeans(sqlT,objUrlT);
		return obT;
	}
	
	public String getXML(Map map,String senderID,String methodName) throws Exception{
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("Response");
		Element SenderID = rootElement.addElement("SenderID");
		SenderID.setText(senderID);
		Element ServiceID = rootElement.addElement("ServiceID");
		ServiceID.setText(serviceIdMethod.getServiceIdMethod(methodName));
		Element Content = rootElement.addElement("Method");
		Element Name = Content.addElement("Name");
		Name.setText(methodName);
		Element Items = Content.addElement("Items");
		String methodId=serviceIdMethod.getServiceIdMethod(methodName);
		
		if(authorization.isAuthorization(senderID,methodId)) 
		{   
			
			Element Item = Items.addElement("Item");
			Element resultinfo = Item.addElement("Name");
			resultinfo.setText("ResultInfo");
			Element value= Items.addElement("Value");
			value.addAttribute("Type", "Fields");
			/*
			Element data1=value.addElement("Data");
			data1.addAttribute("Type", "string");
			data1.setText("NAME");
			
			Element data2=value.addElement("Data");
			data2.addAttribute("Type", "string");
			data2.setText("CODE");
			
			Element data3=value.addElement("Data");
			data3.addAttribute("Type", "string");
			data3.setText("DESP");
			
			Element data4=value.addElement("Data");
			data4.addAttribute("Type", "string");
			data4.setText("BZ");*/
			String str[]=Tools.DICCOLUM;
			//Map<String,String> mapstr=new HashMap<String, String>();
			if(str!=null)
			{
				for(String columstr:str)
				{   
					Element data1=value.addElement("Data");
					if("ID".equals(columstr))
					{
						data1.addAttribute("Type", "int");
					}else{
						data1.addAttribute("Type", "string");
					}
					data1.setText(columstr);
					//mapstr.put(columstr, "1");
				}
			
			}
			
			/*Element data5=value.addElement("Data");
			data5.addAttribute("Type", "int");
			data5.setText("ID");*/
			
			
			Element Item2 = Items.addElement("Item");
			Element result = Item.addElement("Name");
			resultinfo.setText("Result");
			Element value1= Items.addElement("Value");
			value1.addAttribute("Type", "Records");
			Element records=value1.addElement("Records");
			
			
			
		Object[] obT=srvDirectoryModle(map);
		if(obT!=null&&obT.length>0){
		for(int i=0;i<obT.length;i++){
			DicBean so =(DicBean) obT[i];
			Element record = records.addElement("Record");
			Element name = record.addElement("Data");
			//NAME,ID,CODE,DESP,PARENTCODE,BZ,TYPE,STANDARD,DECODE
			for(String columstr:str)
			{
				if("NAME".equals(columstr)) 
				{
					if(so.getNAME()!=null){
						name.setText(so.getNAME());
					}else{
						name.setText("");
					}
					 continue;
				}
				if("ID".equals(columstr)) 
				{
					Element ID = record.addElement("Data");
					if(so.getID()!=null){
						ID.setText(""+so.getID());
					}else{
						ID.setText("");
					}
					continue;
				}
				
				if("CODE".equals(columstr)) 
				{
					Element Code = record.addElement("Data");
					if(so.getCODE()!=null){
						Code.setText(so.getCODE());
					}else{
						Code.setText("");
					}
					 
				}
				
				if("PARENTCODE".equals(columstr)) 
				{
					Element PARENTCODE = record.addElement("Data");
					if(so.getPARENTCODE()!=null){
						PARENTCODE.setText(so.getPARENTCODE());
					}else{
						PARENTCODE.setText("");
					}
					continue;
				}
				
				if("DESP".equals(columstr)) 
				{
					Element DESP = record.addElement("Data");
					if(so.getDESP()!=null){
						DESP.setText(so.getDESP());
					}else{
						DESP.setText("");
					}
					continue;
				}
				
				if("TYPE".equals(columstr)) 
				{
					Element  TYPE = record.addElement("Data");
					if(so.getTYPE()!=null){
						 TYPE.setText(so.getTYPE());
					}else{
						 TYPE.setText("");
					}
					continue;
				}
				
				if("STANDARD".equals(columstr)) 
				{
					Element  STANDARD = record.addElement("Data");
					if(so.getSTANDARD()!=null){
						STANDARD.setText(so.getSTANDARD());
					}else{
						STANDARD.setText("");
					}
					continue;
				}
				
				if("DECODE".equals(columstr)) 
				{
					Element  DECODE = record.addElement("Data");
					if(so.getDECODE()!=null){
						DECODE.setText(so.getDECODE());
					}else{
						DECODE.setText("");
					}
					continue; 
				}
			}
			 
			
			/*Element ID = record.addElement("Data");
			System.out.println("DID:======"+so.getDID());
			if(so.getDID()!=null){
				ID.setText(""+so.getDID());
			}else{
				ID.setText("");
			}*/
			 
		}
		}else{
			records.setText("NOT DATA");
		}
		}else{
			Items.setText("NOT AUTH");
		}
		return rootElement.asXML();
	}
	public Object[] getQueryDateObjModle (Map map) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public Object[] getQueryDicModle(Map map) throws Exception{
		ResultSetToObject pdb = new ResultSetToObject();
		Object[] obT = null;
		Object[] ob = null;
		String sqlT="";
		String objUrlT="";
		int index=0;
		if(map.get("PARENTCODE")!=null)
		{  
			sqlT="select * from "+Tools.EZSPATIAL+".ez_dic_def t where  parentcode='"+(String)map.get("PARENTCODE")+"'";
		}else{
			sqlT="select * from "+Tools.EZSPATIAL+".ez_dic_def t where type='GROUP' and parentcode='0'";
		}
		 
		System.err.println(sqlT);
			objUrlT="com.easymap.dao.DicBean";
		obT = pdb.parseDataEntityBeans(sqlT,objUrlT);
		return obT;
	}
	
	
	//字典查询接口
	public String getQueryXML(Map map,String senderID,String methodName) throws Exception{
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("Response");
		Element SenderID = rootElement.addElement("SenderID");
		SenderID.setText(senderID);
		Element ServiceID = rootElement.addElement("ServiceID");
		ServiceID.setText(serviceIdMethod.getServiceIdMethod(methodName));
		Element Content = rootElement.addElement("Method");
		Element Name = Content.addElement("Name");
		Name.setText(methodName);
		Element Items = Content.addElement("Items");
		String methodId=serviceIdMethod.getServiceIdMethod(methodName);
		
		if(authorization.isAuthorization(senderID,methodId)) 
		{   
			Element Item = Items.addElement("Item");
			Element resultinfo = Item.addElement("Name");
			resultinfo.setText("ResultInfo");
			
			Element value= Items.addElement("Value");
			value.addAttribute("Type", "Fields");
			String str[]=Tools.DICCOLUM;
			//Map<String,String> mapstr=new HashMap<String, String>();
			if(str!=null)
			{
				for(String columstr:str)
				{   
					Element data1=value.addElement("Data");
					if("ID".equals(columstr))
					{
						data1.addAttribute("Type", "int");
					}else{
						data1.addAttribute("Type", "string");
					}
					data1.setText(columstr);
					//mapstr.put(columstr, "1");
				}
			
			}
			/*Element data1=value.addElement("Data");
			data1.addAttribute("Type", "string");
			data1.setText("NAME");
			
			Element data2=value.addElement("Data");
			data2.addAttribute("Type", "string");
			data2.setText("CODE");
			
			Element data3=value.addElement("Data");
			data3.addAttribute("Type", "string");
			data3.setText("DESP");
			
			Element data4=value.addElement("Data");
			data4.addAttribute("Type", "string");
			data4.setText("BZ");
			
			Element data6=value.addElement("Data");
			data6.addAttribute("Type", "string");
			data6.setText("PARENTCODE");
			*/
			/*Element data5=value.addElement("Data");
			data5.addAttribute("Type", "int");
			data5.setText("ID");*/
			
			
			Element Item2 = Items.addElement("Item");
			Element result = Item.addElement("Name");
			resultinfo.setText("Result");
			Element value1= Items.addElement("Value");
			value1.addAttribute("Type", "Records");
			Element records=value1.addElement("Records");
			
			
			
		Object[] obT=getQueryDicModle(map);
		if(obT!=null&&obT.length>0){
		for(int i=0;i<obT.length;i++){
			DicBean so =(DicBean) obT[i];
			Element record = records.addElement("Record");
			Element name = record.addElement("Data");
			 
			 
			for(String columstr:str)
			{
				if("NAME".equals(columstr)) 
				{
					if(so.getNAME()!=null){
						name.setText(so.getNAME());
					}else{
						name.setText("");
					}
					 continue;
				}
				if("ID".equals(columstr)) 
				{
					Element ID = record.addElement("Data");
					if(so.getID()!=null){
						ID.setText(""+so.getID());
					}else{
						ID.setText("");
					}
					continue;
				}
				
				if("CODE".equals(columstr)) 
				{
					Element Code = record.addElement("Data");
					if(so.getCODE()!=null){
						Code.setText(so.getCODE());
					}else{
						Code.setText("");
					}
					 
				}
				
				if("PARENTCODE".equals(columstr)) 
				{
					Element PARENTCODE = record.addElement("Data");
					if(so.getPARENTCODE()!=null){
						PARENTCODE.setText(so.getPARENTCODE());
					}else{
						PARENTCODE.setText("");
					}
					continue;
				}
				
				if("DESP".equals(columstr)) 
				{
					Element DESP = record.addElement("Data");
					if(so.getDESP()!=null){
						DESP.setText(so.getDESP());
					}else{
						DESP.setText("");
					}
					continue;
				}
				
				if("TYPE".equals(columstr)) 
				{
					Element  TYPE = record.addElement("Data");
					if(so.getTYPE()!=null){
						 TYPE.setText(so.getTYPE());
					}else{
						 TYPE.setText("");
					}
					continue;
				}
				
				if("STANDARD".equals(columstr)) 
				{
					Element  STANDARD = record.addElement("Data");
					if(so.getSTANDARD()!=null){
						STANDARD.setText(so.getSTANDARD());
					}else{
						STANDARD.setText("");
					}
					continue;
				}
				
				if("DECODE".equals(columstr)) 
				{
					Element  DECODE = record.addElement("Data");
					if(so.getDECODE()!=null){
						DECODE.setText(so.getDECODE());
					}else{
						DECODE.setText("");
					}
					continue; 
				}
			}
			/*Element ID = record.addElement("Data");
			System.out.println("DID:======"+so.getDID());
			if(so.getDID()!=null){
				ID.setText(""+so.getDID());
			}else{
				ID.setText("");
			}*/
			 
		}
		}else{
			records.setText("NOT DATA");
		}
		}else{
			Items.setText("NOT AUTH");
		}
		return rootElement.asXML();
	}
	
	
	
	public String getEnumDictionaryCodeXML(Map map,String senderID,String methodName) throws Exception{
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("Response");
		Element SenderID = rootElement.addElement("SenderID");
		SenderID.setText(senderID);
		Element ServiceID = rootElement.addElement("ServiceID");
		ServiceID.setText(serviceIdMethod.getServiceIdMethod(methodName));
		Element Content = rootElement.addElement("Method");
		Element Name = Content.addElement("Name");
		Name.setText(methodName);
		Element Items = Content.addElement("Items");
		String methodId=serviceIdMethod.getServiceIdMethod(methodName);
		
		if(authorization.isAuthorization(senderID,methodId)) 
		{   
			Element Item = Items.addElement("Item");
			Element resultinfo = Item.addElement("Name");
			resultinfo.setText("ResultInfo");
			Element value= Items.addElement("Value");
			value.addAttribute("Type", "Fields");
			Element data1=value.addElement("Data");
			data1.addAttribute("Type", "string");
			data1.setText("NAME");
			
			Element data2=value.addElement("Data");
			data2.addAttribute("Type", "string");
			data2.setText("CODE");
			
			Element data3=value.addElement("Data");
			data3.addAttribute("Type", "string");
			data3.setText("DESP");
			
			 
			Element data6=value.addElement("Data");
			data6.addAttribute("Type", "string");
			data6.setText("DEFID");
			/*Element data5=value.addElement("Data");
			data5.addAttribute("Type", "int");
			data5.setText("ID");*/
			
			
			Element Item2 = Items.addElement("Item");
			Element result = Item.addElement("Name");
			resultinfo.setText("Result");
			Element value1= Items.addElement("Value");
			value1.addAttribute("Type", "Records");
			Element records=value1.addElement("Records");
			
			
			
		Object[] obT=getEnumDictionaryCodeModle(map);
		if(obT!=null&&obT.length>0){
		for(int i=0;i<obT.length;i++){
			DicBean so =(DicBean) obT[i];
			Element record = records.addElement("Record");
			Element name = record.addElement("Data");
			 
			 
			if(so.getNAME()!=null){
				name.setText(so.getNAME());
			}else{
				name.setText("");
			}
			Element Code = record.addElement("Data");
			if(so.getCODE()!=null){
				Code.setText(so.getCODE());
			}else{
				Code.setText("");
			}
			
			Element DESP = record.addElement("Data");
			if(so.getDESP()!=null){
				DESP.setText(so.getDESP());
			}else{
				DESP.setText("");
			}
		 
			
			Element DEFID = record.addElement("Data");
			if(so.getDEFID()!=null){
				DEFID.setText(""+so.getDEFID());
			}else{
				DEFID.setText("");
			}
			/*Element ID = record.addElement("Data");
			System.out.println("DID:======"+so.getDID());
			if(so.getDID()!=null){
				ID.setText(""+so.getDID());
			}else{
				ID.setText("");
			}*/
			 
		}
		}else{
			records.setText("NOT DATA");
		}
		}else{
			Items.setText("NOT AUTH");
		}
		return rootElement.asXML();
	}
	public Object[] getEnumDictionaryCodeModle(Map map) throws Exception{
		ResultSetToObject pdb = new ResultSetToObject();
		Object[] obT = null;
		Object[] ob = null;
		String sqlT="";
		String objUrlT="";
		int index=0;
		if(map.get("CODE")!=null)
		{  
	      sqlT="select * from "+Tools.EZSPATIAL+".ez_dic_codes where defid=(select id from "+Tools.EZSPATIAL+".ez_dic_def where code='"+(String)map.get("CODE")+"')";
		} else return null;
		 
		System.err.println(sqlT);
			objUrlT="com.easymap.dao.DicBean";
		obT = pdb.parseDataEntityBeans(sqlT,objUrlT);
		return obT;
	}
	
	
	public Object[] getDictionaryCodeModle(Map map) throws Exception{
		ResultSetToObject pdb = new ResultSetToObject();
		Object[] obT = null;
		Object[] ob = null;
		String sqlT="";
		String objUrlT="";
		int index=0;
		if(map.get("CODE")!=null&&map.get("DICCODE")!=null)
		{  
		  sqlT="select * from "+Tools.EZSPATIAL+".ez_dic_codes where  code='"+(String)map.get("DICCODE")+"' and defid=(select id from "+Tools.EZSPATIAL+".ez_dic_def where code='"+(String)map.get("CODE")+"')";
		} else return null;
		 
		System.err.println(sqlT);
			objUrlT="com.easymap.dao.DicBean";
		obT = pdb.parseDataEntityBeans(sqlT,objUrlT);
		return obT;
	}
	
	
	
	public String getDictionaryCodeXML(Map map,String senderID,String methodName) throws Exception{
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("Response");
		Element SenderID = rootElement.addElement("SenderID");
		SenderID.setText(senderID);
		Element ServiceID = rootElement.addElement("ServiceID");
		ServiceID.setText(serviceIdMethod.getServiceIdMethod(methodName));
		Element Content = rootElement.addElement("Method");
		Element Name = Content.addElement("Name");
		Name.setText(methodName);
		Element Items = Content.addElement("Items");
		String methodId=serviceIdMethod.getServiceIdMethod(methodName);
		
		if(authorization.isAuthorization(senderID,methodId)) 
		{   
			Element Item = Items.addElement("Item");
			Element resultinfo = Item.addElement("Name");
			resultinfo.setText("ResultInfo");
			Element value= Items.addElement("Value");
			value.addAttribute("Type", "Fields");
			Element data1=value.addElement("Data");
			data1.addAttribute("Type", "string");
			data1.setText("NAME");
			
			Element data2=value.addElement("Data");
			data2.addAttribute("Type", "string");
			data2.setText("CODE");
			
			Element data3=value.addElement("Data");
			data3.addAttribute("Type", "string");
			data3.setText("DESP");
			
			 
			Element data6=value.addElement("Data");
			data6.addAttribute("Type", "string");
			data6.setText("DEFID");
			/*Element data5=value.addElement("Data");
			data5.addAttribute("Type", "int");
			data5.setText("ID");*/
			
			
			Element Item2 = Items.addElement("Item");
			Element result = Item.addElement("Name");
			resultinfo.setText("Result");
			Element value1= Items.addElement("Value");
			value1.addAttribute("Type", "Records");
			Element records=value1.addElement("Records");
			
			
			
		Object[] obT=getDictionaryCodeModle(map);
		if(obT!=null&&obT.length>0){
		for(int i=0;i<obT.length;i++){
			DicBean so =(DicBean) obT[i];
			Element record = records.addElement("Record");
			Element name = record.addElement("Data");
			 
			 
			if(so.getNAME()!=null){
				name.setText(so.getNAME());
			}else{
				name.setText("");
			}
			Element Code = record.addElement("Data");
			if(so.getCODE()!=null){
				Code.setText(so.getCODE());
			}else{
				Code.setText("");
			}
			
			Element DESP = record.addElement("Data");
			if(so.getDESP()!=null){
				DESP.setText(so.getDESP());
			}else{
				DESP.setText("");
			}
		 
			
			Element DEFID = record.addElement("Data");
			if(so.getDEFID()!=null){
				DEFID.setText(""+so.getDEFID());
			}else{
				DEFID.setText("");
			}
			/*Element ID = record.addElement("Data");
			System.out.println("DID:======"+so.getDID());
			if(so.getDID()!=null){
				ID.setText(""+so.getDID());
			}else{
				ID.setText("");
			}*/
			 
		}
		}else{
			records.setText("NOT DATA");
		}
		}else{
			Items.setText("NOT AUTH");
		}
		return rootElement.asXML();
	}
	@Override
	public Object[] getDateObjModle(Map map) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
 
	
	
}
