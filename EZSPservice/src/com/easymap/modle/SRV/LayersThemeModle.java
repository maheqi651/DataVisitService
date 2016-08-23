package com.easymap.modle.SRV;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.easymap.base.hcontrol.ResultSetToObject;
import com.easymap.base.minterface.modle;
import com.easymap.base.readdatabase.ConnectioSDB;
import com.easymap.base.tool.serviceIdMethod;
import com.easymap.dao.DicBean;
import com.easymap.dao.SRVdirectory;
import com.easymap.dao.TableBean;
import com.easymap.filter.Tools;
import com.easymap.modle.authorization.authorization;


public class LayersThemeModle implements modle{
	public LayersThemeModle(){}
	 
	public String getXML(Map map,String senderID,String methodName) throws Exception{
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("Response");
		Element SenderID = rootElement.addElement("SenderID");
		SenderID.setText(senderID);
		Element ServiceID = rootElement.addElement("ServiceID");
		ServiceID.setText(serviceIdMethod.getServiceIdMethod(methodName));
		Element Content = rootElement.addElement("Content");
		Element Name = Content.addElement("Name");
		Name.setText(methodName);
		
		Element Items = Content.addElement("Items");
		String methodId=serviceIdMethod.getServiceIdMethod(methodName);
		if(authorization.isAuthorization(senderID,methodId)) 
		{   
			
			Object[] obT=getQueryTableModle(map);
			Element Item=Items.addElement("Item");
			if(obT!=null&&obT.length>0)
			{
				TableBean tb=(TableBean)obT[0];
				Element TableNames=Item.addElement("TableName");
				TableNames.setText(tb.getENNAME());
				Element Codes=Item.addElement("Code");
				Codes.setText(tb.getCODE());
				Element Names=Item.addElement("Name");
				Names.setText(tb.getCHNAME());
			}else{
				Item.setText("NOT DATA");
			}
		}else{
			Items.setText("NOT AUTH");
		}
		return rootElement.asXML();
	}
	
	
	public Object[] getQueryTableModle(Map map) throws Exception{
		ResultSetToObject pdb = new ResultSetToObject();
		Object[] obT = null;
		Object[] ob = null;
		String sqlT="";
		String objUrlT="";
		String TableName=null;
		String DRCatalogCode=null;
		if(map.get("DRCatalogCode")!=null)
		{
			DRCatalogCode=(String)map.get("DRCatalogCode");
		}
		
		if(map.get("TableName")!=null)
		{
			TableName=(String)map.get("TableName");
		}
		if(TableName!=null&&DRCatalogCode!=null)
		{  //都不为空先查询Themecode  然后根据themecode  和tableName 进行查询
			sqlT="  select code,chname,enname from "+Tools.EZSPATIAL+".EZ_STD_LAYERS_LAYER_THEME t where themeid=" +
				 " (select id from  "+Tools.EZSPATIAL+".ez_std_layers_theme where code='"+DRCatalogCode+"') and enname='"+TableName+"' ";
		}else{
			if(TableName!=null)
			{
				sqlT="select code,chname,enname from "+Tools.EZSPATIAL+".EZ_STD_LAYERS_LAYER_THEME t where enname='"+TableName+"'";
			}else{
				return null;
			}
		}
		System.err.println(sqlT);
			objUrlT="com.easymap.dao.TableBean";
		obT = pdb.parseDataEntityBeans(sqlT,objUrlT);
		return obT;
	}
	@Override
	public Object[] getDateObjModle(Map map) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	 
	    
	 
	
	
 
	
	
}
