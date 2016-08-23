package com.easymap.datacenter.util;

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
import com.easymap.datacenter.model.MateDataBean;
import com.easymap.datacenter.model.MateDataDefBean;
import com.easymap.filter.Tools;
import com.easymap.modle.authorization.authorization;


public class MetaDataModle implements modle{
	public MetaDataModle(){}
	public Object[] srvDirectoryModle(Map map) throws Exception{
		ResultSetToObject pdb = new ResultSetToObject();
		Object[] obT = null;
		Object[] ob = null;
		String sqlT="";
		String objUrlT="";
		String md_code =null;
		String layercode=null;
		if(map.get("LAYERCODE")!=null)
		{  
			layercode=(String)map.get("LAYERCODE");
		}
		if(map.get("MD_CODE")!=null)
		{
			md_code=(String)map.get("MD_CODE");
		}
		sqlT="select  t.* " 
		+" from "+Tools.EZSPATIAL+".EZ_METADATA_ITEM t where  1=1 ";
			if(layercode!=null)
			{
				sqlT=sqlT+" and layer_code ='"+layercode+"'";
			}
			if(md_code!=null)
			{
				sqlT=sqlT+" and md_code ='"+md_code+"'";
			}
			
		System.err.println(sqlT);
			objUrlT="com.easymap.datacenter.model.MateDataBean";
		obT = pdb.parseDataEntityBeans(sqlT,objUrlT);
		return obT;
	}
	
	
	public Object[] srvMataDataDEFModle(Map map) throws Exception{
		ResultSetToObject pdb = new ResultSetToObject();
		Object[] obT = null;
		Object[] ob = null;
		String sqlT="";
		String objUrlT="";
		String code =null;
	 
		 
		if(map.get("CODE")!=null)
		{
			code=(String)map.get("CODE");
		}
		sqlT="select  t.* " 
		+" from "+Tools.EZSPATIAL+".EZ_METADATA_ITEM_DEF t where  1=1 ";
			 
			if(code!=null)
			{
				sqlT=sqlT+" and code ='"+code+"'";
			}
		System.err.println(sqlT);
			objUrlT="com.easymap.datacenter.model.MateDataDefBean";
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
			String str[]={"MD_CODE","LAYERID","LAYER_CODE","MD_VALUE","STDID","ID"};
			if(str!=null)
			{
				for(String columstr:str)
				{   
					Element data1=value.addElement("Data");
					if("ID".equals(columstr)||"STDID".equals(columstr)||"LAYERID".equals(columstr))
					{
						data1.addAttribute("Type", "int");
					}else{
						data1.addAttribute("Type", "string");
					}
					data1.setText(columstr);
				}
			}
			
			Element Item2 = Items.addElement("Item");
			Element result = Item.addElement("Name");
			resultinfo.setText("Result");
			Element value1= Items.addElement("Value");
			value1.addAttribute("Type", "Records");
			Element records=value1.addElement("Records");
		Object[] obT=srvDirectoryModle(map);
		if(obT!=null&&obT.length>0){
		for(int i=0;i<obT.length;i++){
		    MateDataBean so =(MateDataBean) obT[i];
			Element record = records.addElement("Record");
			for(String columstr:str)
			{
				Element name = record.addElement("Data");
				if("MD_CODE".equals(columstr)) 
				{
					if(so.getMD_CODE()!=null){
						name.setText(so.getMD_CODE());
					}else{
						name.setText("NO TEXT");
					}
					 continue;
				}
				if("ID".equals(columstr)) 
				{
				 
					if(so.getID()!=null){
						name.setText(""+so.getID());
					}else{
						name.setText("NO TEXT");
					}
					continue;
				}
				
				if("LAYERID".equals(columstr)) 
				{
				 
					if(so.getLAYERID()!=null){
						name.setText(""+so.getLAYERID());
					}else{
						name.setText("NO TEXT");
					}
					 
				}
				
				 
				
				if("LAYER_CODE".equals(columstr)) 
				{
					 
					if(so.getLAYER_CODE()!=null){
						name.setText(so.getLAYER_CODE());
					}else{
						name.setText("NO TEXT");
					}
					continue;
				}
				
				if("MD_VALUE".equals(columstr)) 
				{
					 
					if(so.getMD_VALUE()!=null){
						 name.setText(so.getMD_VALUE());
					}else{
						 name.setText("NO TEXT");
					}
					continue;
				}
				
				if("STDID".equals(columstr)) 
				{
				 
					if(so.getSTDID()!=null){
						name.setText(so.getSTDID()+"");
					}else{
						name.setText("NO TEXT");
					}
					continue;
				}
			}
		}
		}else{
			records.setText("NOT DATA");
		}
		}else{
			Items.setText("NOT AUTH");
		}
		return rootElement.asXML();
	}
	
	public String getXMLMataDef(Map map,String senderID,String methodName) throws Exception{
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
			String str[]={"CODE","NAME","PARENTCODE","TYPE","DATATYPE","DESP","OWNER","OPTIONS","APPNAME"};
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
				}
			}
			Element Item2 = Items.addElement("Item");
			Element result = Item.addElement("Name");
			resultinfo.setText("Result");
			Element value1= Items.addElement("Value");
			value1.addAttribute("Type", "Records");
			Element records=value1.addElement("Records");
			Object[] obT=srvMataDataDEFModle(map);
			if(obT!=null&&obT.length>0){
				for(int i=0;i<obT.length;i++){
					MateDataDefBean so =(MateDataDefBean) obT[i];
					Element record = records.addElement("Record");
					//String str[]={"CODE","NAME","PARENTCODE","TYPE","DATATYPE","DESP","OWNER","OPTIONS","ID"};
					for(String columstr:str)
					{
						Element name = record.addElement("Data");
						if("CODE".equals(columstr)) 
						{
							if(so.getCODE()!=null){
								name.setText(so.getCODE());
							}else{
								name.setText("NO TEXT");
							}
							continue;
						}
						if("ID".equals(columstr)) 
						{

							if(so.getID()!=null){
								name.setText(""+so.getID());
							}else{
								name.setText("NO TEXT");
							}
							continue;
						}

						if("NAME".equals(columstr)) 
						{

							if(so.getNAME()!=null){
								name.setText(""+so.getNAME());
							}else{
								name.setText("NO TEXT");
							}

						}
						if("APPNAME".equals(columstr)) 
						{

							if(so.getAPPNAME()!=null){
								name.setText(""+so.getAPPNAME());
							}else{
								name.setText("NO TEXT");
							}

						}



						if("PARENTCODE".equals(columstr)) 
						{

							if(so.getPARENTCODE()!=null){
								name.setText(so.getPARENTCODE());
							}else{
								name.setText("NO TEXT");
							}
							continue;
						}

						if("TYPE".equals(columstr)) 
						{

							if(so.getTYPE()!=null){
								name.setText(so.getTYPE());
							}else{
								name.setText("NO TEXT");
							}
							continue;
						}
						
						if("DATATYPE".equals(columstr)) 
						{

							if(so.getDATATYPE()!=null){
								name.setText(so.getDATATYPE()+"");
							}else{
								name.setText("NO TEXT");
							}
							continue;
						}
						if("DESP".equals(columstr)) 
						{

							if(so.getDESP()!=null){
								name.setText(so.getDESP()+"");
							}else{
								name.setText("NO TEXT");
							}
							continue;
						}
						if("OWNER".equals(columstr)) 
						{

							if(so.getOWNER()!=null){
								name.setText(so.getOWNER()+"");
							}else{
								name.setText("NO TEXT");
							}
							continue;
						}
						if("OPTIONS".equals(columstr)) 
						{

							if(so.getOPTIONS()!=null){
								name.setText(so.getOPTIONS()+"");
							}else{
								name.setText("NO TEXT");
							}
							continue;
						}
					}
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
