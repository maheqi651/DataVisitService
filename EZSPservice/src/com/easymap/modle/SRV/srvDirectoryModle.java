package com.easymap.modle.SRV;

import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.easymap.base.hcontrol.ResultSetToObject;
import com.easymap.base.minterface.modle;
import com.easymap.base.tool.serviceIdMethod;
import com.easymap.dao.SRVdirectory;
import com.easymap.filter.Tools;
import com.easymap.modle.authorization.authorization;


public class srvDirectoryModle implements modle{
	public srvDirectoryModle(){}
	public Object[] srvDirectoryModle(Map map) throws Exception{
		ResultSetToObject pdb = new ResultSetToObject();
		Object[] obT = null;
		Object[] ob = null;
		String sqlT="";
		String objUrlT="";
			//sqlT="SELECT T.NAME,T.INFO,T.TYPE,T.SERVICEID,T.PARENTSERVICEID,T.METHODNAME FROM EZSPATIAL.EZ_SERVICE_INFO T WHERE 1=1";
		sqlT="";
		System.out.println(map.get("SenderID"));
		if(map.get("SenderID")!=null&&!map.get("SenderID").equals("")){
			sqlT="SELECT T.NAME,T.INFO,T.TYPE,T.SERVICEID,T.PARENTSERVICEID,T.METHODNAME FROM "+Tools.EZSPATIAL+".EZ_SERVICE_INFO T WHERE 1=1"
            +" and T.serviceid in (select p.sercode  from "+Tools.YW6+".ez_p_function_service p where p.funccode = '"+map.get("SenderID")+"' group by p.sercode ) ";
 		}else
			return null;
		if(map.get("ServiceName")!=null&&!map.get("ServiceName").equals("")){
				sqlT+=" AND T.NAME='"+map.get("ServiceName")+"'";
			}
			if(map.get("ServiceID")!=null&&!map.get("ServiceID").equals("")){
				sqlT+=" AND T.SERVICEID='"+map.get("ServiceID")+"'";
			}
			if(map.get("ServiceType")!=null&&!map.get("ServiceType").equals("")){
				sqlT+=" AND T.TYPE='"+map.get("ServiceType")+"'";
			}
			System.err.println(sqlT);
			objUrlT="com.easymap.dao.SRVdirectory";
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
		Element Content = rootElement.addElement("Content");
		Element Name = Content.addElement("Name");
		Name.setText(methodName);
		Element Items = Content.addElement("Items");
		String methodId=serviceIdMethod.getServiceIdMethod(methodName);
		if(authorization.isAuthorization(senderID,methodId)) 
		{   
		Object[] obT=srvDirectoryModle(map);
		if(obT!=null&&obT.length>0){
		for(int i=0;i<obT.length;i++){
			SRVdirectory so =(SRVdirectory) obT[i];
			Element Item = Items.addElement("Item");
			Element NameI = Item.addElement("Name");
			if(so.getMETHODNAME()!=null){
				NameI.setText(so.getMETHODNAME());
			}else{
				NameI.setText("");
			}
			Element Type = Item.addElement("Type");
			if(so.getTYPE()!=null){
				Type.setText(so.getTYPE());
			}else{
				Type.setText("");
			}
			Element Cname = Item.addElement("Cname");
			if(so.getNAME()!=null){
				Cname.setText(so.getNAME());
			}else{
				Cname.setText("");
			}
			
			Element Desp = Item.addElement("Desp");
			if(so.getNAME()!=null){
				Desp.setText(so.getINFO());
			}else{
				Desp.setText("");
			}
			
			Element ID = Item.addElement("ID");
			if(so.getSERVICEID()!=null){
				ID.setText(so.getSERVICEID());
			}else{
				ID.setText("");
			}
		}
		}else{
			Items.setText("NOT SERVICE");
		}
		}else{
			Items.setText("NOT AUTH");
		}
		return rootElement.asXML();
	}
	public Object[] getDateObjModle(Map map) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
